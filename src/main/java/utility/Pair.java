package utility;

public class Pair<T,V> {
    T t;
    V v;

    public Pair(){ }

    public Pair(T t, V v){
        this.t = t;
        this.v = v;

    }


    public void setT(T t) {
        this.t = t;
    }

    public void setV(V v) {
        this.v = v;
    }

    public T getT() {
        return t;
    }

    public V getV(){
        return v;
    }
}
