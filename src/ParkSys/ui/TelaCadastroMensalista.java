package ParkSys.ui;

import ParkSys.entities.Mensalista;
import ParkSys.services.GerenciadorEstacionamento;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroMensalista extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtPlaca;
    private GerenciadorEstacionamento gerenciador;

    public TelaCadastroMensalista(JFrame parent) {
        super(parent, "Cadastro de Clientes Mensalistas", true);
        this.gerenciador = GerenciadorEstacionamento.getInstancia();

        setSize(400, 260);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Painel do Formulário
        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pnlForm.add(new JLabel("Nome do Cliente:"));
        txtNome = new JTextField();
        pnlForm.add(txtNome);

        pnlForm.add(new JLabel("CPF (Apenas números):"));
        txtCpf = new JTextField();
        pnlForm.add(txtCpf);

        pnlForm.add(new JLabel("Placa do Veículo:"));
        txtPlaca = new JTextField();
        pnlForm.add(txtPlaca);

        add(pnlForm, BorderLayout.CENTER);

        // Painel de Botões
        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Cadastrar");
        JButton btnCancelar = new JButton("Cancelar");

        pnlAcoes.add(btnSalvar);
        pnlAcoes.add(btnCancelar);
        add(pnlAcoes, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String cpf = txtCpf.getText().trim();
            String placa = txtPlaca.getText().trim().toUpperCase();

            // Validação de campos vazios
            if (nome.isEmpty() || cpf.isEmpty() || placa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Todos os campos são obrigatórios para o cadastro.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Instancia o objeto Veiculo exigido pelo construtor (usando o tipo padrão CARRO)
            ParkSys.entities.Veiculo veiculoMensalista = new ParkSys.entities.Veiculo(placa, ParkSys.enums.TipoVeiculo.CARRO);
            
            // 2. Cria o Mensalista passando os parâmetros na ordem exata do seu construtor: (cpf, nome, veiculo)
            Mensalista novoMensalista = new Mensalista(cpf, nome, veiculoMensalista);
            
            // C03: Delega o armazenamento para a LinkedList gerenciada pelo Controlador
            gerenciador.adicionarMensalista(novoMensalista);

            JOptionPane.showMessageDialog(this, "🎉 Mensalista '" + nome + "' cadastrado com sucesso!");
            dispose();
        });
    }
}