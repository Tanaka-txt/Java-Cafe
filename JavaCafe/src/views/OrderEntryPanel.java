package views;

import javax.swing.*;
import exceptions.OutOfStockException;
import models.Order;
import models.OrderItem;
import models.Product;
import persistence.InventoryManager;
import persistence.SalesManager;
import java.awt.*;
import java.io.File;

@SuppressWarnings("serial")
public class OrderEntryPanel extends JPanel {
    private InventoryManager inventoryManager;
    private SalesManager salesManager;
    private Order currentOrder;

    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    private JLabel lblTotal;
    
    private JPanel rightPanel; 

    public OrderEntryPanel(InventoryManager inventoryManager, SalesManager salesManager) {
        this.inventoryManager = inventoryManager;
        this.salesManager = salesManager;
        this.currentOrder = new Order();

        setLayout(new BorderLayout(15, 15)); 
        setBackground(Color.decode("#1E1E2E")); 
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 

        add(createCartPanel(), BorderLayout.WEST);

        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.decode("#1E1E2E")); 
        rightPanel.add(createMenuPanel(), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
    }

    public void refreshMenu() {
        rightPanel.removeAll();
        rightPanel.add(createMenuPanel(), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBackground(Color.decode("#1E1E2E")); 

        JLabel lblTitle = new JLabel("🛒 Pedido Atual");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        panel.add(lblTitle, BorderLayout.NORTH);

        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        
        cartList.setBackground(Color.decode("#282A36")); 
        cartList.setForeground(Color.decode("#F8F8F2")); 
        cartList.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        JScrollPane scrollPane = new JScrollPane(cartList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(7, 1, 8, 8));
        bottomPanel.setBackground(Color.decode("#1E1E2E")); 
        
        lblSubtotal = new JLabel("Subtotal: R$ 0,00");
        lblSubtotal.setForeground(Color.LIGHT_GRAY);
        lblSubtotal.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        lblTax = new JLabel("Taxa (10%): R$ 0,00");
        lblTax.setForeground(Color.LIGHT_GRAY);
        lblTax.setFont(new Font("SansSerif", Font.PLAIN, 18));

        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTotal.setForeground(Color.decode("#FF79C6")); 

        JButton btnFinalize = createRoundedButton("Finalizar Venda", "#50FA7B", "#1E1E2E"); 
        JButton btnDailySummary = createRoundedButton("Resumo Diário", "#8BE9FD", "#1E1E2E"); 
        JButton btnClear = createRoundedButton("Limpar Carrinho", "#FF5555", "#FFFFFF"); 
        JButton btnRemoveItem = createRoundedButton("Remover Item", "#FFB86C", "#1E1E2E"); // Cor Laranja

        btnFinalize.addActionListener(e -> finalizeSale());
        btnDailySummary.addActionListener(e -> showDailySummary());
        btnClear.addActionListener(e -> {
            currentOrder.clearOrder();
            updateCartView();
        });

        btnRemoveItem.addActionListener(e -> {
            int selectedIndex = cartList.getSelectedIndex(); // Pega a linha selecionada
            if (selectedIndex != -1) {
                currentOrder.removeItemByIndex(selectedIndex); // Remove do pedido
                updateCartView(); // Atualiza a tela
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um item no carrinho para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        bottomPanel.add(lblSubtotal);
        bottomPanel.add(lblTax);
        bottomPanel.add(lblTotal);
        bottomPanel.add(btnFinalize);
        bottomPanel.add(btnRemoveItem);
        bottomPanel.add(btnDailySummary);
        bottomPanel.add(btnClear);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane createMenuPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20)); 
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(Color.decode("#1E1E2E")); 

        for (Product product : inventoryManager.getAllProducts()) {
            
            JButton btnProduct = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2.setColor(new Color(0, 0, 0, 60)); 
                    g2.fillRoundRect(6, 6, getWidth() - 6, getHeight() - 6, 20, 20); 
                    
                    g2.setColor(Color.decode("#2A2D3E")); 
                    g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
                    
                    g2.setColor(Color.decode("#3F435C"));
                    g2.drawLine(15, getHeight() - 75, getWidth() - 21, getHeight() - 75);

                    super.paintComponent(g2); 
                    g2.dispose();
                }
            };
            
            btnProduct.setLayout(new BorderLayout()); 
            btnProduct.setContentAreaFilled(false);
            btnProduct.setBorderPainted(false);
            btnProduct.setFocusPainted(false);
            btnProduct.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnProduct.setPreferredSize(new Dimension(160, 220)); 

            // --- LÓGICA DE IMAGEM COM PROPORÇÃO PERFEITA ---
            JLabel imgLabel = new JLabel();
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setVerticalAlignment(SwingConstants.CENTER);

            String[] extensoes = {".png", ".jpg", ".jpeg"};
            ImageIcon finalIcon = null;
            
            for (String ext : extensoes) {
                File imgFile = new File("imagens/" + product.getName() + ext);

                if (!imgFile.exists()) {
                    imgFile = new File("JavaCafe/imagens/" + product.getName() + ext);
                }

                if (imgFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(imgFile.getAbsolutePath());
                    
                    // Pega o tamanho original da foto
                    int origWidth = originalIcon.getIconWidth();
                    int origHeight = originalIcon.getIconHeight();
                    
                    // Tamanho máximo que a foto pode ocupar no card
                    int maxWidth = 110;
                    int maxHeight = 110;
                    
                    // Calcula a escala para não distorcer a imagem
                    double ratio = Math.min((double) maxWidth / origWidth, (double) maxHeight / origHeight);
                    int newWidth = (int) (origWidth * ratio);
                    int newHeight = (int) (origHeight * ratio);
                    
                    // Redimensiona com a nova proporção calculada
                    finalIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH));
                    break;
                }
            }

