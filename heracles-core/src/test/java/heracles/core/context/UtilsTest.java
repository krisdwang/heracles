package heracles.core.context;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import heracles.core.context.model.CfgdefModel;
import heracles.core.context.util.PredefinedCfgDefEnum;
import heracles.core.context.util.Utils;
import heracles.core.zookeeper.ZnodeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test-context.xml" })
@ActiveProfiles(value="test")
public class UtilsTest {
	
	@Test
	public void testDeepClone() {
		try {
			Properties obj = new Properties();
			Properties objClone = (Properties)Utils.deepClone(obj);
			assertTrue(obj != objClone);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testBeanToXml() {
		try {
			String xml = getXmlData("/cfgdefModel.xml");
			CfgdefModel model = (CfgdefModel) Utils.xmlToBean(CfgdefModel.class, xml);
			String xmlNew = Utils.beanToXml(model);
		
			assertTrue(StringUtils.isBlank(xml) == StringUtils.isBlank(xmlNew));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMapToProperties() {
		try {
			Map<String, Object> map = Utils.propertiesToMap("abc=def");
			String context = Utils.mapToProperties(map);		
			assertTrue(context.startsWith("abc=def"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testResolveSystemProperty() {
		try {
			System.setProperty("abc", "def");
			assertThat(Utils.resolveSystemProperty("abc"), equalTo("def"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testResolveSystemProperty2() {
		try {
			PredefinedCfgDefEnum.getPredefinedCfgDefKey();
			PredefinedCfgDefEnum.getPredefinedCfgDefKeyToPath();
			PredefinedCfgDefEnum.values();
			PredefinedCfgDefEnum.getPath("mysql");
			assertThat(Utils.resolveSystemProperty("def"), equalTo(""));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testResolveSystemProperty3() {
		try {
			System.setProperty("abc", "def");
			assertThat(ZnodeUtils.resolveSystemProperty("abc"), equalTo("def"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testResolveSystemProperty4() {
		try {
			assertThat(ZnodeUtils.resolveSystemProperty("def"), equalTo(""));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void testGetBootStrapPath() {
		try {
			assertThat(ZnodeUtils.getBootStrapPath(), equalTo("/default/bootstrap"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }
	
	private String getXmlData(String url) {
		InputStream is = this.getClass().getResourceAsStream(url);
		if (is != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			} catch (UnsupportedEncodingException ue) {
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {
				
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					
				}
			}
			return sb.toString();
		}
		return "";
	}

}
