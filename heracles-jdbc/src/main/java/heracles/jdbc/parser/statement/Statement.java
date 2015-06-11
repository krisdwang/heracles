package heracles.jdbc.parser.statement;

import heracles.jdbc.parser.common.Element;
import heracles.jdbc.parser.common.StatementType;
import heracles.jdbc.parser.statement.select.from.FromItem;

public abstract class Statement implements Element {

	protected FromItem fromItem;

	public abstract StatementType getType();

	public String getSQL() {
		return toStr();
	}

	public FromItem getFromItem() {
		return fromItem;
	}

	public void setFromItem(FromItem fromItem) {
		this.fromItem = fromItem;
	}
}
