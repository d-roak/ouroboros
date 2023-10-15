package ouroboros.messages;

import peersim.core.*;

public class JoinMessage {

	private Node node;
	
	public JoinMessage(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
}
