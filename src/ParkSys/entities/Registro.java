package ParkSys.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Registro implements Serializable, Comparable<Registro> { // Adicionado Comparable

    private static final long serialVersionUID = 1L;

    private Veiculo veiculo;
    private Vaga vaga;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private double valorPago;
    private transient String threadOrigem; // Para uso futuro com Threads

    public Registro() {
    }

    public Registro(Veiculo veiculo, Vaga vaga) {
        this.veiculo = veiculo;
        this.vaga = vaga;
        this.dataEntrada = LocalDateTime.now();
    }

    // Implementacao do Requisito C05 para ordenacao cronologica automatica
    @Override
    public int compareTo(Registro outro) {
        if (this.dataEntrada == null || outro.dataEntrada == null) {
            return 0;
        }
        return this.dataEntrada.compareTo(outro.dataEntrada);
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public String getThreadOrigem() {
        return threadOrigem;
    }

    public void setThreadOrigem(String threadOrigem) {
        this.threadOrigem = threadOrigem;
    }

    @Override
    public String toString() {
        return "Registro{" +
                "veiculo=" + veiculo +
                ", vaga=" + (vaga != null ? vaga.getId() : "null") +
                ", dataEntrada=" + dataEntrada +
                ", dataSaida=" + dataSaida +
                ", valorPago=" + valorPago +
                '}';
    }
}