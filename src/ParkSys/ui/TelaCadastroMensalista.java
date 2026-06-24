package ParkSys.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class TelaCadastroMensalista extends JDialog {
    public TelaCadastroMensalista(JFrame parent) {
        super(parent, "Cadastro de Clientes Mensalistas", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}