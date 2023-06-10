package model;

public interface Analogous<T extends Analogous<T>> {

	boolean identical(T other);
}
