package ouroboros.oracles;

import ouroboros.transports.*;
import peersim.core.*;

public class LatencyOracle {
	
	private Node s;
	private Node d1;
	private Node d2;
	private TCPTransport t;
	
	public LatencyOracle(Node src, Node dest1, Node dest2, TCPTransport trans) {
		s = src;
		d1 = dest1;
		d2 = dest2;
		t = trans;
	}
	
	public Node checkLatency() {
		long l1 = t.getLatency(s, d1);
		long l2 = t.getLatency(s, d2);
		if(l1<l2)
			return d1;
		else
			return d2;
	}
}
