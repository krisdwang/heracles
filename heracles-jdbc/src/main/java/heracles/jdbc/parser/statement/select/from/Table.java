package heracles.jdbc.parser.statement.select.from;

import heracles.jdbc.parser.expression.Alias;

import org.apache.commons.lang3.StringUtils;

public class Table implements FromItem {

	private String database;
	private String schemaName;
	private String name;
	private Alias alias;

	public Table() {
	}

	public Table(String name) {
		this.name = name;
	}

	public Table(String schemaName, String name) {
		this.schemaName = schemaName;
		this.name = name;
	}

	public Table(String database, String schemaName, String name) {
		this.database = database;
		this.schemaName = schemaName;
		this.name = name;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String string) {
		schemaName = string;
	}

	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

	@Override
	public Alias getAlias() {
		return alias;
	}

	@Override
	public void setAlias(Alias alias) {
		this.alias = alias;
	}

	public boolean setTableName(String oldName, String newName) {
		if (name.equals(oldName)) {
			name = newName;
			return true;
		}

		return false;
	}

	@Override
	public String toStr() {
		StringBuilder sb = new StringBuilder();

		if (StringUtils.isNotBlank(database)) {
			sb.append(database);
		}
		if (sb.length() > 0) {
			sb.append(DOT);
		}

		if (StringUtils.isNotBlank(schemaName)) {
			sb.append(schemaName);
		}
		if (sb.length() > 0) {
			sb.append(DOT);
		}

		if (StringUtils.isNotBlank(name)) {
			sb.append(name);
		}

		return String.format("%s%s", sb.toString(), alias != null ? SPACE + alias.toStr() : EMPTY);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
