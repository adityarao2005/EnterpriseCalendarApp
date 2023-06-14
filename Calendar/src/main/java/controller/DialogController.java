package controller;

// DialogController interface - For all subclasses which are going to be displayed through a dialog and be run by Application.getApplication().dialog method must inherit this interface
// This is a controller for FXML controllers
public interface DialogController<R> {

	// Retrieves the result of the dialog
	R getResult();
}
