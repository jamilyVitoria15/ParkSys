package ParkSys.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ParkSys.entities.Registro;
import ParkSys.entities.Vaga;
import ParkSys.entities.Veiculo;
import ParkSys.enums.StatusVaga;
import ParkSys.exceptions.VagaOcupadaException;
import ParkSys.exceptions.VeiculoNaoEncontradoException;

public class GerenciadorEstacionamento {

    // Aplicando o Padrao Singleton
    private static GerenciadorEstacionamento instancia;

    private Map<String, Vaga> vagas;
    private List<Registro> registros;

    // Construtor privado para garantir o Singleton
    private GerenciadorEstacionamento() {
        vagas = new HashMap<>();
        registros = new ArrayList<>();
        inicializarVagas();
    }

    // Metodo de acesso do Singleton
    public static synchronized GerenciadorEstacionamento getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorEstacionamento();
        }
        return instancia;
    }

    // Cria algumas vagas iniciais para o sistema rodar (Ex: A01 ate A10)
    private void inicializarVagas() {
        for (int i = 1; i <= 10; i++) {
            String idVaga = String.format("A%02d", i);
            vagas.put(idVaga, new Vaga(idVaga, StatusVaga.LIVRE));
        }
    }

    // Registro de Entrada com protecao Thread-Safe (synchronized)
    public synchronized Registro registrarEntrada(Veiculo veiculo, String idVaga) throws VagaOcupadaException {
        Vaga vaga = vagas.get(idVaga);
        
        if (vaga == null) {
            throw new IllegalArgumentException("Vaga " + idVaga + " nao existe no sistema.");
        }

        if (!vaga.estaDisponivel()) {
            throw new VagaOcupadaException("A vaga " + idVaga + " ja esta ocupada ou reservada.");
        }

        // Altera o status da vaga usando o Enum correto
        vaga.setStatus(StatusVaga.OCUPADA);
        
        // Cria o registro de movimentacao cronologica
        Registro novoRegistro = new Registro(veiculo, vaga);
        novoRegistro.setThreadOrigem(Thread.currentThread().getName());
        
        registros.add(novoRegistro);
        return novoRegistro;
    }

    // Registro de Saida com calculo de tarifa baseada no tipo de veiculo
    public synchronized double registrarSaida(String placa) throws VeiculoNaoEncontradoException {
        Registro registroAtivo = null;

        // Busca o registro aberto para o veiculo correspondente
        for (Registro r : registros) {
            if (r.getVeiculo().getPlaca().equalsIgnoreCase(placa) && r.getDataSaida() == null) {
                registroAtivo = r;
                break;
            }
        }

        if (registroAtivo == null) {
            throw new VeiculoNaoEncontradoException("Nenhum veiculo com a placa " + placa + " foi encontrado estacionado.");
        }

        // Finaliza o registro
        registroAtivo.setDataSaida(java.time.LocalDateTime.now());
        
        // Libera a vaga associada
        Vaga vaga = registroAtivo.getVaga();
        vaga.setStatus(StatusVaga.LIVRE);

        // Regra de negocio: Tarifa por Hora simplificada para teste (minimo 1 hora)
        double tarifaHora = registroAtivo.getVeiculo().getTipo().getTarifaHora();
        registroAtivo.setValorPago(tarifaHora); 

        return tarifaHora;
    }

    public Map<String, Vaga> getVagas() {
        return vagas;
    }

    public List<Registro> getRegistros() {
        return registros;
    }
}