package views;

import javax.swing.*;
import persistence.InventoryManager;
import persistence.SalesManager;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private InventoryManager inventoryManager;
    private SalesManager salesManager;

    public MainFrame() {
        // Inicializa os gerenciadores lendo os arquivos CSV
        this.inventoryManager = new InventoryManager("estoque.csv");
        this.salesManager = new SalesManager("vendas.csv");
        this.inventoryManager.loadInventory();

        // Configurações básicas da janela principal
        setTitle("Java Café - Point of Sale System");
        setSize(1024, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // Cria o sistema de abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Instancia os painéis (Caixa e Estoque)
        OrderEntryPanel orderPanel = new OrderEntryPanel(inventoryManager, salesManager);
        InventoryPanel inventoryPanel = new InventoryPanel(inventoryManager, salesManager, orderPanel);

        // Adiciona as abas
        tabbedPane.addTab("Entrada de Pedidos (Caixa)", orderPanel);
        tabbedPane.addTab("Gerenciamento de Estoque", inventoryPanel);

        // Atualiza os dados do Estoque e Dashboard automaticamente ao clicar na aba
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                inventoryPanel.refreshAll();
            }
        });

        // Adiciona o sistema de abas na tela
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) { e.printStackTrace(); }
            new MainFrame().setVisible(true);
        });
    }
}