public class EntradaSimulacao {
    private int[] cadeia;

    public EntradaSimulacao(String input) {
        String[] partes = input.trim().split("\\s+");
        cadeia = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            cadeia[i] = Integer.parseInt(partes[i]);
        }
    }

    public int[] getCadeia()       { return cadeia; }
    public int getTotalPaginas()   { return cadeia.length; }
}