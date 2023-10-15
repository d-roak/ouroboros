package ouroboros.messages;

import peersim.core.Node;

public class DisconnectRightMessage extends DisconnectMessage {

	public DisconnectRightMessage(Node node, Node connect) {
		super(node, connect);
	}

}