            if (finalIcon != null) {
                imgLabel.setIcon(finalIcon);
            } else {
                imgLabel.setText("☕");
                imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
                imgLabel.setForeground(Color.decode("#44475A"));
            }
            btnProduct.add(imgLabel, BorderLayout.CENTER);
            // ------------------------------------------------

            JPanel footer = new JPanel(new GridLayout(2, 1, 2, 2));
            footer.setOpaque(false); 
            footer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10)); 

            JLabel lblName = new JLabel(product.getName());
            lblName.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblName.setForeground(Color.WHITE);

            JLabel lblPrice = new JLabel(String.format("R$ %.2f", product.getPrice()));
            lblPrice.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblPrice.setForeground(Color.decode("#50FA7B")); 

            footer.add(lblName);
            footer.add(lblPrice);

            btnProduct.add(footer, BorderLayout.SOUTH);

            btnProduct.addActionListener(e -> {
                currentOrder.addItem(product, 1);
                updateCartView();
            });
            gridPanel.add(btnProduct);
        }
        
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getViewport().setBackground(Color.decode("#1E1E2E")); 
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        return scrollPane;
    }

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
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setForeground(Color.decode(fgColorHex)); 
        btn.setContentAreaFilled(false); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateCartView() {
        cartListModel.clear();
        for (OrderItem item : currentOrder.getItems()) {
            cartListModel.addElement(item.toString());
        }
        lblSubtotal.setText(String.format("Subtotal: R$ %.2f", currentOrder.getSubtotal()));
        lblTax.setText(String.format("Taxa (10%%): R$ %.2f", currentOrder.getTaxAmount()));
        lblTotal.setText(String.format("TOTAL: R$ %.2f", currentOrder.getTotal()));
    }

    private void finalizeSale() {
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            for (OrderItem item : currentOrder.getItems()) {
                if (item.getQuantity() > item.getProduct().getStockQuantity()) {
                    throw new OutOfStockException("Estoque insuficiente para o produto: " + item.getProduct().getName() + ". Disponível: " + item.getProduct().getStockQuantity());
                }
            }
            for (OrderItem item : currentOrder.getItems()) item.getProduct().decreaseStock(item.getQuantity());

            salesManager.saveOrder(currentOrder);
            inventoryManager.saveInventory();

            StringBuilder receipt = new StringBuilder("=== RECIBO ===\n\n");
            for (OrderItem item : currentOrder.getItems()) receipt.append(item.toString()).append("\n");
            receipt.append("\n-------------------------\n");
            receipt.append(String.format("Total Pago: R$ %.2f\n", currentOrder.getTotal()));
            receipt.append("=========================");

            JOptionPane.showMessageDialog(this, receipt.toString(), "Venda Finalizada", JOptionPane.INFORMATION_MESSAGE);

            currentOrder.clearOrder();
            updateCartView();
            refreshMenu(); 

        } catch (OutOfStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Estoque", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDailySummary() {
        double[] summary = salesManager.getDailySummary();
        JOptionPane.showMessageDialog(this, String.format("=== RESUMO DIÁRIO ===\n\nTotal Faturado Hoje: R$ %.2f\nTransações: %.0f\n", summary[0], summary[1]), "Resumo", JOptionPane.INFORMATION_MESSAGE);
    }
}