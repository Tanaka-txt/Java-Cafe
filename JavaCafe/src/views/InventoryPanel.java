package views; // Pacote onde estão as classes da interface gráfica

// Importações necessárias para tabela, interface, modelos e gerenciadores
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import models.Product;
import persistence.InventoryManager;
import persistence.SalesManager;

@SuppressWarnings("serial") // Suprime aviso sobre serialização

public class InventoryPanel extends JPanel {    // Painel que representa a aba de gerenciamento de estoque
    private InventoryManager inventoryManager;  // Acesso ao estoque
    private SalesManager salesManager;          // Acesso às vendas
    private OrderEntryPanel orderEntryPanel;    // Referência ao painel de pedidos para atualizar quando o estoque mudar

    // Componentes da tabela
    private JTable table;                 // Tabela que mostra os produtos
    private DefaultTableModel tableModel; // Modelo de dados da tabela (linhas e colunas)
    
    // Labels do dashboard superior
    private JLabel lblFinancial; // Mostra ganhos do dia e taxa
    private JLabel lblAlerts;    // Mostra alertas de estoque baixo

    public InventoryPanel(InventoryManager inventoryManager, SalesManager salesManager, OrderEntryPanel orderEntryPanel) { // Construtor que recebe os gerenciadores do MainFrame
        this.inventoryManager = inventoryManager;
        this.salesManager = salesManager;
        this.orderEntryPanel = orderEntryPanel;
        
        // Configurações do layout do painel
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Fundo Dark Mode
        setBackground(Color.decode("#1E1E2E")); 

        // 1. DASHBOARD SUPERIOR
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBackground(Color.decode("#282A36")); 
        
        // Borda com título branco
        topPanel.setBorder(BorderFactory.createTitledBorder(null, "Dashboard de Hoje e Alertas de Estoque", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("SansSerif", Font.BOLD, 12), Color.WHITE));

        // Label financeiro (ganhos e taxa)
        lblFinancial = new JLabel();
        lblFinancial.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblFinancial.setForeground(Color.decode("#50FA7B")); // Verde brilhante
        
        // Label de alertas (estoque baixo)
        lblAlerts = new JLabel();
        lblAlerts.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        topPanel.add(lblFinancial);
        topPanel.add(lblAlerts);
        add(topPanel, BorderLayout.NORTH);

        // 2. TABELA NO CENTRO
        String[] columns = {"Produto", "Preço (R$)", "Quantidade em Estoque"};
        tableModel = new DefaultTableModel(columns, 0) { // 0 = sem linhas inicialmente
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Impede edição direta na tabela (só via botões)
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Só pode selecionar uma linha por vez
        table.setRowHeight(30);                           // Altura da linha para ficar mais legível 
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Cores para a Tabela
        table.setBackground(Color.decode("#282A36")); 
        table.setForeground(Color.WHITE); 
        table.setGridColor(Color.decode("#44475A"));
        
        // Estilização do cabeçalho da tabela
        table.getTableHeader().setBackground(Color.decode("#44475A")); 
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Coloca a tabela dentro de um scroll (rolagem) para muitos produtos
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.decode("#1E1E2E")); 
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // 3. BOTÕES INFERIORES
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        bottomPanel.setBackground(Color.decode("#1E1E2E")); 

        // Botões personalizados com cores diferentes
        JButton btnAddStock = createRoundedButton("Adicionar Estoque", "#8BE9FD", "#1E1E2E"); // Ciano
        JButton btnNewProduct = createRoundedButton("Novo Produto", "#F1FA8C", "#1E1E2E"); // Amarelo
        JButton btnRemoveProduct = createRoundedButton("Remover Produto", "#FF79C6", "#FFFFFF"); // Rosa/Roxo
        JButton btnClearSales = createRoundedButton("Limpar Vendas", "#FFB86C", "#1E1E2E"); // Laranja

        // Adiciona as ações aos botões
        btnAddStock.addActionListener(e -> addStockToSelected());
        btnNewProduct.addActionListener(e -> createNewProduct());
        btnRemoveProduct.addActionListener(e -> removeSelectedProduct());
        btnClearSales.addActionListener(e -> clearTodaySales());

        // Adiciona todos os botões ao painel inferior
        bottomPanel.add(btnAddStock);
        bottomPanel.add(btnNewProduct);
        bottomPanel.add(btnRemoveProduct);
        bottomPanel.add(btnClearSales);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshAll(); // Carrega os dados iniciais
    }

