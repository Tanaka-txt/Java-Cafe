package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por representar um pedido realizado pelo cliente.
 * Ela armazena os itens adicionados ao carrinho e calcula automaticamente
 * os valores financeiros do pedido, como subtotal, taxa e total.
 */
public class Order {

    // Lista que contém todos os itens presentes no pedido
	private List<OrderItem> items;

    // Valor da soma dos produtos sem considerar taxas
    private double subtotal;

    // Taxa aplicada sobre o subtotal (10%)
    private double taxRate = 0.10; 

    // Valor monetário correspondente à taxa calculada
    private double taxAmount;

    // Valor final do pedido (subtotal + taxa)
    private double total;

    /**
     * Construtor da classe.
     * Inicializa a lista de itens e define todos os valores monetários como zero.
     */
    public Order() {
        this.items = new ArrayList<>();
        this.subtotal = 0.0;
        this.taxAmount = 0.0;
        this.total = 0.0;
    }

    /**
     * Adiciona um produto ao pedido
     * Caso o produto já exista na lista, apenas incrementa sua quantidade.
     * Caso contrário, cria um novo OrderItem e o adiciona ao pedido.
     * 
     * @param product
     * @param quantity
     */
    public void addItem(Product product, int quantity) {

        // Procura o produto na lista para evitar itens duplicados
        for (OrderItem item : items) {

            // Se encontra o produto, apenas soma as quantidades
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + quantity);
                
                // Atualiza os valores finaceiros do pedido
                calculateTotals();
                return;
            }
        }
        // Se o produto não existir no pedido, cria um novo item
        items.add(new OrderItem(product, quantity));
        
        // Recalcula os valores após a inclusão do produto
        calculateTotals();
    }

    /**
     * Remove todos os itens do pedido,
     * retornando o carrinho ao estado inicial
     */
    public void clearOrder() {
        items.clear();
        calculateTotals();
    }

    /**
     * Remove uma unidade de um item específico do pedido.
     * Se a quantidade for maios que 1, apenas decrementa.
     * Se houver apenas uma unidade, remove o item da lista.
     * 
     * @param index Ìndice do item na lista
     */
    public void removeItemByIndex(int index) {

        // Verifica se o índice informado é válido
        if (index >= 0 && index < items.size()) {
            OrderItem item = items.get(index);
            
            // Diminui apenas uma unidade do produto
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                // Remove completamente do pedido
                items.remove(index);
            }
            // Atualiza os valores do pedido após a remoção
            calculateTotals(); 
        }
    }
    
    /**
     * Recalcula todos os valores financeiros do pedido.
     * O subtotal é obtido pela soma dos subtotais de cada item.
     * Em seguida, calcula-se a taxa e o valor total.
     */
    private void calculateTotals() {

        // Reinicia o subtotal para realizar um novo cálculo
        subtotal = 0.0;

        // Soma o valor de todos os itens presentes no pedido
        for (OrderItem item : items) {
            subtotal += item.getSubtotal();
        }

        // Calcula o valor da taxa aplicada ao subtotal
        taxAmount = subtotal * taxRate;

        // Calcula o valor final do pedido
        total = subtotal + taxAmount;
    }

    // Método getters utilizados para acessar os dados do pedido
    public List<OrderItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTaxAmount() { return taxAmount; }
    public double getTotal() { return total; }
}

