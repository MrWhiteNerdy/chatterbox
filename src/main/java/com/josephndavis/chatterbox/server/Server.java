package com.josephndavis.chatterbox.server;

import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class Server implements Runnable {
	
	private ServerThread[] clients;
	private ServerSocket serverSocket;
	private Thread thread = null;
	private int clientCount;
	private int port;
	
	public Server(int port) {
		clients = new ServerThread[50];
		this.port = port;
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			ServerMain.getServerController().appendStatusText("Server started on " + new Date());
			ServerMain.getServerController().appendStatusText("IP address: " + InetAddress.getLocalHost() + ". Port: " + port);
			ServerMain.getServerController().appendStatusText("Waiting for client connections...");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		while (thread != null) {
			try {
				addThread(serverSocket.accept());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
		}
		thread.start();
	}
	
	public void stop() {
		if (thread != null) {
			thread = null;
		}
	}
	
	private void addThread(Socket socket) {
		if (clientCount < clients.length) {
			ServerMain.getServerController().appendStatusText("Client accepted: " + socket.getPort());
			clients[clientCount] = new ServerThread(this, socket);
			
			try {
				clients[clientCount].open();
				Thread thread = new Thread(clients[clientCount]);
				thread.start();
				clientCount++;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Platform.runLater(
					() -> {
						ServerMain.getServerController().setClientCount(String.valueOf(clientCount));
					}
			);
		} else {
			ServerMain.getServerController().appendStatusText("Client refused. Maximum of " + clients.length + " clients reached");
		}
	}
	
	public synchronized void remove(int id) {
		int pos = findClient(id);
		if (pos >= 0) {
			ServerThread toTerminate = clients[pos];
			ServerMain.getServerController().appendStatusText("Removing client: " + id);
			if (pos < clientCount - 1) {
				System.arraycopy(clients, pos + 1, clients, pos + 1 - 1, clientCount - (pos + 1));
			}
			
			clientCount--;
			
			Platform.runLater(
					() -> {
						ServerMain.getServerController().setClientCount(String.valueOf(clientCount));
					}
			);
			
			try {
				toTerminate.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void handle(int id, Message msg) {
		if (msg.getType().equals("login")) {
			if (findUserThread(msg.getFrom()) == null) {
				if (DBManager.findUser(msg.getFrom(), msg.getContents())) {
					clients[findClient(id)].setUsername(msg.getFrom());
					clients[findClient(id)].send(new Message(msg.getFrom(), "server", "login", "true"));
					announce("newUser", "server", msg.getFrom());
					sendUserList(msg.getFrom());
				} else {
					clients[findClient(id)].send(new Message(msg.getFrom(), "server", "login", "false"));
				}
			} else {
				clients[findClient(id)].send(new Message(msg.getFrom(), "server", "login", "false"));
			}
		} else if (msg.getType().equals("register")) {
			if (findUserThread(msg.getFrom()) == null) {
				if (DBManager.registerUser(msg.getFrom(), msg.getContents())) {
					clients[findClient(id)].setUsername(msg.getFrom());
					clients[findClient(id)].send(new Message(msg.getFrom(), "server", "register", "true"));
					clients[findClient(id)].send(new Message(msg.getFrom(), "server", "login", "true"));
				} else {
					clients[findClient(id)].send(new Message(msg.getFrom(), "server", "register", "false"));
				}
			} else {
				clients[findClient(id)].send(new Message(msg.getFrom(), "server", "register", "false"));
			}
		} else if (msg.getType().equals("msg")) {
			DBManager.insertMessage(msg);
			if (msg.getTo().equals("All")) {
				announce("msg", msg.getFrom(), msg.getContents());
			} else {
				findUserThread(msg.getTo()).send(new Message(msg.getTo(), msg.getFrom(), msg.getType(), msg.getContents()));
				clients[findClient(id)].send(new Message(msg.getTo(), msg.getFrom(), msg.getType(), msg.getContents()));
			}
		} else if (msg.getType().equals("logout")) {
			remove(id);
			announce("logout", "server", msg.getFrom());
		}
	}
	
	private void announce(String type, String from, String content) {
		Message msg = new Message("All", from, type, content);

		for (int i = 0; i < clientCount; i++) {
			clients[i].send(msg);
		}
	}
	
	private void sendUserList(String to) {
		for (int i = 0; i < clientCount; i++) {
			findUserThread(to).send(new Message(to, "server", "newUser", clients[i].getUsername()));
		}
	}
	
	private int findClient(int id) {
		for (int i = 0; i < clientCount; i++) {
			if (clients[i].getID() == id) {
				return i;
			}
		}
		
		return -1;
	}
	
	private ServerThread findUserThread(String user) {
		for (int i = 0; i < clientCount; i++) {
			if (clients[i].getUsername().equals(user)) {
				return clients[i];
			}
		}
		
		return null;
	}
	
}

class ServerThread extends Thread {
	
	private Server server;
	private Socket socket;
	private int id;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String username;
	
	public ServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		id = socket.getPort();
		username = "";
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void send(Message message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Message message = (Message) in.readObject();
				server.handle(id, message);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				server.remove(id);
			}
		}
	}
	
	public void open() throws IOException {
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public void close() throws IOException {
		if (socket != null) {
			socket.close();
		}
		
		if (in != null) {
			in.close();
		}
		
		if (out != null) {
			out.close();
		}
	}
	
}
