package ParkSys.services;

import ParkSys.entities.Vaga;
import ParkSys.entities.Veiculo;
import ParkSys.entities.Registro;
import ParkSys.entities.Mensalista;
import ParkSys.enums.StatusVaga;
import ParkSys.exceptions.VagaOcupadaException;
import ParkSys.exceptions.VeiculoNaoEncontradoException;
import ParkSys.observer.EstacionamentoObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class GerenciadorEstacionamento {

    // C01: Armazenar as vagas em HashMap para garantir acesso O(1) por ID
    private Map<String, Vaga> vagas = new HashMap<>();
    
    // C02: Manter os registros de entrada/saída em ArrayList
    private List<Registro> registrosAtivos = new ArrayList<>();
    
    // C03: Manter os mensalistas em LinkedList (adequada para inserção/remoção nas pontas)
    private List<Mensalista> mensalistas = new LinkedList<>();
    
    private List<EstacionamentoObserver> observers = new ArrayList<>();
    private static GerenciadorEstacionamento instancia;

    private GerenciadorEstacionamento() {}

    public static synchronized GerenciadorEstacionamento getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorEstacionamento();
        }
        return instancia;
    }

    // Adaptado para o HashMap (C01)
    public void adicionarVaga(Vaga vaga) {
        this.vagas.put(vaga.getId().toUpperCase(), vaga);
    }

    // =========================================================================
    // REQUISITO C04: Retornar registros em ordem cronológica crescente usando TreeSet
    // =========================================================================
    public TreeSet<Registro> getRegistrosOrdenados() {
        // O TreeSet usará automaticamente o compareTo implementado em Registro
        return new TreeSet<>(registrosAtivos); 
    }

    // =========================================================================
    // REGRAS DE NEGÓCIO COM HAMAP (C01) E REQUISITOS DE OBSERVER
    // =========================================================================

    public synchronized void registrarEntrada(Veiculo veiculo, String idVaga) throws VagaOcupadaException {
        String key = idVaga.toUpperCase();
        
        // Busca O(1) usando a chave do HashMap
        if (!vagas.containsKey(key)) {
            System.out.println("⚠️ Vaga " + idVaga + " não encontrada no sistema.");
            return;
        }

        Vaga vaga = vagas.get(key);

        if (!vaga.estaDisponivel()) {
            throw new VagaOcupadaException("A vaga " + idVaga + " já está ocupada!");
        }
        
        // M04: Grava o nome da Thread atual no campo transient do Registro
        vaga.setStatus(StatusVaga.OCUPADA);
        Registro novoRegistro = new Registro(veiculo, vaga);
        novoRegistro.setThreadOrigem(Thread.currentThread().getName());
        
        registrosAtivos.add(novoRegistro);
        System.out.println("🚗 [" + Thread.currentThread().getName() + "] Veículo [" + veiculo.getPlaca() + "] entrou na vaga [" + idVaga + "].");
        
        notificarObservers(idVaga, false);
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

    // Métodos do Observer
    public void registrarObserver(EstacionamentoObserver observer) { this.observers.add(observer); }
    public void removerObserver(EstacionamentoObserver observer) { this.observers.remove(observer); }
    private void notificarObservers(String idVaga, boolean estaDisponivel) {
        for (EstacionamentoObserver obs : observers) { obs.atualizarVaga(idVaga, estaDisponivel); }
    }

    // Retorna a coleção de valores do HashMap para uso do Menu/Relatórios
    public List<Vaga> getVagas() {
        return new ArrayList<>(vagas.values());
    }
}