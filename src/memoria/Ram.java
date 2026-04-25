package memoria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ram {
    private final int      quantFrames;
    private final int[]    frames;        // frames[i] = página (-1 = livre)
    private final boolean[] bitReferencia; // bit R por frame (usado pelo Clock)

    public Ram(int quantFrames) {
        this.quantFrames  = quantFrames;
        this.frames       = new int[quantFrames];
        this.bitReferencia = new boolean[quantFrames];
        Arrays.fill(frames, -1);
    }

    public boolean temFrameLivre() {
        for (int f : frames) if (f == -1) return true;
        return false;
    }

    // Retorna o índice do frame onde a página foi colocada
    public int adicionarPagina(int pagina) {
        for (int i = 0; i < quantFrames; i++) {
            if (frames[i] == -1) {
                frames[i] = pagina;
                return i;
            }
        }
        return -1; // não deveria chegar aqui se temFrameLivre() foi verificado
    }

    public void removerDoFrame(int frame) {
        frames[frame] = -1;
        bitReferencia[frame] = false;
    }

    public int getPaginaNoFrame(int frame)      { return frames[frame]; }
    public int getQuantFrames()                  { return quantFrames; }
    public int[] getFrames()                     { return frames; }
    public boolean getBitR(int frame)            { return bitReferencia[frame]; }
    public void setBitR(int frame, boolean val)  { bitReferencia[frame] = val; }

    // Retorna lista de páginas atualmente na RAM
    public List<Integer> getPaginasNaRAM() {
        List<Integer> lista = new ArrayList<>();
        for (int f : frames) if (f != -1) lista.add(f);
        return lista;
    }

    @Override
    public String toString() {
        return Arrays.toString(frames);
    }
}
