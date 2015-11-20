package util;

import java.io.Serializable;

public class Packet implements Serializable {

	private static long serialVersionUID = 1L;
	private PacketType type;
	private Object contents;
	
	public Packet(PacketType type, Object contents) {
		this.type = type;
		this.setContents(contents);
	}
	
	public PacketType getType() {
		return type;
	}
	public void setType(PacketType type) {
		this.type = type;
	}

	public Object getContents() {
		return contents;
	}

	public void setContents(Object contents) {
		this.contents = contents;
	}
	
}