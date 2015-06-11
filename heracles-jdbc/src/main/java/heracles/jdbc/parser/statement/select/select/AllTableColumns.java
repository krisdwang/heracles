package heracles.jdbc.parser.statement.select.select;

import heracles.jdbc.parser.statement.select.from.Table;

@Deprecated
public class AllTableColumns implements SelectItem {

	private Table table;

	public AllTableColumns() {
	}

	public AllTableColumns(Table table) {
		this.table = table;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	@Override
	public String toStr() {
		return table + ".*";
	}
}
