package cosc345.app.lib;

/**
 * Collection of utility methods.
 */
public class Utilities {
    /**
     * Generic function to find the index of an element in an object .
     * Code from http://www.techiedelight.com/find-index-element-array-java/
     * @param a the array to search.
     * @param target the item to search for
     * @param <T> the type of the object array.
     * @return the index of the target if found, -1 otherwise.
     */
    public static<T> int find(T[] a, T target)
    {
        for (int i = 0; i < a.length; i++)
            if (target.equals(a[i]))
                return i;

        return -1;
    }
}
