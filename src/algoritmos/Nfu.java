package algoritmos;

import memoria.Ram;
import memoria.TabelaDePaginas;

public class Nfu implements AlgoritmoSubstituicao{
    @Override
    public String getNome(){
        return "NFU";
    }

    @Override
    public ResultadoSimulacao executar(int[] cadeia, int quantFrames) {
        Ram ram = new Ram(quantFrames);
        TabelaDePaginas tabela = new TabelaDePaginas();
        ResultadoSimulacao resultado = new ResultadoSimulacao(this.getNome());
        for(int pagina : cadeia){
            if(tabela.estaNaRAM(pagina)){ //hit
                tabela.obter(pagina).contador++; //incrementa o contador da página acessada
                resultado.registrarHit(ram.toString());
            }
            else{ //fault
                if(ram.temFrameLivre()){
                    int frame = ram.adicionarPagina(pagina);
                    tabela.carregar(pagina, frame);
                    tabela.obter(pagina).contador = 1; //primeiro acesso
                }
                else{ //encontrar a página com menor contador entre as que estão na ram
                    int vitima = -1;
                    int menorContador = Integer.MAX_VALUE;
                    for(int f=0; f<ram.getQuantFrames(); f++){
                        int paginaNoFrame = ram.getPaginaNoFrame(f);
                        int contadorAtual = tabela.obter(paginaNoFrame).contador;
                        if(contadorAtual < menorContador){
                            menorContador = contadorAtual;
                            vitima = paginaNoFrame;
                        }
                    }
                    int frameVitima = tabela.obter(vitima).frameAlocado;
                    ram.removerDoFrame(frameVitima);
                    tabela.remover(vitima);
                    ram.getFrames()[frameVitima] = pagina;
                    tabela.carregar(pagina, frameVitima);
                    tabela.obter(pagina).contador = 1;
                }
                resultado.registrarFault(ram.toString());
            }
        }
        return resultado;
    }
}
