package models;

import exceptions.OutOfStockException;

/**
 * Classe responsável por representar um produto do sistema.
 * Armazena informações como nome, preço e quantidade disponível
 * em estoque, além de fornecer operações para controle de estoque.
 */
public class Product {

    // Nome do produto
	private String name;

    // Preço unitário do produto
    private double price;

    // Quantidade disponível em estoque 
    private int stockQuantity;

    /**
     * Construtor da classe.
     * Inicializa um produto com nome, preço e quantidade em estoque.
     * 
     * @param name Nome do produto
     * @param price Preço unitário do produto
     * @param stockQuantity Quantidade inicial disponível em estoque
     */
    public Product(String name, double price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Reduz a quantidade disponível em estoque.
     * Antes de realizar a operação, verifica se existe
     * estoque suficiente para atender à solicitação.
     * 
     * Caso a quantidade solicitada seja maior do que a disponível,
     * uma exceção é lançada para impedir que o estoque fique negativo.
     * 
     * @param amount Quantidade que será retirada do estoque
     * @throws OutOfStockException Caso não haja estoque suficiente
     */
    public void decreaseStock(int amount) throws OutOfStockException {
        
        // Verifica se há estoque suficiente para a retirada 
        if (amount > this.stockQuantity) {
            throw new OutOfStockException("Estoque insuficiente para o produto: " + this.name + ". Disponível: " + this.stockQuantity);
        }

        // Atualiza a quantidade disponível após a venda
        this.stockQuantity -= amount;
    }
    
    /**
     * Adiciona unidades ao estoque do produto.
     * A operação só é realizada se a quandtidade informada
     * for maior que zero.
     * 
     * @param amount Quantidade que será adicionada ao estoque
     */
    public void addStock(int amount) {

        // Evita adicionar valores negativos ou nulos
        if (amount > 0) {
            this.stockQuantity += amount;
        }
    }

    // Métodos getters utilizados para acessar os atributos do produto
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    // Métodos setters utilizados para alterar os atributos do produto
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    

    /**
     * Retorna uma representação textual do produto.
     * Esse método é útil para exibir o produto em listas
     * e componentes da interface gráfica.
     */
    @Override
    public String toString() {
        return name + " - R$ " + String.format("%.2f", price);
    }
}