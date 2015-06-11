package heracles.data.cache.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;

import heracles.data.cache.config.model.CacheType;

public class UtilsTest {

	@Test
	public void testGetEnumFromString() {
		CacheType caheType = Utils.getEnumFromString("redis", CacheType.class, null);
		assertSame(CacheType.REDIS, caheType);
	}

	@Test(expected = BeanDefinitionParsingException.class)
	public void testGetEnumFromStringError() {
		XmlReaderContext ctx = mock(XmlReaderContext.class);
		doThrow(BeanDefinitionParsingException.class).when(ctx).error(anyString(), any());
		ParserContext pc = new ParserContext(ctx, null);

		Utils.getEnumFromString("redis1", CacheType.class, pc);
	}

	@Test(expected = NullPointerException.class)
	public void testIsMultipleNull() {
		Utils.isMulitiple(null);
	}

	@Test
	public void testIsMultipleEmpty() {
		assertFalse(Utils.isMulitiple(Collections.emptyList()));
	}

	@Test
	public void testIsMultipleOne() {
		Collection<String> cltn = new ArrayList<String>();
		cltn.add("notempty");
		assertFalse(Utils.isMulitiple(cltn));
	}

	@Test
	public void testIsMultipleNotEmpty() {
		Collection<String> cltn = new ArrayList<String>();
		cltn.add("notempty1");
		cltn.add("notempty2");
		assertTrue(Utils.isMulitiple(cltn));
	}
	
	@Test
	public void testConstructor() {
		new Utils();
	}

}