    // Método auxiliar para criar os botões arredondados no painel de Estoque. Reaproveita a mesma lógica do OrderEntryPanel para manter consistência visual
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

    /**
     * Método público para atualizar TODOS os dados do painel:
     * - Tabela de produtos
     * - Dashboard financeiro
     * - Alertas de estoque
     * Chamado quando o estoque muda ou ao clicar na aba
     */

    public void refreshAll() {
        // 1. ATUALIZA A TABELA
        tableModel.setRowCount(0); // Limpa todas as linhas da tabela
        for (Product p : inventoryManager.getAllProducts()) { // Para cada produto no estoque, adiciona uma linha com nome, preço formatado e quantidade
            tableModel.addRow(new Object[]{p.getName(), String.format("%.2f", p.getPrice()), p.getStockQuantity()});
        }

        // 2. ATUALIZA DASHBOARD FINANCEIRO
        double totalRevenue = salesManager.getDailySummary()[0];    // Total faturado hoje
        double subtotal = totalRevenue / 1.10;                      // Calcula subtotal (tira 10% de taxa)
        double tax = totalRevenue - subtotal;                       // Calcula o valor da taxa
        lblFinancial.setText(String.format("Ganho de Hoje: R$ %.2f   |   Taxa (10%%): R$ %.2f", totalRevenue, tax));

        // 3. ATUALIZA ALERTAS DE ESTOQUE BAIXO
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

    private void addStockToSelected() {     // Adiciona estoque ao produto selecionado na tabela
        int row = table.getSelectedRow();   // Pega a linha selecionada
        if (row == -1) {                    // Se nenhuma linha foi selecionada
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 0); // Pega o nome do produto
        String input = JOptionPane.showInputDialog(this, "Quantas unidades ADICIONAR a " + name + "?", "0"); // Pede ao usuário quantas unidades adicionar
        if (input != null && !input.isEmpty()) {
            try {
                inventoryManager.getProduct(name).addStock(Integer.parseInt(input)); // Adiciona estoque
                inventoryManager.saveInventory(); // Salva no arquivo CSV
                refreshAll(); // Atualiza a interface
                orderEntryPanel.refreshMenu(); // Atualiza o menu do caixa também 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Apenas números inteiros válidos.");
            }
        }
    }

    private void createNewProduct() { // Cria um novo produto com nome, preço e estoque
        // Cria campos de texto para o usuário preencher
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        Object[] message = {"Nome:", nameField, "Preço (Ex: 10.50):", priceField, "Estoque:", stockField};

        // Mostra uma janela de diálogo para preencher os dados
        if (JOptionPane.showConfirmDialog(this, message, "Novo Produto", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().replace(",", "."));
                int stock = Integer.parseInt(stockField.getText());
                
                if (name.isEmpty() || price <= 0 || stock < 0) throw new NumberFormatException(); // Valida os dados

                inventoryManager.addOrUpdateProduct(new Product(name, price, stock)); // Adiciona o novo produto ao estoque
                refreshAll(); // Atualiza a interface
                orderEntryPanel.refreshMenu(); // Atualiza o menu do caixa
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dados inválidos! Use ponto para os centavos.");
            }
        }
    }

    private void removeSelectedProduct() { // Remove o produto selecionado da tabela (com confirmação)
        int row = table.getSelectedRow();  // Pega a linha selecionada
        if (row == -1) {                   // Se nenhuma linha foi selecionada
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Deletar " + name + " permanentemente?", "Excluir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { // Confirma se o usuário realmente quer deletar
            inventoryManager.removeProduct(name); // Remove do estoque
            refreshAll(); // Atualiza a interface
            orderEntryPanel.refreshMenu(); // Atualiza o menu do caixa
        }
    }

    private void clearTodaySales() { // Limpa as vendas do dia (com confirmação)
        if (JOptionPane.showConfirmDialog(this, "Tem certeza que deseja ZERAR as vendas de hoje?", "Limpar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            salesManager.clearDailySales(); // Limpa as vendas
            refreshAll(); // Atualiza a interfac
            JOptionPane.showMessageDialog(this, "O histórico de vendas de hoje foi apagado.");
        }
    }
}