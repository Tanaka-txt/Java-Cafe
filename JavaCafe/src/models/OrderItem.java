package models;

public class OrderItem {
	private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Retorna o valor total deste item específico
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    // Getters e Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    @Override
    public String toString() {
        return quantity + "x " + product.getName() + " - R$ " + String.format("%.2f", getSubtotal());
    }
}
