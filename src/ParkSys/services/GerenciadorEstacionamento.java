package ParkSys.services;

import ParkSys.entities.Vaga;
import ParkSys.entities.Veiculo;
import ParkSys.entities.Registro;
import ParkSys.enums.StatusVaga;
import ParkSys.exceptions.VagaOcupadaException;
import ParkSys.exceptions.VeiculoNaoEncontradoException;
import ParkSys.observer.EstacionamentoObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorEstacionamento {

    // Instância única do Singleton
    private static GerenciadorEstacionamento instancia;

    private List<Vaga> vagas = new ArrayList<>();
    private List<Registro> registrosAtivos = new ArrayList<>();
    private List<EstacionamentoObserver> observers = new ArrayList<>();

    // Construtor privado para garantir o padrão Singleton
    private GerenciadorEstacionamento() {}

    // Método público para obter a instância única
    public static synchronized GerenciadorEstacionamento getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorEstacionamento();
        }
        return instancia;
    }

    public void adicionarVaga(Vaga vaga) {
        this.vagas.add(vaga);
    }

    // =========================================================================
    // MÉTODOS DO PADRÃO OBSERVER
    // =========================================================================
    
    public void registrarObserver(EstacionamentoObserver observer) {
        this.observers.add(observer);
    }

    public void removerObserver(EstacionamentoObserver observer) {
        this.observers.remove(observer);
    }

    private void notificarObservers(String idVaga, boolean estaDisponivel) {
        for (EstacionamentoObserver observer : observers) {
            observer.atualizarVaga(idVaga, estaDisponivel);
        }
    }

    // =========================================================================
    // REGRAS DE NEGÓCIO
    // =========================================================================

    public synchronized void registrarEntrada(Veiculo veiculo, String idVaga) throws VagaOcupadaException {
        for (Vaga vaga : vagas) {
            if (vaga.getId().equalsIgnoreCase(idVaga)) {
                if (!vaga.estaDisponivel()) {
                    throw new VagaOcupadaException("A vaga " + idVaga + " já está ocupada!");
                }
                
                vaga.setStatus(StatusVaga.OCUPADA);
                
                Registro novoRegistro = new Registro(veiculo, vaga);
                registrosAtivos.add(novoRegistro);
                
                System.out.println("🚗 Veículo [" + veiculo.getPlaca() + "] entrou na vaga [" + idVaga + "].");
                
                notificarObservers(idVaga, false);
                return;
            }
        }
        System.out.println("⚠️ Vaga " + idVaga + " não encontrada no sistema.");
    }

    public synchronized void registrarSaida(String placa) throws VeiculoNaoEncontradoException {
        Registro registroEncontrado = null;
        for (Registro r : registrosAtivos) {
            if (r.getVeiculo().getPlaca().equalsIgnoreCase(placa)) {
                registroEncontrado = r;
                break;
            }
        }
        
        if (registroEncontrado == null) {
            throw new VeiculoNaoEncontradoException("Veículo com a placa " + placa + " não foi localizado.");
        }
        
        registroEncontrado.setDataSaida(LocalDateTime.now());
        
        Vaga vagaLiberada = registroEncontrado.getVaga();
        vagaLiberada.setStatus(StatusVaga.LIVRE);
        
        registrosAtivos.remove(registroEncontrado);
        System.out.println("💸 Veículo [" + placa + "] liberou a vaga [" + vagaLiberada.getId() + "].");
        
        notificarObservers(vagaLiberada.getId(), true);
    }

    public List<Vaga> getVagas() {
        return vagas;
    }
}