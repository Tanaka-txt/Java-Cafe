package persistence;

import models.Order;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/*
 * Aqui vou usar para registros dos faturamentos no arquivo.csv
 * 
 * Quando o evento de finalizar venda é clicaco no OrderEntryPanel a tela envia 
 * Order "pedido" inteiro para o método saveOrder(order order). Eai por meio da
 * LocalDate.now() e as infos do pedido é escrita uma nova linha no arquivo csv. 
 * 
 * O FileWriter(filePath, true) = escreve append (no fim do arquivo)
 * 
 * Para manter os dados do estoque atualizados ao mesmo tempo na aba do Caixa e no estoque
 * é feito por isso no mainFrame:
 * this.inventoryManager = new InventoryManager("estoque.csv");
 * this.salesManager = new SalesManager("vendas.csv");
 * Dentro dessa mesma funcionalidade no mainFrame é feito a injeção de dependência.
 * 
*/
public class SalesManager {
    private String filePath; // guarda nome do arquivo

    public SalesManager(String filePath) { // Construtor para ter por padrão o nome do arquivo
        this.filePath = filePath;
    }

    public void saveOrder(Order order) { // Salva o pedido
        try (FileWriter fw = new FileWriter(filePath, true); // Abre para leitura append
             PrintWriter pw = new PrintWriter(fw)) { // 
            LocalDate today = LocalDate.now(); // Salva a data
            pw.println(today.toString() + "," + order.getTotal());
            // Ele escreve a data e todo o objeto que ao finalizar venda é criado.
            // 2026-06-22, 35.50
        } catch (IOException e) { // se der qualquer erro cai aqui
            System.err.println("Erro ao salvar a venda: " + e.getMessage());
        }
    }

    public double[] getDailySummary() { // Relatorio do dia
        double totalRevenue = 0.0; // Total de 
        int transactionCount = 0; // contador da transação
        String todayStr = LocalDate.now().toString(); // Data do dia

        File file = new File(filePath); // Abertura do aqruivo
        if (!file.exists()) return new double[]{0.0, 0};  // verifica se existe o arquivo

        try (Scanner scanner = new Scanner(file)) { // Inicia o scanner para leitura
            while (scanner.hasNextLine()) { // inicia o loop e limá o cache de memória
            	// garante não ter entradas indesejadas.
                String line = scanner.nextLine(); // pega a linha toda do arquivo
                String[] data = line.split(","); //Corta o texto onde tem ","
                if (data.length == 2 && data[0].trim().equals(todayStr)) { 
                	// data.length garaten que a linha foi cortada em 2 partes
                	// data[0] = ve a data da linha e compara com a data de hj
                    totalRevenue += Double.parseDouble(data[1].trim()); // pega e vai somando o total do dia
                    transactionCount++; // contador de vendas feitas
                }
            }
        } catch (Exception e) {}
        return new double[]{totalRevenue, transactionCount};
    }

    // Limpeza das vendas do dia
    public void clearDailySales() {
        File file = new File(filePath); // Abre o arquivo
        if (!file.exists()) return; // Ve se foi possível abrir

        List<String> linhasRestantes = new ArrayList<>(); 
        // Cria uma lista vazia na memoria, e guarda as vendas de outros dias para não ser apagado
        
        String todayStr = LocalDate.now().toString(); // Horario local

        try (Scanner scanner = new Scanner(file)) { // Abre pra leitura
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Le alinha toda
                if (!line.startsWith(todayStr)) { // Verifica se a linha começa com a data de hj
                    linhasRestantes.add(line); // Venda antiga, coloca no array vazio criado
                }
            }
        } catch (FileNotFoundException e) {}

        // Sobrescreve o arquivo apenas com as vendas dos outros dias
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
        	// 
            for (String linha : linhasRestantes) { // pega as linhas escritas no array vazio
            	// e escreve no arquivo de novo
                pw.println(linha);
            }
        } catch (IOException e) {} // excessões de I/O  (Padrão do java) "e" recebe o erro 
    }
}
