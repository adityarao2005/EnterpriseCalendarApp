package model;

// Checks whether two items are identical
public interface Analogous<T extends Analogous<T>> {

	boolean identical(T other);
}
