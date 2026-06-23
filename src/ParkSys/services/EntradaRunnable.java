package ParkSys.services;

import ParkSys.entities.Veiculo;
import ParkSys.enums.TipoVeiculo;
import ParkSys.exceptions.VagaOcupadaException;

public class EntradaRunnable implements Runnable {

    private GerenciadorEstacionamento gerenciador;
    private String placaSimulada;
    private String vagaDesejada;

    public EntradaRunnable(String placaSimulada, String vagaDesejada) {
        this.gerenciador = GerenciadorEstacionamento.getInstancia();
        this.placaSimulada = placaSimulada;
        this.vagaDesejada = vagaDesejada;
    }

    @Override
    public void run() {
        try {
            System.out.println("[Thread Entrada] Tentando estacionar o veículo " + placaSimulada + " na vaga " + vagaDesejada + "...");
            // Simula um tempo de processamento/chegada aleatório de até 1 segundo
            Thread.sleep((long) (Math.random() * 1000));
            
            Veiculo veiculo = new Veiculo(placaSimulada, TipoVeiculo.CARRO);
            gerenciador.registrarEntrada(veiculo, vagaDesejada);
            
            System.out.println("[Thread Entrada] Sucesso! Veículo " + placaSimulada + " ocupou a vaga " + vagaDesejada);
        } catch (VagaOcupadaException e) {
            System.err.println("[Thread Entrada] Bloqueio de Concorrência: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("[Thread Entrada] Simulação interrompida.");
        }
    }
}