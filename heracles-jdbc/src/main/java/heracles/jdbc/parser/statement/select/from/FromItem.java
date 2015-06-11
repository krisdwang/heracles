package heracles.jdbc.parser.statement.select.from;

import heracles.jdbc.parser.common.Element;
import heracles.jdbc.parser.expression.Alias;

public interface FromItem extends Element {

	Alias getAlias();

	void setAlias(Alias alias);

	Object clone() throws CloneNotSupportedException;
}
