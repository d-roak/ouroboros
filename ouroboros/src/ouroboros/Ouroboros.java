package ouroboros;

import java.util.*;

import ouroboros.events.Event;
import ouroboros.messages.*;
import ouroboros.transports.*;
import peersim.cdsim.*;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.util.RandPermutation;

public class Ouroboros implements Protocol, EDProtocol, CDProtocol, Linkable {
	/******************************
	 * Configuration Constants
	 ******************************/
	private static final String NEIGHBOR_SIZE = "neighborsize";
	private static final String POINTERS_SIZE = "pointersize";
	private static final String PASSIVE_SIZE = "passivesize";
	private static final String SAMPLE_SIZE = "samplesize";
	private static final String TRANSPORT_PROTOCOL = "transport";
	private static final int RATIO = 2;

	/******************************
	 * Variables
	 ******************************/
	private boolean activate = false;
	private int myPid;
	private String name;
	private Node node;
	private boolean processing;
	private List<Event> queue;
	private int transport;

	/******************************
	 * Configuration Variables
	 ******************************/
	private static int neighborSize;
	private static int pointerSize;
	private static int passiveSize;
	private static int sampleSize;

	/******************************
	 * Active View
	 ******************************/
	private List<Node> lowerNeighbors;
	private List<Node> higherNeighbors;
	private Set<Node> pointers;

	/******************************
	 * Passive View
	 ******************************/
	private Set<Node> passive;

	/******************************
	 * Constructor
	 * 
	 * @throws Exception
	 ******************************/
	public Ouroboros(String name) {
		processing = false;
		node = CommonState.getNode();

		// Configuration
		myPid = CommonState.getPid();
		this.name = name;
		transport = Configuration.lookupPid(Configuration.getString(name + "." + TRANSPORT_PROTOCOL));
		Ouroboros.neighborSize = Configuration.getInt(name + "." + NEIGHBOR_SIZE);
		Ouroboros.pointerSize = Configuration.getInt(name + "." + POINTERS_SIZE);
		Ouroboros.passiveSize = Configuration.getInt(name + "." + PASSIVE_SIZE);
		Ouroboros.sampleSize = Configuration.getInt(name + "." + SAMPLE_SIZE);

		// Init structs
		queue = new LinkedList<Event>();
		passive = new TreeSet<Node>();
		lowerNeighbors = new ArrayList<Node>(neighborSize);
		higherNeighbors = new ArrayList<Node>(neighborSize);
		pointers = new TreeSet<Node>();
	}

