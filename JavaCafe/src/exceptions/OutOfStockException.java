package exceptions;

public class OutOfStockException extends Exception {
    public OutOfStockException(String message) {
        // Repassa a mensagem de erro para a classe pai (Exception)
        super(message);
    }
}
