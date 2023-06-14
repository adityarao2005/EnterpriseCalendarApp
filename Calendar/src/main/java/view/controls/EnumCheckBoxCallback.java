package view.controls;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.util.Callback;

// A callback for checkboxes of enums
public class EnumCheckBoxCallback<T extends Enum<T>> implements Callback<T, ObservableValue<Boolean>> {

	// Enum set to store the selected enums
	private EnumSet<T> enums;

	private Map<T, ObservableValue<Boolean>> selectedTypes = new HashMap<>();

	// Creates a callback based on the enum type
	@SuppressWarnings("unchecked")
	public EnumCheckBoxCallback(@NamedArg("enumType") String enumType) throws Exception {
		enums = EnumSet.noneOf((Class<T>) Class.forName(enumType));

		for (T param : EnumSet.allOf((Class<T>) Class.forName(enumType))) {

			// Create a boolean property which handles the delegation
			SimpleBooleanProperty observable = new SimpleBooleanProperty();

			// Add the listener
			observable.addListener((obs, wasSelected, isNowSelected) -> {
				if (isNowSelected)
					enums.add(param);
				else
					enums.remove(param);
			});

			selectedTypes.put(param, observable);
		}
	}

	// The callback method
	@Override
	public ObservableValue<Boolean> call(T param) {

		return selectedTypes.get(param);
	}

	// Get the enum class from the list
	public static Class<?> getEnumClass(ObservableList<?> list) {
		return list.isEmpty() ? null : list.get(0).getClass();
	}

	// Set the enum class from a list
	public static <T extends Enum<T>> void setEnumClass(ObservableList<? super T> list, Class<T> enumClass) {
		if (!enumClass.isEnum()) {
			throw new IllegalArgumentException(enumClass.getName() + " is not a enum type");
		}
		list.addAll(enumClass.getEnumConstants());
	}

	// Getters and setters
	public EnumSet<T> getEnums() {
		return enums;
	}

	public void setEnums(EnumSet<T> enums) {
		this.enums = enums;
	}

}
