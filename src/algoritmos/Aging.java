package algoritmos;

import memoria.Ram;
import memoria.TabelaDePaginas;

import java.util.*;

public class Aging implements AlgoritmoSubstituicao {
    @Override
    public String getNome() {
        return "ALGORITMO DE ENVELHECIMENTO (AGING)";
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

        HashMap<Integer, Integer> registrador = new HashMap<>();

        for (int pagina : cadeia) {

            if (tabelaDePaginas.estaNaRAM(pagina)) {
                tabelaDePaginas.obter(pagina).bitReferencia = true;
                resultado.registrarHit(RAM.toString());

            } else { // page fault

                if (RAM.temFrameLivre()) {
                    int frame = RAM.adicionarPagina(pagina);
                    tabelaDePaginas.carregar(pagina, frame);
                    tabelaDePaginas.obter(pagina).bitReferencia = true;
                } else { // n tem frame livre
                    List<Integer> paginasRam = RAM.getPaginasNaRAM();
                    int vitima = Collections.min(paginasRam, Comparator.comparingInt(p -> registrador.getOrDefault(p, 0)));
                    int frameVitima = tabelaDePaginas.obter(vitima).frameAlocado;

                    RAM.removerDoFrame(frameVitima);
                    tabelaDePaginas.remover(vitima);
                    registrador.remove(vitima);

                    RAM.getFrames()[frameVitima] = pagina;
                    tabelaDePaginas.carregar(pagina, frameVitima);
                    tabelaDePaginas.obter(pagina).bitReferencia = true;
                }

                resultado.registrarFault(RAM.toString());
            }
            for (int p : RAM.getPaginasNaRAM()) {
                TabelaDePaginas.EntradaTabela entrada = tabelaDePaginas.obter(p);
                int registradorAtualizado = registrador.getOrDefault(p, 0);


                if (entrada.bitReferencia) {
                registradorAtualizado++;
                } else {
                    if (registradorAtualizado > 0) {
                        registradorAtualizado--;
                    }

                }

                registrador.put(p, registradorAtualizado);
                entrada.bitReferencia = false;
            }
        }
        return resultado;
    }

}
