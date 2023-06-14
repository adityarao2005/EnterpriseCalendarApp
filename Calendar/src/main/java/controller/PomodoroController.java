package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class PomodoroController implements DialogController<String> {
	@FXML
	private AnchorPane finishedScreen;

	@FXML
	private AnchorPane workingScreen;

	@FXML
	private Label timeLabel;

	@FXML
	private BorderPane container;

	@FXML
	private Spinner<Integer> workDuration;

	@FXML
	private Spinner<Integer> breakDuration;

	@FXML
	private DialogPane dialogPane;

	@FXML
	private ButtonType closeButton;

	private Node closeButtonNode;

	@FXML
	private void initialize() {
		closeButtonNode = dialogPane.lookupButton(closeButton);
	}

	@FXML
	private void stopAction() {

	}

	@FXML
	private void skipAction() {

	}

	@FXML
	private void finishAction() {

	}

	@FXML
	private void onBegin() {

	}

	@Override
	public String getResult() {
		return "ok'd";
	}

}
