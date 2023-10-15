package ouroboros.messages;

import peersim.core.*;

public class ConnectMessage {
	private Node node;
	
	public ConnectMessage(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
}
