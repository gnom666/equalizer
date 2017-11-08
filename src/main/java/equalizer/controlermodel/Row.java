package equalizer.controlermodel;

public class Row {
	public long personId;
	public int numPersons = 0;
	public double totalPaid = 0.0;
	public double paidPerPerson = 0.0;
	public double difference = 0.0;
	public double extra = 0;
	
	@Override
	public String toString() {
		return "Row [personId=" + personId + ", numPersons=" + numPersons + ", totalPaid=" + totalPaid
				+ ", paidPerPerson=" + paidPerPerson + ", difference=" + difference + ", extra=" + extra + "]";
	}
}
