package com.josephndavis.chatterbox.client.controllers;

import com.josephndavis.chatterbox.client.ClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.josephndavis.chatterbox.server.Message;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
	
	@FXML private TextArea chatTxtArea;
	@FXML private TextField msgBox;
	@FXML private Button sendBtn;
	@FXML private ListView userList;
	@FXML private Label header;
	@FXML private Button viewHistoryBtn;
	
	public ListView getUserList() {
		return userList;
	}
	
	public void handleSendBtnAction() {
		if (msgBox.getText().equals("")) {
			chatTxtArea.appendText("Message field cannot be empty\n");
		} else {
			Message message = new Message((String)userList.getSelectionModel().getSelectedItem(), header.getText(), "msg", msgBox.getText());
			ClientMain.getLoginController().getClient().send(message);
			msgBox.setText("");
		}
	}

	public void addMessage(String text) {
		chatTxtArea.appendText(text + "\n");
	}
	
	public void setHeader(String text) {
		header.setText(text);
	}

	public void addUser(String user) {
		userList.getItems().add(user);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ClientMain.setChatController(this);
		userList.getItems().add("All");
		userList.getSelectionModel().select(0);
		userList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}

	public void handleViewHistoryBtnAction() throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/history.fxml"));
		Stage stage = new Stage();
		stage.setTitle("Chat Application - History");
		stage.setScene(new Scene(root, 600, 400));
		stage.show();
		ClientMain.getHistoryController().retrieveHistory((String)userList.getSelectionModel().getSelectedItem());
	}
}
