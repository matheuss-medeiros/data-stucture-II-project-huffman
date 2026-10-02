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
                System.out.println("[ETAPA 1] Analisando frequências do arquivo...");
                Codificador codificador = new Codificador();
                int frequencias[] = codificador.contarFrequencias(arquivoEntrada);

                System.out.println("[ETAPA 2 e 3] Construindo Min-Heap e Árvore de Huffman...");
                int totalCaracteres = 0;
                MinHeap minHeap = new MinHeap(new ArrayList<>());
                for(int i = 0; i<frequencias.length; i++){
                    if(frequencias[i] > 0){
                        minHeap.inserirNo(new No((char)i, frequencias[i]));
                        totalCaracteres += frequencias[i];
                    }
                }

                ArvoreHuffman arvore = new ArvoreHuffman();
                No raiz = arvore.construirArvore(minHeap);

                System.out.println("[ETAPA 4 e 5] Gerando códigos e escrevendo arquivo comprimido...");

                codificador.gerarTabela(raiz);
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