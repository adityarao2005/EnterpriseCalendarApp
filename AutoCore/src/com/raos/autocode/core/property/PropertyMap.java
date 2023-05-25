package com.raos.autocode.core.property;

public interface PropertyMap {

	void registerProperty(String name, Class<?> clazz);

	<T> Property<T> getProperty(String name);

	<T> T getValue(String name);

	<T> void setValue(String name, T value);

}
