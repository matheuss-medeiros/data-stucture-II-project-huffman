import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Decodificador{

    public static No lerCabecalho(DataInputStream dis) throws IOException{
        boolean ehFolha = dis.readBoolean();

        if(ehFolha){
            char c = dis.readChar();
            return new No(c, 0);

        }else{
            No esq = lerCabecalho(dis);
            No dir = lerCabecalho(dis);
            No pai = new No('\0', 0);
            pai.setEsquerda(esq);
            pai.setDireita(dir);
            return pai;
        }
    }

    public static void descomprimir(BufferedInputStream entradaBits, No raiz, BufferedOutputStream saidaTexto, int totalChar) throws IOException{
        No atual = raiz;
        int charLidos = 0;
        int byteLido;

        while((byteLido = entradaBits.read()) != -1 && (charLidos < totalChar)){
            for(int i = 7; i >= 0; i--){
                if(charLidos >= totalChar) break;

                int bit = (byteLido >> i) & 1;

                if(bit == 0){
                    atual = atual.getEsquerda();
                }else{
                    atual = atual.getDireita();
                }

                if(atual.getDireita() == null && atual.getEsquerda() == null){
                    saidaTexto.write(atual.getCaractere());
                    charLidos++;
                    atual = raiz;
                }
            }
        }
        saidaTexto.flush();

    }
}
