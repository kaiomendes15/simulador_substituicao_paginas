package algoritmos;

public interface AlgoritmoSubstituicao {
    String getNome();
    ResultadoSimulacao executar(int[] cadeia, int quantFrames);
}
