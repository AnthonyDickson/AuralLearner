package cosc345.app.lib;

import java.util.Random;

/**
 * Collection of utility methods.
 */
public class Utilities {
    public static final Random random = new Random();

    /**
     * Generic function to indexOf the index of an element in an object .
     * Code from http://www.techiedelight.com/find-index-element-array-java/
     * @param target the item to search for
     * @param a the array to search.
     * @param <T> the type of the object array.
     * @return the index of the target if found, -1 otherwise.
     */
    public static<T> int indexOf(T target, T[] a)
    {
        for (int i = 0; i < a.length; i++) {
            if (target.equals(a[i])) {
                return i;
            }
        }

        return -1;
    }
}
