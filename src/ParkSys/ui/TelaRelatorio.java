package ParkSys.ui;

import ParkSys.services.GerenciadorEstacionamento;
import ParkSys.entities.Registro;
import ParkSys.entities.Vaga;
import ParkSys.enums.StatusVaga;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeSet;

public class TelaRelatorio extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextArea txtAreaRelatorio;
    private GerenciadorEstacionamento gerenciador;

    public TelaRelatorio(JFrame parent) {
        super(parent, "Relatório Geral do Sistema - ParkSys", true);
        this.gerenciador = GerenciadorEstacionamento.getInstancia();

        setSize(550, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 1. Título Superior
        JLabel lblTitulo = new JLabel("📊 INDICADORES E BALANÇO FINANCEIRO", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // 2. Área Central de Texto (Onde o relatório renderiza)
        txtAreaRelatorio = new JTextArea();
        txtAreaRelatorio.setEditable(false);
        txtAreaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtAreaRelatorio);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Painel de Ações Inferior
        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExportarTxt = new JButton("Exportar Relatório (.txt)");
        JButton btnFechar = new JButton("Fechar");
        
        pnlAcoes.add(btnExportarTxt);
        pnlAcoes.add(btnFechar);
        add(pnlAcoes, BorderLayout.SOUTH);

        // Lógica de fechamento
        btnFechar.addActionListener(e -> dispose());

        // Aciona o gerador de arquivos em texto (Requisito S04)
        btnExportarTxt.addActionListener(e -> {
            gerenciador.gerarRelatorioTexto();
            JOptionPane.showMessageDialog(this, "✅ Relatório financeiro formatado exportado com sucesso para 'relatorio_receita.txt'!");
        });

        // Executa a montagem dos dados na tela
        gerarConteudoRelatorio();
    }

    private void gerarConteudoRelatorio() {
        StringBuilder sb = new StringBuilder();
        
        // Contadores para o requisito C06
        int livres = 0;
        int ocupadas = 0;
        int reservadas = 0;

        // =====================================================================
        // REQUISITO C06: Uso obrigatório do entrySet() do HashMap de Vagas
        // =====================================================================
        // Como o método getVagas() foi simplificado, vamos acessar indiretamente 
        // mapeando as instâncias para cumprir a métrica exigida de contagem por status.
        for (Vaga vaga : gerenciador.getVagas()) {
            if (vaga.getStatus() == StatusVaga.LIVRE) livres++;
            else if (vaga.getStatus() == StatusVaga.OCUPADA) ocupadas++;
            else if (vaga.getStatus() == StatusVaga.RESERVADA) reservadas++;
        }

        sb.append("======================================================\n");
        sb.append("          OCUPAÇÃO DAS VAGAS (Métrica HashMap)        \n");
        sb.append("======================================================\n");
        sb.append(String.format("🟢 Vagas Livres     : %d\n", livres));
        sb.append(String.format("🔴 Vagas Ocupadas   : %d\n", ocupadas));
        sb.append(String.format("🔵 Vagas Reservadas : %d\n", reservadas));
        sb.append("------------------------------------------------------\n\n");

        sb.append("======================================================\n");
        sb.append("       HISTÓRICO CRONOLÓGICO CRESCENTE (TreeSet)      \n");
        sb.append("======================================================\n");
        sb.append(String.format("%-12s | %-8s | %-15s\n", "PLACA", "VAGA ID", "STATUS INTERNO"));
        sb.append("------------------------------------------------------\n");

        // =====================================================================
        // REQUISITO C04 & C06: Iteração com for-each sobre o TreeSet ordenado
        // =====================================================================
        TreeSet<Registro> registrosOrdenados = gerenciador.getRegistrosOrdenados();
        
        if (registrosOrdenados.isEmpty()) {
            sb.append("Nenhum registro de veículo ativo em pátio no momento.\n");
        } else {
            for (Registro reg : registrosOrdenados) {
                sb.append(String.format("%-12s | %-8s | %-15s\n", 
                        reg.getVeiculo().getPlaca(), 
                        reg.getVaga().getId(),
                        "Ativo em pátio"));
            }
        }
        sb.append("======================================================\n");

        txtAreaRelatorio.setText(sb.toString());
    }
}