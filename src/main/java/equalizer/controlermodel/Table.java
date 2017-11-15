package equalizer.controlermodel;

import java.util.List;

public class Table {
	
	public List<Row> rows;

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public Table(List<Row> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "Table [rows=" + rows + "]";
	}
	
	public String formatedString () {
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n id\tnumP\ttPaid\t\tpaidP\t\tdiff\t\textra\t\t\n"
				+ " --------------------------------------------------------------------\n");
		rows.forEach(r -> {
			sb.append(" " + r.personId + "\t"
					+ r.numPersons + "\t"
					+ r.totalPaid + "\t\t"
					+ r.paidPerPerson + "\t\t"
					+ r.difference + "\t\t"
					+ r.extra + "\n");
		});
		
		return sb.toString();
	}
}
