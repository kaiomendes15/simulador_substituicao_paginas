import algoritmos.*;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

//        System.out.print("Informe a cadeia de páginas: ");
//        String input = sc.nextLine();

        EntradaSimulacao entrada = new EntradaSimulacao("1 2 1 3 2 1 4 1 1 2 5 1 2 3 4 5");
        int[] cadeia = entrada.getCadeia();

//        System.out.print("Informe a quantidade de frames: ");
//        int quantFrames = sc.nextInt();
        int quantFrames = 3;

        System.out.println("\n=== Simulação de Substituição de Páginas ===");
        System.out.println("Cadeia : " + Arrays.toString(cadeia));
        System.out.println("Frames : " + quantFrames);
        System.out.println("--------------------------------------------");

        List<AlgoritmoSubstituicao> algoritmos = List.of(
                new Fifo(),
                new Nfu(),
                new Aging(),
                new Lru()
        );

        for (AlgoritmoSubstituicao alg : algoritmos) {
            ResultadoSimulacao resultado = alg.executar(cadeia, quantFrames);
            System.out.println(resultado);
        }
    }
}