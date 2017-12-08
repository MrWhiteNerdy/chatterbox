package server;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DBManager {
	
	private static Connection conn;
	
	private static Connection getConnection() {
		if (conn == null) {
			String url = "jdbc:mysql://localhost:3306/chat-app";
			String username = "scott";
			String password = "tiger";
			
			try {
				conn = DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return conn;
	}
	
	public static boolean findUser(String username, String password) {
		String sql = "SELECT * FROM users WHERE username = ?";
		
		try {
			PreparedStatement ps = getConnection().prepareStatement(sql);
			ps.setString(1, username);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				String hash = rs.getString("password");
				
				return checkPassword(password, hash);
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean registerUser(String username, String password) {
		String sql = "SELECT * FROM users WHERE username = ?";
		
		try {
			PreparedStatement ps = getConnection().prepareStatement(sql);
			ps.setString(1, username);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				return false;
			} else {
				sql = "INSERT INTO users (username, password) VALUES (?, ?)";
				ps = getConnection().prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, hashPassword(password));
				
				ps.execute();
				
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void insertMessage(Message msg) {
		String sql = "INSERT INTO messages (recipient, sender, contents) VALUES (?, ?, ?)";
		
		try {
			PreparedStatement ps = getConnection().prepareStatement(sql);
			ps.setString(1, msg.getTo());
			ps.setString(2, msg.getFrom());
			ps.setString(3, msg.getContents());
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ResultSet getMessages(String sender, String recipient) {
		String sql;
		PreparedStatement ps;
		
		try {
			if (recipient.equals("All")) {
				sql = "SELECT * FROM messages WHERE recipient = ?";
				ps = getConnection().prepareStatement(sql);
				ps.setString(1, recipient);
			} else {
				sql = "SELECT * FROM messages WHERE (recipient = ? AND sender = ?) OR (recipient = ? AND sender = ?)";
				ps = getConnection().prepareStatement(sql);
				ps.setString(1, recipient);
				ps.setString(2, sender);
				ps.setString(3, sender);
				ps.setString(4, recipient);
			}
			
			return ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void deleteMessages() {
		try {
			String sql = "DELETE FROM messages";
			PreparedStatement ps = getConnection().prepareStatement(sql);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String hashPassword(String password) {
		int workload = 12;
		String salt = BCrypt.gensalt(workload);
		
		return BCrypt.hashpw(password, salt);
	}
	
	private static boolean checkPassword(String password, String hash) {
		boolean passwordVerified;
		
		if (null == hash || !hash.startsWith("$2a$"))
			throw new IllegalArgumentException("Invalid hash provided for comparison");
		
		passwordVerified = BCrypt.checkpw(password, hash);
		
		return passwordVerified;
	}
	
}
