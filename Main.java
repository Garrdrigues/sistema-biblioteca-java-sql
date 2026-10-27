
// 1. HIERARQUIA DE ITENS DO ACERVO

abstract class ItemBiblioteca {
    private final String codigo;
    private final String titulo;
    private boolean disponivel;

    public ItemBiblioteca(String codigo, String titulo) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.disponivel = true; // Todo item inicia disponível
    }

    // Métodos abstratos: variam conforme o tipo de item
    public abstract int getPrazoDias();
    public abstract double getMultaDiaria();
    public abstract String getTipoDescricao();

    // Alteração de estado controlada pacote-privado 
    void marcarComoEmprestado() {
        this.disponivel = false;
    }

    void marcarComoDevolvido() {
        this.disponivel = true;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    @Override
    public String toString() {
        String status = disponivel ? "Disponível" : "Emprestado";
        return String.format("[%s] Código: %-6s | Título: %-30s | Prazo: %2d dias | Multa/dia: R$ %.2f | Status: %s",
                getTipoDescricao(), codigo, titulo, getPrazoDias(), getMultaDiaria(), status);
    }
}

class Livro extends ItemBiblioteca {
    private final String autor;

    public Livro(String codigo, String titulo, String autor) {
        super(codigo, titulo);
        this.autor = autor;
    }

    @Override
    public int getPrazoDias() {
        return 14;
    }

    @Override
    public double getMultaDiaria() {
        return 0.50;
    }

    @Override
    public String getTipoDescricao() {
        return "LIVRO  ";
    }

    public String getAutor() {
        return autor;
    }
}

class Revista extends ItemBiblioteca {
    private final int edicao;

    public Revista(String codigo, String titulo, int edicao) {
        super(codigo, titulo);
        this.edicao = edicao;
    }

    @Override
    public int getPrazoDias() {
        return 7;
    }

    @Override
    public double getMultaDiaria() {
        return 1.00;
    }

    @Override
    public String getTipoDescricao() {
        return "REVISTA";
    }

    public int getEdicao() {
        return edicao;
    }
}

// Categoria extra (Dica 10):
class DVD extends ItemBiblioteca {
    public DVD(String codigo, String titulo) {
        super(codigo, titulo);
    }

    @Override
    public int getPrazoDias() {
        return 3;
    }

    @Override
    public double getMultaDiaria() {
        return 2.00;
    }

    @Override
    public String getTipoDescricao() {
        return "DVD    ";
    }
}

// 2. HIERARQUIA DE UTILIZADORES

abstract class Usuario {
    private final String nome;
    private int quantidadeEmprestada;

    public Usuario(String nome) {
        this.nome = nome;
        this.quantidadeEmprestada = 0;
    }

    public abstract int getLimiteItens();

    public boolean podeEmprestar() {
        return quantidadeEmprestada < getLimiteItens();
    }

    void registrarNovoEmprestimo() {
        this.quantidadeEmprestada++;
    }

    void registrarDevolucao() {
        if (this.quantidadeEmprestada > 0) {
            this.quantidadeEmprestada--;
        }
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidadeEmprestada() {
        return quantidadeEmprestada;
    }
}

class Aluno extends Usuario {
    public Aluno(String nome) {
        super(nome);
    }

    @Override
    public int getLimiteItens() {
        return 3;
    }
}

class Professor extends Usuario {
    public Professor(String nome) {
        super(nome);
    }

    @Override
    public int getLimiteItens() {
        return 5;
    }
}

// 3. CLASSE BIBLIOTECA

class Biblioteca {
    private final ItemBiblioteca[] acervo;
    private int totalItens;

    public Biblioteca(int capacidadeMaximaAcervo) {
        this.acervo = new ItemBiblioteca[capacidadeMaximaAcervo];
        this.totalItens = 0;
    }

    public void cadastrarItem(ItemBiblioteca item) {
        if (totalItens < acervo.length) {
            acervo[totalItens] = item;
            totalItens++;
        } else {
            System.out.println("Erro: Capacidade máxima do acervo atingida.");
        }
    }

