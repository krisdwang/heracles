package heracles.jdbc.rule.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

public class Utils {
	public static String getGroovyRuleEngine(String script) {
		Assert.hasText(script);

		StringBuilder sb = new StringBuilder();
		sb.append("package heracles.jdbc.rule;");
		sb.append("class GroovyRuleEngine extends AbstractRuleEngine");
		sb.append("{");
		sb.append("public String eval(Object key)");
		sb.append("{");
		sb.append(script);
		sb.append("}");
		sb.append("}");

		return sb.toString();
	}

	public static String getScriptFromExpr(String expr) {
		Assert.hasText(expr);

		return String.format("return getIndexes()[%s];", expr);
	}

	public static String getShardingKey(String rule) {
		Assert.hasText(rule);

		Pattern pattern = Pattern.compile("\\#.*?\\#");
		Matcher matcher = pattern.matcher(rule);
		while (matcher.find()) {
			String name = matcher.group();
			return name.substring(1, name.length() - 1);
		}

		return null;
	}

	public static String replaceShardingKey(String rule) {
		Assert.hasText(rule);

		Pattern pattern = Pattern.compile("\\#.*?\\#");
		Matcher matcher = pattern.matcher(rule);
		if (matcher.find()) {
			return matcher.replaceAll("key");
		}

		return rule;
	}
}
