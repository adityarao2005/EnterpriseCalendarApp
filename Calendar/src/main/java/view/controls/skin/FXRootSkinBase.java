/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * Skin base for all controls adapting FXML with an fx:root
 * @author Raos
 * @param <E> - the control class that this is the skin for
 * @param <ROOT> - the root node class
 */
public abstract class FXRootSkinBase<E extends Control, ROOT extends Node> extends SkinBase<E> implements Initializable {

	/**
	 * Constructs the FXRootSkinBase class
	 * @param e - the control that the skin is for
	 * @param fxml - the location of the fxml file
	 * @param rootSupplier - the supplier of the root node object
	 */
	@SafeVarargs
	public FXRootSkinBase(E e, URL fxml, Supplier<ROOT> rootSupplier, Pair<String, Object>... resources) {
		super(e);
		FXMLLoader loader = new FXMLLoader(fxml);
		loader.setController(this);
		loader.setRoot(rootSupplier.get());
		loader.setResources(new ObjectResourceBundle(resources));
		try {
			this.getChildren().add(loader.<ROOT>load());
		} catch (IOException ex) {
			Logger.getLogger(FXRootSkinBase.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private static class ObjectResourceBundle extends ResourceBundle {
		private Map<String, Object> map = new HashMap<>();
		
		public ObjectResourceBundle(Pair<String, Object>[] pairs) {
			for (Pair<String, Object> pair : pairs)
				map.put(pair.getKey(), pair.getValue());
		}
		
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