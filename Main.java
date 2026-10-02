//Beatriz de Assis Siqueira, RA:10741570
//Daniel Arais Motta, RA:10419718
//Matheus Santos Medeiros, RA:10748040
//Pedro Araujo Botelho, RA:10738317

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

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
                // TODO: Chamar os métodos de compressão aqui
                //ETAPA 1
                System.out.println("[ETAPA 1] Tabela de frequencia de caracteres");
                Codificador codificador = new Codificador();
                int frequencias[] = codificador.contarFrequencias(arquivoEntrada);

                for(int i = 0; i < frequencias.length; i++){
                    if(frequencias[i] > 0){
                        String charExibicao = (i == 10 || i == 13) ? "\\n" : String.valueOf((char)i);
                        System.out.println("Caractere '" + charExibicao + "' (ASCII: " + i + "): " + frequencias[i]);
                    }
                }

                //ETAPA 2
                System.out.println("[ETAPA 2] Min-Heap inicial");
                int totalCaracteres = 0;
                MinHeap minHeap = new MinHeap(new ArrayList<>());
                for(int i = 0; i<frequencias.length; i++){
                    if(frequencias[i] > 0){
                        minHeap.inserirNo(new No((char)i, frequencias[i]));
                        totalCaracteres += frequencias[i];
                    }
                }

                System.out.print("[ ");
                for(int i = 0; i < minHeap.getArrayList().size(); i++){
                    No n = minHeap.getArrayList().get(i);
                    String charExibicao = (n.getCaractere() == '\n' || n.getCaractere() == '\r') ? "\\n" : String.valueOf(n.getCaractere());
                    System.out.print("No('" + charExibicao + "', " + n.getFrequencia() + ")");
                    if(i < minHeap.getArrayList().size() - 1) System.out.print(", ");
                }
                System.out.println(" ]");

                //ETAPA 3
                System.out.println("[ETAPA 3] Arvore de Huffman (contruida em memoria)");
                ArvoreHuffman arvore = new ArvoreHuffman();
                No raiz = arvore.construirArvore(minHeap);

                //ETAPA 4
                System.out.println("[ETAPA 4] Tabela de codigos de Huffman");
                codificador.gerarTabela(raiz);
                String[] codigos = codificador.getTabelaCodigos();

                for(int i = 0; i< codigos.length; i++){
                    if(codigos[i] != null){
                        String charExibicao = (i == 10 || i == 13) ? "\\n" : String.valueOf((char)i);
                        System.out.println("Caractere '" + charExibicao + "': " + codigos[i]);
                    }
                }

                //ETAPA 5
                try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(arquivoSaida)))) {
                    codificador.comprimirArquivo(arquivoEntrada, raiz, dos, totalCaracteres);
                }            
                
                long tamanhoOriginalBytes = totalCaracteres;
                File arquivoOut = new File(arquivoSaida);
                long tamanhoComprimidoBytes = arquivoOut.length();
                long tamanhoComprimidoBits = tamanhoComprimidoBytes * 8;
                long tamanhoOriginalBits = tamanhoOriginalBytes * 8;

                double taxaCompressao = (1.0 - ((double) tamanhoComprimidoBytes / tamanhoOriginalBytes)) * 100.0;

                System.out.println("\nETAPA 5: Resumo da Compressao");
                System.out.println("Tamanho original.... " + tamanhoOriginalBits + " bits (" + tamanhoOriginalBytes + " bytes)");
                System.out.println("Tamanho comprimido. " + tamanhoComprimidoBits + " bits (" + tamanhoComprimidoBytes + " bytes)");
                System.out.printf("Taxa de compressao. %.2f%%\n", taxaCompressao);
                
                System.out.println("\nArquivo comprimido com sucesso para: " + arquivoSaida);


            } else if (operacao.equalsIgnoreCase("d")) {
                System.out.println("[INFO] Iniciando descompressão do arquivo: " + arquivoEntrada);

                try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(arquivoEntrada)));
                     BufferedOutputStream saidaTexto = new BufferedOutputStream(new FileOutputStream(arquivoSaida))) {
        
                    int totalCaracteres = dis.readInt();
                    No raiz = Decodificador.lerCabecalho(dis);
                    Decodificador.descomprimir(dis, raiz, saidaTexto, totalCaracteres);
                }

                System.out.println("Arquivo descomprimido com sucesso para: " + arquivoSaida);

            } else {
                System.out.println("Operação inválida! Use 'c' para comprimir ou 'd' para descomprimir.");
            }

        } catch (IOException e) {
            System.err.println("Erro durante a execução do processo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}