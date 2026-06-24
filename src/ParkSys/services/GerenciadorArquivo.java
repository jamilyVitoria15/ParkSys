package ParkSys.services;

import ParkSys.entities.Registro;
import ParkSys.entities.Vaga;
import ParkSys.entities.Mensalista;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class GerenciadorArquivo {

    // =========================================================================
    // REQUISITO S02 & S05: Serializar os dados do sistema com bloco try-catch-finally
    // =========================================================================
    public void serializar(Map<String, Vaga> vagas, List<Registro> registros, List<Mensalista> mensalistas, String path) {
        ObjectOutputStream oos = null;
        boolean sucesso = false;

        try {
            FileOutputStream fos = new FileOutputStream(path);
            oos = new ObjectOutputStream(fos);

            DadosParkSys dados = new DadosParkSys(vagas, registros, mensalistas);
            oos.writeObject(dados);
            sucesso = true;

        } catch (IOException e) {
            System.err.println("❌ Erro ao serializar dados: " + e.getMessage());
        } finally {
            // S05: O bloco finally deve fechar os fluxos e logar o resultado da operação
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar stream de saída: " + e.getMessage());
                }
            }
            System.out.println("💾 [BACKUP] Operação de salvamento finalizada. Status Sucesso: " + sucesso);
        }
    }

    // =========================================================================
    // REQUISITO S03 & S05: Desserializar os dados tratando FileNotFoundException
    // =========================================================================
    public DadosParkSys desserializar(String path) {
        ObjectInputStream ois = null;
        boolean sucesso = false;

        try {
            FileInputStream fis = new FileInputStream(path);
            ois = new ObjectInputStream(fis);

            DadosParkSys dados = (DadosParkSys) ois.readObject();
            sucesso = true;
            return dados;

        } catch (FileNotFoundException e) {
            // S03: Tratar FileNotFoundException inicializando estruturas vazias
            System.out.println("⚠️ Arquivo de persistência '" + path + "' não encontrado. Criando base de dados vazia...");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Erro ao ler arquivo de persistência: " + e.getMessage());
            return null;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar stream de entrada: " + e.getMessage());
                }
            }
            System.out.println("📂 [RESTAURAÇÃO] Operação de leitura finalizada. Arquivo carregado: " + sucesso);
        }
    }

    // =========================================================================
    // REQUISITO S04: Exportar relatório em formato legível .txt usando BufferedWriter
    // =========================================================================
    public void exportarRelatorioTxt(List<Registro> registros, String path) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            writer.write("=====================================================\n");
            writer.write("               RELATÓRIO FINANCEIRO PARKSYS          \n");
            writer.write("=====================================================\n");
            writer.write("Gerado em: " + LocalDateTime.now().format(formatter) + "\n");
            writer.write("-----------------------------------------------------\n\n");

            double receitaTotal = 0.0;

            if (registros.isEmpty()) {
                writer.write("Nenhum registro de movimentação finalizado nesta sessão.\n");
            } else {
                writer.write(String.format("%-12s | %-15s | %-20s | %-10s\n", "PLACA", "VAGA", "DATA SAÍDA", "VALOR PAGO"));
                writer.write("-----------------------------------------------------\n");
                
                for (Registro r : registros) {
                    String dataSaidaFormated = r.getDataSaida() != null ? r.getDataSaida().format(formatter) : "Em aberto";
                    writer.write(String.format("%-12s | %-15s | %-20s | R$ %.2f\n",
                            r.getVeiculo().getPlaca(),
                            r.getVaga().getId(),
                            dataSaidaFormated,
                            r.getValorPago()));
                    receitaTotal += r.getValorPago();
                }
            }

            writer.write("\n-----------------------------------------------------\n");
            writer.write(String.format("RECEITA TOTAL CONSOLIDADA: R$ %.2f\n", receitaTotal));
            writer.write("=====================================================\n");

            System.out.println("📄 Relatório formatado exportado com sucesso em: " + path);

        } catch (IOException e) {
            System.err.println("❌ Erro ao exportar relatório em texto: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar o writer do relatório: " + e.getMessage());
                }
            }
        }
    }
}