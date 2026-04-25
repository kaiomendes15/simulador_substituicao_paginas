package algoritmos;

import memoria.Ram;
import memoria.TabelaDePaginas;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Fifo implements AlgoritmoSubstituicao{
    @Override
    public String getNome() {
        return "FIFO";
    }

    @Override
    public ResultadoSimulacao executar(int[] cadeia, int quantFrames) {

        ArrayList<Integer> disco = new ArrayList<>();

        // todas as paginas no disco
        for (int pagina : cadeia) {
            disco.add(pagina);
        }

        Ram RAM  = new Ram(quantFrames);
        TabelaDePaginas tabelaDePaginas = new TabelaDePaginas();
        ResultadoSimulacao resultado = new ResultadoSimulacao(this.getNome());

        Queue<Integer> fila = new LinkedList<>();

        for (int pagina : cadeia) {

            if (tabelaDePaginas.estaNaRAM(pagina)) {
                resultado.registrarHit(RAM.toString());

            } else { // page fault

                if (RAM.temFrameLivre()) {
                    int frame = RAM.adicionarPagina(pagina);
                    tabelaDePaginas.carregar(pagina, frame);
                } else { // n tem frame livre
                    int vitima = fila.poll();
                    int frameVitima = tabelaDePaginas.obter(vitima).frameAlocado;

                    RAM.removerDoFrame(frameVitima);
                    tabelaDePaginas.remover(vitima);

                    RAM.getFrames()[frameVitima] = pagina;
                    tabelaDePaginas.carregar(pagina, frameVitima);
                }

                fila.add(pagina);
                resultado.registrarFault(RAM.toString());
            }
        }
        return resultado;
    }
}
