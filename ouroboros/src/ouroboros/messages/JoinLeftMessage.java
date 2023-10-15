package ouroboros.messages;

import peersim.core.Node;

public class JoinLeftMessage {

	private Node node;
	
	public JoinLeftMessage(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
}
