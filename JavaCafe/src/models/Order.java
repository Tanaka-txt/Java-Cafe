package models;

import java.util.ArrayList;
import java.util.List;

public class Order {
	private List<OrderItem> items;
    private double subtotal;
    private double taxRate = 0.10; // Exemplo: 10% de imposto/taxa
    private double taxAmount;
    private double total;

    public Order() {
        this.items = new ArrayList<>();
        this.subtotal = 0.0;
        this.taxAmount = 0.0;
        this.total = 0.0;
    }

    public void addItem(Product product, int quantity) {
        // Verifica se o produto já está no pedido para apenas somar a quantidade
        for (OrderItem item : items) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + quantity);
                calculateTotals();
                return;
            }
        }
        // Se não estiver, adiciona um novo item
        items.add(new OrderItem(product, quantity));
        calculateTotals();
    }

    public void clearOrder() {
        items.clear();
        calculateTotals();
    }

    // Calcula os valores toda vez que um item é adicionado
    private void calculateTotals() {
        subtotal = 0.0;
        for (OrderItem item : items) {
            subtotal += item.getSubtotal();
        }
        taxAmount = subtotal * taxRate;
        total = subtotal + taxAmount;
    }

    // Getters
    public List<OrderItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTaxAmount() { return taxAmount; }
    public double getTotal() { return total; }
}

