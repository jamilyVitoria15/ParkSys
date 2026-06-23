package ParkSys.exceptions;

public class PlacaInvalidaException extends Exception {
    private static final long serialVersionUID = 1L;

    public PlacaInvalidaException(String mensagem) {
        super(mensagem);
    }
}