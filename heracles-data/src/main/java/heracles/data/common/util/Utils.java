package heracles.data.common.util;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

public class Utils implements Constants {

	public static boolean isMatch(String methodName, String mappedName) {
		return PatternMatchUtils.simpleMatch(mappedName, methodName);
	}

	public static boolean isRead(String key) {
		return Constants.READ_KEY.equalsIgnoreCase(key);
	}

	public static boolean isWrite(String key) {
		return Constants.WRITE_KEY.equalsIgnoreCase(key);
	}

	public static String getMatchTableName(String sql) {
		Assert.hasText(sql);
		Pattern pattern = Pattern.compile("\\$\\[.*?\\]\\$");
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			String name = matcher.group();
			return name.substring(2, name.length() - 2);
		}
		return null;
	}

	public static String getRegexTableName(String tableName) {
		Assert.hasText(tableName);
		return "\\$\\[" + tableName + "\\]\\$";
	}

	public static String getShardingTableName(String tableName, String realTableName, String sql) {
		Assert.hasText(tableName);
		Assert.hasText(realTableName);
		Assert.hasText(sql);
		return sql.replaceAll(getRegexTableName(tableName), realTableName);
	}

	public static String getOrderBy(Sort sort) {
		if (sort == null) {
			return null;
		}

		Iterator<Order> it = sort.iterator();
		StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			Order order = it.next();
			sb.append(order.getProperty());
			if (order.getDirection().equals(Direction.DESC)) {
				sb.append(" desc");
			}
			sb.append(", ");
		}

		String orderBy = sb.toString();

		return orderBy.substring(0, orderBy.length() - 2);
	}

	public static String trimSql(String sql) {
		Assert.hasText(sql);
		String targetSql = StringUtils.replace(sql, "\n", " ");
		targetSql = StringUtils.replace(targetSql, "\t", " ");
		targetSql = targetSql.replaceAll(" +", " ");
		return targetSql.trim();
	}

	public static Object getSpelValue(Object[] args, String[] paraNames, String key, BeanFactory beanFactory) {
		Assert.notEmpty(args);
		Assert.notEmpty(paraNames);
		Assert.hasText(key);

		if (args.length != paraNames.length) {
			throw new IllegalArgumentException("args length must be equal to paraNames length");
		}

		ExpressionParser ep = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();

		for (int i = 0; i < paraNames.length; i++) {
			context.setVariable(paraNames[i], args[i]);
		}

		if (beanFactory != null) {
			context.setBeanResolver(new BeanFactoryResolver(beanFactory));
		}

		return ep.parseExpression(key).getValue(context);
	}
}
