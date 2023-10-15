package ouroboros.observers;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ouroboros.Protocol;
import peersim.config.Configuration;
import peersim.config.MissingParameterException;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.util.IncrementalFreq;
import peersim.util.IncrementalStats;

public class InDegree implements Control {

	static private int networkSize;
	
	private static final String PAR_PROT = "protocol";
	private static final String PAR_FILE = "filename";
	private static final String PAR_MANY_FILES = "manyfiles";
	private static final String PAR_TARGET_CYLCES = "x";
	
	/**
	 * Selects a method to use when printing results. Three methods are known:
	 * "stats" will use {@link IncrementalStats#toString}. "freq" will
	 * use {@link IncrementalFreq#print}. "list" will print the
	 * degrees of the sample nodes one by one in one line, separated by spaces.
	 * Default is "stats".
	 * @config
	 */
	private static final String PAR_METHOD = "method";
	
	/*** Parameters Vars ***/
	private final int membershipProtocol;
	private String log_file = "";
	private final String method;
	private boolean manyFiles;
	
	private final String name;
	
	/*** Internal state vars ***/
	private int[] inDegrees;

	/*** Output control variables ***/
	private boolean usingFile;
	PrintStream output = null;
	private int cycle;
	private ArrayList<Integer> targetCycles;
	
	public InDegree(String name) {
		this.name = name;
		this.cycle = 0;
		this.membershipProtocol = Configuration.lookupPid(Configuration.getString(this.name+"."+PAR_PROT));
		this.method = Configuration.getString(this.name+"."+PAR_METHOD);
		
		if(Configuration.contains(this.name+"."+PAR_TARGET_CYLCES)) {
			this.targetCycles = new ArrayList<Integer>();
			StringTokenizer st = new StringTokenizer(Configuration.getString(this.name+"."+PAR_TARGET_CYLCES)," ");
			while(st.hasMoreTokens()) {
				this.targetCycles.add(Integer.parseInt(st.nextToken()));
			}
		} else 
			this.targetCycles = null;
		
		try {
			Configuration.getString(this.name+"."+PAR_MANY_FILES);
			this.manyFiles = true;
		} catch (MissingParameterException e) {
			this.manyFiles = false;
		}
		
		try {
			this.log_file = Configuration.getString(this.name+"."+PAR_FILE);
		} catch (MissingParameterException e) {
			this.log_file = "";
		}
		
		if(this.log_file.compareTo("") == 0) {
			this.output = System.out;
			this.usingFile = false;
		} else {
			if(this.manyFiles)
				this.usingFile = true;
			else {
				try {
					this.output = new PrintStream(this.log_file+".txt");
				} catch (FileNotFoundException e) {
					System.err.println("Can't open log file...");
					e.printStackTrace();
					System.exit(1);
				}
				this.usingFile = false;
			}
		}
			
		InDegree.networkSize = Network.size();
		this.inDegrees = new int[InDegree.networkSize];
	}
	
	//Helper, reset Stats :D
	private void resetStats() {
		for(int i = 0; i < InDegree.networkSize; i++)
			this.inDegrees[i] = 0;
	}
	
	private Object makeStats() {
		if(this.method.equals("stats")) {
			IncrementalStats stats = new IncrementalStats();
			for(int i = 0; i < InDegree.networkSize; i++)
				stats.add(this.inDegrees[i]);
			return stats;
		} else if (this.method.equals("freq")) {
			IncrementalFreq stats = new IncrementalFreq();
			for(int i = 0; i < InDegree.networkSize; i++)
				stats.add(this.inDegrees[i]);
			return stats;
		} else if (this.method.equals("list")) {
			StringBuffer stats = new StringBuffer();
			for(int i = 0; i < InDegree.networkSize; i++)
				stats.append(this.inDegrees[i] + " ");
			return stats;
		}
		return null;
	}
	
	private void printStats(Object results) {
		if(results == null) {
			System.err.println("Method configuration invalid: " + this.method + " stoping simulation");
			System.exit(1);
		}
		
		if(this.usingFile) {
			try {
				this.output = new PrintStream(this.log_file+"_"+this.cycle+".txt");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		this.output.println(cycle + ";" + results.toString());
		
		if(this.usingFile)
			this.output.close();
	}
	
	//this returns false because the simulation is to continue
	public boolean execute() {
		if(this.targetCycles != null && !this.targetCycles.contains(this.cycle)) {
			this.cycle++;
			return false;
		}
		
		resetStats();
		
		for(int node = 0; node < Network.size(); node++) {
			Node current = Network.get(node);
			if(current.isUp()) {
				Linkable nodeProtocol = ((Linkable)current.getProtocol(membershipProtocol));
				
				for(int i = 0; i < ((Protocol)nodeProtocol).getViewSize(); i++) {
					Node temp = nodeProtocol.getNeighbor(i);
					if(temp != null && temp.isUp())
						this.inDegrees[temp.getIndex()] ++;
				}
			}
		}
		
		this.printStats(makeStats());
		this.cycle++;
		return false;
	}

	
}


