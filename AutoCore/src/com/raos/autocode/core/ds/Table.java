package com.raos.autocode.core.ds;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

// Represents a table of values
// TODO: Needs more implementation
// Right now
// The table provides insertion
// Sorting, filtering, limiting table required
// Cloning
// Union, Intersection, Disjoint
// TODO: Make a LINQ library that supports these operations
public class Table implements Iterable<Tuple> {
	// Template for table
	private Class<?>[] template;
	// Rows of the table
	private List<Tuple> rows;

	// Constructor
	private Table(Class<?>[] template) {
		this.template = template;
	}

	// Factory method
	public static Table createTable(Class<?>... classes) {
		return new Table(classes);
	}

	// Returns a tuple for value
	public Tuple getRow(int row) {
		return rows.get(row);
	}

	// Adds row in the form of a tuple
	public void addRow(Tuple tuple) {
		// First security check
		if (template.length != tuple.length())
			throw new RuntimeException("Incompatible Lengths");

		// Second security check, type check
		for (int i = 0; i < template.length; i++) {
			if (!template[i].isInstance(tuple.get(i))) {
				throw new RuntimeException(
						String.format("Incompatible Types: %s, %s", template[i].getClass(), tuple.get(i).getClass()));
			}
		}

		// Adds tuple
		rows.add(tuple);
	}

	// Adds a row
	public void addRow(Object... objects) {
		addRow(new Tuple(objects));
	}

	// Update the row
	public void updateRow(Tuple tuple, int row) {
		// First security check
		if (template.length != tuple.length())
			throw new RuntimeException("Incompatible Lengths");

		// Second security check, type check
		for (int i = 0; i < template.length; i++) {
			if (!template[i].isInstance(tuple.get(i))) {
				throw new RuntimeException(
						String.format("Incompatible Types: %s, %s", template[i].getClass(), tuple.get(i).getClass()));
			}
		}

		// Updates the row
		rows.set(row, tuple);
	}

	// Removes the row
	public void removeRow(int row) {
		rows.remove(row);
	}

	// Gets a column in the form of an array
	@SuppressWarnings("unchecked")
	public <E> E[] getColumn(int column) {
		return rows.stream().map(t -> t.get(column)).toArray(i -> (E[]) Array.newInstance(template[column], i));
	}

	// Allows for foreach loop
	@Override
	public Iterator<Tuple> iterator() {
		return rows.iterator();
	}

	// Returns the templates
	public List<Class<?>> getTemplate() {
		return Collections.unmodifiableList(Arrays.asList(template));
	}

	// Sorts the table
	public void sortBy(int column, boolean descending) {
		// Creates a comparator based on the column
		Comparator<Tuple> comparator = Comparator.comparing(t -> t.get(column));

		// Reverse the order if descending is requested
		if (descending)
			comparator = comparator.reversed();

		// Sort the list
		Collections.sort(rows, comparator);
	}

	// Filters the tuple based on predicate
	public Table filter(Predicate<Tuple> filter) {
		// Creates a new table
		Table table = Table.createTable(template);

		// Adds all the accepted tuples
		for (Tuple tuple : this)
			if (filter.test(tuple))
				table.addRow(tuple);

		// Returns the new table
		return table;

	}

	// Limits to the first n rows
	public Table limit(int n) {
		// Creates a new table
		Table table = Table.createTable(template);

		// Adds all the accepted tuples
		for (int i = 0; i < n; i++)
			table.addRow(rows.get(i));

		// Returns the new table
		return table;

	}

	// Only allows distinct values
	public Table distinct() {
		// Creates a new table
		Table table = Table.createTable(template);

		Set<Tuple> set = new HashSet<>();
		// Adds all the accepted tuples
		for (Tuple tuple : this)
			if (set.add(tuple))
				table.addRow(tuple);

		// Returns the new table
		return table;

	}

	// Selects only certain columns
	// For now it is integer
	// TODO: Later make this generic to any key value and replace indexers with maps
	public Table select(int... columns) {
		// Creates a new template based on the columns selected
		Class<?>[] template = new Class<?>[columns.length];

		// Copy template values
		for (int i = 0; i < columns.length; i++)
			template[i] = this.template[i];

		Table table = new Table(template);

		// Loop through all the rows and create copies of the tuples
		for (Tuple tuple : this) {
			// Creates a new object array
			Object[] objects = new Object[columns.length];

			// Copy tuple values
			for (int i = 0; i < columns.length; i++)
				objects[i] = tuple.get(i);

			// Add the row to the table
			table.addRow(objects);
		}

		// Return the table
		return table;
	}

	// Unions the two tables
	public static Table unionAll(Table first, Table second) {
		// First check if the datatypes are the same
		if (!Arrays.equals(first.template, second.template))
			throw new RuntimeException("Unable to union: Types are not the same");

		// Creates a new table
		Table table = Table.createTable(first.template);

		// Add all the values
		table.rows.addAll(first.rows);

		table.rows.addAll(second.rows);

		// Return table
		return table;
	}

	// Finds the intersection between two tables
	public static Table intersectionAll(Table first, Table second) {
		// First check if the datatypes are the same
		if (!Arrays.equals(first.template, second.template))
			throw new RuntimeException("Unable to intersect: Types are not the same");

		// Creates a new table
		Table table = Table.createTable(first.template);

		// Removes duplicates

		// Get all values from the first table
		for (Tuple tuple : first.rows)
			if (second.rows.contains(tuple))
				table.addRow(tuple);

		// Get all values from the second table
		for (Tuple tuple : second.rows)
			if (first.rows.contains(tuple))
				table.addRow(tuple);

		// Return table
		return table;
	}

	// Gets all values of the first table except the values that are in the second
	// table
	public static Table except(Table first, Table second) {
		// First check if the datatypes are the same
		if (!Arrays.equals(first.template, second.template))
			throw new RuntimeException("Unable to intersect: Types are not the same");

		// Creates a new table
		Table table = Table.createTable(first.template);

		// Get all values from the first table
		for (Tuple tuple : first.rows)
			if (!second.rows.contains(tuple))
				table.addRow(tuple);

		// Return table
		return table;
	}

	// Unions the two tables
	public static Table union(Table first, Table second) {
		// Returns a distinct unionall
		return unionAll(first, second).distinct();
	}

	// Finds the intersection between two tables
	public static Table intersection(Table first, Table second) {
		// Returns a distinct intersectionall
		return intersection(first, second).distinct();
	}

	// TODO: Add joins
	// Join - Joins both table columns to create one big table
	// Inner join - removes the column that doesnt intersect with the other column
	// Left Join - uses al values from the left and any rows from the right not
	// there replaced with NULL
	// Right Join - uses al values from the right and any rows from the left not
	// there replaced with NULL
	// Full Join - uses values from both and if any not available, represented by
	// null
}
