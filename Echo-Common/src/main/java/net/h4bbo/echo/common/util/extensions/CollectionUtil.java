package net.h4bbo.echo.common.util.extensions;

import java.util.*;

public class CollectionUtil {
    public static <T> T pickRandom(Iterable<T> source) {
        return pickRandom(source, 1).iterator().next();
    }

    public static <T> Iterable<T> pickRandom(Iterable<T> source, int count) {
        List<T> list = toList(source);
        Collections.shuffle(list);
        return list.subList(0, Math.min(count, list.size()));
    }

    public static <T> Iterable<T> shuffle(Iterable<T> source) {
        List<T> list = toList(source);
        Collections.shuffle(list);
        return list;
    }

    public static <T> List<T> getPage(List<T> list, int page, int pageSize) {
        int startIndex = page * pageSize;
        if (startIndex >= list.size()) {
            return new ArrayList<>();
        }
        int endIndex = Math.min(startIndex + pageSize, list.size());
        return list.subList(startIndex, endIndex);
    }

    public static <T> int countPages(List<T> list, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero.");
        }
        return ((list.size() - 1) / pageSize) + 1;
    }

    public static <T> List<T> create(T... values) {
        return Arrays.asList(values);
    }

    public static <T> List<List<T>> batch(List<T> originalList, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than zero.");
        }

        List<List<T>> listOfChunks = new ArrayList<>();
        for (int i = 0; i < originalList.size() / chunkSize; i++) {
            listOfChunks.add(originalList.subList(i * chunkSize, Math.min((i + 1) * chunkSize, originalList.size())));
        }

        int remainder = originalList.size() % chunkSize;
        if (remainder != 0) {
            listOfChunks.add(originalList.subList(originalList.size() - remainder, originalList.size()));
        }

        return listOfChunks;
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        if (iterable instanceof Collection<?>) {
            return new ArrayList<>((Collection<T>) iterable);
        } else {
            List<T> list = new ArrayList<>();
            for (T item : iterable) {
                list.add(item);
            }
            return list;
        }
    }
}