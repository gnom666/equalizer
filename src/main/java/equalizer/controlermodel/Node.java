package equalizer.controlermodel;

public class Node implements Comparable<Node> {
	public long personId;
	public double ammount = 0.0;
	
	public Node (long idp, double amm) {
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
