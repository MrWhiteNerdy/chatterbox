package server;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

	public TextArea statusTxtArea;
	public TextField portFld;
	public Button startBtn;
	public Label msg;
	public Label clientCount;
	
	public void handleStartBtnAction(MouseEvent mouseEvent) {
		if (portFld.getText().equals("")) {
			msg.setText("Port cannot be empty");
		} else {
			msg.setText("");
			startBtn.setDisable(true);
			ServerMain.startServer(Integer.parseInt(portFld.getText()));
		}
	}

	public void appendStatusText(String text) {
		statusTxtArea.appendText(text + "\n");
	}
	
	public void setClientCount(String count) {
		clientCount.setText(count);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ServerMain.setServerController(this);
	}
}
