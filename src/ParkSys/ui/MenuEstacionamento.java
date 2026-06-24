package ParkSys.ui;

import ParkSys.entities.Vaga;
import ParkSys.entities.Veiculo;
import ParkSys.enums.TipoVeiculo;
import ParkSys.services.GerenciadorEstacionamento;
import ParkSys.exceptions.VagaOcupadaException;
import ParkSys.exceptions.VeiculoNaoEncontradoException;

import java.util.Scanner;

public class MenuEstacionamento {

    private GerenciadorEstacionamento gerenciador;
    private Scanner scanner;

    public MenuEstacionamento() {
        // Conecta diretamente ao Singleton do sistema
        this.gerenciador = GerenciadorEstacionamento.getInstancia();
        this.scanner = new Scanner(System.in);
    }

    // Método para iniciar o loop do menu no console
    public void exibirMenu() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n--- MENU ADMINISTRATIVO PARKSYS ---");
            System.out.println("1. Registrar Entrada de Veículo");
            System.out.println("2. Registrar Saída de Veículo (Liberar Vaga)");
            System.out.println("3. Listar Status Geral das Vagas");
            System.out.println("0. Sair do Painel");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        executarEntrada();
                        break;
                    case 2:
                        executarSaida();
                        break;
                    case 3:
                        listarVagas();
                        break;
                    case 0:
                        System.out.println("Encerrando painel de atendimento...");
                        break;
                    default:
                        System.out.println("⚠️ Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Por favor, digite um número válido.");
            }
            
            // Pequena pausa para melhor leitura do console
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
    }

    private void executarEntrada() {
        System.out.println("\n--- REGISTRO DE ENTRADA ---");
        System.out.print("Digite a placa do veículo: ");
        String placa = scanner.nextLine().toUpperCase();

        System.out.print("Digite o ID da vaga desejada (ex: A1): ");
        String idVaga = scanner.nextLine().toUpperCase();

        // Cria o veículo simulando entrada padrão de CARRO
        Veiculo veiculo = new Veiculo(placa, TipoVeiculo.CARRO);

        try {
            // Chama o método thread-safe do gerenciador
            gerenciador.registrarEntrada(veiculo, idVaga);
            System.out.println("✅ Processo de entrada finalizado com sucesso.");
        } catch (VagaOcupadaException e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }

    private void executarSaida() {
        System.out.println("\n--- REGISTRO DE SAÍDA ---");
        System.out.print("Digite a placa do veículo a ser liberado: ");
        String placa = scanner.nextLine().toUpperCase();

        try {
            gerenciador.registrarSaida(placa);
            System.out.println("✅ Processo de saída/pagamento finalizado.");
        } catch (VeiculoNaoEncontradoException e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }

    private void listarVagas() {
        System.out.println("\n--- MAPA GERAL DE VAGAS ---");
        if (gerenciador.getVagas().isEmpty()) {
            System.out.println("Nenhuma vaga cadastrada no sistema.");
            return;
        }

        for (Vaga vaga : gerenciador.getVagas()) {
            String statusColorido = vaga.estaDisponivel() ? "🟢 LIVRE" : "🔴 OCUPADA";
            System.out.println("-> Vaga [" + vaga.getId() + "] | Status: " + statusColorido);
        }
    }
}