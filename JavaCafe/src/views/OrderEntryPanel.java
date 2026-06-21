package views; // Pacote onde estão as classes da interface gráfica

// Importações necessárias para a interface, tratamento de exceções, modelos de dados e gerenciadores
import javax.swing.*;
import exceptions.OutOfStockException;
import models.Order;
import models.OrderItem;
import models.Product;
import persistence.InventoryManager;
import persistence.SalesManager;
import java.awt.*;
import java.io.File;

@SuppressWarnings("serial") // Suprime aviso sobre serialização

public class OrderEntryPanel extends JPanel {   // Painel que representa a aba de caixa/pedidos
    private InventoryManager inventoryManager;  // Acesso aos produtos do estoque
    private SalesManager salesManager;          // Acesso para salvar as vendas
    private Order currentOrder;                 // Pedido atual que está sendo montado

    // Componentes da interface do carrinho
    private DefaultListModel<String> cartListModel; // Modelo que guarda os dados da lista
    private JList<String> cartList;                 // A lista visual que mostra os itens do carrinho
    private JLabel lblSubtotal;                     // Mostra o subtotal (sem taxa)
    private JLabel lblTax;                          // Mostra o valor da taxa (10%)
    private JLabel lblTotal;                        // Mostra o total com taxa
    
    private JPanel rightPanel; // Painel da direita que contém os produtos

    public OrderEntryPanel(InventoryManager inventoryManager, SalesManager salesManager) { // Construtor que recebe os gerenciadores do MainFrame
        this.inventoryManager = inventoryManager;
        this.salesManager = salesManager;
        this.currentOrder = new Order();

        // Configurações do layout do painel principal
        setLayout(new BorderLayout(15, 15)); 
        setBackground(Color.decode("#1E1E2E")); 
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 

        add(createCartPanel(), BorderLayout.WEST); // Adiciona o carrinho (lista de itens) na parte esquerda

        // Cria o painel da direita que terá os produtos
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.decode("#1E1E2E")); 
        rightPanel.add(createMenuPanel(), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
    }

    public void refreshMenu() {     // Método público para atualizar o menu de produtos (estoque muda)
        rightPanel.removeAll();     // Remove tudo que está no painel da direita
        rightPanel.add(createMenuPanel(), BorderLayout.CENTER); // Recria o menu com dados atualizados
        rightPanel.revalidate();    // Recalcula o layout
        rightPanel.repaint();       // Redesenha na tela
    }

