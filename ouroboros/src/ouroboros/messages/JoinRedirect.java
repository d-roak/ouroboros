package ouroboros.messages;

import peersim.core.Node;

public class JoinRedirect {

	private Node node;
	
	public JoinRedirect(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
}
