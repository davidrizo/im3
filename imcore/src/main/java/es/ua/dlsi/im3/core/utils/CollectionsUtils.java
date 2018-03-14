package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.List;

/**
 * @autor drizo
 */
public class CollectionsUtils {
    public static <T> void replace(List<T> list, T oldElement, T newElement) throws IM3Exception {
        int index = list.indexOf(oldElement);
        if (index < 0) {
            throw new IM3Exception("Element to be replaced '" + oldElement + "' cannot be found in list");
        }
        list.remove(index);
        list.add(index, newElement);
    }
}
