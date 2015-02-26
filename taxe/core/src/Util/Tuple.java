package Util;

import java.util.ArrayList;
import java.util.List;

public class Tuple<T, U> {
    private final T val1;
    private final U val2;

    public Tuple(T val1, U val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    public T getFirst() {
        return val1;
    }

    public U getSecond() {
        return val2;
    }

    public static <T, U> ArrayList<T> getFirstsFromList(List<Tuple<T, U>> list) {
        ArrayList<T> al = new ArrayList<T>();
        for (Tuple<T, U> t : list) {
            al.add(t.getFirst());
        }
        return al;
    }

}
