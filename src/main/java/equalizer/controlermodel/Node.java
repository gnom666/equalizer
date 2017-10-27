package equalizer.controlermodel;

public class Node implements Comparable<Node> {
	public Long personId;
	public double ammount = 0.0;
	
	public Node (Long idp, double amm) {
		personId = idp;
		ammount = amm;
	}

	@Override
	public int compareTo(Node o) {
		if (this.ammount < o.ammount) return -1;
		if (this.ammount > o.ammount) return 1;
		return 0;
	}
}
