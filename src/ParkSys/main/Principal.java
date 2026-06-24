package ParkSys.main;

import ParkSys.ui.TelaInicial;
import javax.swing.SwingUtilities;

public class Principal {
    public static void main(String[] args) {
        // Dispara o carregamento seguro da interface gráfica Swing
        SwingUtilities.invokeLater(() -> {
            TelaInicial interfaceGrafica = new TelaInicial();
            interfaceGrafica.setVisible(true);
        });
    }
}