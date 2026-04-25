package memoria;

import java.util.HashMap;
import java.util.Map;

public class TabelaDePaginas {

    public static class EntradaTabela {
        public boolean naRAM;
        public int     frameAlocado;   // -1 se não estiver na RAM
        public boolean bitReferencia;  // usado pelo Clock e Aging
        public int     contador;       // usado pelo NFU
        public int     registroAging;  // registrador de envelhecimento (Aging)
        // ex: byte de 8 bits simulado como int

        public EntradaTabela() {
            naRAM        = false;
            frameAlocado = -1;
            bitReferencia = false;
            contador      = 0;
            registroAging = 0;
        }
    }

    private final Map<Integer, EntradaTabela> tabela = new HashMap<>();

    public EntradaTabela obter(int pagina) {
        return tabela.computeIfAbsent(pagina, k -> new EntradaTabela());
    }

    public boolean estaNaRAM(int pagina) {
        EntradaTabela e = tabela.get(pagina);
        return e != null && e.naRAM;
    }

    public void carregar(int pagina, int frame) {
        EntradaTabela e = obter(pagina);
        e.naRAM        = true;
        e.frameAlocado = frame;
        e.bitReferencia = true;  // ao carregar, já marca como referenciada
    }

    public void remover(int pagina) {
        EntradaTabela e = obter(pagina);
        e.naRAM        = false;
        e.frameAlocado = -1;
        e.bitReferencia = false;
    }
}