package ParkSys.main;

import ParkSys.entities.Vaga;
import ParkSys.enums.StatusVaga;
import ParkSys.services.GerenciadorEstacionamento;
import ParkSys.services.EntradaRunnable;
import ParkSys.services.MonitorRunnable;
import ParkSys.observer.PainelMonitor;

public class Principal {

    public static void main(String[] args) {
        System.out.println("===================================================");
        System.out.println("   INICIALIZANDO SISTEMA DE ESTACIONAMENTO PARKSYS ");
        System.out.println("===================================================\n");

        // 1. Obtém a instância única (Singleton) do gerenciador
        GerenciadorEstacionamento gerenciador = GerenciadorEstacionamento.getInstancia();

        // 2. Cria e adiciona algumas vagas iniciais com o status LIVRE
        gerenciador.adicionarVaga(new Vaga("A1", StatusVaga.LIVRE));
        gerenciador.adicionarVaga(new Vaga("A2", StatusVaga.LIVRE));
        gerenciador.adicionarVaga(new Vaga("B1", StatusVaga.LIVRE));
        gerenciador.adicionarVaga(new Vaga("B2", StatusVaga.LIVRE));

        // 3. PADRÃO OBSERVER: Cria o painel digital e registra no gerenciador
        PainelMonitor painelDigital = new PainelMonitor();
        gerenciador.registrarObserver(painelDigital);
        
        System.out.println("✅ Painel Monitor Digital cadastrado e observando as vagas...");
        System.out.println("---------------------------------------------------\n");

        // 4. THREADS: Instancia as simulações de acordo com os seus construtores
        // Passando a Placa e a Vaga desejada que a sua EntradaRunnable pede!
        Thread threadEntrada1 = new Thread(new EntradaRunnable("ABC-1234", "A1"), "Thread-Entrada-Carro1");
        Thread threadEntrada2 = new Thread(new EntradaRunnable("XYZ-5678", "B1"), "Thread-Entrada-Carro2");
        
  
        Thread threadMonitor = new Thread(new MonitorRunnable(), "Thread-Auditoria-Vagas");
        // 5. Inicia a execução das Threads
        System.out.println("🚀 Disparando Threads concorrentes de simulação...\n");
        threadEntrada1.start();
        threadEntrada2.start();
        threadMonitor.start();

        // Aguarda um tempo de simulação para ver as notificações no console
        try {
            Thread.sleep(15000); 
        } catch (InterruptedException e) {
            System.out.println("⚠️ Execução principal interrompida.");
        }

        System.out.println("\n===================================================");
        System.out.println("         FIM DA SIMULAÇÃO DO PARKSYS               ");
        System.out.println("===================================================");
        System.exit(0);
        
        ParkSys.ui.MenuEstacionamento menu = new ParkSys.ui.MenuEstacionamento();
        menu.exibirMenu();
    }
    
    
}