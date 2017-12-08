package com.josephndavis.chattykathy.client;

import com.josephndavis.chattykathy.client.controllers.ChatController;
import com.josephndavis.chattykathy.client.controllers.HistoryController;
import com.josephndavis.chattykathy.client.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {
	
	private static LoginController loginController;
	private static ChatController chatController;
	private static HistoryController historyController;
	
	public static LoginController getLoginController() {
		return loginController;
	}
	
	public static void setLoginController(LoginController loginController) {
		ClientMain.loginController = loginController;
	}
	
	public static ChatController getChatController() {
		return chatController;
	}
	
	public static void setChatController(ChatController chatController) {
		ClientMain.chatController = chatController;
	}

	public static HistoryController getHistoryController() {
		return historyController;
	}

	public static void setHistoryController(HistoryController historyController) {
		ClientMain.historyController = historyController;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
		primaryStage.setTitle("Chat Application - Welcome");
		primaryStage.setScene(new Scene(root, 600, 400));
		primaryStage.show();
		ClientMain.getLoginController().setStage(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
