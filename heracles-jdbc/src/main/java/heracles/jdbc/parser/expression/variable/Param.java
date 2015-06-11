package heracles.jdbc.parser.expression.variable;

public class Param implements Variable {

	@Override
	public String toStr() {
		return QUESTION_MARK;
	}
}