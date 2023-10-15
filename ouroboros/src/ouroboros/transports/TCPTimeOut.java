package ouroboros.transports;

import peersim.core.Node;

public class TCPTimeOut extends TCPEvent {

	public TCPTimeOut(Node src, Node src2, int pid) {
		super(src, src2, pid);
	}
	
}
