package ParkSys.exceptions;

public class VagaOcupadaException extends Exception {
    private static final long serialVersionUID = 1L;

    public VagaOcupadaException(String mensagem) {
        super(mensagem);
    }
}