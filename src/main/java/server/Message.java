package server;

import java.io.Serializable;

public class Message implements Serializable {
	
	private String to;
	private String from;
	private String type;
	private String contents;
	
	public Message(String to, String from, String type, String contents) {
		this.to = to;
		this.from = from;
		this.type = type;
		this.contents = contents;
	}
	
	public String getTo() {
		return to;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getType() {
		return type;
	}
	
	public String getContents() {
		return contents;
	}
}
