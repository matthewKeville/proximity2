package keville.util;

import java.util.Collection;
import java.util.LinkedList;

public class Iterables {

  public static <T> Collection<T> toList(Iterable<T> iterable) {
    Collection<T> collection = new LinkedList<T>();
    for ( T t : iterable ) {
      collection.add(t);
    }
    return collection;
  }

}
