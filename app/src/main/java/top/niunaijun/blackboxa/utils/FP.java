package top.niunaijun.blackboxa.utils;

import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;

/**
 * Created by huluxia-wc on 2014/9/11.
 * <p>
 * A functional programming style utility
 */
public class FP {

    /*
     * Generic function object
     */

    public interface UnaryFunc<R, A> {
        R apply(A x);
    }

    public interface BinaryFunc<R, A, B> {
        R apply(A a, B b);
    }

    /* Predicate */
    public static abstract class Pred<A> implements UnaryFunc<Boolean, A> {
        @Override
        public Boolean apply(A x) {
            return pred(x);
        }

        public abstract boolean pred(A x);
    }

    /* Equality */
    public static abstract class Eq<A> implements BinaryFunc<Boolean, A, A> {
        @Override
        public Boolean apply(A x, A y) {
            return eq(x, y);
        }

        public abstract boolean eq(A x, A y);
    }

    public static class Tuple<A, B, C> {
        public A a;
        public B b;
        public C c;

        public Tuple(A x, B y, C z) {
            a = x;
            b = y;
            c = z;
        }
    }

    public static <A, B, C> Tuple<A, B, C> makeTuple(A a, B b, C c) {
        return new Tuple<A, B, C>(a, b, c);
    }

    public static <E> Pred<E> negate(final Pred<E> p) {
        return new Pred<E>() {
            @Override
            public boolean pred(E x) {
                return !p.pred(x);
            }
        };
    }

    public static int limit(int x, int low, int high) {
        return Math.min(Math.max(low, x), high);
    }

    public static int maximum(int... xs) {
        int m = Integer.MIN_VALUE;
        for (int x : xs)
            m = Math.max(m, x);
        return m;
    }

    /*
     * Functions over abstract list
     */

    /**
     * Find among a list by using predicate function p
     */
    public static <E> E find(Pred<E> p, List<E> xs) {
        if (!empty(xs))
            for (E x : xs)
                if (p.pred(x))
                    return x;
        return null;
    }

    /**
     * Find among a list for an element
     */
    public static <E> E find(final E x, List<E> xs) {
        return find(new Pred<E>() {
            @Override
            public boolean pred(E y) {
                return y.equals(x);
            }
        }, xs);
    }

    public static <E> int findIndex(Pred<E> p, List<E> xs) {
        int i, n = length(xs);
        for (i = 0; i < n && !p.pred(xs.get(i)); ++i) ;
        return i == n ? -1 : i;
    }

    /**
     * looks up a key in an association list (which is realized by pair)
     */
    public static <K, V> V lookup(K k, List<Pair<K, V>> xs) {
        if (!empty(xs))
            for (Pair<K, V> x : xs)
                if (k == x.first)
                    return x.second;
        return null;
    }

    public static <E> E lookup(int k, SparseArray<E> xs) {
        return FP.empty(xs) ? null : xs.get(k);
    }

    /**
     * Remove duplicated items by using a compare function, O(N^2) time bound
     */
    public static <E> List<E> nubBy(final Eq<E> cmp, List<E> xs) {
        List<E> ys = new ArrayList<E>();
        if (!empty(xs))
            for (final E x : xs) {
                if (find(new Pred<E>() {
                    @Override
                    public boolean pred(E y) {
                        return cmp.eq(x, y);
                    }
                }, ys) == null)
                    ys.add(x);
            }
        return ys;
    }

    /**
     * Remove duplicate items by using Object.equals(). O(N^2) time bound
     * The name nub means `essence'.
     */
    public static <E> List<E> nub(List<E> xs) {
        return nubBy(new Eq<E>() {
            @Override
            public boolean eq(E x, E y) {
                return y.equals(x);
            }
        }, xs);
    }

    /*
     * Auxiliary functions
     */

    /**
     * Test if a collection is either NIL or empty
     */
    public static boolean empty(Collection<?> xs) {
        return xs == null || xs.isEmpty();
    }

    /**
     * Test if an array is either NIL or empty
     */
    public static <T> boolean empty(T[] xs) {
        return xs == null || xs.length == 0;
    }

    public static boolean empty(SparseArray<?> xs) {
        return xs == null || xs.size() == 0;
    }

    public static boolean empty(SparseIntArray xs) {
        return xs == null || xs.size() == 0;
    }

    public static boolean empty(int[] xs) {
        return xs == null || xs.length == 0;
    }

    public static boolean empty(long[] xs) {
        return xs == null || xs.length == 0;
    }

