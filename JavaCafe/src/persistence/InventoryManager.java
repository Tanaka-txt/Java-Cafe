package persistence;

import models.Product;
import java.io.*;
import java.util.*;
/*
 * Usada para Salvar e carregar os produtos do arquivo de texto. É o principal fator
 * para a abertura do programa estar sempre atualizada.
 * 
 * O inventoryPanel precisa que uma outra classe abra o arquivo.csv nesse caso o
 * responsável por isso é esse código, ele pede pra esse código com o 
 * 
 * getAllProducts() = pede os produtos
 * AddOrUpdateProduct() = add novo produto ou edita
 * saveInventory() = para reescrever o csv com novos dados
 * 
 * O OrderEntryPanel.java  usa essa classe para criar a aba principal dos nosso produtos
 * 
*/

// Constructor
public class InventoryManager {
    private String filePath; // guarda o caminho do arquivo
    private Map<String, Product> products; // guarda os produtos em memória

    public InventoryManager(String filePath) {
        this.filePath = filePath; // recebe o caminho do arquivo
        this.products = new LinkedHashMap<>(); // recebimento chave valor
}

    public void loadInventory() { // Carregamento dos dados
        File file = new File(filePath); // 
        if (!file.exists()) { // Verificação se o arquivo existe, se não :
            createDefaultInventory(); // Cria um cardápio padrão
            saveInventory(); // salva o inventory
            return;
        }

    products.clear(); // limpa a memória para garantir que não tem nada
    	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    		br.readLine(); // lê cabeçalho do csv 
            String line;
            while ((line = br.readLine()) != null) { // loop para leitura de linha a linha até null
                String[] data = line.split(","); // virgula serve como separador
                if (data.length == 3) { // verificação para validação de 3 indices
                    String name = data[0].trim(); // .trim é usado para remover espaços na entrada do data[0] = nome do produto
                    double price = Double.parseDouble(data[1].trim()); // Conversão para numero para contas
                    int stock = Integer.parseInt(data[2].trim()); // conversão para numeros
                    products.put(name, new Product(name, price, stock)); // criação do objeto para inserir no mapa products
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar o estoque: " + e.getMessage());
        }
    }

    public void saveInventory() { // Escrita dos dados estoque
    	// Esse try garante que o arquivo seja fechado automaticamente após a escrita
    	// mesmo que acontece um erro. Evita travamento
    	// o FileWrinter abre o arquivo para escrita, por não ter um true, ele reescreve por
    	// cima de tudo
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("Nome,Preco,Quantidade");
            // chave : valor
            for (Product product : products.values()) { // extrai a lista de objetos
            	// padrão de escrita csv
                pw.println(product.getName() + "," + product.getPrice() + "," + product.getStockQuantity());
            }
        } catch (IOException e) { // se qaulquer erro der cai aqui
            System.err.println("Erro ao salvar o estoque: " + e.getMessage()); // pront erro do Exceptions
        }
    }
    
    // Só é usado se caso não houver nada ainda no programa, ai esse é uma inicialização
    private void createDefaultInventory() { // Fallback
    	// Insere os dados dos produtos com o .put (garante que não inicie vazio)
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

    // Usa o nome como chave para devolver o objeto do produto instantaneamente.
    public Product getProduct(String name) { 
    	return products.get(name); 
    }
    
    // GetAllProducts devolve a coleção completa 
    public Collection<Product> getAllProducts() { 
    	return products.values(); 
    }
    
    // Add ou edit produtos
    public void addOrUpdateProduct(Product product) {
        products.put(product.getName(), product); // .put(chave, valor)
        saveInventory(); // salva
    }

    // NOVO: Remove um produto permanentemente
    public void removeProduct(String name) { // remove produto do products
        products.remove(name); // remove
        saveInventory(); // salve no csv
    }
}
