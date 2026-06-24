package ParkSys.enums;

public enum StatusVaga {
    // T02: Constantes com descrição textual e indicador de disponibilidade
    LIVRE("Vaga Disponível", true),
    OCUPADA("Vaga Ocupada", false),
    RESERVADA("Vaga Reservada para Mensalista", false);

    private final String descricao;
    private final boolean disponivel;

    private StatusVaga(String descricao, boolean disponivel) {
        this.descricao = descricao;
        this.disponivel = disponivel;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isDisponivel() {
        return disponivel;
    }
}