package ouroboros.controls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import ouroboros.Ouroboros;
import peersim.config.Configuration;
import peersim.core.*;
import peersim.util.RandPermutation;

public class NeighborsControl implements Control {

	private static final String PAR_PROT = "protocol";
	private static final String PAR_FILE = "filename";
	private final int protocolID;
	private final String fileName;
	private static int networkSize;
	FileOutputStream output = null;
	String write;
	File file;
	
	public NeighborsControl(String name) {
		this.protocolID = Configuration.lookupPid(Configuration.getString(name+"."+PAR_PROT));
		networkSize = Network.size();
		fileName = Configuration.getString(name+"."+PAR_FILE);
		
		try {
			file = new File(fileName);
			output = new FileOutputStream(file);
			if(!file.exists())
				file.createNewFile();
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean execute() {
		RandPermutation rp = new RandPermutation(CommonState.r);
		rp.reset(networkSize);
		Set<Node> temp = new TreeSet<Node>();
		for(int i = 0; i < networkSize; i++) {
			temp.add(Network.get(rp.next()));
		}
		for(Node n: temp) {
			try {
				if(((Ouroboros)n.getProtocol(protocolID)).lowerNeighbor()!=null){
					write = (System.currentTimeMillis()+" Overlay OPEN "+n.getID()+" to "+((Ouroboros)n.getProtocol(protocolID)).lowerNeighbor().getID()+"\n");
					byte[] w = write.getBytes();
					output.write(w);
				}
				if(((Ouroboros)n.getProtocol(protocolID)).higherNeighbor()!=null){
					write = (System.currentTimeMillis()+" Overlay OPEN "+n.getID()+" to "+((Ouroboros)n.getProtocol(protocolID)).higherNeighbor().getID()+"\n");
					byte[] w = write.getBytes();
					output.write(w);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
