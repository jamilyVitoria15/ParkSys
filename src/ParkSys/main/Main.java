package ParkSys.main;

import java.util.Collections;
import java.util.List;
import ParkSys.entities.Registro;
import ParkSys.entities.Veiculo;
import ParkSys.enums.TipoVeiculo;
import ParkSys.exceptions.VagaOcupadaException;
import ParkSys.services.GerenciadorEstacionamento;

public class Main {

    public static void main(String[] args) {
        // Obtém a instância única do Gerenciador (Singleton)
        GerenciadorEstacionamento gerenciador = GerenciadorEstacionamento.getInstancia();

        System.out.println("=== INICIANDO SIMULAÇÃO DO SISTEMA PARKSYS ===");

        // 1. Criando Threads para simular múltiplos veículos tentando estacionar ao mesmo tempo
        Thread simulaçãoCarro = new Thread(() -> {
            try {
                Veiculo carro = new Veiculo("ABC-1234", TipoVeiculo.CARRO);
                gerenciador.registrarEntrada(carro, "A01");
                System.out.println("[Thread Carro] Carro ABC-1234 estacionou com sucesso na vaga A01.");
            } catch (VagaOcupadaException e) {
                System.err.println("[Thread Carro] Erro ao estacionar: " + e.getMessage());
            }
        }, "Thread-Carro-Simulado");

        Thread simulaçãoMoto = new Thread(() -> {
            try {
                // Forçando um atraso mínimo para garantir a ordem cronológica de entrada
                Thread.sleep(100); 
                Veiculo moto = new Veiculo("XYZ-5678", TipoVeiculo.MOTO);
                gerenciador.registrarEntrada(moto, "A02");
                System.out.println("[Thread Moto] Moto XYZ-5678 estacionou com sucesso na vaga A02.");
            } catch (VagaOcupadaException | InterruptedException e) {
                System.err.println("[Thread Moto] Erro ao estacionar: " + e.getMessage());
            }
        }, "Thread-Moto-Simulado");

        // Nova thread tentando ocupar a vaga A01 que já estará preenchida (Gera a Exceção)
        Thread simulaçãoConcorrência = new Thread(() -> {
            try {
                Thread.sleep(200); // Aguarda os outros estacionarem
                Veiculo suv = new Veiculo("SUV-9999", TipoVeiculo.SUV);
                System.out.println("[Thread Concorrência] Tentando estacionar SUV-9999 na vaga A01...");
                gerenciador.registrarEntrada(suv, "A01");
            } catch (VagaOcupadaException e) {
                System.out.println("[Thread Concorrência] Bloqueio verificado com sucesso! Motivo: " + e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Thread-Concorrencia-Vaga");

        // Iniciando os processos simultâneos (Threads)
        simulaçãoCarro.start();
        simulaçãoMoto.start();
        simulaçãoConcorrência.start();

        // Aguarda a execução das Threads terminarem para exibir os relatórios finais
        try {
            simulaçãoCarro.join();
            simulaçãoMoto.join();
            simulaçãoConcorrência.join();
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== SIMULANDO REGISTRO DE SAÍDA E CÁLCULO DE TARIFA ===");
        try {
            double valorPago = gerenciador.registrarSaida("ABC-1234");
            System.out.println("Saída confirmada! Veículo ABC-1234 liberado. Valor Cobrado: R$ " + valorPago);
        } catch (Exception e) {
            System.err.println("Erro na saída: " + e.getMessage());
        }

        System.out.println("\n=== EXIBIÇÃO CRONOLÓGICA DOS REGISTROS (COMPARABLE - C05) ===");
        List<Registro> listaRegistros = gerenciador.getRegistros();
        
        // Ordena automaticamente usando a lógica do compareTo que você implementou em Registro
        Collections.sort(listaRegistros);

        for (Registro r : listaRegistros) {
            System.out.println("Placa: " + r.getVeiculo().getPlaca() + 
                               " | Tipo: " + r.getVeiculo().getTipo().getDescricao() + 
                               " | Entrada: " + r.getDataEntrada() + 
                               " | Thread de Origem: " + r.getThreadOrigem());
        }
    }
}