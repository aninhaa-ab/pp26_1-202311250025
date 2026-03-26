// INTERFACE IMPLEMENTADOR 
interface Implementador {
    void getDados(String tipo);
}

// IMPLEMENTAÇÕES CONCRETAS 
class PublicacaoImplBD implements Implementador {
    @Override
    public void getDados(String tipo) {
        System.out.println("[PublicacaoImplBD] getDados chamado com tipo: " + tipo);
    }
}

class PublicacaoImplXML implements Implementador {
    @Override
    public void getDados(String tipo) {
        System.out.println("[PublicacaoImplXML] getDados chamado com tipo: " + tipo);
        System.out.println("[PublicacaoImplXML] tipo.setTitulo(\"...\") chamado");
        System.out.println("[PublicacaoImplXML] tipo.setAutores(...) chamado");
        System.out.println("[PublicacaoImplXML] tipo.setOutros(...) chamado");
    }
}

// ABSTRAÇÃO 
abstract class Publicacao {
    protected Implementador imp;

    public Publicacao(Implementador imp) {
        this.imp = imp;
    }

    public void obterDados(String tipo) {
        System.out.println("[Publicacao] obterDados chamado");
        imp.getDados(tipo);
    }

    public abstract void getTitulo();
    public abstract void getAutor(int id);
}

// ABSTRAÇÕES REFINADAS 
class Livro extends Publicacao {
    public Livro(Implementador imp) {
        super(imp);
    }

    public void getISBN() {
        System.out.println("[Livro] getISBN chamado");
    }

    @Override
    public void getTitulo() {
        System.out.println("[Livro] getTitulo chamado");
    }

    @Override
    public void getAutor(int id) {
        System.out.println("[Livro] getAutor chamado com id: " + id);
    }
}

class Revista extends Publicacao {
    public Revista(Implementador imp) {
        super(imp);
    }

    public void getArtigo() {
        System.out.println("[Revista] getArtigo chamado");
    }

    @Override
    public void getTitulo() {
        System.out.println("[Revista] getTitulo chamado");
    }

    @Override
    public void getAutor(int id) {
        System.out.println("[Revista] getAutor chamado com id: " + id);
    }
}

// CLIENTE 
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Cliente do Padrão Bridge ===\n");

        // Livro com implementação BD
        System.out.println("-- Livro com PublicacaoImplBD --");
        Implementador implBD = new PublicacaoImplBD();
        Livro livro = new Livro(implBD);
        livro.getISBN();
        livro.getTitulo();
        livro.getAutor(1);
        livro.obterDados("livro");

        System.out.println();

        // Revista com implementação XML
        System.out.println("-- Revista com PublicacaoImplXML --");
        Implementador implXML = new PublicacaoImplXML();
        Revista revista = new Revista(implXML);
        revista.getArtigo();
        revista.getTitulo();
        revista.getAutor(2);
        revista.obterDados("revista");

        System.out.println();

        // Livro com implementação XML (demonstra a flexibilidade do Bridge)
        System.out.println("-- Livro com PublicacaoImplXML (Bridge: troca de implementador) --");
        Livro livroXML = new Livro(implXML);
        livroXML.getTitulo();
        livroXML.obterDados("livroXML");

        System.out.println("\n=== Fim da execução ===");
    }
}
