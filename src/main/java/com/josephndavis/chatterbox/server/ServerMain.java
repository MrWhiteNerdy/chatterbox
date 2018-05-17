package com.josephndavis.chatterbox.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application {
	
	private static ServerController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/server.fxml"));
        primaryStage.setTitle("Chat Application - Server");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        primaryStage.setOnHiding(e -> {
            Platform.runLater(() -> {
                System.exit(0);
            });
        });
    }
    
    public static void startServer(int port) {
	    Server server = new Server(port);
	    server.start();
    }
	
	public static ServerController getServerController() {
		return controller;
	}
	
	public static void setServerController(ServerController controller) {
		ServerMain.controller = controller;
	}

    public static void main(String[] args) {
        launch(args);
    }
}
