package ParkSys.services;

import ParkSys.entities.Vaga;

public class MonitorRunnable implements Runnable {

    private GerenciadorEstacionamento gerenciador;
    private boolean rodando = true;

    public MonitorRunnable() {
        this.gerenciador = GerenciadorEstacionamento.getInstancia();
    }

    public void parar() {
        this.rodando = false;
    }

    @Override
    public void run() {
        System.out.println("[Thread Monitor] Inicializada com sucesso.");
        while (rodando) {
            try {
                Thread.sleep(3000);
                System.out.println("\n--- [Thread Monitor] Estado Atual das Vagas ---");
                
                // Corrigido: Iterando sobre a List<Vaga> de forma correta
                for (Vaga vaga : gerenciador.getVagas()) {
                    String status = vaga.estaDisponivel() ? "LIVRE" : "OCUPADA";
                    System.out.println("Vaga " + vaga.getId() + ": " + status);
                }
                
                System.out.println("----------------------------------------------\n");
            } catch (InterruptedException e) {
                System.err.println("[Thread Monitor] Monitorização interrompida.");
                break;
            }
        }
    }
}