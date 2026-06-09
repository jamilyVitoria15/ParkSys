package ParkSys.entities;

import java.io.Serializable;

public class Mensalista implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String cpf;
    private String placa;
    private double valorMensalidade;

    public Mensalista() {
    }

    public Mensalista(String nome, String cpf, String placa, double valorMensalidade) {
        this.nome = nome;
        this.cpf = cpf;
        this.placa = placa;
        this.valorMensalidade = valorMensalidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public double getValorMensalidade() {
        return valorMensalidade;
    }

    public void setValorMensalidade(double valorMensalidade) {
        this.valorMensalidade = valorMensalidade;
    }

    @Override
    public String toString() {
        return "Mensalista{" +
                "nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", placa='" + placa + '\'' +
                ", valorMensalidade=" + valorMensalidade +
                '}';
    }
}
