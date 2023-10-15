package ouroboros.inet;

import peersim.core.Node;

public abstract class Oracle {

	private static Oracle instance;
	
	public static final String inet = "inet";
	
	private static Double precision = null;
	private static Double error = null;
	
	protected Oracle() {
		Oracle.instance = null;
	}
	
	public abstract Long utilityFunction(Node requester, Node target);
	
	public abstract Long perfectUtilityFunction(Node requester, Node target);
	
	public static Oracle generateInstance() {
		return null;
	}
	
	public static Long utility(Node requester, Node target) {
		return Oracle.instance.utilityFunction(requester, target);
	}
	
	public static Long perfectUtility(Node requester, Node target) {
		return Oracle.instance.perfectUtilityFunction(requester, target);
	}
	
	public static void setUp(String description) {
		if(Oracle.precision != null && Oracle.error != null) {
			if(description.compareToIgnoreCase(Oracle.inet) == 0)
				Oracle.instance = InetTopologyOracle.generateInstance(Oracle.precision.doubleValue(), Oracle.error.doubleValue());
			else {
				System.err.println("No Such Oracle.");
				System.exit(1);
			}
				
		} else {
			if(description.compareToIgnoreCase(Oracle.inet) == 0)
				Oracle.instance = InetTopologyOracle.generateInstance();
			else {
				System.err.println("No Such Oracle.");
				System.exit(1);
			}		
		}
	}
	
	public static void setConfigurations(double precision, double error) {
		Oracle.precision = new Double(precision);
		Oracle.error = new Double(error);
	}
	
	public abstract void loadCostsValues(int[] costs);
	
	public abstract void loadTopologyPaths(Path[][] paths);
	 
	public static void loadCosts(int[] costs) {
		if(Oracle.instance != null)
			Oracle.instance.loadCostsValues(costs);
	}
	
	public static void loadTopology(Path[][] paths) {
		if(Oracle.instance != null)
			Oracle.instance.loadTopologyPaths(paths);
	}
}
