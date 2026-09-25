import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso incorreto dos argumentos!");
            System.out.println("Para comprimir:   java -jar huffman.jar c <arquivo_entrada> <arquivo_saida>");
            System.out.println("Para descomprimir: java -jar huffman.jar d <arquivo_entrada> <arquivo_saida>");
            return;
        }

        String operacao = args[0];
        String arquivoEntrada = args[1];
        String arquivoSaida = args[2];

        try {
            if (operacao.equalsIgnoreCase("c")) {
                System.out.println("[ETAPA 1] Analisando frequências do arquivo...");
                System.out.println("[ETAPA 2 e 3] Construindo Min-Heap e Árvore de Huffman...");
                System.out.println("[ETAPA 4 e 5] Gerando códigos e escrevendo arquivo comprimido...");
                
                // TODO: Chamar os métodos de compressão aqui
                
                System.out.println("Arquivo comprimido com sucesso para: " + arquivoSaida);

            } else if (operacao.equalsIgnoreCase("d")) {
                System.out.println("[INFO] Iniciando descompressão do arquivo: " + arquivoEntrada);

                try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(arquivoEntrada)));
                BufferedOutputStream saidaTexto = new BufferedOutputStream(new FileOutputStream(arquivoSaida))) {
        
                int totalCaracteres = dis.readInt();

                No raiz = Decodificador.lerCabecalho(dis);

                Decodificador.descomprimir(dis, raiz, saidaTexto, totalCaracteres);
            } else {
                        System.out.println("Operação inválida! Use 'c' para comprimir ou 'd' para descomprimir.");
            }

        } catch (IOException e) {
                    System.err.println("Erro durante a execução do processo: " + e.getMessage());
                    e.printStackTrace();
        }
    }
}