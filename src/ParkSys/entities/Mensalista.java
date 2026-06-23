package ParkSys.entities;

import java.io.Serializable;

public class Mensalista implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cpf;
    private String nome;
    private Veiculo veiculo;
    private boolean mensalidadePaga;

    public Mensalista() {
    }

    public Mensalista(String cpf, String nome, Veiculo veiculo) {
        this.cpf = cpf;
        this.nome = nome;
        this.veiculo = veiculo;
        this.mensalidadePaga = true; // Inicia ativo por padrão
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public boolean isMensalidadePaga() {
        return mensalidadePaga;
    }

    public void setMensalidadePaga(boolean mensalidadePaga) {
        this.mensalidadePaga = mensalidadePaga;
    }

    @Override
    public String toString() {
        return "Mensalista{" +
                "cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", veiculo=" + (veiculo != null ? veiculo.getPlaca() : "null") +
                ", mensalidadePaga=" + mensalidadePaga +
                '}';
    }
}