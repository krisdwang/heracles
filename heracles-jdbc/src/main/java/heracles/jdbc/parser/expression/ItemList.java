package heracles.jdbc.parser.expression;

public interface ItemList extends Expression {
	int size();

	boolean isEmpty();

	boolean isNotEmpty();
}
