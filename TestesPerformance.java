import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class TestesPerformance {

    public static void main(String[] args) {

        try {
            // 1. Testes de Tamanho (Análise de Performance - Tempo)
            System.out.println(">>> PARTE 1: TESTES DE TAMANHO (Crescimento Linear)");
            executarTesteCompleto("arquivo_1KB.txt", 1024, "comum");
            executarTesteCompleto("arquivo_100KB.txt", 100 * 1024, "comum");
            executarTesteCompleto("arquivo_1MB.txt", 1024 * 1024, "comum");
            executarTesteCompleto("arquivo_10MB.txt", 10 * 1024 * 1024, "comum");

            // 2. Testes de Perfil de Dados (Taxa de Compressão - Espaço)
            System.out.println("\n>>> PARTE 2: TESTES DE PERFIL DE DADOS (Taxas de Compressão)");
            // Arquivo muito repetitivo (ex: "AAAAA...") de 1MB
            executarTesteCompleto("arquivo_repetitivo_1MB.txt", 1024 * 1024, "repetitivo");
            
            // Arquivo com caracteres totalmente aleatórios de 1MB
            executarTesteCompleto("arquivo_aleatorio_1MB.txt", 1024 * 1024, "aleatorio");

            // 3. Teste com o arquivo oficial do professor
            System.out.println("\n>>> PARTE 3: TESTE COM O ARQUIVO OFICIAL DO PROFESSOR");
            executarTesteArquivoExistente("arq_de_teste.txt");

            System.out.println("\nTodos os testes foram concluídos com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro durante os testes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
     //Gera o arquivo falso, comprime, descomprime, mede os tempos e calcula a taxa.
    private static void executarTesteCompleto(String nomeArquivo, int tamanhoBytes, String tipo) throws IOException {
        System.out.println("------------------------------------------------------");
        System.out.println("Testando: " + nomeArquivo + " | Tipo: " + tipo + " | Tamanho: " + tamanhoBytes + " bytes");

        // 1. Gera o arquivo de entrada
        gerarArquivoFalso(nomeArquivo, tamanhoBytes, tipo);

        String arquivoComprimido = nomeArquivo + ".huff";
        String arquivoDescomprimido = "restaurado_" + nomeArquivo;

        // 2. Medir tempo de Compressão
        long tempoInicioComp = System.nanoTime();
        comprimir(nomeArquivo, arquivoComprimido);
        long tempoFimComp = System.nanoTime();
        long tempoCompressaoMs = (tempoFimComp - tempoInicioComp) / 1_000_000;

        // 3. Medir tempo de Descompressão
        long tempoInicioDesc = System.nanoTime();
        descomprimir(arquivoComprimido, arquivoDescomprimido);
        long tempoFimDesc = System.nanoTime();
        long tempoDescompressaoMs = (tempoFimDesc - tempoInicioDesc) / 1_000_000;

        // 4. Calcular Taxa de Compressão
        long tamanhoOriginal = Files.size(Paths.get(nomeArquivo));
        long tamanhoComp = Files.size(Paths.get(arquivoComprimido));
        double taxaCompressao = (1.0 - ((double) tamanhoComp / tamanhoOriginal)) * 100.0;

        // 5. Exibir Resultados
        System.out.println(" -> Tempo de Compressao   : " + tempoCompressaoMs + " ms");
        System.out.println(" -> Tempo de Descompressao: " + tempoDescompressaoMs + " ms");
        System.out.printf(" -> Tamanho Original      : %d bytes\n", tamanhoOriginal);
        System.out.printf(" -> Tamanho Comprimido    : %d bytes\n", tamanhoComp);
        System.out.printf(" -> Taxa de Compressao    : %.2f%%\n", taxaCompressao);
    }

    //Método para testar arquivos reais já existentes no disco (ex: arq_de_teste.txt)
    private static void executarTesteArquivoExistente(String nomeArquivo) throws IOException {
        System.out.println("------------------------------------------------------");
        System.out.println("Testando Arquivo Real: " + nomeArquivo);

        String arquivoComprimido = nomeArquivo + ".huff";
        String arquivoDescomprimido = "restaurado_" + nomeArquivo;

        // Medir Compressão
        long tempoInicioComp = System.nanoTime();
        comprimir(nomeArquivo, arquivoComprimido);
        long tempoFimComp = System.nanoTime();
        long tempoCompressaoMs = (tempoFimComp - tempoInicioComp) / 1_000_000;

        // Medir Descompressão
        long tempoInicioDesc = System.nanoTime();
        descomprimir(arquivoComprimido, arquivoDescomprimido);
        long tempoFimDesc = System.nanoTime();
        long tempoDescompressaoMs = (tempoFimDesc - tempoInicioDesc) / 1_000_000;

        // Calcular Taxa
        long tamanhoOriginal = Files.size(Paths.get(nomeArquivo));
        long tamanhoComp = Files.size(Paths.get(arquivoComprimido));
        double taxaCompressao = (1.0 - ((double) tamanhoComp / tamanhoOriginal)) * 100.0;

        System.out.println(" -> Tempo de Compressao   : " + tempoCompressaoMs + " ms");
        System.out.println(" -> Tempo de Descompressao: " + tempoDescompressaoMs + " ms");
        System.out.printf(" -> Tamanho Original      : %d bytes\n", tamanhoOriginal);
        System.out.printf(" -> Tamanho Comprimido    : %d bytes\n", tamanhoComp);
        System.out.printf(" -> Taxa de Compressao    : %.2f%%\n", taxaCompressao);
    }

    
    //Lógica de Compressão centralizada com base na sua Main_2.java
    private static void comprimir(String arquivoEntrada, String arquivoSaida) throws IOException {
        Codificador codificador = new Codificador();
        int frequencias[] = codificador.contarFrequencias(arquivoEntrada);

        int totalCaracteres = 0;
        MinHeap minHeap = new MinHeap(new ArrayList<>());
        for(int i = 0; i < frequencias.length; i++){
            if(frequencias[i] > 0){
                minHeap.inserirNo(new No((char)i, frequencias[i]));
                totalCaracteres += frequencias[i];
            }
        }

        ArvoreHuffman arvore = new ArvoreHuffman();
        No raiz = arvore.construirArvore(minHeap);
        codificador.gerarTabela(raiz);

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(arquivoSaida)))) {
            codificador.comprimirArquivo(arquivoEntrada, raiz, dos, totalCaracteres);
        }
    }

    
    //Lógica de Descompressão centralizada
    private static void descomprimir(String arquivoEntrada, String arquivoSaida) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(arquivoEntrada)));
             BufferedOutputStream saidaTexto = new BufferedOutputStream(new FileOutputStream(arquivoSaida))) {
            
            // Consumir a assinatura "HUFF" gerada pelo Codificador
            dis.readUTF();
            
            int totalCaracteres = dis.readInt();
            No raiz = Decodificador.lerCabecalho(dis);
            Decodificador.descomprimir(dis, raiz, saidaTexto, totalCaracteres);
        }
    }

    
    //Método auxiliar para gerar arquivos de teste baseados no tipo solicitado
    private static void gerarArquivoFalso(String nomeArquivo, int tamanhoBytes, String tipo) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(nomeArquivo))) {
            Random random = new Random();
            
            for (int i = 0; i < tamanhoBytes; i++) {
                if (tipo.equals("repetitivo")) {
                    bos.write('A');
                } else if (tipo.equals("aleatorio")) {
                    bos.write(random.nextInt(256));
                } else {
                    int charCode = random.nextInt(30);
                    if (charCode < 26) bos.write('a' + charCode);
                    else if (charCode < 29) bos.write(' ');
                    else bos.write('\n');
                }
            }
        }
    }
}