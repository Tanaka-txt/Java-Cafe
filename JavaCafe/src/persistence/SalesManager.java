package persistence;

import models.Order;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SalesManager {
    private String filePath;

    public SalesManager(String filePath) {
        this.filePath = filePath;
    }

    public void saveOrder(Order order) {
        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            LocalDate today = LocalDate.now();
            pw.println(today.toString() + "," + order.getTotal());
        } catch (IOException e) {
            System.err.println("Erro ao salvar a venda: " + e.getMessage());
        }
    }

    public double[] getDailySummary() {
        double totalRevenue = 0.0;
        int transactionCount = 0;
        String todayStr = LocalDate.now().toString();

        File file = new File(filePath);
        if (!file.exists()) return new double[]{0.0, 0}; 

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                if (data.length == 2 && data[0].trim().equals(todayStr)) {
                    totalRevenue += Double.parseDouble(data[1].trim());
                    transactionCount++;
                }
            }
        } catch (Exception e) {}
        return new double[]{totalRevenue, transactionCount};
    }

    // NOVO: Método para limpar vendas apenas do dia atual
    public void clearDailySales() {
        File file = new File(filePath);
        if (!file.exists()) return;

        List<String> linhasRestantes = new ArrayList<>();
        String todayStr = LocalDate.now().toString();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Se a linha NÃO for de hoje, guarda ela
                if (!line.startsWith(todayStr)) {
                    linhasRestantes.add(line);
                }
            }
        } catch (FileNotFoundException e) {}

        // Sobrescreve o arquivo apenas com as vendas dos outros dias
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (String linha : linhasRestantes) {
                pw.println(linha);
            }
        } catch (IOException e) {}
    }
}