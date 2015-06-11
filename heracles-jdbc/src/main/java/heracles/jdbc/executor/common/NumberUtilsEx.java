package heracles.jdbc.executor.common;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.util.Assert;

public class NumberUtilsEx {
	public static BigInteger max(final BigInteger[] array) {
		Assert.notEmpty(array);

		BigInteger max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max.max(array[i]);
		}
		return max;
	}

	public static BigDecimal max(final BigDecimal[] array) {
		Assert.notEmpty(array);

		BigDecimal max = array[0];
		for (int i = 1; i < array.length; i++) {
			max = max.max(array[i]);
		}
		return max;
	}

	public static BigInteger min(final BigInteger[] array) {
		Assert.notEmpty(array);

		BigInteger min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min.min(array[i]);
		}
		return min;
	}

	public static BigDecimal min(final BigDecimal[] array) {
		Assert.notEmpty(array);

		BigDecimal min = array[0];
		for (int i = 1; i < array.length; i++) {
			min = min.min(array[i]);
		}
		return min;
	}

	public static byte sum(final byte[] array) {
		validateArray(array);

		byte sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}

		return sum;
	}

	public static short sum(final short[] array) {
		validateArray(array);

		short sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}

		return sum;
	}

	public static int sum(final int[] array) {
		validateArray(array);

		int sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}

		return sum;
	}

	public static long sum(final long[] array) {
		validateArray(array);

		long sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}

		return sum;
	}

	public static float sum(final float[] array) {
		validateArray(array);

		float sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}

		return sum;
	}

	public static double sum(final double[] array) {
		validateArray(array);

		double sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum += array[i];
		}

		return sum;
	}

	public static BigDecimal sum(final BigDecimal[] array) {
		Assert.notEmpty(array);

		BigDecimal sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum = sum.add(array[i]);
		}

		return sum;
	}

	public static BigInteger sum(final BigInteger[] array) {
		Assert.notEmpty(array);

		BigInteger sum = array[0];
		for (int i = 1; i < array.length; i++) {
			sum = sum.add(array[i]);
		}

		return sum;
	}

	public static byte avg(final byte[] array) {
		byte sum = sum(array);
		return (byte) (sum / array.length);
	}

	public static short avg(final short[] array) {
		short sum = sum(array);
		return (short) (sum / array.length);
	}

	public static int avg(final int[] array) {
		int sum = sum(array);
		return sum / array.length;
	}

	public static long avg(final long[] array) {
		long sum = sum(array);
		return sum / array.length;
	}

	public static float avg(final float[] array) {
		float sum = sum(array);
		return sum / array.length;
	}

	public static double avg(final double[] array) {
		double sum = sum(array);
		return sum / array.length;
	}

	public static BigInteger avg(final BigInteger[] array) {
		BigInteger sum = sum(array);
		BigInteger count = new BigInteger(String.valueOf(array.length));
		return sum.divide(count);
	}

	public static BigDecimal avg(final BigDecimal[] array) {
		BigDecimal sum = sum(array);
		BigDecimal count = new BigDecimal(String.valueOf(array.length));
		return sum.divide(count);
	}

	private static void validateArray(final Object array) {
		if (array == null) {
			throw new IllegalArgumentException("The Array must not be null");
		} else if (Array.getLength(array) == 0) {
			throw new IllegalArgumentException("Array cannot be empty.");
		}
	}
}