    /**
     * Test if a abstract string is either NIL or empty
     */
    public static boolean empty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean empty(Map<?, ?> m) {
        return m == null || m.isEmpty();
    }

    /**
     * Test if a collection is either NIL or empty
     */
    public static boolean notEmpty(Collection<?> xs) {
        return xs != null && !xs.isEmpty();
    }

    /**
     * Test if an array is either NIL or empty
     */
    public static <T> boolean notEmpty(T[] xs) {
        return xs != null && xs.length > 0;
    }

    public static boolean notEmpty(SparseArray<?> xs) {
        return xs == null && xs.size() == 0;
    }

    public static boolean notEmpty(SparseIntArray xs) {
        return xs != null && xs.size() > 0;
    }

    public static boolean notEmpty(int[] xs) {
        return xs != null && xs.length > 0;
    }

    public static boolean notEmpty(long[] xs) {
        return xs != null && xs.length > 0;
    }

    /**
     * Test if a abstract string is either NIL or empty
     */
    public static boolean notEmpty(CharSequence s) {
        return s != null && s.length() > 0;
    }

    public static boolean notEmpty(Map<?, ?> m) {
        return m != null && !m.isEmpty();
    }

    public static String nonNull(String string) {
        return string == null ? "" : string;
    }


    /**
     * Safe return the size of a collection even it's NIL
     */
    public static int size(Collection<?> xs) {
        return xs == null ? 0 : xs.size();
    }

    public static int size(CharSequence s) {
        return s == null ? 0 : s.length();
    }

    public static <T> int size(T[] xs) {
        return xs == null ? 0 : xs.length;
    }

    public static int size(int[] xs) {
        return xs == null ? 0 : xs.length;
    }

    public static int size(long[] xs) {
        return xs == null ? 0 : xs.length;
    }

    public static int size(Map<?, ?> m) {
        return m == null ? 0 : m.size();
    }

    public static int size(SparseArray<?> xs) {
        return xs == null ? 0 : xs.size();
    }

    public static int size(SparseIntArray xs) {
        return xs == null ? 0 : xs.size();
    }

    /**
     * Defined just as alias of 'size'.
     */
    public static int length(Collection<?> xs) {
        return size(xs);
    }

    public static int length(CharSequence s) {
        return size(s);
    }

    public static <T> int length(T[] xs) {
        return size(xs);
    }

    public static int length(int[] xs) {
        return size(xs);
    }

    public static int length(Map<?, ?> m) {
        return size(m);
    }

    public static int length(SparseArray<?> xs) {
        return size(xs);
    }

    public static int length(SparseIntArray xs) {
        return size(xs);
    }

    public static <T> boolean elem(T x, T[] xs) {
        return !empty(xs) && Arrays.asList(xs).contains(x);
    }

    public static <T> boolean elem(T x, Collection<T> xs) {
        return !empty(xs) && xs.contains(x);
    }

    public static <T> void swap(List<T> xs, int i, int j) {
        T tmp = xs.get(i);
        xs.set(i, xs.get(j));
        xs.set(j, tmp);
    }

    public static <T> void swap(T[] xs, int i, int j) {
        T tmp = xs[i];
        xs[i] = xs[j];
        xs[j] = tmp;
    }

    public static <T> void shift(List<T> xs, int from, int to) {
        T tmp = xs.get(from);
        for (int d = from < to ? 1 : -1; from != to; from += d)
            xs.set(from, xs.get(from + d));
        xs.set(to, tmp);
    }

    public static <T> void shift(T[] xs, int from, int to) {
        T tmp = xs[from];
        for (int d = from < to ? 1 : -1; from != to; from += d)
            xs[from] = xs[from + d];
        xs[to] = tmp;
    }

    /**
     * Safe add element to a list even the list is empty
     */
    public static <E> List<E> add(List<E> xs, E x) {
        if (xs == null)
            xs = new ArrayList<E>();
        xs.add(x);
        return xs;
    }

    /**
     * Safe remove the first occurrent of x in xs
     */
    public static <E> List<E> delBy(final Eq<E> cmp, List<E> xs, E x) {
        int i, n = FP.length(xs);
        for (i = 0; i < n && !cmp.eq(xs.get(i), x); ++i) ;
        if (i < n)
            xs.remove(i);
        return xs;
    }

    public static <E> List<E> del(List<E> xs, E x) {
        return delBy(new Eq<E>() {
            @Override
            public boolean eq(E x, E y) {
                return FP.eq(x, y);
            }
        }, xs, x);
    }

