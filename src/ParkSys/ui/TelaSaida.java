package ParkSys.ui;

import ParkSys.services.GerenciadorEstacionamento;
import ParkSys.exceptions.VeiculoNaoEncontradoException;

import javax.swing.*;
import java.awt.*;

public class TelaSaida extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField txtPlaca;
    private GerenciadorEstacionamento gerenciador;

    public TelaSaida(JFrame parent) {
        super(parent, "Registrar Saída / Pagamento", true);
        this.gerenciador = GerenciadorEstacionamento.getInstancia();

        setSize(380, 180);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlForm = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        pnlForm.add(new JLabel("Digite a Placa do Veículo para dar Saída:"));
        txtPlaca = new JTextField();
        txtPlaca.setFont(new Font("Arial", Font.BOLD, 14));
        pnlForm.add(txtPlaca);

        add(pnlForm, BorderLayout.CENTER);

        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFinalizar = new JButton("Processar Saída");
        JButton btnCancelar = new JButton("Voltar");

        pnlAcoes.add(btnFinalizar);
        pnlAcoes.add(btnCancelar);
        add(pnlAcoes, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());

        btnFinalizar.addActionListener(e -> {
            String placa = txtPlaca.getText().trim().toUpperCase();

            if (placa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Por favor, informe a placa do veículo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Invoca o método thread-safe do gerenciador
                gerenciador.registrarSaida(placa);
                JOptionPane.showMessageDialog(this, "✅ Veículo " + placa + " liberado com sucesso do pátio.");
                dispose();
                
            } catch (VeiculoNaoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, "❌ Erro: " + ex.getMessage(), "Não Encontrado", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}