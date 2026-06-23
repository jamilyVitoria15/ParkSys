package ParkSys.exceptions;

public class VeiculoNaoEncontradoException extends Exception {
    private static final long serialVersionUID = 1L;

    public VeiculoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}