    /**
     * span, applied to a predicate p and a list xs, returns a tuple where first
     * element is longest prefix (possibly empty) of xs of elements that satisfy
     * p and second element is the remainder of the list.
     */
    public static <E> Pair<List<E>, List<E>> span(Pred<E> p, List<E> xs) {
        return Pair.create(takeWhile(p, xs), dropWhile(p, xs));
    }

    /**
     * Safe take the first n elements from a list
     */
    public static <E> List<E> take(int n, List<E> xs) {
        List<E> ys = new ArrayList<E>();
        if (empty(xs) || n <= 0)
            return ys;
        ys.addAll(xs.subList(0, Math.min(n, length(xs))));
        return ys;
    }

    public static String take(int n, String s) {
        return s.substring(0, limit(n, 0, FP.length(s)));
    }

    public static <K, V> Map<K, V> take(int n, Map<K, V> xs) {
        Map<K, V> ys = new HashMap<K, V>();
        for (Entry<K, V> k : xs.entrySet())
            if (n-- > 0)
                ys.put(k.getKey(), k.getValue());
        return ys;
    }

    public static <E> List<E> takeWhile(Pred<E> p, List<E> xs) {
        int i, n = length(xs);
        for (i = 0; i < n && p.pred(xs.get(i)); ++i) ;
        return take(i, xs);
    }

    /**
     * Safe drop the first n elements from a list
     */
    public static <E> List<E> drop(int n, List<E> xs) {
        List<E> ys = new ArrayList<E>();
        if (xs == null || n > length(xs))
            return ys;
        ys.addAll(xs.subList(Math.max(0, n), length(xs)));
        return ys;
    }

    public static String drop(int n, String s) {
        if (s == null || n > length(s))
            return "";
        return s.substring(Math.max(0, n));
    }

    public static <E> List<E> dropWhile(Pred<E> p, List<E> xs) {
        int i, n = length(xs);
        for (i = 0; i < n && p.pred(xs.get(i)); ++i) ;
        return drop(n, xs);
    }

    public static <E> E head(LinkedList<E> xs) {
        return empty(xs) ? null : xs.element();
    }

    public static <E> LinkedList<E> tail(LinkedList<E> xs) {
        if (empty(xs)) return xs;
        LinkedList<E> ys = new LinkedList<E>(xs);
        ys.remove();
        return ys;
    }

    public static <E> LinkedList<E> cons(E x, LinkedList<E> xs) {
        xs = empty(xs) ? new LinkedList<E>() : xs;
        xs.addFirst(x);
        return xs;
    }

    public static <E> E first(List<E> xs) {
        return FP.empty(xs) ? null : xs.get(0);
    }

    public static <E> E second(List<E> xs) {
        return FP.size(xs) < 2 ? null : xs.get(1);
    }

    /**
     * Access the last element of a list
     */
    public static <E> E last(List<E> xs) {
        return FP.empty(xs) ? null : xs.get(FP.lastIndex(xs));
    }

    /**
     * Safe return the index of the last element even if the list is NIL
     *
     * @return -1 if the list is NIL
     */
    public static int lastIndex(List<?> xs) {
        return FP.empty(xs) ? -1 : xs.size() - 1;
    }

    public static <E> E first(Collection<E> xs) {
        if (empty(xs))
            return null;
        return xs.iterator().next();
    }

    /**
     * Safe convert a collection to list even it's NIL
     */
    public static <E> List<E> toList(Collection<? extends E> xs) {
        return empty(xs) ? new ArrayList<E>() : new ArrayList<E>(xs);
    }

    public static <T> List<T> toList(T x) {
        return Collections.singletonList(x);
    }

    /**
     * Safe convert an array to list even it's NIL
     */
    public static <T> List<T> toList(T[] xs) {
        List<T> ys = new ArrayList<T>();
        if (!empty(xs))
            for (T x : xs)
                ys.add(x);
        return ys;
    }

    public static List<Integer> toList(int[] xs) {
        List<Integer> ys = new ArrayList<Integer>();
        if (!empty(xs))
            for (int x : xs)
                ys.add(x);
        return ys;
    }

    public static List<Long> toList(long[] xs) {
        List<Long> ys = new ArrayList<Long>();
        if (!empty(xs))
            for (long x : xs)
                ys.add(x);
        return ys;
    }

    public static <E> List<Pair<Integer, E>> toList(SparseArray<E> xs) {
        List<Pair<Integer, E>> ys = new ArrayList<Pair<Integer, E>>();
        if (!empty(xs))
            for (int i = 0; i < xs.size(); ++i)
                ys.add(Pair.create(xs.keyAt(i), xs.valueAt(i)));
        return ys;
    }

