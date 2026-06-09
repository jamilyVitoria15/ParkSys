package ParkSys.entities;

import java.io.Serializable;

import ParkSys.enums.StatusVaga;

public class Vaga implements Serializable {

    private static final long serialVersionUID = 1L;

    private int numero;
    private StatusVaga status;

    public Vaga() {
    }

    public Vaga(int numero, StatusVaga status) {
        this.numero = numero;
        this.status = status;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
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
                "numero=" + numero +
                ", status=" + status +
                '}';
    }
}
