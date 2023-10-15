package ouroboros.messages;

import java.util.*;
import peersim.core.*;

public class ShuffleMessage {
	
	private Set<Node> sample;
	
	public ShuffleMessage(Set<Node> sample) {
		this.sample = sample;
	}
	
	public Set<Node> getSample() {
		return sample;
	}
}
