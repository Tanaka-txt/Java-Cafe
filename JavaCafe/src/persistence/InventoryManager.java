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
        products.put("Coffee Americano", new Product("Coffee Americano", 120.0, 50));
        products.put("Espresso Romano", new Product("Espresso Romano", 188.0, 40));
        products.put("Coffee Milk", new Product("Coffee Milk", 90.0, 35));
        products.put("Café Mocha", new Product("Café Mocha", 135.0, 30));
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