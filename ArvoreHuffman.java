public class ArvoreHuffman {

    public No construirArvore(MinHeap minHeap) {
        while (minHeap.getArrayList().size() > 1) {
            No esquerdo = minHeap.removeMinimo();
            No direito = minHeap.removeMinimo();
            
            int somaFrequencias = esquerdo.getFrequencia() + direito.getFrequencia();
            
            No noPai = new No('\0', somaFrequencias);
            noPai.setEsquerda(esquerdo);
            noPai.setDireita(direito);

            minHeap.inserirNo(noPai);
        }
        
        return minHeap.removeMinimo();
    }
}