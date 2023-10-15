package ouroboros.inet;

import java.io.*;
import java.util.*;

import peersim.config.Configuration;
import peersim.core.*;

/*******************************************************
 * This sets ups a static class which is used during 
 * simulation that is called Oracle.
 * 
 * This initialization also provides nodes with cost
 * information which is provided by a file that should contain
 * all pairs of nodes and theirs costs extracted from an 
 * implementation.
 *******************************************************/
public class InetTopologyInit implements Control {

//	PARAMETERS ID
	private static final String PAR_ORACLE = "oracle";
	private static final String PAR_ORA_PRECISION = "precision";
	private static final String PAR_ORA_ERROR = "error";
	private static final String PAR_INET_CONF_FILE = "inputfile";

//	PARAMETERS VARIABLES
	private final String oracleType;
	
//	Other variables
	private String name;
	private String inputFile;
	private int costs[];
	private int netSize;
	
	public InetTopologyInit(String name) {
		this.name = name;
		this.oracleType = Configuration.getString(this.name+"."+PAR_ORACLE);
		this.inputFile = Configuration.getString(this.name+"."+PAR_INET_CONF_FILE);
		this.netSize = Network.size();
		this.costs = new int[this.netSize * this.netSize];
		try {
			Oracle.setConfigurations(Configuration.getDouble(this.name+"."+PAR_ORA_PRECISION), Configuration.getDouble(this.name+"."+PAR_ORA_ERROR));
		} catch (Exception e) {
			//Do nothing this was an actual try...
		}
	}
	
	@SuppressWarnings("resource")
	public boolean execute() {
		Oracle.setUp(this.oracleType);
		
		System.err.println(new Date(System.currentTimeMillis()).toString() + ": start loading.");
		
		Scanner sc;
		try {
			sc = new Scanner(new File(this.inputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return true;
		}
		
		StringTokenizer st = null;
		int node1, node2, cost;
		long counter = 0;
		long fivePercent = ((this.netSize * this.netSize) - this.netSize) / 20;
		System.err.print("Complete: ");
		System.err.flush();
		
		while(sc.hasNext()) {
			st = new StringTokenizer(sc.nextLine());
			node1 = Integer.parseInt(st.nextToken());
			node2 = Integer.parseInt(st.nextToken());
			if(node1>=netSize)
				break;
			if(node2>=netSize)
				continue;
			
			cost = Integer.parseInt(st.nextToken());
			this.costs[(int) ((node1 * this.netSize) + node2)] = cost;
			this.costs[(int) ((node2 * this.netSize) + node1)] = cost;
			counter++;
			if(counter % fivePercent == 0) {
				System.err.print("|");
				System.err.flush();
			}
		}
		
		System.err.println();
		Oracle.loadCosts(this.costs);
		this.costs = null;
		System.err.println(new Date(System.currentTimeMillis()).toString() + ": load complete.");
		return false;
	}
	
}
