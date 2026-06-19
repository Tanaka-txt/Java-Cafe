package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import models.Product;
import persistence.InventoryManager;
import persistence.SalesManager;

@SuppressWarnings("serial")
public class InventoryPanel extends JPanel {
    private InventoryManager inventoryManager;
    private SalesManager salesManager;
    private OrderEntryPanel orderEntryPanel; 

    private JTable table;
    private DefaultTableModel tableModel;
    
    private JLabel lblFinancial;
    private JLabel lblAlerts;

    public InventoryPanel(InventoryManager inventoryManager, SalesManager salesManager, OrderEntryPanel orderEntryPanel) {
        this.inventoryManager = inventoryManager;
        this.salesManager = salesManager;
        this.orderEntryPanel = orderEntryPanel;
        
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Fundo Dark Mode
        setBackground(Color.decode("#1E1E2E")); 

        // 1. DASHBOARD SUPERIOR
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBackground(Color.decode("#282A36")); 
        
        // Borda com título branco
        topPanel.setBorder(BorderFactory.createTitledBorder(null, "Dashboard de Hoje e Alertas de Estoque", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("SansSerif", Font.BOLD, 12), Color.WHITE));

        lblFinancial = new JLabel();
        lblFinancial.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblFinancial.setForeground(Color.decode("#50FA7B")); // Verde brilhante
        
        lblAlerts = new JLabel();
        lblAlerts.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        topPanel.add(lblFinancial);
        topPanel.add(lblAlerts);
        add(topPanel, BorderLayout.NORTH);

        // 2. TABELA NO CENTRO
        String[] columns = {"Produto", "Preço (R$)", "Quantidade em Estoque"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30); 
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Cores Modernas para a Tabela
        table.setBackground(Color.decode("#282A36")); 
        table.setForeground(Color.WHITE); 
        table.setGridColor(Color.decode("#44475A"));
        
        table.getTableHeader().setBackground(Color.decode("#44475A")); 
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.decode("#1E1E2E")); 
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // 3. BOTÕES INFERIORES ARREDONDADOS E COLORIDOS
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        bottomPanel.setBackground(Color.decode("#1E1E2E")); 

        JButton btnAddStock = createRoundedButton("Adicionar Estoque", "#8BE9FD", "#1E1E2E"); // Ciano
        JButton btnNewProduct = createRoundedButton("Novo Produto", "#F1FA8C", "#1E1E2E"); // Amarelo
        JButton btnRemoveProduct = createRoundedButton("Remover Produto", "#FF79C6", "#FFFFFF"); // Rosa/Roxo
        JButton btnClearSales = createRoundedButton("Limpar Vendas", "#FFB86C", "#1E1E2E"); // Laranja

        btnAddStock.addActionListener(e -> addStockToSelected());
        btnNewProduct.addActionListener(e -> createNewProduct());
        btnRemoveProduct.addActionListener(e -> removeSelectedProduct());
        btnClearSales.addActionListener(e -> clearTodaySales());

        bottomPanel.add(btnAddStock);
        bottomPanel.add(btnNewProduct);
        bottomPanel.add(btnRemoveProduct);
        bottomPanel.add(btnClearSales);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshAll(); 
    }

    /**
     * 🎨 Método auxiliar para criar os botões arredondados no painel de Estoque também
     */
    private JButton createRoundedButton(String text, String bgColorHex, String fgColorHex) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode(bgColorHex));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(Color.decode(fgColorHex)); 
        btn.setContentAreaFilled(false); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void refreshAll() {
        tableModel.setRowCount(0); 
        for (Product p : inventoryManager.getAllProducts()) {
            tableModel.addRow(new Object[]{p.getName(), String.format("%.2f", p.getPrice()), p.getStockQuantity()});
        }

        double totalRevenue = salesManager.getDailySummary()[0];
        double subtotal = totalRevenue / 1.10; 
        double tax = totalRevenue - subtotal;
        lblFinancial.setText(String.format("Ganho de Hoje: R$ %.2f   |   Taxa (10%%): R$ %.2f", totalRevenue, tax));

        StringBuilder lowStock = new StringBuilder("⚠️ Alertas (<= 11 un): ");
        boolean hasLowStock = false;
        for (Product p : inventoryManager.getAllProducts()) {
            if (p.getStockQuantity() <= 11) {
                lowStock.append(p.getName()).append(" (").append(p.getStockQuantity()).append("), ");
                hasLowStock = true;
            }
        }
        if (hasLowStock) {
            lowStock.setLength(lowStock.length() - 2); 
            lblAlerts.setText(lowStock.toString());
            lblAlerts.setForeground(Color.decode("#FF5555")); // Vermelho
        } else {
            lblAlerts.setText("✅ Estoque Seguro: Nenhum produto abaixo de 11 unidades.");
            lblAlerts.setForeground(Color.decode("#F8F8F2")); // Branco
        }
    }

    private void addStockToSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Quantas unidades ADICIONAR a " + name + "?", "0");
        if (input != null && !input.isEmpty()) {
            try {
                inventoryManager.getProduct(name).addStock(Integer.parseInt(input));
                inventoryManager.saveInventory();
                refreshAll();
                orderEntryPanel.refreshMenu(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Apenas números inteiros válidos.");
            }
        }
    }

    private void createNewProduct() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        Object[] message = {"Nome:", nameField, "Preço (Ex: 10.50):", priceField, "Estoque:", stockField};

        if (JOptionPane.showConfirmDialog(this, message, "Novo Produto", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().replace(",", "."));
                int stock = Integer.parseInt(stockField.getText());
                
                if (name.isEmpty() || price <= 0 || stock < 0) throw new NumberFormatException();

                inventoryManager.addOrUpdateProduct(new Product(name, price, stock));
                refreshAll();
                orderEntryPanel.refreshMenu(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dados inválidos! Use ponto para os centavos.");
            }
        }
    }

    private void removeSelectedProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Deletar " + name + " permanentemente?", "Excluir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            inventoryManager.removeProduct(name);
            refreshAll();
            orderEntryPanel.refreshMenu(); 
        }
    }

    private void clearTodaySales() {
        if (JOptionPane.showConfirmDialog(this, "Tem certeza que deseja ZERAR as vendas de hoje?", "Limpar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            salesManager.clearDailySales();
            refreshAll();
            JOptionPane.showMessageDialog(this, "O histórico de vendas de hoje foi apagado.");
        }
    }
}