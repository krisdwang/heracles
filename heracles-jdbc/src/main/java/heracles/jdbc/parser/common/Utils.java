package heracles.jdbc.parser.common;

import heracles.jdbc.parser.exception.SQLParserException;
import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.ItemList;
import heracles.jdbc.parser.expression.operators.BinaryExpression;
import heracles.jdbc.parser.expression.operators.relational.EqualsTo;
import heracles.jdbc.parser.expression.operators.relational.In;
import heracles.jdbc.parser.expression.variable.Variable;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

// FIXME Anders 方法需要优化
public class Utils {
	private static Pattern pattern = Pattern.compile("/\\*.*?\\*/");

	public static String exprToSQL(Expression expr) {
		if (expr instanceof Variable) {
			return ((Variable) expr).toStr();
		}

		if (expr instanceof ItemList) {
			return String.format("(%s)", expr.toStr());
		}

		if (expr instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression) expr;
			String left = exprToSQL(be.getLeftExpression());
			String right = exprToSQL(be.getRightExpression());
			return String.format("%s %s %s", left, be.getOperator(), right);
		}

		// if (expr instanceof ItemList) {
		// ItemList il = (ItemList) expr;
		// String left = exprToSQL(il.getLeftExpression());
		// String right = exprToSQL(il.getRightExpression());
		// return String.format("%s %s %s", left, be.getOperator(), right);
		// }

		throw new SQLParserException("not support expression : " + expr);
	}

	@Deprecated
	public static void exprToMap(Expression expr, Map<String, String> map) {
		if (expr instanceof EqualsTo) {
			EqualsTo equalsTo = (EqualsTo) expr;
			map.put(equalsTo.getLeftExpression().toStr(), equalsTo.getRightExpression().toStr());
			return;
		}

		if (expr instanceof In) {
			In equalsTo = (In) expr;
			map.put(equalsTo.getLeftExpression().toStr(), equalsTo.getRightExpression().toStr());
			return;
		}

		if (expr instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression) expr;
			exprToMap(be.getLeftExpression(), map);
			exprToMap(be.getRightExpression(), map);
			return;
		}

		throw new SQLParserException("not support expression : " + expr);
	}

	public static String trimLastChar(String str) {
		return trimLastChars(str, 1);
	}

	public static String trimLastTwoChars(String str) {
		return trimLastChars(str, 2);
	}

	public static String trimLastChars(String str, int length) {
		return str.substring(0, str.length() - length);
	}

	public static String trimLastChar(StringBuilder sb) {
		return trimLastChar(sb.toString());
	}

	public static String trimLastTwoChars(StringBuilder sb) {
		return trimLastChars(sb.toString(), 2);
	}

	// public static void exprToKeyValues(Expression expr, String keyName, Set<Object> keyValues) {
	// if (expr instanceof EqualsTo) {
	// EqualsTo et = (EqualsTo) expr;
	// Column l = (Column) et.getLeftExpression();
	// Numerical r = (Numerical) et.getRightExpression();
	// if (l.getName().equals(keyName)) {
	// keyValues.add(r.getValue());
	// }
	// }
	//
	// if (expr instanceof BinaryExpression) {
	// BinaryExpression be = (BinaryExpression) expr;
	// String left = exprToSQL(be.getLeftExpression());
	// String right = exprToSQL(be.getRightExpression());
	// return String.format("%s %s %s", left, be.getOperator(), right);
	// }
	//
	// // if (expr instanceof ItemList) {
	// // ItemList il = (ItemList) expr;
	// // String left = exprToSQL(il.getLeftExpression());
	// // String right = exprToSQL(il.getRightExpression());
	// // return String.format("%s %s %s", left, be.getOperator(), right);
	// // }
	//
	// throw new SQLParserException("not support expression : " + expr);
	// }

	public static String trimComments(String sql) {
		Matcher matcher = pattern.matcher(sql);
		if (matcher.find()) {
			String str = matcher.group();
			if (!str.equals(Constants.OPEN_COMMENT_WS + HintType.HINT_FORCE_READ + Constants.CLOSE_COMMENT_WS)) {
				return matcher.replaceAll(StringUtils.EMPTY);
			}
		}
		return sql;
	}
}
