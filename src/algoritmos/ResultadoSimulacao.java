package algoritmos;

import java.util.ArrayList;
import java.util.List;

public class ResultadoSimulacao {
    private final String      algoritmo;
//    private final int         totalAcessos;
    private       int         pageFaults;
    private final List<String> log;

    public ResultadoSimulacao(String algoritmo/*, int totalAcessos*/) {
        this.algoritmo    = algoritmo;
//        this.totalAcessos = totalAcessos;
        this.pageFaults   = 0;
        this.log          = new ArrayList<>();
    }

    public void registrarFault(String estadoFrames) {
        pageFaults++;
        System.out.println("[FAULT] frames= " + estadoFrames);
    }

    public void registrarHit(String estadoFrames) {
        System.out.println("[HIT  ] frames= " + estadoFrames);
    }

    public int    getPageFaults()  { return pageFaults; }
//    public int    getTotalAcessos(){ return totalAcessos; }
    public List<String> getLog()   { return log; }

    @Override
    public String toString() {
        return String.format("%-8s | Faults: %3d",
                algoritmo, pageFaults); /*,totalAcessos,
                (double) pageFaults / totalAcessos * 100);*/
    }
}
