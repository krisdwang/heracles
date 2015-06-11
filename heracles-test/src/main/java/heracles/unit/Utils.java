package heracles.unit;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * 
 * @author azen
 * 
 */
public class Utils {

	/**
	 * properties转map
	 * 
	 * @param pro
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> propertiesToMap(String pro) {

		if (StringUtils.isBlank(pro)) {
			return new HashMap<String, Object>();
		}
		String[] strArray = pro.split("\n");
		Map<String, Object> result = new HashMap<String, Object>();
		if (strArray != null && strArray.length > 0) {

			for (String str : strArray) {
				result.put(StringUtils.substringBefore(str, "="),
						StringUtils.substringAfter(str, "="));
			}
		}

		return result;
	}

	/**
	 * xml转bean
	 * 
	 * @param xml
	 * @return Object
	 * @throws JAXBException
	 */
	public static Object springXmlToBean(Class<?> type, String xml)
			throws Exception {
		if (StringUtils.isBlank(xml)) {
			throw new IllegalArgumentException();
		}
		Object obj = new Object();
		JAXBContext context = null;
		context = JAXBContext.newInstance(type);
		Unmarshaller um = null;
		um = context.createUnmarshaller();
		StringReader sr = new StringReader(xml);

		SAXParserFactory sax = SAXParserFactory.newInstance();
		sax.setNamespaceAware(false);
		XMLReader xmlReader = sax.newSAXParser().getXMLReader();
		Source source = new SAXSource(xmlReader, new InputSource(sr));

		obj = (Object) um.unmarshal(source);
		return obj;
	}
}
