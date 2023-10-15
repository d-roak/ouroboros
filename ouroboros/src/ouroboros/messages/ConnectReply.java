package ouroboros.messages;

import peersim.core.*;

public class ConnectReply {
	private Node node;
	
	public ConnectReply(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
}
