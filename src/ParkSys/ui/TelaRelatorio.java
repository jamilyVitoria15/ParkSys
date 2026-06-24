package ParkSys.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class TelaRelatorio extends JDialog {
    public TelaRelatorio(JFrame parent) {
        super(parent, "Relatório Geral de Ocupação e Caixa", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }
}