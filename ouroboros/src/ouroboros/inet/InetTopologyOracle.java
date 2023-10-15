package ouroboros.inet;

import peersim.core.*;

public class InetTopologyOracle extends Oracle {
	
	private static double precision;
	private static double error;
	
	private static final double DEFAULT_PRECISION = 0.8;
	private static final double DEFAULT_ERROR = 0.5;
	
	private int[] costs;
	private int netSize;
	
	private InetTopologyOracle() {
		InetTopologyOracle.precision = InetTopologyOracle.DEFAULT_PRECISION;
		InetTopologyOracle.error = InetTopologyOracle.DEFAULT_ERROR;
		this.netSize = Network.size();
	}
	
	private InetTopologyOracle(double precision) {
		InetTopologyOracle.precision = precision;
		InetTopologyOracle.error = InetTopologyOracle.DEFAULT_ERROR;
		this.netSize = Network.size();
	}
	
	private InetTopologyOracle(double precision, double error) {
		InetTopologyOracle.precision = precision;
		InetTopologyOracle.error = error;
		this.netSize = Network.size();
	}
	
	@Override
	public Long utilityFunction(Node requester, Node target) {
		long realValue = this.perfectUtilityFunction(requester, target);
		if(CommonState.r.nextDouble() >= InetTopologyOracle.precision) {
			long error = CommonState.r.nextLong( (long) (realValue * InetTopologyOracle.error) );
			if(CommonState.r.nextBoolean()) {
				return new Long(realValue - error);
			} else {
				return new Long(realValue + error);
			}
		} else {
			return new Long(realValue);
		}
	}

	@Override
	public Long perfectUtilityFunction(Node requester, Node target) {
		return new Long(this.costs[(int) ((requester.getID() * this.netSize) + target.getID())])/100;
	}
	
	public static Oracle generateInstance() {
		return new InetTopologyOracle();
	}
	
	public static Oracle generateInstance(double precision) {
		return new InetTopologyOracle(precision);
	}

	public static Oracle generateInstance(double precision, double error) {
		return new InetTopologyOracle(precision, error);
	}

	@Override
	public void loadCostsValues(int[] costs) {
		this.costs = costs;
	}

	@Override
	public void loadTopologyPaths(Path[][] paths) {
		return;
	}
}
