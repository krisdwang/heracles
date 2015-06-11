package heracles.core.context.util;

import heracles.core.zookeeper.PropertiesUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * 
 * @author kriswang
 * 
 */
public class Utils {

	private static Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	/**
	 * 深度克隆
	 * 
	 * @param src
	 * @return
	 * @throws Throwable
	 */
	public static Object deepClone(Object src) throws Throwable {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(src);
		oos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);

		Object obj = ois.readObject();
		ois.close();
		return obj;
	}

	/**
	 * bean转xml
	 * 
	 * @param obj
	 * @return String
	 * @throws JAXBException
	 */
	public static String beanToXml(Object obj) throws JAXBException {
		JAXBContext context = null;
		context = JAXBContext.newInstance(obj.getClass());
		Marshaller m = null;
		m = context.createMarshaller();
		StringWriter sw = new StringWriter();
		m.marshal(obj, sw);
		return sw.toString();
	}

	/**
	 * xml转bean
	 * 
	 * @param xml
	 * @return Object
	 * @throws JAXBException
	 */
	public static Object xmlToBean(Class<?> type, String xml) throws JAXBException {
		if (StringUtils.isBlank(xml)) {
			throw new IllegalArgumentException();
		}
		Object obj = new Object();
		JAXBContext context = null;
		context = JAXBContext.newInstance(type);
		Unmarshaller um = null;
		um = context.createUnmarshaller();
		StringReader sr = new StringReader(xml);
		obj = (Object) um.unmarshal(sr);
		return obj;
	}

	/**
	 * xml转bean
	 * 
	 * @param xml
	 * @return Object
	 * @throws JAXBException
	 */
	public static Object springXmlToBean(Class<?> type, String xml) throws Exception {
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

	/**
	 * 按行读取文件
	 * 
	 * @param file
	 * @return String
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		InputStream stream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			throw e;
		} finally {
			stream.close();
		}
		return sb.toString();
	}

	/**
	 * 写文件
	 * 
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public static void writeFile(File file, String content) throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(content, 0, content.length());
			fw.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}

	/**
	 * Actually load properties from the given EncodedResource into the given Properties instance.
	 * 
	 * @param props the Properties instance to load into
	 * @param type the resource to load from
	 * @param file the PropertiesPersister to use
	 * @throws IOException in case of I/O errors
	 */
	public static void fillProperties(Properties props, Class<?> type, File file) throws IOException {
		InputStream stream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			stream.close();
		}
	}

	/**
	 * properties转map
	 * 
	 * @param pro
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> propertiesToMap(String pro) {
		if (StringUtils.isBlank(pro)) {
			return new LinkedHashMap<String, Object>();
		}
		String[] strArray = pro.split("\n");
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		if (strArray != null && strArray.length > 0) {

			for (String str : strArray) {
				result.put(StringUtils.substringBefore(str, "="), StringUtils.substringAfter(str, "="));
			}
		}

		return result;
	}

	/**
	 * map转properties
	 * 
	 * @param map
	 * @return String
	 */
	public static String mapToProperties(Map<String, Object> map) {
		String result = "";
		if (map != null) {
			Iterator<String> iter = map.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				result += (key + "=" + map.get(key) + "\n");
			}
		}
		return result;
	}

	/**
	 * properties转string
	 * 
	 * @param props
	 * @return
	 */
	public static String propertiesToString(Properties props) {
		String result = "";
		if (props != null) {
			Iterator<Object> iter = props.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				result += (key + "=" + props.get(key) + "\n");
			}
		}
		return result;
	}

	public static String resolveSystemProperty(String key) {
		try {
			String value = System.getProperty(key);
			if (value == null) {
				value = System.getenv(key);
			}
			if (value == null) {
				value = "";
			}
			return value;
		} catch (Throwable ex) {
			ex.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取xml内容
	 * 
	 * @param url
	 * @return
	 */
	public static String getXmlData(String url) {
		LOGGER.info("resolve url [" + url + "]");
		InputStream is = PropertiesUtils.class.getResourceAsStream(url);
		if (is != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			} catch (UnsupportedEncodingException ue) {
				LOGGER.error("UnsupportedEncodingException : " + ue.getMessage());
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				if(br != null) {
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
				}
			} catch (IOException e) {
				LOGGER.error("Could not load xml from " + url + ": " + e.getMessage());
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					LOGGER.error("Could not close inputStream from " + url + ": " + e.getMessage());
				}
			}
			return sb.toString();
		}
		return Constants.EMPTY_STRING;
	}

}