    public static List<Pair<Integer, Integer>> toList(SparseIntArray xs) {
        List<Pair<Integer, Integer>> ys = new ArrayList<Pair<Integer, Integer>>();
        if (!empty(xs))
            for (int i = 0; i < xs.size(); ++i)
                ys.add(Pair.create(xs.keyAt(i), xs.valueAt(i)));
        return ys;
    }

    public static int[] toArray(List<Integer> xs) {
        int n = length(xs);
        int[] ys = new int[n];
        for (int i = 0; i < n; ++i)
            ys[i] = xs.get(i);
        return ys;
    }

    public static List<Integer> toIntegerList(List<Long> list) {
        if (list == null) {
            return null;
        } else {
            List<Integer> intList = new ArrayList<Integer>();
            for (Long value : list) {
                intList.add(value.intValue());
            }
            return intList;
        }
    }

    public static List<Long> toLongList(List<Integer> list) {
        if (list == null) {
            return null;
        } else {
            List<Long> intList = new ArrayList<Long>();
            for (Integer value : list) {
                intList.add(value.longValue());
            }
            return intList;
        }
    }

    /**
     * Safe reference to a list even it's NIL
     *
     * @return reference to the list if it's not NIL, or an empty list.
     */
    public static <E> List<E> ref(List<E> xs) {
        return xs == null ? new ArrayList<E>() : xs;
    }

    @SuppressWarnings({"unchecked"})
    public static <E> E[] ref(E[] xs) {
        return xs == null ? (E[]) new Object[]{} : xs;
    }

    public static int[] ref(int[] xs) {
        return xs == null ? new int[]{} : xs;
    }

    public static String ref(String s) {
        return s == null ? "" : s;
    }

    /**
     * Safe zipper
     */
    public static <A, B> List<Pair<A, B>> zip(List<A> as, List<B> bs) {
        List<Pair<A, B>> xs = new ArrayList<Pair<A, B>>();
        if (!empty(as) && !empty(bs)) {
            Iterator<A> a = as.iterator();
            Iterator<B> b = bs.iterator();
            while (a.hasNext() && b.hasNext())
                xs.add(Pair.create(a.next(), b.next()));
        }
        return xs;
    }

    /**
     * Safe equal predicate
     */
    public static boolean eq(Object a, Object b) {
        if (a == null && b == null)
            return true;
        else if (a == null)
            return false;
        else
            return a.equals(b);
    }

    public static boolean isPrefixOf(String prefix, String s) {
        if (empty(prefix))
            return true;
        if (empty(s))
            return false;
        return s.startsWith(prefix);
    }

    public static <E> boolean isPrefixOf(List<E> prefix, List<E> xs) {
        if (empty(prefix))
            return true;
        if (empty(xs))
            return false;
        return eq(prefix, take(length(prefix), xs));
    }

