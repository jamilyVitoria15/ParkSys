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
            numeroInicial = Integer.parseInt(idVagaInicial.substring(1)); // Pega o número
        } catch (NumberFormatException e) {
            throw new VagaOcupadaException("ID de vaga inválido: " + idVagaInicial);
        }

        int vagasNecessarias = veiculo.getTipo().getVagasOcupadas();
        List<Vaga> vagasParaBloquear = new ArrayList<>();

        // 1. Validar e coletar se todas as vagas consecutivas necessárias estão livres
        for (int i = 0; i < vagasNecessarias; i++) {
            int numeroVagaAtual = numeroInicial + i;
            
            // Testa os dois formatos comuns: "A1" e "A01" para garantir compatibilidade
            String idFormatoCurto = prefixo + numeroVagaAtual;
            String idFormatoLongo = prefixo + (numeroVagaAtual < 10 ? "0" + numeroVagaAtual : numeroVagaAtual);
            
            String idVagaAtual = null;
            if (vagas.containsKey(idFormatoCurto)) {
                idVagaAtual = idFormatoCurto;
            } else if (vagas.containsKey(idFormatoLongo)) {
                idVagaAtual = idFormatoLongo;
            }

            // Se a vaga sequencial não existir em nenhum formato no HashMap, lança o erro (C01)
            if (idVagaAtual == null) {
                throw new VagaOcupadaException("Espaço insuficiente ou vaga inexistente! O veículo precisa de " 
                        + vagasNecessarias + " vaga(s) consecutiva(s), mas a sequência a partir de " 
                        + idVagaInicial + " falhou na posição: " + idFormatoCurto);
            }

            Vaga vaga = vagas.get(idVagaAtual);
            
            if (!vaga.getStatus().isDisponivel()) {
                throw new VagaOcupadaException("Bloqueio: A vaga sequencial " + idVagaAtual + " não está disponível!");
            }

            vagasParaBloquear.add(vaga);
        }

        // 2. Altera o status de todas as vagas validadas para OCUPADA
        for (Vaga vaga : vagasParaBloquear) {
            vaga.setStatus(StatusVaga.OCUPADA);
        }

        // 3. Cria o registro vinculado à vaga inicial escolhida
        Registro novoRegistro = new Registro(veiculo, vagasParaBloquear.get(0));
        novoRegistro.setThreadOrigem(Thread.currentThread().getName());
        registrosAtivos.add(novoRegistro);
        
        System.out.println("🚗 [" + Thread.currentThread().getName() + "] Veículo [" + veiculo.getPlaca() 
                + "] entrou ocupando " + vagasNecessarias + " vaga(s) a partir de [" + idVagaInicial + "].");

        // Notifica a TelaInicial via Observer
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
    
    public synchronized void adicionarMensalista(Mensalista mensalista) {
        if (this.mensalistas == null) {
            this.mensalistas = new LinkedList<>();
        }
        this.mensalistas.add(mensalista);
        System.out.println("👤 [" + Thread.currentThread().getName() + "] Mensalista '" 
                + mensalista.getNome() + "' cadastrado com sucesso na LinkedList.");
    }

    // Retorna a coleção de valores do HashMap para uso do Menu/Relatórios
    public List<Vaga> getVagas() {
        return new ArrayList<>(vagas.values());
    }
    
 // S03 & S06: Carrega os dados ou inicializa o mapa padrão de 30 vagas caso esteja vazio
    public void carregarDadosDoDisco() {
        GerenciadorArquivo ga = new GerenciadorArquivo();
        DadosParkSys dadosLidos = ga.desserializar("estacionamento.ser");
        
        if (dadosLidos != null && dadosLidos.getVagas() != null && !dadosLidos.getVagas().isEmpty()) {
            this.vagas = dadosLidos.getVagas();
            this.registrosAtivos = dadosLidos.getRegistros();
            this.mensalistas = dadosLidos.getMensalistas();
            System.out.println("✨ Dados anteriores restaurados com sucesso do disco.");
        } else {
            // Se o arquivo não existir ou o mapa estiver vazio, popula as 30 vagas oficiais do projeto
            System.out.println("🏭 Inicializando a infraestrutura padrão de 30 vagas (Fileiras A e B)...");
            inicializarVagasPadrao();
        }
    }

    // Cria a estrutura padrão exigida no PDF: 30 vagas distribuídas entre as fileiras A e B (01 a 15)
    private void inicializarVagasPadrao() {
        this.vagas.clear();
        String[] fileiras = {"A", "B"};
        
        for (String fileira : fileiras) {
            for (int i = 1; i <= 15; i++) {
                // Formata com dois dígitos para manter a consistência visual (ex: "A01", "B15")
                String idVaga = fileira + (i < 10 ? "0" + i : i);
                this.vagas.put(idVaga, new Vaga(idVaga, StatusVaga.LIVRE));
            }
        }
    }

    public void salvarDadosNoDisco() {
        GerenciadorArquivo ga = new GerenciadorArquivo();
        ga.serializar(this.vagas, this.registrosAtivos, this.mensalistas, "estacionamento.ser");
    }

    public void gerarRelatorioTexto() {
        GerenciadorArquivo ga = new GerenciadorArquivo();
        ga.exportarRelatorioTxt(this.registrosAtivos, "relatorio_receita.txt");
    }
}