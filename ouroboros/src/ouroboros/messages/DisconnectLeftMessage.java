package ouroboros.messages;

import peersim.core.Node;

public class DisconnectLeftMessage extends DisconnectMessage {

	public DisconnectLeftMessage(Node node, Node connect) {
		super(node, connect);
	}

}
