package heracles.jdbc.executor.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public class ArrayUtilsEx {
	public static List<Object> toListIgnoreNull(final Object[] array) {
		Assert.notEmpty(array);

		List<Object> list = new ArrayList<Object>();
		for (Object value : array) {
			if (value != null) {
				list.add(value);
			}
		}

		return list;
	}
}
