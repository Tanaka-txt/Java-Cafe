package models;

import exceptions.OutOfStockException;

public class Product {
	private String name;
    private double price;
    private int stockQuantity;

    public Product(String name, double price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Regra de Negócio: Tentar baixar o estoque
    public void decreaseStock(int amount) throws OutOfStockException {
        if (amount > this.stockQuantity) {
            throw new OutOfStockException("Estoque insuficiente para o produto: " + this.name + ". Disponível: " + this.stockQuantity);
        }
        this.stockQuantity -= amount;
    }
    
    // Método para repor estoque (útil para o Inventory Management)
    public void addStock(int amount) {
        if (amount > 0) {
            this.stockQuantity += amount;
        }
    }

    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    
    @Override
    public String toString() {
        return name + " - R$ " + String.format("%.2f", price);
    }
}