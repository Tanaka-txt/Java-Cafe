/**
 * Classe principal da interface gráfica do sistema Java Café.
 * Gerencia a janela principal com duas abas:
 * - Caixa: para registrar vendas
 * - Estoque: para administrar produtos
 */
package views; // Pacote onde estão as classes da interface gráfica

// Importações necessárias para a interface gráfica e gerenciamento de dados
import javax.swing.*;
import persistence.InventoryManager;
import persistence.SalesManager;

@SuppressWarnings("serial") // Suprime aviso sobre serialização

public class MainFrame extends JFrame { // Herda de JFrame para ser uma janela
    private InventoryManager inventoryManager; // Gerencia o estoque de produtos
    private SalesManager salesManager; // Gerencia as vendas realizadas

    public MainFrame() {
        // Inicializa os gerenciadores lendo os arquivos CSV
        
        this.inventoryManager = new InventoryManager("estoque.csv"); // O InventoryManager vai ler o arquivo "estoque.csv" que têm todos os produtos
        this.salesManager = new SalesManager("vendas.csv"); // O SalesManager vai ler o arquivo "vendas.csv" com o histórico de vendas
        this.inventoryManager.loadInventory(); // Carrega os dados do estoque do arquivo CSV para a memória

        // Configurações básicas da janela principal
        setTitle("Java Café - Point of Sale System");
        setSize(1024, 600); // Define o tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o programa quando clicar no X
        setLocationRelativeTo(null); // Centraliza a janela

        // Cria o sistema de abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Instancia os painéis (Caixa e Estoque)
        OrderEntryPanel orderPanel = new OrderEntryPanel(inventoryManager, salesManager); // O painel de pedidos recebe os gerenciadores (produtos e vendas)
        InventoryPanel inventoryPanel = new InventoryPanel(inventoryManager, salesManager, orderPanel); // O painel de estoque recebe os gerenciadores e o orderPanel para atualizações

        // Adiciona os paíneis com as abas
        tabbedPane.addTab("Entrada de Pedidos (Caixa)", orderPanel); // Aba onde o atendente registra as vendas
        tabbedPane.addTab("Gerenciamento de Estoque", inventoryPanel); // Aba para administrar os produtos

        // Atualiza os dados do Estoque e Dashboard automaticamente ao clicar na aba
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) { // Se a aba selecionada for a de estoque (índice 1)
                inventoryPanel.refreshAll(); // Atualiza os dados do estoque e dashboard automaticamente
            }
        });

        // Adiciona o sistema de abas na tela (coloca o tabbedPane dentro do JFrame)
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // Garante que a interface seja criada na thread correta, evitando problemas de concorrência no Swing
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Tenta usar o tema visual do sistema operacional
            } catch (Exception e) { 
                e.printStackTrace(); // Se der erro, só imprime o erro e continua com o tema padrão
            }
            new MainFrame().setVisible(true); // Cria uma nova janela MainFrame e a torna visível (setVisible(true))
        });
    }
}