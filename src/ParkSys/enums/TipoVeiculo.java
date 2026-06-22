package ParkSys.enums;

public enum TipoVeiculo {

    MOTO("Motocicleta", 5.0, 1),
    CARRO("Automóvel", 10.0, 1),
    SUV("SUV", 18.0, 2),
    CAMINHAO("Caminhão", 30.0, 3);

    private final String descricao;
    private final double tarifaHora;
    private final int vagasOcupadas;

    TipoVeiculo(String descricao, double tarifaHora, int vagasOcupadas) {
        this.descricao = descricao;
        this.tarifaHora = tarifaHora;
        this.vagasOcupadas = vagasOcupadas;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getTarifaHora() {
        return tarifaHora;
    }

    public int getVagasOcupadas() {
        return vagasOcupadas;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}