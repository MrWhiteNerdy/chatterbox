package com.josephndavis.chatterbox.client.controllers;

import com.josephndavis.chatterbox.client.Client;
import com.josephndavis.chatterbox.client.ClientMain;
import com.josephndavis.chatterbox.server.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
	
	@FXML private Label msg;
	
	@FXML private TextField passwordFld;
	@FXML private TextField usernameFld;
	
	@FXML private TextField ipAddressFld;
	@FXML private TextField portFld;
	
	@FXML private Button registerBtn;
	@FXML private Button loginBtn;
	@FXML private Button connectBtn;
	
	private Client client;
	
	private Stage stage;
	
	public Client getClient() {
		return client;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setMsgText(String text) {
		msg.setText(text);
	}
	
	public void connect() {
		if (connectInfoNotFilledIn()) {
			msg.setText("IP address and port cannot be empty");
		} else {
			try {
				client = new Client(ipAddressFld.getText(), Integer.parseInt(portFld.getText()));
				Thread thread = new Thread(client);
				thread.start();
				
				ipAddressFld.setDisable(true);
				portFld.setDisable(true);
				usernameFld.setDisable(false);
				passwordFld.setDisable(false);
				
				connectBtn.setDisable(true);
				registerBtn.setDisable(false);
				loginBtn.setDisable(false);
			} catch (IOException e) {
				e.printStackTrace();
				msg.setText("Could not connect to server");
			}
		}
	}
	
	public void login() {
	    if (loginInfoNotFilledIn()) {
            msg.setText("Username and password cannot be empty");
        } else {
	    	client.send(new Message("server", usernameFld.getText(), "login", passwordFld.getText()));
        }
	}
	
	public void register() throws IOException {
        if (loginInfoNotFilledIn()) {
            msg.setText("Username and password cannot be empty");
        } else {
        	client.send(new Message("server", usernameFld.getText(), "register", passwordFld.getText()));
        }
	}
	
	public void proceed(String username) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/chat.fxml"));
			ClientMain.getLoginController().getClient().setUsername(username);
			ClientMain.getChatController().setHeader(username);
			Stage stage = new Stage();
			stage.setTitle("Chat Application - Chat");
			stage.setScene(new Scene(root, 600, 400));
			stage.show();
			this.stage.close();
			
			stage.setOnHiding(we -> {
				Platform.runLater(() -> {
					client.send(new Message("server", ClientMain.getLoginController().getClient().getUsername(), "logout", ""));
					System.exit(0);
				});
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean loginInfoNotFilledIn() {
	    return usernameFld.getText().equals("") || passwordFld.getText().equals("");
    }

    private boolean connectInfoNotFilledIn() {
	    return ipAddressFld.getText().equals("") || portFld.getText().equals("");
    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ClientMain.setLoginController(this);
	}
}
