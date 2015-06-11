package heracles.jdbc.executor.merge;

import heracles.jdbc.common.rs.MergeResultSet;
import heracles.jdbc.executor.merge.function.Avg;
import heracles.jdbc.executor.merge.function.Count;
import heracles.jdbc.executor.merge.function.Function;
import heracles.jdbc.executor.merge.function.Max;
import heracles.jdbc.executor.merge.function.Min;
import heracles.jdbc.executor.merge.function.Sum;
import heracles.jdbc.parser.expression.Expression;
import heracles.jdbc.parser.expression.function.AvgFunc;
import heracles.jdbc.parser.expression.function.CountFunc;
import heracles.jdbc.parser.expression.function.MaxFunc;
import heracles.jdbc.parser.expression.function.MinFunc;
import heracles.jdbc.parser.expression.function.SumFunc;
import heracles.jdbc.parser.statement.Statement;
import heracles.jdbc.parser.statement.select.Select;
import heracles.jdbc.parser.statement.select.select.SelectExpression;
import heracles.jdbc.parser.statement.select.select.SelectItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class MergeExecutor {
	public MergeResultSet merge(ResultSet[] resultSets, Statement statement) throws SQLException {
		Select select = (Select) statement;
		switch (select.getMergeType()) {
		case NONE:
			return mergeForNone(resultSets);
		case FUNCTION:
			return mergeForFunc(resultSets, select);
		default:
			throw new UnsupportedOperationException();
		}
	}

	private MergeResultSet mergeForNone(ResultSet[] resultSets) throws SQLException {
		MergeResultSet mergeResultSet = new MergeResultSet();
		for (ResultSet resultSet : resultSets) {
			mergeResultSet.addResultSet(resultSet);
		}
		return mergeResultSet;
	}

	private MergeResultSet mergeForFunc(ResultSet[] resultSets, Select select) throws SQLException {
		if (ArrayUtils.isEmpty(resultSets)) {
			return new MergeResultSet();
		}

		int rows = select.getSelectItems().size();
		int cols = resultSets.length;
		Object[][] results = new Object[rows][cols];

		for (int i = 0; i < cols; i++) {
			while (resultSets[i].next()) {
				for (int j = 0; j < rows; j++) {
					results[j][i] = resultSets[i].getObject(j + 1);
				}
			}
		}

		MergeResultSet mergeResultSet = new MergeResultSet(resultSets[0]);

		List<SelectItem> selectItems = select.getSelectItems();
		for (int i = 0; i < selectItems.size(); i++) {
			Expression expr = ((SelectExpression) selectItems.get(i)).getExpression();

			Function function = null;
			if (expr instanceof MaxFunc) {
				function = new Max();
			} else if (expr instanceof MinFunc) {
				function = new Min();
			} else if (expr instanceof AvgFunc) {
				function = new Avg();
			} else if (expr instanceof SumFunc) {
				function = new Sum();
			} else if (expr instanceof CountFunc) {
				function = new Count();
			} else {
				throw new UnsupportedOperationException();
			}

			mergeResultSet.update(1, i + 1, function.calc(results[i]));
		}

		return mergeResultSet;
	}
}
