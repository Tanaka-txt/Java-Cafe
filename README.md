# ☕ Java Café - Point of Sale System

Um sistema de Ponto de Venda desenvolvido como trabalho final da disciplina de **Programação Orientada a Objetos (SCC0504)** no Instituto de Ciências Matemáticas e de Computação da Universidade de São Paulo (ICMC - USP).

## 💻 Sobre o Projeto
O sistema simula o terminal de caixa e a gestão de estoque de um café. O objetivo principal foi aplicar os conceitos de Orientação a Objetos na prática, separando o projeto em pacotes organizados (Models, Views e Persistence) para manter o código estruturado e integrado a uma interface gráfica.

## ✨ Funcionalidades
- **Entrada de Pedidos (Caixa):** Interface interativa com botões de produtos, cálculo automático de subtotal, taxas (10%) e total.
- **Gerenciamento de Estoque:** Tabela com listagem de produtos, adição e remoção de itens, e controle de quantidade em tempo real.
- **Resumo Financeiro:** Dashboard com o lucro diário, contagem de transações e alertas automáticos de estoque baixo.
- **Persistência de Dados:** Salvamento automático do inventário e do histórico de vendas utilizando arquivos `.csv`.
- **Tratamento de Exceções:** Sistema à prova de falhas com validação rigorosa (ex: bloqueio de venda sem estoque utilizando a classe customizada `OutOfStockException`).
- **Design Customizado:** Interface em *Dark Mode*, botões arredondados renderizados via `Graphics2D` e adaptação inteligente do redimensionamento das imagens.

## 🛠️ Tecnologias Utilizadas
- **Linguagem:** Java (JDK)
- **Interface Gráfica (GUI):** Java Swing & AWT
- **Armazenamento:** Arquivos de Texto (I/O e manipulação de arquivos CSV)
- **IDEs suportadas:** Compatível e testado no VS Code e Eclipse.

## 🚀 Como Compilar e Executar o Projeto

### Pré-requisitos
* **Java Development Kit (JDK)** devidamente instalado e configurado nas variáveis de ambiente do seu sistema.
* **Git** (opcional, para clonar o repositório).

### Passo a Passo

1. **Clone o repositório** para a sua máquina local:
   ```bash
   git clone [https://github.com/Tanaka-txt/Java-Cafe.git](https://github.com/Tanaka-txt/Java-Cafe.git)
2. **Abra a pasta do projeto** (`JavaCafe`) em uma IDE compatível (Eclipse, VS Code, IntelliJ, etc.).

3. **Certifique-se** de que a pasta `src` está configurada como o diretório principal de fontes (*Source Folder*).

4. **Aguarde** a IDE realizar o *build* (compilação automática) das dependências do projeto.

5. **Navegue** até o pacote `views`, abra o arquivo `MainFrame.java` e execute a aplicação (*Run as Java Application*).

*(Nota: Não é necessária a configuração prévia de bancos de dados ou servidores externos, pois o sistema cria e consome seus próprios arquivos de texto localmente na raiz do projeto).*

## 👥 Equipe de Desenvolvimento
- [Giovana Rafaela Marmo de Almeida]
- [Júlio César Tanaka Vergamini]
- [Laysa Almeida Oliveira]
