package ouroboros.controls;

import ouroboros.Protocol;
import peersim.config.Configuration;
import peersim.core.*;

public class NodeInit implements Control {

	private static final String PAR_PROT = "protocol";
	private final int protocolID;
	private String name;
	private static int networkSize;
	
	public NodeInit(String name) {
		this.name = name;
		this.protocolID = Configuration.lookupPid(Configuration.getString(this.name+"."+PAR_PROT));
		NodeInit.networkSize = Network.size();
	}
	
	public boolean execute() {
		for(int i = 0; i < networkSize; i++) {
			((Protocol) Network.get(i).getProtocol(this.protocolID)).initializer(Network.get(i));
		}
		return false;
	}
}