    @SuppressWarnings("unchecked")
    public static <T> void convert(T[] dst, Object[] src) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = (T) src;
        }
    }

    /**
     * Safe concatenation
     * Note that the first list is MUTATED if it isn't empty.
     */
    public static <T> List<T> concat(List<T> xs, List<T> ys) {
        List<T> zs = ref(xs);
        zs.addAll(ref(ys));
        return zs;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[] xs, T[] ys) {
        T[] zs = (T[]) new Object[length(xs) + length(ys)];
        int i = 0;
        for (T x : xs)
            zs[i++] = x;
        for (T y : ys)
            zs[i++] = y;
        return zs;
    }

    public static int[] concat(int[] xs, int[] ys) {
        int[] zs = new int[length(xs) + length(ys)];
        int i = 0;
        for (int x : xs)
            zs[i++] = x;
        for (int y : ys)
            zs[i++] = y;
        return zs;
    }

    /**
     * Safe union, O(N^2) time bound
     * Note that
     * 1. the first list is mutated if it isn't empty.
     * 2. Only the duplicated elements in the second list is removed, those duplicated ones in first is kept.
     * If the order needn't be kept, we strongly recommend to use set instead!
     */
    public static <T> List<T> unionBy(final Eq<T> cmp, List<T> xs, List<T> ys) {
        ys = ref(ys);
        if (empty(xs))
            return ys;
        for (T y : ys) {
            boolean e = false;
            for (T x : xs)
                if (cmp.eq(x, y)) {
                    e = true;
                    break;
                }
            if (!e)
                xs.add(y);
        }
        return xs;
    }

    public static <T> List<T> union(List<T> xs, List<T> ys) {
        return unionBy(new Eq<T>() {
            @Override
            public boolean eq(T x, T y) {
                return FP.eq(x, y);
            }
        }, xs, ys);
    }

    /**
     * list difference xs \\ ys
     * invariant: (xs ++ ys) \\ xs == ys
     */
    public static <T> List<T> diffBy(final Eq<T> eq, List<T> xs, List<T> ys) {
        List<T> zs = toList(xs);
        for (T y : ys)
            zs = delBy(eq, zs, y);
        return zs;
    }

    public static <T> List<T> diff(List<T> xs, List<T> ys) {
        return diffBy(new Eq<T>() {
            @Override
            public boolean eq(T x, T y) {
                return FP.eq(x, y);
            }
        }, xs, ys);
    }

    /**
     * mapping
     */
    public static <A, B> List<B> map(UnaryFunc<B, A> f, List<A> xs) {
        List<B> ys = new ArrayList<B>();
        for (A x : ref(xs))
            ys.add(f.apply(x));
        return ys;
    }

    /**
     * filtering
     */
    public static <E> List<E> filter(Pred<E> p, List<E> xs) {
        List<E> ys = new ArrayList<E>();
        for (E x : xs)
            if (p.pred(x))
                ys.add(x);
        return ys;
    }

    /**
     * folding left
     */
    public static <S, E> S fold(BinaryFunc<S, S, E> f, S s, Collection<E> xs) {
        if (!empty(xs))
            for (E x : xs)
                s = f.apply(s, x);
        return s;
    }

    /**
     * ordered insertion.
     * O(\lg N) algorithm as it uses binary search
     * Please ensure the list support random access, so that the binary search make sense.
     */
    public static <E> List<E> insert(Comparator<E> cmp, E x, List<E> xs) {
        int pos = Collections.binarySearch(xs, x, cmp);
        pos = (pos < 0) ? -pos - 1 : pos;
        xs.add(-pos - 1, x);
        return xs;
    }

    /**
     * A wrapper to java.util.Collections.sort().
     * for chained style usage.
     */
    public static <E> List<E> sort(Comparator<E> cmp, List<E> xs) {
        xs = ref(xs);
        try {
            Collections.sort(xs, cmp);
        } catch (Exception e) {
            //YLog.error(FP.class, "Failed to sort %s for %s", xs, e);
        }
        return xs;
    }

    public static int sum(Integer[] xs) {
        int n = 0;
        for (int x : xs)
            n += x;
        return n;
    }

    public static long sum(Long[] xs) {
        long n = 0;
        for (long x : xs)
            n += x;
        return n;
    }

    public static int sum(List<Integer> xs) {
        int n = 0;
        for (int x : xs)
            n += x;
        return n;
    }

    public static long sum(List<Long> xs, Long l) {
        long n = 0;
        for (long x : xs)
            n += x;
        return n;
    }

    public static int ord(boolean x) {
        return x ? 1 : 0;
    }

    public static int ord(Integer x) {
        return x == null ? 0 : x;
    }

    public static <E> List<E> replicate(int n, E x) {
        List<E> xs = new ArrayList<E>();
        while (n-- > 0)
            xs.add(x);
        return xs;
    }

    public static <E> List<E> replicate(int n, Callable<E> gen) {
        List<E> xs = new ArrayList<E>();
        try {
            while (n-- > 0)
                xs.add(gen.call());
        } catch (Exception e) {
        }
        return xs;
    }

    /**
     * Tree based Map (ordable)
     */
    public static class M {
        public static <K, V> List<Pair<K, V>> toList(Map<K, V> m) {
            List<Pair<K, V>> xs = new ArrayList<Pair<K, V>>();
            if (!empty(m))
                for (Entry<K, V> e : m.entrySet())
                    xs.add(Pair.create(e.getKey(), e.getValue()));
            return xs;
        }


        public static <K extends Comparable<K>, V> Map<K, V> fromList(List<Pair<K, V>> xs) {
            Map<K, V> m = new TreeMap<K, V>();
            if (!empty(xs))
                for (Pair<K, V> p : xs)
                    m.put(p.first, p.second);
            return m;
        }

        public static <V> Map<Integer, V> fromList(SparseArray<V> xs) {
            Map<Integer, V> m = new TreeMap<Integer, V>();
            if (!empty(xs))
                for (int i = 0; i < xs.size(); ++i)
                    m.put(xs.keyAt(i), xs.valueAt(i));
            return m;
        }

        public static <V> List<V> values(SparseArray<V> m) {
            List<V> xs = new ArrayList<V>();
            int i, n = size(m);
            for (i = 0; i < n; ++i)
                xs.add(m.valueAt(i));
            return xs;
        }
    }
}
