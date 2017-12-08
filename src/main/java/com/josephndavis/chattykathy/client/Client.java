package com.josephndavis.chattykathy.client;

import server.Message;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private String username;
	
	public Client(String ipAddress, int port) throws IOException {
		Socket socket = new Socket(ipAddress, port);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public void run() {
		boolean keepRunning = true;
		while (keepRunning) {
			try {
				Message msg = (Message) in.readObject();
				
				if (msg.getType().equals("login")) {
					if (msg.getContents().equals("true")) {
						Platform.runLater(
								() -> {
									ClientMain.getLoginController().proceed(msg.getTo());
								}
						);
					} else {
						Platform.runLater(
								() -> {
									ClientMain.getLoginController().setMsgText("Login was unsuccessful");
								}
						);
					}
				} else if (msg.getType().equals("register")) {
					if (msg.getContents().equals("false")) {
						Platform.runLater(
								() -> {
									ClientMain.getLoginController().setMsgText("Register was unsuccessful");
								}
						);
					}
				} else if (msg.getType().equals("newUser")) {
					Platform.runLater(
							() -> {
								if (!msg.getContents().equals(ClientMain.getLoginController().getClient().getUsername())) {
									boolean exists = false;
									
									for (int i = 0; i < ClientMain.getChatController().getUserList().getItems().size(); i++) {
										if (ClientMain.getChatController().getUserList().getItems().get(i).equals(msg.getContents())) {
											exists = true;
											break;
										}
									}
									
									if (!exists) {
										ClientMain.getChatController().addUser(msg.getContents());
									}
								}
							}
					);
				} else if (msg.getType().equals("msg")) {
					if (msg.getTo().equals(ClientMain.getLoginController().getClient().getUsername())) {
						ClientMain.getChatController().addMessage("[ " + msg.getFrom() + " > Me ]: " + msg.getContents());
					} else {
						ClientMain.getChatController().addMessage("[ " + msg.getFrom() + " > " + msg.getTo() + " ]: " + msg.getContents());
					}
				} else if (msg.getType().equals("logout")) {
					Platform.runLater(() -> {
						ClientMain.getChatController().getUserList().getItems().remove(msg.getContents());
					});
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				keepRunning = false;
			}
		}
	}
	
	public void send(Message msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
