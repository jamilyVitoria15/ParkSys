package ParkSys.services;

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
                // Monitoriza a cada 3 segundos o estado do estacionamento
                Thread.sleep(3000);
                System.out.println("\n--- [Thread Monitor] Estado Atual das Vagas ---");
                gerenciador.getVagas().forEach((id, vaga) -> {
                    String status = vaga.estaDisponivel() ? "LIVRE" : "OCUPADA";
                    System.out.println("Vaga " + id + ": " + status);
                });
                System.out.println("----------------------------------------------\n");
            } catch (InterruptedException e) {
                System.err.println("[Thread Monitor] Monitorização interrompida.");
                break;
            }
        }
    }
}