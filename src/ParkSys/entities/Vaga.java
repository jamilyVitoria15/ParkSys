package ParkSys.entities;

import java.io.Serializable;
import ParkSys.enums.StatusVaga;

public class Vaga implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id; // Alterado para String para o cenário real do estacionamento
    private StatusVaga status;

    public Vaga() {
    }

    public Vaga(String id, StatusVaga status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga status) {
        this.status = status;
    }

    public boolean estaDisponivel() {
        return status == StatusVaga.LIVRE;
    }

    @Override
    public String toString() {
        return "Vaga{" +
                "id='" + id + '\'' +
                ", status=" + status +
                '}';
    }
}