package exceptions;

/* 
 * utilizado para filtragem do estoque, é executado quando o lançamento tenta
 * diminuir uma quantidade que o estoque não permite
 * 
 * Impede estoque negativo
 * ele vai avisar o usuário quando isso acontecer, evitando vendo impossível
 * 
 * Usei em decreaseStock(int amount) em Product.java
 * Usei em finalizeSale() em OrderEntryPanel = uso pra antes de concluir a venda
 * verificamos se todos os itens tem estoque suficiente
 * 
 * Basicamente sempre que usaros essa classe vai ser recebida uma mensagem
 * 
 * Esse exception é uma classe nativa do java ela serve comp se fosse uma exceção
 * personalizada, mas que vai se comportar como uma execeção verificada padrão do java.
 * 
 * O super(message) = ele passa a mensagem das excecões no "banco de dados e excecões
 * do java" e lá o java vai saber como agir
*/
public class OutOfStockException extends Exception {
    public OutOfStockException(String message) {
        // Repassa a mensagem de erro para a classe pai (Exception)
        super(message);
    }
}
