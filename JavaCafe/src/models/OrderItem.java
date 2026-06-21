package models;

/**
 * Classe que representa um item individual de um pedido.
 * Cada item é composto por um produto e pela quantidade
 * desse produto adicionada ao carrinho
 */
public class OrderItem {

    //Produto associado ao item do pedido
	private Product product;

    // Quantidade do produto selecionada pelo cliente 
    private int quantity;

    /**
     * Construtor da classe.
     * Incializa o item com um produto e uma quantidade.
     * 
     * @param product Produto que compôe o item do pedido
     * @param quantity Quantidade de produto selecionada
     */
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Calcula o subtotal desse item.
     * O valor é obtido multiplicando o preço unitário
     * do produto pela quantidade selecionada.
     * 
     * @return Valor total deste item do pedido
     */
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    // Método getters e setters utilizados para acessar
    // e modificar os atributos do objeto.
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    /**
     * Retorna uma representação textual do item.
     * Esse método é utilizado quando o objeto precisa
     * ser exibido em listas ou componetnes da interface
     */
    @Override
    public String toString() {
        return quantity + "x " + product.getName() + " - R$ " + String.format("%.2f", getSubtotal());
    }
}