    private JPanel createCartPanel() { // Método que cria o painel do carrinho
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBackground(Color.decode("#1E1E2E")); 

        JLabel lblTitle = new JLabel("🛒 Pedido Atual");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        panel.add(lblTitle, BorderLayout.NORTH);

        cartListModel = new DefaultListModel<>();   // Armazena os itens em forma de texto
        cartList = new JList<>(cartListModel);      // Componente visual da lista de itens
        
        // Estilização da lista
        cartList.setBackground(Color.decode("#282A36")); 
        cartList.setForeground(Color.decode("#F8F8F2")); 
        cartList.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        JScrollPane scrollPane = new JScrollPane(cartList); // Coloca a lista dentro de um scroll (rolagem) para muitos itens
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); // Scroll mais rápido
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(7, 1, 8, 8)); // Painel de baixo com os valores e botões
        bottomPanel.setBackground(Color.decode("#1E1E2E")); 
        
        // Labels de valores
        lblSubtotal = new JLabel("Subtotal: R$ 0,00"); 
        lblSubtotal.setForeground(Color.LIGHT_GRAY);
        lblSubtotal.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        lblTax = new JLabel("Taxa (10%): R$ 0,00"); // Taxa de serviço fixa de 10% sobre o subtotal
        lblTax.setForeground(Color.LIGHT_GRAY);
        lblTax.setFont(new Font("SansSerif", Font.PLAIN, 18));

        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTotal.setForeground(Color.decode("#FF79C6")); 

        // Botões personalizados com cores diferentes
        JButton btnFinalize = createRoundedButton("Finalizar Venda", "#50FA7B", "#1E1E2E"); 
        JButton btnDailySummary = createRoundedButton("Resumo Diário", "#8BE9FD", "#1E1E2E"); 
        JButton btnClear = createRoundedButton("Limpar Carrinho", "#FF5555", "#FFFFFF"); 
        JButton btnRemoveItem = createRoundedButton("Remover Item", "#FFB86C", "#1E1E2E"); // Cor Laranja

        // Adiciona as ações aos botões
        btnFinalize.addActionListener(e -> finalizeSale());         // Finaliza a venda
        btnDailySummary.addActionListener(e -> showDailySummary()); // Mostra resumo do dia
        btnClear.addActionListener(e -> {   // Limpa o carrinho
            currentOrder.clearOrder();      // Remove todos os itens do pedido
            updateCartView();               // Atualiza a tela
        });

        btnRemoveItem.addActionListener(e -> { 
            int selectedIndex = cartList.getSelectedIndex();    // Pega a linha selecionada
            if (selectedIndex != -1) {
                currentOrder.removeItemByIndex(selectedIndex);  // Remove do pedido
                updateCartView();                               // Atualiza a tela
            } else {
                // Aviso se nada foi selecionado
                JOptionPane.showMessageDialog(this, "Selecione um item no carrinho para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Adiciona todos os componentes ao painel de baixo
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

    private JScrollPane createMenuPanel() { // Método que cria o painel com os produtos (menu)
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20)); // Grid com 3 colunas, espaçamento 20px 
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(Color.decode("#1E1E2E")); 

        // Para cada produto no estoque vai ser criado um botão personalizado com desenho próprio
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
            
            // Configurações do botão
            btnProduct.setLayout(new BorderLayout());  // Layout para organizar imagem e texto
            btnProduct.setContentAreaFilled(false); // Não desenha fundo padrão
            btnProduct.setBorderPainted(false);     // Sem borda padrão
            btnProduct.setFocusPainted(false);      // Sem destaque de foco
            btnProduct.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor de mão
            btnProduct.setPreferredSize(new Dimension(160, 220)); // Tamanho do card

            // --- LÓGICA DE CARREGAR IMAGEM DO PRODUTO ---
            // O sistema procura a imagem em 3 etapas:
            // 1. Tenta .png, .jpg, .jpeg na pasta "imagens/"
            // 2. Se não achar, tenta em "JavaCafe/imagens/"
            // 3. Se ainda não achar, usa emoji de café ☕ como fallback

            JLabel imgLabel = new JLabel();
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setVerticalAlignment(SwingConstants.CENTER);

            String[] extensoes = {".png", ".jpg", ".jpeg"}; // Tenta carregar a imagem com diferentes extensões
            ImageIcon finalIcon = null;
            
            for (String ext : extensoes) {
                // Caminhos onde pode estar a imagem
                File imgFile = new File("imagens/" + product.getName() + ext);
                if (!imgFile.exists()) {
                    imgFile = new File("JavaCafe/imagens/" + product.getName() + ext); // Tentativa alternativa
                }

                if (imgFile.exists()) { // Se encontrou a imagem
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
                    break; // Sai do loop pois encontrou a imagem
                }
            }

            if (finalIcon != null) {
                imgLabel.setIcon(finalIcon); // Coloca a imagem no label
            } else {
                imgLabel.setText("☕"); // Se não encontrou imagem, coloca um emoji de café
                imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
                imgLabel.setForeground(Color.decode("#44475A"));
            }
            btnProduct.add(imgLabel, BorderLayout.CENTER);
            // ------------------------------------------------

            // Painel do rodapé do card (nome e preço)
            JPanel footer = new JPanel(new GridLayout(2, 1, 2, 2));
            footer.setOpaque(false); // Transparente para ver o fundo do botão
            footer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10)); 

            JLabel lblName = new JLabel(product.getName()); // Nome do produto
            lblName.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblName.setForeground(Color.WHITE);

            JLabel lblPrice = new JLabel(String.format("R$ %.2f", product.getPrice())); // Preço formatado
            lblPrice.setFont(new Font("SansSerif", Font.PLAIN, 16));
            lblPrice.setForeground(Color.decode("#50FA7B")); 

            footer.add(lblName);
            footer.add(lblPrice);

            btnProduct.add(footer, BorderLayout.SOUTH);

            // Ação ao clicar no produto: adiciona ao carrinho
            btnProduct.addActionListener(e -> {
                currentOrder.addItem(product, 1); // Adiciona 1 unidade do produto
                updateCartView();       // Atualiza a tela do carrinho
            });
            gridPanel.add(btnProduct);  // Adiciona o botão ao grid
        }
        
        JScrollPane scrollPane = new JScrollPane(gridPanel); // Coloca o grid em um painel com scroll
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getViewport().setBackground(Color.decode("#1E1E2E")); 
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        return scrollPane;
    }

    // Método que cria botões com cantos arredondados e cor personalizada
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
        
        // Configurações do botão
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setForeground(Color.decode(fgColorHex)); 
        btn.setContentAreaFilled(false); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateCartView() { // Método que atualiza a visualização do carrinho
        cartListModel.clear(); // Limpa a lista atual
        for (OrderItem item : currentOrder.getItems()) { // Para cada item no pedido, adiciona sua representação em texto à lista
            cartListModel.addElement(item.toString());
        }
        // Atualiza os valores (subtotal, taxa, total)
        lblSubtotal.setText(String.format("Subtotal: R$ %.2f", currentOrder.getSubtotal()));
        lblTax.setText(String.format("Taxa (10%%): R$ %.2f", currentOrder.getTaxAmount()));
        lblTotal.setText(String.format("TOTAL: R$ %.2f", currentOrder.getTotal()));
    }

    private void finalizeSale() { // Método que finaliza a venda
        // FLUXO DE FINALIZAÇÃO:
        // 1. Verifica se carrinho não está vazio
        // 2. Valida estoque de cada item (lança exceção se faltar)
        // 3. Dá baixa no estoque
        // 4. Salva venda no histórico
        // 5. Gera e exibe recibo
        // 6. Limpa carrinho e atualiza interface
        
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O carrinho está vazio!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try { // Verifica estoque de cada item antes de vender
            for (OrderItem item : currentOrder.getItems()) {
                if (item.getQuantity() > item.getProduct().getStockQuantity()) {
                    // Se não tem estoque suficiente, lança exceção personalizada
                    throw new OutOfStockException("Estoque insuficiente para o produto: " + item.getProduct().getName() + ". Disponível: " + item.getProduct().getStockQuantity());
                }
            }
            // Dá baixa no estoque (diminui a quantidade)
            for (OrderItem item : currentOrder.getItems()) item.getProduct().decreaseStock(item.getQuantity());

            // Salva a venda no histórico e salva o estoque atualizado
            salesManager.saveOrder(currentOrder);
            inventoryManager.saveInventory();

            // Constrói o recibo para mostrar ao usuário
            StringBuilder receipt = new StringBuilder("=== RECIBO ===\n\n");
            for (OrderItem item : currentOrder.getItems()) receipt.append(item.toString()).append("\n");
            receipt.append("\n-------------------------\n");
            receipt.append(String.format("Total Pago: R$ %.2f\n", currentOrder.getTotal()));
            receipt.append("=========================");

            // Mostra o recibo na tela
            JOptionPane.showMessageDialog(this, receipt.toString(), "Venda Finalizada", JOptionPane.INFORMATION_MESSAGE);

            // Limpa o carrinho e atualiza a tela
            currentOrder.clearOrder();
            updateCartView();
            refreshMenu(); // Atualiza o menu para mostrar estoque atualizado

        } catch (OutOfStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Estoque", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método que mostra o resumo diário de vendas
    private void showDailySummary() {
        double[] summary = salesManager.getDailySummary(); // Pega os dados do gerenciador
        // Mostra em uma janela o total faturado e número de transações
        JOptionPane.showMessageDialog(this, String.format("=== RESUMO DIÁRIO ===\n\nTotal Faturado Hoje: R$ %.2f\nTransações: %.0f\n", summary[0], summary[1]), "Resumo", JOptionPane.INFORMATION_MESSAGE);
    }
}