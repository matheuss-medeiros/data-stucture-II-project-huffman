//Beatriz de Assis Siqueira, RA:10741570
//Daniel Arais Motta, RA:10419718
//Matheus Santos Medeiros, RA:10748040
//Pedro Araujo Botelho, RA:10738317

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class Codificador {
    private String[] tabelaCodigos = new String[256];

    public String[] getTabelaCodigos() {
        return tabelaCodigos;
    }

    public void gerarTabela(No raiz) {
        preencherTabela(raiz, "");
    }

    private void preencherTabela(No no, String caminho){
        // 1. Condição de parada (se o nó for nulo)
        if(no == null) return;
        
        // 2. Verificar se é folha (usando os getters que o Matheus fez)
        // Se for folha, guarda o 'caminho' no vetor usando o caractere do nó como índice
        if(no.getEsquerda() == null && no.getDireita() == null){
            tabelaCodigos[no.getCaractere()] = caminho;
            return;
        } else {
        // 3. Se não for folha, descer na árvore:
        // Chamada recursiva para a esquerda (adicionando "0" ao caminho)
        // Chamada recursiva para a direita (adicionando "1" ao caminho)
            preencherTabela(no.getEsquerda(), caminho + "0");
            preencherTabela(no.getDireita(), caminho + "1");
        }
    }

    public void escreverCabecalho(No no, DataOutputStream dos) throws IOException {
        // 1. Condição de parada (se o nó for nulo)
        if(no == null) return;

        // 2. Verifica se o nó é folha
        if(no.getEsquerda() == null && no.getDireita() == null){
            // Se for folha:
            //   - Grava 'true' usando dos.writeBoolean(true)
            dos.writeBoolean(true);
            //   - Grava o caractere usando dos.writeChar(no.getCaractere())
            dos.writeChar(no.getCaractere());
        } else {
            // 3. Se não for folha:
            //   - Grava 'false' usando dos.writeBoolean(false)
            dos.writeBoolean(false);
            //   - Chama a recursividade para a esquerda
            escreverCabecalho(no.getEsquerda(), dos);
            //   - Chama a recursividade para a direita
            escreverCabecalho(no.getDireita(), dos);
        }
    }

    public void comprimirArquivo(String arquivoEntrada, No raiz, DataOutputStream dos, int totalCaracteres) throws IOException {
        //Grava assinatura de segurança no ficheiro
        dos.writeUTF("HUFF");
        
        // grava o total de caracteres 
        dos.writeInt(totalCaracteres);

        // grava a arvore de Huffman
        escreverCabecalho(raiz, dos);

        //acumular bits
        int bufferBits = 0;
        int quantidadeBits = 0;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(arquivoEntrada))) {
            int byteLido;
            while ((byteLido = bis.read()) != -1) {
                char caractere = (char) byteLido;
                String codigoHuf = tabelaCodigos[caractere];

                if(codigoHuf == null) continue; // Ignora caracteres que não estão na tabela

                for (int i = 0; i < codigoHuf.length(); i++) {
                    char bitChar = codigoHuf.charAt(i);
                
                    // Desloca o buffer 1 casa para a esquerda (abre espaço para o novo bit)
                    bufferBits = bufferBits << 1; 
                    
                    // Se o bit da string for '1', aplicamos um OR lógico com 1
                    if (bitChar == '1') {
                        bufferBits = bufferBits | 1;
                    }
                    
                    quantidadeBits++;
                    
                    // 5. Se o buffer completou 8 bits (1 byte), gravamos no arquivo e zeramos
                    if (quantidadeBits == 8) {
                        dos.write(bufferBits);
                        bufferBits = 0;
                        quantidadeBits = 0;
                    }
                }
            }
        }

        if (quantidadeBits > 0) {
            int casasParaDeslocar = 8 - quantidadeBits;
            bufferBits = bufferBits << casasParaDeslocar;
            dos.write(bufferBits);
        }
    
        dos.flush();
    }

    public int[] contarFrequencias(String arquivoEntrada) throws IOException {
        int[] frequencias = new int[256];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(arquivoEntrada))) {
            int byteLido;
            while ((byteLido = bis.read()) != -1) {
                frequencias[byteLido]++;
            }
        }      
        return frequencias;
    }   
}
