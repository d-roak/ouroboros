package ouroboros.messages;

import peersim.core.*;

public abstract class DisconnectMessage {
	
	private Node node;
	private Node connect;
	
	public DisconnectMessage(Node node, Node connect) {
		this.node = node;
		this.connect = connect;
	}
	
	public Node getNode() {
		return node;
	}
	
	public Node connect() {
		return connect;
	}
}
