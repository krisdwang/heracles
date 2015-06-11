package heracles.jdbc.parser.statement.select.select;

public class AllColumns implements SelectItem {

	@Override
	public String toStr() {
		return STAR;
	}
}
