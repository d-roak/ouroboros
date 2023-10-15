package ouroboros.transports;

import peersim.core.Node;

public class TCPOpen extends TCPEvent {

	public TCPOpen(Node src, Node dest, int pid) {
		super(src, dest, pid);
	}
	
}
