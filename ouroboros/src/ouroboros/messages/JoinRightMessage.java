package ouroboros.messages;

import peersim.core.Node;

public class JoinRightMessage {

	private Node node;
	
	public JoinRightMessage(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
}
