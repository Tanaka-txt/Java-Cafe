package persistence;

import models.Product;
import java.io.*;
import java.util.*;

public class InventoryManager {
    private String filePath;
    private Map<String, Product> products;

    public InventoryManager(String filePath) {
        this.filePath = filePath;
        this.products = new LinkedHashMap<>(); 
    }

    public void loadInventory() {
        File file = new File(filePath);
        if (!file.exists()) {
            createDefaultInventory();
            saveInventory();
            return;
        }

        products.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String name = data[0].trim();
                    double price = Double.parseDouble(data[1].trim());
                    int stock = Integer.parseInt(data[2].trim());
                    products.put(name, new Product(name, price, stock));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar o estoque: " + e.getMessage());
        }
    }

    public void saveInventory() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Nome,Preco,Quantidade");
            for (Product product : products.values()) {
                pw.println(product.getName() + "," + product.getPrice() + "," + product.getStockQuantity());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar o estoque: " + e.getMessage());
        }
    }

    private void createDefaultInventory() {
        // --- BEBIDAS QUENTES ---
        products.put("Espresso Simples", new Product("Espresso Simples", 5.90, 50));
        products.put("Espresso Duplo", new Product("Espresso Duplo", 8.50, 40));
        products.put("Macchiato", new Product("Macchiato", 9.90, 30));
        products.put("Caffé Latte", new Product("Caffé Latte", 10.90, 30));
        products.put("Cappuccino", new Product("Cappuccino", 11.90, 30));
        products.put("Chocolate Quente", new Product("Chocolate Quente", 12.50, 30));
        products.put("McCafé Canadá Quente", new Product("McCafé Canadá Quente", 14.90, 20));

        // --- BEBIDAS GELADAS E SOBREMESAS ---
        products.put("McCafé Canadá Gelado", new Product("McCafé Canadá Gelado", 14.90, 20));
        products.put("Iced Latte", new Product("Iced Latte", 13.90, 25));
        products.put("Iced Mix Café", new Product("Iced Mix Café", 15.90, 20));
        products.put("Iced Mix Morango", new Product("Iced Mix Morango", 15.90, 20));
        products.put("Affogato", new Product("Affogato", 15.90, 15));

        // --- COMIDINHAS ---
        products.put("Pão de Queijo", new Product("Pão de Queijo", 5.50, 60));
        products.put("Mini Pão de Queijo", new Product("Mini Pão de Queijo", 8.90, 40));
        products.put("Croissant de Chocolate", new Product("Croissant de Chocolate", 11.50, 20));
        products.put("Cookies de baunilha", new Product("Cookies de baunilha", 7.00, 30));
        products.put("Cookies de chocolate", new Product("Cookies de chocolate", 7.00, 30));
    }

    public Product getProduct(String name) { return products.get(name); }
    
    public Collection<Product> getAllProducts() { return products.values(); }

    public void addOrUpdateProduct(Product product) {
        products.put(product.getName(), product);
        saveInventory();
    }

    // NOVO: Remove um produto permanentemente
    public void removeProduct(String name) {
        products.remove(name);
        saveInventory();
    }
}