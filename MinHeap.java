
import java.util.ArrayList;

public class MinHeap {
    private ArrayList<No> arrayList;

    public MinHeap(ArrayList<No> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<No> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<No> arrayList) {
        this.arrayList = arrayList;
    }

    public No getLeftNodeFromPosition(Integer i){
        Integer pos = (2 * i) + 1;
        if(pos >= arrayList.size()){
            return null;
        }
        No leftNode = arrayList.get(pos);
        return leftNode;
    }

    public No getRightNodeFromPosition(Integer i){
        Integer pos = (2 * i) + 2;
        if(pos >= arrayList.size()){
            return null;
        }
        No rightNode = arrayList.get(pos);
        return rightNode;
    }

    public No getParentNodeFromPosition(Integer i){
        Integer pos = (i - 1) / 2;
        if(pos > arrayList.size()){
            return null;
        }
        No parentNode = arrayList.get(pos);
        return parentNode;
    }

    public void inserirNo(No no){
        arrayList.add(no);
        int pos = arrayList.size() - 1;

        while(pos > 0){
            int paiPos = (pos - 1) / 2;
            int frequenciaPai = arrayList.get(paiPos).getFrequencia();

            if(frequenciaPai > no.getFrequencia()){
                No noPai = arrayList.get(paiPos);
                arrayList.set(paiPos, no);
                arrayList.set(pos, noPai);
                pos = paiPos;
            } else{
                break;
            }
        }
    }

    // public No removeMinimo(){
    //     No noRemovido = arrayList.get(0);
    //     arrayList.set(0, arrayList.get(arrayList.size() - 1));
    //     //procuro o menor
    //     int pos = 0;
    //     int menorFreq = arrayList.get(pos).getFrequencia();
    //     for(int i = 0; i < Math.log(arrayList.size()) / Math.log(2); i++){
    //         posMenor = (posMenor * 2) + 1;
    //         menorFreq = arrayList.get(pos).getFrequencia(); 
    //     }
        
    //     //preparo para o mov
    //     No menor = arrayList.get(pos);
    //     No subs = arrayList.get(0);

    //     arrayList.set(0, menor);
    //     arrayList.set(pos, subs);
        
    //     return noRemovido;
    // }
}
