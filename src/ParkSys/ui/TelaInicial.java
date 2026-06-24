package ParkSys.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ParkSys.entities.Vaga;
import ParkSys.observer.EstacionamentoObserver;
import ParkSys.services.GerenciadorEstacionamento;

// P04 e P05: TelaInicial implementando a visualização gráfica e o padrão Observer
public class TelaInicial extends JFrame implements EstacionamentoObserver {
    
    private GerenciadorEstacionamento gerenciador;
    private JTextArea txtMonitorVagas;

    public TelaInicial() {
        // P01: Obtém a instância única via Singleton
        this.gerenciador = GerenciadorEstacionamento.getInstancia();
        
        // Configurações básicas da janela Swing
        setTitle("ParkSys - Sistema de Gestão de Estacionamento");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Vamos tratar o fechamento manualmente
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Cabeçalho da interface gráfica
        JLabel lblTitulo = new JLabel("PARKSYS - CONTROLADOR GERAL", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // 2. Painel Lateral de Botões (Menu de Opções)
        JPanel pnlBotoes = new JPanel(new GridLayout(5, 1, 10, 10));
        pnlBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnEntrada = new JButton("Registrar Entrada");
        JButton btnSaida = new JButton("Registrar Saída");
        JButton btnMensalista = new JButton("Cadastrar Mensalista");
        JButton btnRelatorio = new JButton("Visualizar Relatório");
        JButton btnExportar = new JButton("Exportar Backup (.ser)");

        pnlBotoes.add(btnEntrada);
        pnlBotoes.add(btnSaida);
        pnlBotoes.add(btnMensalista);
        pnlBotoes.add(btnRelatorio);
        pnlBotoes.add(btnExportar);
        add(pnlBotoes, BorderLayout.WEST);

        // 3. Painel Central - Painel Monitor Digital (P04)
        JPanel pnlMonitor = new JPanel(new BorderLayout());
        pnlMonitor.setBorder(BorderFactory.createTitledBorder("Painel Monitor de Vagas (Tempo Real)"));
        
        txtMonitorVagas = new JTextArea();
        txtMonitorVagas.setEditable(false);
        txtMonitorVagas.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(txtMonitorVagas);
        pnlMonitor.add(scroll, BorderLayout.CENTER);
        add(pnlMonitor, BorderLayout.CENTER);

        // =====================================================================
        // REQUISITO S06: Ações de persistência automatizada nos eventos de janela
        // =====================================================================
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // S06: Ao iniciar a aplicação, desserializa os dados automaticamente
                gerenciador.carregarDadosDoDisco();
                atualizarPainelTexto();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                // S06 & P06: Ao fechar, salva os dados automaticamente e remove o observador
                int confirmacao = JOptionPane.showConfirmDialog(
                        TelaInicial.this,
                        "Deseja salvar as alterações e fechar o sistema?",
                        "Sair do ParkSys",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirmacao == JOptionPane.YES_OPTION) {
                    gerenciador.salvarDadosNoDisco();
                    gerenciador.removerObserver(TelaInicial.this); // P06
                    System.exit(0);
                }
            }
        });

        // Configuração dos gatilhos dos botões para abrir as subjanelas
        btnEntrada.addActionListener(e -> new TelaRegistroEntrada(this).setVisible(true));
        btnSaida.addActionListener(e -> new TelaSaida(this).setVisible(true));
        btnMensalista.addActionListener(e -> new TelaCadastroMensalista(this).setVisible(true));
        btnRelatorio.addActionListener(e -> new TelaRelatorio(this).setVisible(true));
        
        btnExportar.addActionListener(e -> {
            gerenciador.salvarDadosNoDisco();
            JOptionPane.showMessageDialog(this, "Cópia binária de segurança (estacionamento.ser) gerada com sucesso!");
        });

        // P06: Registra a própria janela como observadora do gerenciador
        gerenciador.registrarObserver(this);
    }

    // P02 e P03: Método disparado automaticamente via padrão Observer
    @Override
    public void atualizarVaga(String idVaga, boolean estaDisponivel) {
        atualizarPainelTexto();
    }

    // P05: Atualiza o layout visual sem violar as regras do domínio
    public void atualizarPainelTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s | %-30s\n", "VAGA ID", "STATUS ATUAL"));
        sb.append("--------------------------------------------------\n");
        
        // P05: Varre de forma indireta as vagas cadastradas no gerenciador
        for (Vaga vaga : gerenciador.getVagas()) {
            sb.append(String.format("%-10s | %-30s\n", vaga.getId(), vaga.getStatus().getDescricao()));
        }
        txtMonitorVagas.setText(sb.toString());
    }
}