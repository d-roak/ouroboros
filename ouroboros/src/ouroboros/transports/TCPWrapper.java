package ouroboros.transports;

import peersim.core.Node;

public class TCPWrapper extends TCPEvent {

	private Object msg;
	
	public TCPWrapper(Node src, Node dest, Object msg, int pid) {
		super(src, dest, pid);
		this.msg = msg;
	}
	
	public Object getMessage() {
		return msg;
	}
	
}