	/******************************
	 * Protocol Methods
	 ******************************/
	public void join(Node dest, int pid) {
		if (node.getID() == dest.getID())
			return;
		((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, dest, new JoinMessage(dest), pid);
	}

	public void joinLeft(Node dest, int pid) {
		if (node.getID() == dest.getID())
			return;
		((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, dest, new JoinLeftMessage(dest), pid);
	}

	public void joinRight(Node dest, int pid) {
		if (node.getID() == dest.getID())
			return;
		((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, dest, new JoinRightMessage(dest), pid);
	}

	public void connect(Node dest, int pid) {
		if (node.getID() == dest.getID())
			return;
		((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, dest, new ConnectMessage(dest), pid);
	}

	public void disconnectLeft(Node dest, Node connect, int pid) {
		if (node.getID() == dest.getID())
			return;
		((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, dest,
				new DisconnectLeftMessage(dest, connect), pid);
	}

	public void disconnectRight(Node dest, Node connect, int pid) {
		if (node.getID() == dest.getID())
			return;
		((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, dest,
				new DisconnectRightMessage(dest, connect), pid);
	}

	public void sendSample(Node dest, int pid) {
		Set<Node> sample = new TreeSet<Node>();
		// Add node
		sample.add(node);

		// Add neighbors
		if (!lowerNeighbors.isEmpty())
			sample.add(lowerNeighbors.get(0));
		if (!higherNeighbors.isEmpty())
			sample.add(higherNeighbors.get(0));

		// Add pointers
		Iterator<Node> it = pointers.iterator();
		Node n;
		while (it.hasNext() && sample.size() < sampleSize) {
			n = it.next();
			sample.add(n);
		}

		// Fill with passive
		List<Node> temp = new ArrayList<Node>();
		temp.addAll(passive);
		RandPermutation rp = new RandPermutation(CommonState.r);
		rp.reset(passive.size());
		while (!temp.isEmpty() && sample.size() < sampleSize && rp.hasNext()) {
			sample.add(temp.get(rp.next()));
		}
		((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, dest, new ShuffleMessage(sample), pid);
	}

	public void mergeSample(Set<Node> sample) {
		if (sample.isEmpty())
			return;

		List<Node> temp = new ArrayList<Node>();
		Object[] samp = sample.toArray();
		RandPermutation rp = new RandPermutation(CommonState.r);
		rp.reset(passive.size());
		temp.addAll(passive);
		passive.clear();
		while (passive.size() <= passiveSize / RATIO && rp.hasNext()) {
			passive.add(temp.get(rp.next()));
		}
		rp.reset(sample.size());
		while (passive.size() <= passiveSize && rp.hasNext()) {
			passive.add((Node) samp[rp.next()]);
		}
	}

	// --------------------------
	// - Active View Methods
	// --------------------------

	public boolean addPointer(Node node) {
		if (pointers.size() < pointerSize)
			return pointers.add(node);
		else
			return false;
	}

	@Override
	public boolean addNeighbor(Node node) {
		connect(node, myPid);
		return false;
	}

	public boolean addNeighbor(Node node, int pid) {
		if (lowerNeighbors.contains(node) || higherNeighbors.contains(node))
			return false;
		if (lowerNeighbors.isEmpty() && higherNeighbors.isEmpty()) {
			lowerNeighbors.add(0, node);
			higherNeighbors.add(0, node);
			return true;
		}

		addPassive(node);
		return false;
	}

	public boolean addLeft(Node node, int pid) {
		if (lowerNeighbors.contains(node) || higherNeighbors.contains(node))
			return false;
		if (!lowerNeighbors.isEmpty()) {
			addPassive(lowerNeighbors.get(0));
			disconnectLeft(lowerNeighbors.get(0), node, myPid);
		}
		lowerNeighbors.add(0, node);
		return true;
	}

	public boolean addRight(Node node, int pid) {
		if (lowerNeighbors.contains(node) || higherNeighbors.contains(node))
			return false;
		if (!higherNeighbors.isEmpty()) {
			addPassive(higherNeighbors.get(0));
			disconnectRight(higherNeighbors.get(0), node, myPid);
		}
		higherNeighbors.add(0, node);
		return true;
	}

	@Override
	public boolean contains(Node node) {
		if (lowerNeighbors.get(0).equals(node) || higherNeighbors.get(0).equals(node) || pointers.contains(node))
			return true;
		return false;
	}

	@Override
	public Node getNeighbor(int i) {
		List<Node> temp = new ArrayList<Node>();
		if (!lowerNeighbors.isEmpty())
			temp.add(lowerNeighbors.get(0));
		if (!higherNeighbors.isEmpty())
			temp.add(higherNeighbors.get(0));
		temp.addAll(pointers);
		Node n = temp.get(i);
		temp = null;
		return n;
	}

	@Override
	public int degree() {
		if (!lowerNeighbors.isEmpty() && !higherNeighbors.isEmpty())
			return pointers.size() + 2;
		else if (!lowerNeighbors.isEmpty() || !higherNeighbors.isEmpty())
			return pointers.size() + 1;
		else
			return pointers.size();
	}

	public Node removeActive(Node node) {
		if (!lowerNeighbors.isEmpty() && lowerNeighbors.get(0).equals(node))
			lowerNeighbors = new ArrayList<Node>(neighborSize);
		else if (!higherNeighbors.isEmpty() && higherNeighbors.get(0).equals(node))
			higherNeighbors = new ArrayList<Node>(neighborSize);
		else if (pointers.contains(node)) {
			pointers.remove(node);
			return node;
		}
		return null;
	}

	// --------------------------
	// - Passive View Methods
	// --------------------------
	public boolean passiveFull() {
		return passiveSize <= passive.size() ? true : false;
	}

	public void addPassive(Node node) {
		if (passive.size() < passiveSize)
			passive.add(node);
		else {
			removePassive();
			passive.add(node);
		}
	}

	public Node removePassive() {
		Object[] temp = passive.toArray();
		Object n = temp[CommonState.r.nextInt(passive.size())];
		passive.remove((Node) n);
		return (Node) n;
	}

	public Node removePassive(Node node) {
		if (passive.contains(node))
			passive.remove(node);
		return node;
	}

	// --------------------------
	// - Event Processor
	// --------------------------

	@Override
	public void processEvent(Node node, int pid, Object event) {
		if(!processing)
			processing = true;
		else 
			queue.add(new Event(node, this.node, pid, event));
		
		if (event instanceof JoinMessage) {
			if (addNeighbor(node, pid))
				((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node, new JoinOk(), pid);
			else if (higherNeighbors.isEmpty())
				return;
			else if (lowerNeighbors.isEmpty())
				((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node,
						new JoinRedirect(higherNeighbors.get(0)), pid);
			else if (((OuroborosNode) node).between(this.node, higherNeighbors.get(0))) {
				((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node, new JoinOkLeft(),
						pid);
				addRight(node, pid);
			} else {
				if (this.node.getID() > node.getID())
					((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node,
							new JoinRedirect(lowerNeighbors.get(0)), pid);
				else
					((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node,
							new JoinRedirect(higherNeighbors.get(0)), pid);
			}
		} else if (event instanceof JoinLeftMessage) {
			addRight(node, pid);
			((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node, new JoinOkLeft(), pid);
		} else if (event instanceof JoinRightMessage) {
			addLeft(node, pid);
			((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node, new JoinOkRight(), pid);
		} else if (event instanceof JoinOk) {
			addNeighbor(node, pid);
		} else if (event instanceof JoinOkLeft) {
			addLeft(node, pid);
		} else if (event instanceof JoinOkRight) {
			addRight(node, pid);
		} else if (event instanceof JoinRedirect) {
			Node n = ((JoinRedirect) event).getNode();
			join(n, pid);
		} else if (event instanceof DisconnectLeftMessage) {
			higherNeighbors.clear();
			Node n = ((DisconnectMessage) event).connect();
			joinRight(n, pid);
		} else if (event instanceof DisconnectRightMessage) {
			lowerNeighbors.clear();
			Node n = ((DisconnectMessage) event).connect();
			joinLeft(n, pid);
		} else if (event instanceof ConnectMessage) {
			if (addPointer(node))
				((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node,
						new ConnectReply(this.node), pid);
		} else if (event instanceof ConnectReply) {
			addPointer(node);
		} else if (event instanceof ShuffleMessage) {
			((TCPTransport) CommonState.getNode().getProtocol(transport)).send(this.node, node,
					new ShuffleReply(((ShuffleMessage) event).getSample()), pid);
			mergeSample(((ShuffleMessage) event).getSample());
		} else if (event instanceof ShuffleReply)
			mergeSample(((ShuffleReply) event).getSample());
		else if (event instanceof TCPClose)
			recover(node, pid);
		else if (event instanceof TCPTimeOut)
			recover(node, pid);
		
		processing = false;
		if(!queue.isEmpty()) {
			Event e = queue.remove(0);
			processEvent(e.getSrc(), e.getPid(), e.getEvent());
		}	
	}

	// --------------------------
	// - Other Methods
	// --------------------------

	public Object clone() {
		Object o = new Ouroboros(name);
		((Ouroboros) o).transport = this.transport;
		return o;
	}

	@Override
	public void nextCycle(Node node, int pid) {
		if (!activate)
			return;

		if (degree() != 0) {
			RandPermutation rp = new RandPermutation(CommonState.r);
			rp.reset(degree());
			sendSample(getNeighbor(rp.next()), pid);
		}
		
		
		refreshPassive();
		refreshNeighbors(pid);
		//refreshPointers(pid);
	}

	public void refreshPassive() {
		Set<Node> temp = new TreeSet<Node>();
		for (Node n : passive) {
			if ((!lowerNeighbors.isEmpty() && n.equals(lowerNeighbors.get(0)))
					|| (!higherNeighbors.isEmpty() && n.equals(higherNeighbors.get(0))) || pointers.contains(n))
				temp.add(n);
		}
		for (Node n : temp) {
			passive.remove(n);
		}
		passive.remove(node);
	}

	public void refreshNeighbors(int pid) {
		Set<Node> temp = new TreeSet<Node>();
		temp.addAll(passive);
		temp.addAll(pointers);
		for (Node n : temp) {
			if (higherNeighbors.isEmpty() || lowerNeighbors.isEmpty())
				;
			else if ((lowerNeighbors.get(0).getID() < node.getID() && ((OuroborosNode) n).between(lowerNeighbors.get(0), node))
					|| (lowerNeighbors.get(0).getID() > node.getID() && n.getID() < node.getID())) {
				((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, n, new JoinRightMessage(n),
						pid);
				break;
			} else if ((higherNeighbors.get(0).getID() > node.getID() && ((OuroborosNode) n).between(node, higherNeighbors.get(0)))
					|| (higherNeighbors.get(0).getID() < node.getID() && n.getID() > node.getID())) {
				((TCPTransport) CommonState.getNode().getProtocol(transport)).send(node, n, new JoinLeftMessage(n),
						pid);
				break;
			}
		}

	}

	public void refreshPointers(int pid) {
		Set<Node> temp = new TreeSet<Node>();
		temp.addAll(passive);
		Iterator<Node> it = temp.iterator();
		while (it.hasNext()) {
			Node no = it.next();
			if (!lowerNeighbors.contains(no) && !higherNeighbors.contains(no)) {
				connect(no, pid);
			}
		}
	}

	@Override
	public void onKill() {
		node = null;
		passive = null;
		lowerNeighbors = null;
		higherNeighbors = null;
		pointers = null;
	}

	public void recover(Node node, int pid) {
		if (pointers.contains(node))
			connect(removePassive(), pid);
		removeActive(node);
	}

	@Override
	public void initializer(Node node) {
		activate = true;
		join(node, myPid);
	}

	@Override
	public void pack() {
		// Not necessary
	}

	@Override
	public int getViewSize() {
		return degree();
	}

	public Node lowerNeighbor() {
		if (lowerNeighbors.isEmpty())
			return null;
		return lowerNeighbors.get(0);
	}

	public Node higherNeighbor() {
		if (higherNeighbors.isEmpty())
			return null;
		return higherNeighbors.get(0);
	}
}
