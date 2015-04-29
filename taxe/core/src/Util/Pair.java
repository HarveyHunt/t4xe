package Util;

import java.util.ArrayList;
import java.util.List;

public class Pair<T, U> {
    private final T val1;
    private final U val2;

    public Pair(T val1, U val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    public static <T, U> ArrayList<T> getFirstsFromList(List<Pair<T, U>> list) {
        ArrayList<T> al = new ArrayList<T>();
        for (Pair<T, U> t : list) {
            al.add(t.getFirst());
        }
        return al;
    }

    public T getFirst() {
        return val1;
    }

    public U getSecond() {
        return val2;
    }

}
