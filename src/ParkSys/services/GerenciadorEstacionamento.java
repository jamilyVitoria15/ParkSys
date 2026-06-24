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

    public synchronized void registrarEntrada(Veiculo veiculo, String idVagaInicial) throws VagaOcupadaException {
        String prefixo = idVagaInicial.substring(0, 1).toUpperCase(); // Pega a letra (ex: "A")
        int numeroInicial;
        
        try {
            numeroInicial = Integer.parseInt(idVagaInicial.substring(1)); // Pega o número (ex: 1)
        } catch (NumberFormatException e) {
            System.out.println("⚠️ ID de vaga inválido: " + idVagaInicial);
            return;
        }

        // T03 e T04: Descobre quantas vagas consecutivas o TipoVeiculo necessita
        int vagasNecessarias = veiculo.getTipo().getVagasOcupadas();
        List<Vaga> vagasParaBloquear = new ArrayList<>();

        // 1. Validar e coletar se todas as vagas consecutivas necessárias estão livres
        for (int i = 0; i < vagasNecessarias; i++) {
            int numeroVagaAtual = numeroInicial + i;
            // Formata o ID garantindo o padrão de nomenclatura (ex: "A1", "A2" ou "A01", "A02")
            // Se o seu sistema usa "A1", mude para: prefixo + numeroVagaAtual
            String idVagaAtual = prefixo + (numeroVagaAtual < 10 ? "0" + numeroVagaAtual : numeroVagaAtual);
            
            // Se a vaga atual não existir no HashMap, cancela a operação (C01)
            if (!vagas.containsKey(idVagaAtual)) {
                throw new VagaOcupadaException("Espaço insuficiente! O veículo precisa de " + vagasNecessarias 
                        + " vagas consecutivas, mas a vaga " + idVagaAtual + " não existe.");
            }

            Vaga vaga = vagas.get(idVagaAtual);
            
            // Verifica a disponibilidade usando a flag do status (T02)
            if (!vaga.getStatus().isDisponivel()) {
                throw new VagaOcupadaException("Bloqueio: A vaga sequencial " + idVagaAtual + " não está disponível!");
            }

            vagasParaBloquear.add(vaga);
        }

        // 2. Se passou em todas as validações, altera o status de todas elas para OCUPADA
        for (Vaga vaga : vagasParaBloquear) {
            vaga.setStatus(StatusVaga.OCUPADA);
        }

        // 3. Cria o registro vinculado à vaga inicial escolhida
        Registro novoRegistro = new Registro(veiculo, vagasParaBloquear.get(0));
        
        // M04: Grava o nome da Thread atual no campo transient do Registro para auditoria
        novoRegistro.setThreadOrigem(Thread.currentThread().getName());
        
        // C02: Adiciona ao ArrayList de registros ativos
        registrosAtivos.add(novoRegistro);
        
        System.out.println("🚗 [" + Thread.currentThread().getName() + "] Veículo [" + veiculo.getPlaca() 
                + " (" + veiculo.getTipo().getNomeLegivel() + ")] entrou com sucesso ocupando " 
                + vagasNecessarias + " vaga(s) a partir da [" + idVagaInicial + "].");

        // Notifica o painel sobre a alteração da vaga principal (P03)
        notificarObservers(idVagaInicial, false);
    }
    public synchronized void registrarSaida(String placa) throws VeiculoNaoEncontradoException {
        Registro registroEncontrado = null;
        
        // C02: Busca o registro ativo iterando sobre a lista
        for (Registro r : registrosAtivos) {
            if (r.getVeiculo().getPlaca().equalsIgnoreCase(placa)) {
                registroEncontrado = r;
                break;
            }
        }
        
        if (registroEncontrado == null) {
            throw new VeiculoNaoEncontradoException("Veículo com a placa " + placa + " não foi localizado.");
        }
        
        Veiculo veiculo = registroEncontrado.getVeiculo();
        Vaga vagaInicial = registroEncontrado.getVaga();
        int vagasParaLiberar = veiculo.getTipo().getVagasOcupadas();
        
        String prefixo = vagaInicial.getId().substring(0, 1).toUpperCase();
        int numeroInicial = Integer.parseInt(vagaInicial.getId().substring(1));

        // Desbloqueia todas as vagas consecutivas ocupadas por esse tipo de veículo
        for (int i = 0; i < vagasParaLiberar; i++) {
            int numeroVagaAtual = numeroInicial + i;
            String idVagaAtual = prefixo + (numeroVagaAtual < 10 ? "0" + numeroVagaAtual : numeroVagaAtual);
            
            if (vagas.containsKey(idVagaAtual)) {
                vagas.get(idVagaAtual).setStatus(StatusVaga.LIVRE);
            }
        }
        
        registroEncontrado.setDataSaida(LocalDateTime.now());
        
        // T03: Calcula o valor cobrado usando a tarifa do Enum (Nenhum valor fixo no código!)
        // Exemplo básico: assume 1 hora cheia para fins de teste concorrente
        double valorCalculado = veiculo.getTipo().getTarifaHora(); 
        registroEncontrado.setValorPago(valorCalculado);
        
        registrosAtivos.remove(registroEncontrado);
        System.out.println("💸 Veículo [" + placa + "] saiu. Valor pago: R$ " + valorCalculado 
                + ". Liberadas " + vagasParaLiberar + " vaga(s) a partir da [" + vagaInicial.getId() + "].");
        
        notificarObservers(vagaInicial.getId(), true);
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