package heracles.jdbc.executor.merge.function;

import heracles.jdbc.executor.common.ArrayUtilsEx;
import heracles.jdbc.executor.common.NumberUtilsEx;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class Max implements Function {

	@Override
	public Object calc(Object[] values) {
		if (ArrayUtils.isEmpty(values)) {
			return null;
		}

		List<Object> list = ArrayUtilsEx.toListIgnoreNull(values);

		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		if (list.get(0) instanceof Byte) {
			Byte[] bytes = list.toArray(new Byte[list.size()]);
			return NumberUtils.max(ArrayUtils.toPrimitive(bytes));
		} else if (list.get(0) instanceof Short) {
			Short[] shorts = list.toArray(new Short[list.size()]);
			return NumberUtils.max(ArrayUtils.toPrimitive(shorts));
		} else if (list.get(0) instanceof Integer) {
			Integer[] ints = list.toArray(new Integer[list.size()]);
			return NumberUtils.max(ArrayUtils.toPrimitive(ints));
		} else if (list.get(0) instanceof Long) {
			Long[] longs = list.toArray(new Long[list.size()]);
			return NumberUtils.max(ArrayUtils.toPrimitive(longs));
		} else if (list.get(0) instanceof Float) {
			Float[] floats = list.toArray(new Float[list.size()]);
			return NumberUtils.max(ArrayUtils.toPrimitive(floats));
		} else if (list.get(0) instanceof Double) {
			Double[] doubles = list.toArray(new Double[list.size()]);
			return NumberUtils.max(ArrayUtils.toPrimitive(doubles));
		} else if (list.get(0) instanceof BigDecimal) {
			BigDecimal[] bigDecimals = list.toArray(new BigDecimal[list.size()]);
			return NumberUtilsEx.max(bigDecimals);
		} else if (list.get(0) instanceof BigInteger) {
			BigInteger[] bigIntegers = list.toArray(new BigInteger[list.size()]);
			return NumberUtilsEx.max(bigIntegers);
		} else {
			throw new UnsupportedOperationException();
		}
	}

}
