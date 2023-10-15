package ouroboros;

import peersim.core.Node;

public interface Protocol {
	
	public void initializer(Node node);
	
	public int getViewSize();
}
