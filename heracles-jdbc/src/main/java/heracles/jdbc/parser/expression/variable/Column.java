package heracles.jdbc.parser.expression.variable;

import heracles.jdbc.parser.common.Constants;
import heracles.jdbc.parser.statement.select.from.Table;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Column implements Variable {

	private String tableAlias;
	private String name;
	private Map<String, Table> tableMap = new HashMap<String, Table>();

	public Column(Map<String, Table> tableMap, String name) {
		this.tableMap = tableMap;
		// FIXME Anders 此处的方法需要重新考虑，是否最好
		if (name.contains(Constants.DOT)) {
			String[] str = name.split("\\.");
			this.tableAlias = str[0];
			this.name = str[1];
		} else {
			this.name = name;
		}
	}

	public Table getTable() {
		return tableMap.get(tableAlias);
	}

	public String getName() {
		return name;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	@Override
	public String toStr() {
		StringBuilder sb = new StringBuilder();

		Table table = getTable();
		if (StringUtils.isNotBlank(tableAlias) && table != null) {
			sb.append(table.getAlias() == null ? table.toStr() : table.getAlias().getName());
		}
		if (sb.length() > 0) {
			sb.append(DOT);
		}
		if (StringUtils.isNotBlank(name)) {
			sb.append(name);
		}
		return sb.toString();
	}
}
