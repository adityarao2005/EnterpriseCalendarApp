package com.raos.autocode.core.property;

public interface Pointer<T> extends ReadableProperty<T>, WritableProperty<T> {

	// The equivalence of a property is that the value that the property has
	// is the same as the value of the other property
	default boolean equals(Property<T> other) {
		// Checks that the value of this property is the same as the value of the other
		// property
		return this.get().equals(other.get());
	}

}
