package ParkSys.services;

import ParkSys.entities.Veiculo;
import ParkSys.enums.TipoVeiculo;
import ParkSys.exceptions.VagaOcupadaException;
import java.util.Random;

// REQUISITOS M01, M02 e M03: Tarefa paralela para simulação de fluxo contínuo
public class EntradaRunnable implements Runnable {

    private final GerenciadorEstacionamento gerenciador;
    private final Random random = new Random();
    private final String[] placasFicticias = {"ABC-1234", "XYZ-9876", "MNO-4512", "KJG-8839", "IOP-0021"};

    public EntradaRunnable() {
        this.gerenciador = GerenciadorEstacionamento.getInstancia();
    }

    @Override
    public void run() {
        System.out.println("🚀 [" + Thread.currentThread().getName() + "] Thread de simulação iniciada.");

        // M05: O laço deve rodar continuamente monitorando a flag de interrupção ativa
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Sorteia um tempo de espera aleatório entre 4 e 8 segundos (M02)
                int tempoEspera = 4000 + random.nextInt(4000);
                Thread.sleep(tempoEspera);

                // Sorteia dados para a inserção automatizada
                String placa = placasFicticias[random.nextInt(placasFicticias.length)] + "-" + random.nextInt(9);
                TipoVeiculo[] tipos = TipoVeiculo.values();
                TipoVeiculo tipoSorteado = tipos[random.nextInt(tipos.length)];
                
                // Escolhe uma vaga inicial aleatória entre A01 e A10
                int vagaNum = 1 + random.nextInt(10);
                String vagaId = "A" + (vagaNum < 10 ? "0" + vagaNum : vagaNum);

                Veiculo veiculoSimulado = new Veiculo(placa, tipoSorteado);

                // Tenta inserir no gerenciador thread-safe
                try {
                    gerenciador.registrarEntrada(veiculoSimulado, vagaId);
                } catch (VagaOcupadaException e) {
                    System.out.println("⚠️ [" + Thread.currentThread().getName() + "] Tentativa falhou: " + e.getMessage());
                }

            } catch (InterruptedException e) {
                // M05: Trata a interrupção limpando os recursos e parando a execução com segurança
                System.out.println("🛑 [" + Thread.currentThread().getName() + "] Thread de simulação interrompida via interrupt(). Encerrando...");
                break; 
            }
        }
    }
}