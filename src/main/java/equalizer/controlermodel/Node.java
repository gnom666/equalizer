package equalizer.controlermodel;

public class Node implements Comparable<Node> {
	public long personId;
	public double amount = 0.0;
	
	public Node (long idp, double amm) {
		personId = idp;
		amount = amm;
	}

	@Override
	public int compareTo(Node o) {
		if (this.amount < o.amount) return -1;
		if (this.amount > o.amount) return 1;
		return 0;
	}
}
