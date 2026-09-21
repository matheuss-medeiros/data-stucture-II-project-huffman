public class No implements Comparable<No>{
    private Character caractere;
    private Integer frequencia;
    private No esquerda, direita; 

    public No(char caractere, No direita, No esquerda, int frequencia) {
        this.caractere = caractere;
        this.direita = direita;
        this.esquerda = esquerda;
        this.frequencia = frequencia;
    }

    public char getCaractere() {
        return caractere;
    }

    public void setCaractere(char caractere) {
        this.caractere = caractere;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(int frequencia) {
        this.frequencia = frequencia;
    }

    public No getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(No esquerda) {
        this.esquerda = esquerda;
    }

    public No getDireita() {
        return direita;
    }

    public void setDireita(No direita) {
        this.direita = direita;
    }

    @Override
    public int compareTo(No o) {
        return this.frequencia - o.frequencia;
    }
}