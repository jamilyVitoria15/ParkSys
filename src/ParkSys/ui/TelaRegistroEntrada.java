package ParkSys.ui;

import ParkSys.entities.Veiculo;
import ParkSys.enums.TipoVeiculo;
import ParkSys.services.GerenciadorEstacionamento;
import ParkSys.exceptions.VagaOcupadaException;

import javax.swing.*;
import java.awt.*;

public class TelaRegistroEntrada extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField txtPlaca;
    private JTextField txtVaga;
    private JComboBox<TipoVeiculo> cbTipoVeiculo;
    private GerenciadorEstacionamento gerenciador;

    public TelaRegistroEntrada(JFrame parent) {
        super(parent, "Registrar Entrada de Veículo", true);
        this.gerenciador = GerenciadorEstacionamento.getInstancia();

        setSize(400, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Painel do Formulário (Grid de Inputs)
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pnlForm.add(new JLabel("Placa do Veículo:"));
        txtPlaca = new JTextField();
        pnlForm.add(txtPlaca);

        pnlForm.add(new JLabel("Vaga Inicial (ex: A01):"));
        txtVaga = new JTextField();
        pnlForm.add(txtVaga);

        // T05: Popular o JComboBox dinamicamente usando os valores do Enum TipoVeiculo
        pnlForm.add(new JLabel("Tipo de Veículo:"));
        cbTipoVeiculo = new JComboBox<>(TipoVeiculo.values());
        
        // Customização para exibir o nome legível do Enum na tela
        cbTipoVeiculo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TipoVeiculo) {
                    setText(((TipoVeiculo) value).getNomeLegivel());
                }
                return this;
            }
        });
        pnlForm.add(cbTipoVeiculo);

        add(pnlForm, BorderLayout.CENTER);

        // Painel de Ações (Botões)
        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirmar = new JButton("Confirmar Entrada");
        JButton btnCancelar = new JButton("Cancelar");

        pnlAcoes.add(btnConfirmar);
        pnlAcoes.add(btnCancelar);
        add(pnlAcoes, BorderLayout.SOUTH);

        // Lógica dos Botões
        btnCancelar.addActionListener(e -> dispose());

        btnConfirmar.addActionListener(e -> {
            String placa = txtPlaca.getText().trim().toUpperCase();
            String vagaId = txtVaga.getText().trim().toUpperCase();
            TipoVeiculo tipo = (TipoVeiculo) cbTipoVeiculo.getSelectedItem();

            // Validação simples de campos vazios
            if (placa.isEmpty() || vagaId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Por favor, preencha todos os campos obrigatórios.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Instancia o modelo e delega a ação ao controlador (Singleton)
                Veiculo veiculo = new Veiculo(placa, tipo);
                gerenciador.registrarEntrada(veiculo, vagaId);

                JOptionPane.showMessageDialog(this, "✅ Entrada registrada com sucesso para o veículo " + placa);
                dispose(); // Fecha o formulário de entrada
                
            } catch (VagaOcupadaException ex) {
                // Exibe de forma amigável a exceção personalizada criada nas regras de negócio
                JOptionPane.showMessageDialog(this, "❌ Erro: " + ex.getMessage(), "Vaga Indisponível", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}