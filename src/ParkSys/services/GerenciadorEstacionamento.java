package ParkSys.services;

import ParkSys.entities.Vaga;
import ParkSys.entities.Veiculo;
import ParkSys.entities.Registro;
import ParkSys.enums.StatusVaga; // Importado para gerenciar os estados
import ParkSys.exceptions.VagaOcupadaException;
import ParkSys.exceptions.VeiculoNaoEncontradoException;
import ParkSys.observer.EstacionamentoObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorEstacionamento {

    private List<Vaga> vagas = new ArrayList<>();
    private List<Registro> registrosAtivos = new ArrayList<>();
    
    // Lista de observers conectados
    private List<EstacionamentoObserver> observers = new ArrayList<>();

    public GerenciadorEstacionamento() {}

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
    // REGRAS DE NEGÓCIO COM ATUALIZAÇÃO DE STATUS CORRETA
    // =========================================================================

    public synchronized void registrarEntrada(Veiculo veiculo, String idVaga) throws VagaOcupadaException {
        for (Vaga vaga : vagas) {
            if (vaga.getId().equalsIgnoreCase(idVaga)) { // Corrigido para getId()
                if (!vaga.estaDisponivel()) {           // Corrigido para estaDisponivel()
                    throw new VagaOcupadaException("A vaga " + idVaga + " já está ocupada!");
                }
                
                // Altera o status usando o seu Enum StatusVaga
                vaga.setStatus(StatusVaga.OCUPADA);
                
                // Cria o registro de entrada
                Registro novoRegistro = new Registro(veiculo, vaga);
                registrosAtivos.add(novoRegistro);
                
                System.out.println("🚗 Veículo [" + veiculo.getPlaca() + "] entrou na vaga [" + idVaga + "].");
                
                // Notifica o painel que a vaga não está mais disponível
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
            throw new VeiculoNaoEncontradoException("Veículo com a placa " + placa + " não foi localizado no estacionamento.");
        }
        
        registroEncontrado.setDataSaida(LocalDateTime.now());
        
        // Libera a vaga associada mudando o status para LIVRE
        Vaga vagaLiberada = registroEncontrado.getVaga();
        vagaLiberada.setStatus(StatusVaga.LIVRE);
        
        registrosAtivos.remove(registroEncontrado);
        
        System.out.println("💸 Veículo [" + placa + "] liberou a vaga [" + vagaLiberada.getId() + "]."); // Corrigido para getId()
        
        // Notifica o painel que a vaga está livre novamente
        notificarObservers(vagaLiberada.getId(), true);
    }

    public List<Vaga> getVagas() {
        return vagas;
    }
}