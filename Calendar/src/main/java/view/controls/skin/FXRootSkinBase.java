package view.controls.skin;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SkinBase;
import javafx.util.Pair;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Control;

// Skin base for all controls adapting FXML with an fx:root
public abstract class FXRootSkinBase<E extends Control, ROOT extends Node> extends SkinBase<E>
		implements Initializable {

	// Constructs the FXRootSkinBase class
	@SafeVarargs
	public FXRootSkinBase(E e, URL fxml, Supplier<ROOT> rootSupplier, Pair<String, Object>... resources) {
		// Call super constructor
		super(e);
		// Loads the fxml
		FXMLLoader loader = new FXMLLoader(fxml);
		loader.setController(this);
		loader.setRoot(rootSupplier.get());
		loader.setResources(new ObjectResourceBundle(resources));
		// Add the loaded value to the children
		try {
			this.getChildren().add(loader.<ROOT>load());
		} catch (IOException ex) {
			Logger.getLogger(FXRootSkinBase.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// Create an object resource bundle in the case of loading resources
	private static class ObjectResourceBundle extends ResourceBundle {
		// Create a map
		private Map<String, Object> map = new HashMap<>();

		// Turn pairs to map
		public ObjectResourceBundle(Pair<String, Object>[] pairs) {
			for (Pair<String, Object> pair : pairs)
				map.put(pair.getKey(), pair.getValue());
		}

		// Override values
		@Override
		protected Object handleGetObject(String key) {
			return map.get(key);
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.enumeration(map.keySet());
		}

	}
}