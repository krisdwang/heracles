package heracles.data.cache.spring;

import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author kriswang
 *
 */
public class SpringCacheNamespaceHandler implements HeraclesCacheConstants {

	private Element configElmt;
	
	@SuppressWarnings("unused")
	private HeraclesCacheElementParser elmtParser;
	
	private ParserContext pc;

	public SpringCacheNamespaceHandler(Element configElmt, HeraclesCacheElementParser elmtParser, ParserContext pc) {
		this.configElmt = configElmt;
		this.elmtParser = elmtParser;
		this.pc = pc;
	}
	
	public void parseSpringCache() {
		
		Document appDoc = configElmt.getOwnerDocument();
		
		//Element aliasElmt = appDoc.createElement("alias");
		//aliasElmt.setAttribute("name", REDIS_TWEMPROXY_CACHE_MANAGER);
		//aliasElmt.setAttribute("alias", CACHE_MANAGER);
		//pc.getDelegate().parseCustomElement(aliasElmt);
		
		
		Element cheDvnElmt = appDoc.createElementNS(SPRING_CACHE_NAMESPACE, "annotation-driven");
		pc.getDelegate().parseCustomElement(cheDvnElmt);
	}
	
	
	
}
