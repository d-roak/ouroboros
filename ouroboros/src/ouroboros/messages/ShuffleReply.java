package ouroboros.messages;

import java.util.*;
import peersim.core.*;

public class ShuffleReply {

	private Set<Node> sample;
	
	public ShuffleReply(Set<Node> sample) {
		this.sample = sample;
	}
	
	public Set<Node> getSample() {
		return sample;
	}
}
