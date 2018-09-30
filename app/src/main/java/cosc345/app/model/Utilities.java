package cosc345.app.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Collection of utility methods.
 */
public class Utilities {
    public static final Random random = new Random();

    /**
     * Generic function to indexOf the index of an element in an object .
     * Code from http://www.techiedelight.com/find-index-element-array-java/
     *
     * @param target the item to search for
     * @param a      the array to search.
     * @param <T>    the type of the object array.
     * @return the index of the target if found, -1 otherwise.
     */
    public static <T> int indexOf(T target, T[] a) {
        for (int i = 0; i < a.length; i++) {
            if (target.equals(a[i])) {
                return i;
            }
        }

        return -1;
    }


    /**
     * Restrict the value to specified range, more specifically puts the given value into the range
     * [min, max].
     *
     * @param x   the value to clamp.
     * @param min the minimum value  that <code>x</code> can be.
     * @param max the maximum value that <code>x</code> can be.
     * @return <code>x</code> clamped to the range [<code>min</code>, <code>max</code>].
     */
    public static int clamp(int x, int min, int max) {
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return x;
        }
    }

    /**
     * Restrict the value to specified range, more specifically puts the given value into the range
     * [min, max].
     *
     * @param x   the value to clamp.
     * @param min the minimum value  that <code>x</code> can be.
     * @param max the maximum value that <code>x</code> can be.
     * @return <code>x</code> clamped to the range [<code>min</code>, <code>max</code>].
     */
    public static double clamp(double x, double min, double max) {
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return x;
        }
    }

    /**
     * Calculate the standard deviation of the numbers in the given list.
     *
     * Code pinched from https://www.programiz.com/java-programming/examples/standard-deviation
     *
     * @return the standard deviation of the numbers in the given list.
     */
    public static double stddev(ArrayList<Double> a) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = a.size();

        for(double num : a) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: a) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }

    /**
     * Calculate the mean of the numbers in the given list.
     *
     * Code pinched from https://www.programiz.com/java-programming/examples/standard-deviation
     *
     * @return the mean of the numbers in the given list.
     */
    public static double mean(ArrayList<Double> a) {
        double sum = 0;

        for (double v : a) {
            sum += v;
        }

        return sum / a.size();
    }
}
