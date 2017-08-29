package tmp_model;

public class Node implements Comparable<Node> {
	public Long idPerson;
	public double ammount = 0.0;
	
	public Node (Long idp, double amm) {
		idPerson = idp;
		ammount = amm;
	}

	@Override
	public int compareTo(Node o) {
		if (this.ammount < o.ammount) return -1;
		if (this.ammount > o.ammount) return 1;
		return 0;
	}
}
