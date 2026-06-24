package ParkSys.observer;

public interface EstacionamentoObserver {
    // Método que será chamado sempre que houver mudança no estado de uma vaga
    void atualizarVaga(String idVaga, boolean estaDisponivel);
}