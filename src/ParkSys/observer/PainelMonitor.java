package ParkSys.observer;

public class PainelMonitor implements EstacionamentoObserver {

    @Override
    public void atualizarVaga(String idVaga, boolean estaDisponivel) {
        String status = estaDisponivel ? "LIBERADA" : "OCUPADA";
        System.out.println("\n📢 [PAINEL MONITOR DIGITAL] Notificação Recebida!");
        System.out.println("-> A Vaga [" + idVaga + "] mudou de estado e agora está: " + status);
        System.out.println("---------------------------------------------------\n");
    }
}