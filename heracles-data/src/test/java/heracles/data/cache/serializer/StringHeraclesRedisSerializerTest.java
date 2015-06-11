package heracles.data.cache.serializer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import heracles.data.cache.redis.serializer.StringHeraclesRedisSerializer;

import java.nio.charset.Charset;

import org.junit.Test;

public class StringHeraclesRedisSerializerTest {

	private static final Charset UTF8_CHARSET = Charset.forName("UTF8");

	@Test
	public void testStringSerializerNull() {
		StringHeraclesRedisSerializer srlz = new StringHeraclesRedisSerializer();
		byte[] retVal = srlz.serialize(null);
		assertNull(retVal);
	}

	@Test
	public void testStringSerializerPrimitive() {
		String strVal = "120";
		StringHeraclesRedisSerializer srlz = new StringHeraclesRedisSerializer();
		
		long longVal = 120L;
		byte[] retVal = srlz.serialize(longVal);
		assertArrayEquals(String.valueOf(longVal).getBytes(UTF8_CHARSET), retVal);
		
		Object deSrlzVal = srlz.deserialize(retVal);
		assertEquals(strVal, deSrlzVal);
	}
	
	@Test
	public void testStringSerializerLong() {
		String strVal = "120";
		StringHeraclesRedisSerializer srlz = new StringHeraclesRedisSerializer();

		Long val = Long.valueOf(strVal);
		byte[] retVal = srlz.serialize(val);
		assertArrayEquals(val.toString().getBytes(UTF8_CHARSET), retVal);

		Object deSrlzVal = srlz.deserialize(retVal);
		assertEquals(strVal, deSrlzVal);
	}

	@Test
	public void testStringSerializerString() {
		String strVal = "test-string-serializer";
		StringHeraclesRedisSerializer srlz = new StringHeraclesRedisSerializer();
		byte[] retVal = srlz.serialize(strVal);
		assertArrayEquals(strVal.getBytes(UTF8_CHARSET), retVal);

		Object deSrlzVal = srlz.deserialize(retVal);
		assertEquals(strVal, deSrlzVal);
	}

	@Test
	public void testStringSerializerObject() {
		Object objVal = new Object();
		StringHeraclesRedisSerializer srlz = new StringHeraclesRedisSerializer();
		byte[] retVal = srlz.serialize(objVal);
		assertArrayEquals(objVal.toString().getBytes(UTF8_CHARSET), retVal);

		Object deSrlzVal = srlz.deserialize(retVal);
		assertEquals(objVal.toString(), deSrlzVal);
	}

}
