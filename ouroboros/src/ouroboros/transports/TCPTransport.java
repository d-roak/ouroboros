package ouroboros.transports;

import java.util.*;
import ouroboros.inet.*;
import peersim.config.Configuration;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;

public class TCPTransport implements EDProtocol, Transport, Cleanable {

	private static final String PROTOCOL = "protocol"; 
	
	private static String name;
	private static int myPid;
	private int ouroPid;
	private Map<Node, Set<Node>> connections;
	
	public TCPTransport(String prefix) {
		name = prefix;
		ouroPid = Configuration.lookupPid(Configuration.getString(name+"."+PROTOCOL));
		connections = new HashMap<Node, Set<Node>>();
		myPid = CommonState.getPid();
	}
	
	public Object clone() {
		TCPTransport t = new TCPTransport(name);
		t.connections = connections;
		return t;
	}
	
	@Override
	public void processEvent(Node dest, int pid, Object msg) {
		if (!(msg instanceof TCPEvent)) {
			System.err.println(name+": Received an "+msg.toString()+" message, dropping...");
			return;
		}
		
		Node src = ((TCPEvent) msg).getSrc();
		
		if (!(msg instanceof TCPOpen) && !connections.containsKey(src)){
			System.err.println("Something wrong with src node...");
			return;
		}
		
		if (msg instanceof TCPOpen){
			if(!connections.containsKey(src)){
				System.err.println("No src in connections");
				return;
			}
			connections.get(src).add(dest);
			connections.get(dest).add(src);
		}
		else if (msg instanceof TCPWrapper) {
			if(!connections.get(src).contains(dest)){
				System.err.println(name+": Trying to send a message with TCPClose...");
				return;
			}
			int rpid = ((TCPWrapper)msg).getPid();
			Object rmsg = ((TCPWrapper)msg).getMessage();
			((EDProtocol) dest.getProtocol(rpid)).processEvent(src, rpid, rmsg);
		}
		else if(msg instanceof TCPClose){
			if (connections.get(src).contains(dest))
				connections.remove(dest);
			int rpid = ((TCPEvent)msg).getPid();
			Object rmsg = new TCPClose(src, dest, pid);
			((EDProtocol) dest.getProtocol(rpid)).processEvent(src, rpid, rmsg);
		}
	}
	
	@Override
	public void onKill() {
		Node n = CommonState.getNode();
		for(Node dest: connections.get(n))
			delaySend(getLatency(n, dest), n, new TCPClose(n, dest, ouroPid), myPid);
	}

	@Override
	public void send(Node src, Node dest, Object msg, int pid) {
		if(!connections.containsKey(src))
			connections.put(src, new TreeSet<Node>());
		if(!connections.containsKey(dest))
			connections.put(dest, new TreeSet<Node>());
				
		if (!dest.isUp()) {
			if(connections.get(src).contains(dest))
				connections.get(src).remove(dest);
			this.delaySend(getLatency(src, dest), src, new TCPTimeOut(dest, src, pid), pid);
			return;
		}

		if (!connections.get(src).contains(dest))
			this.delaySend(getLatency(src, dest)*(3/4), dest, new TCPOpen(src, dest, pid), myPid);
		
		this.delaySend(getLatency(src, dest), dest, new TCPWrapper(src, dest, msg, pid), myPid);
	}

	@Override
	public long getLatency(Node src, Node dest) {
		return Oracle.perfectUtility(src, dest);
	}
	
	private void delaySend(long delay, Node dest, Object msg, int pid) {
		EDSimulator.add(delay, msg, dest, pid);
	}

}