    private ItemBiblioteca buscarPorCodigo(String codigo) {
        for (int i = 0; i < totalItens; i++) {
            if (acervo[i].getCodigo().equalsIgnoreCase(codigo)) {
                return acervo[i];
            }
        }
        return null;
    }

    public boolean emprestar(String codigoItem, Usuario usuario) {
        ItemBiblioteca item = buscarPorCodigo(codigoItem);

        if (item == null) {
            System.out.println("Empréstimo recusado: Item [" + codigoItem + "] não encontrado.");
            return false;
        }

        if (!item.isDisponivel()) {
            System.out.println("Empréstimo recusado: O item '" + item.getTitulo() + "' já está emprestado.");
            return false;
        }

        if (!usuario.podeEmprestar()) {
            System.out.printf("Empréstimo recusado: %s atingiu o limite máximo de %d itens simultâneos.%n",
                    usuario.getNome(), usuario.getLimiteItens());
            return false;
        }

        item.marcarComoEmprestado();
        usuario.registrarNovoEmprestimo();
        System.out.printf("Sucesso: '%s' emprestado para %s (Prazo: %d dias | Multa diária: R$ %.2f).%n",
                item.getTitulo(), usuario.getNome(), item.getPrazoDias(), item.getMultaDiaria());
        return true;
    }

    public boolean devolver(String codigoItem, Usuario usuario, int diasAtraso) {
        ItemBiblioteca item = buscarPorCodigo(codigoItem);

        if (item == null || item.isDisponivel()) {
            System.out.println("Devolução inválida: Item não encontrado ou já consta como disponível.");
            return false;
        }

        item.marcarComoDevolvido();
        usuario.registrarDevolucao();

        if (diasAtraso > 0) {
            double multaTotal = diasAtraso * item.getMultaDiaria();
            System.out.printf("Devolução concluída com atraso (%d dias): '%s'. Multa a pagar: R$ %.2f%n",
                    diasAtraso, item.getTitulo(), multaTotal);
        } else {
            System.out.printf("Devolução concluída no prazo: '%s' devolvido por %s.%n",
                    item.getTitulo(), usuario.getNome());
        }
        return true;
    }

    // Laço único polimórfico para verificar o tipo do item
    public void listarAcervo() {
        System.out.println("\n==================== ACERVO DA BIBLIOTECA ====================");
        for (int i = 0; i < totalItens; i++) {
            System.out.println(acervo[i]);
        }
        System.out.println("==============================================================\n");
    }
}

// 4. CLASSE MAIN — CENÁRIO DE TESTE

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca(10);

        // 1. Registo de itens no acervo
        biblioteca.cadastrarItem(new Livro("LIV01", "Código Limpo", "Robert C. Martin"));
        biblioteca.cadastrarItem(new Livro("LIV02", "Engenharia de Software", "Ian Sommerville"));
        biblioteca.cadastrarItem(new Revista("REV01", " Ciência Hoje", 312));
        biblioteca.cadastrarItem(new Revista("REV02", "Pesquisa FAPESP", 298));
        biblioteca.cadastrarItem(new DVD("DVD01", "Documentário Alan Turing"));

        // 2. Registo de utilizadores
        Usuario aluno = new Aluno("Gabriel Rodrigues");
        Usuario professor = new Professor("Dr. Fernando Silva");

        biblioteca.listarAcervo();

        // 3. Empréstimos para o Aluno
        System.out.println("--- Realizando empréstimos ---");
        biblioteca.emprestar("LIV01", aluno);
        biblioteca.emprestar("REV01", aluno);
        biblioteca.emprestar("DVD01", aluno);

        // 4. Empréstimo recusado por limite atingido
        System.out.println("\n--- Testando estouro de limite do Aluno ---");
        biblioteca.emprestar("LIV02", aluno);

        // 5. Empréstimo para o Professor e Devolução
        System.out.println("\n--- Operações adicionais (Professor e Devolução) ---");
        biblioteca.emprestar("LIV02", professor);
        biblioteca.devolver("REV01", aluno, 2); 

        biblioteca.listarAcervo();
    }
}