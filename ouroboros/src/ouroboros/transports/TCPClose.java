package ouroboros.transports;

import peersim.core.Node;

public class TCPClose extends TCPEvent {

	public TCPClose(Node src, Node dest, int dpid) {
		super(src, dest, dpid);
	}
	
}
