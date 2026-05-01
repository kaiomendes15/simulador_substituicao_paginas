package algoritmos;

import memoria.Ram;
import memoria.TabelaDePaginas;

import java.util.LinkedList;

public class Lru implements AlgoritmoSubstituicao{
    @Override
    public String getNome() {
        return "LRU";
    }

    @Override
    public ResultadoSimulacao executar(int[] cadeia, int quantFrames) {
        Ram ram = new Ram(quantFrames);
        TabelaDePaginas tabela = new TabelaDePaginas();
        ResultadoSimulacao resultado = new ResultadoSimulacao(this.getNome());

        LinkedList<Integer> ordem = new LinkedList<>(); //do LRU ao usando mais recentemente

        for(int pagina : cadeia){
            if(tabela.estaNaRAM(pagina)){ //hit
                ordem.remove((Integer) pagina); //página vai ser movida pro fim da lista, pois já houve hit
                ordem.addLast(pagina);
                resultado.registrarHit(ram.toString());
            }
            else{ //fault
                if(ram.temFrameLivre()){
                    int frame = ram.adicionarPagina(pagina);
                    tabela.carregar(pagina, frame);
                } else{
                    int vitima = ordem.removeFirst(); //vitima é a menos recentemente usada (começo da lista)
                    int frameVitima = tabela.obter(vitima).frameAlocado;
                    ram.removerDoFrame(frameVitima);
                    tabela.remover(vitima);
                    ram.getFrames()[frameVitima] = pagina;
                    tabela.carregar(pagina, frameVitima);
                }
                ordem.addLast(pagina);
                resultado.registrarFault(ram.toString());
            }
        }
        return resultado;
    }
}

