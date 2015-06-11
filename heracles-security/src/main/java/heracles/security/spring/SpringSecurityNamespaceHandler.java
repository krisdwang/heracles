package heracles.security.spring;

import heracles.security.config.model.CasAuthTypeModel;
import heracles.security.config.model.CustomFilterModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SpringSecurityNamespaceHandler implements HeraclesSecurityConstants {

	private static final Logger log = LoggerFactory.getLogger(SpringSecurityNamespaceHandler.class);

	private Element configElmt;

	private HeraclesSecurityElementParser elmtParser;

	private ParserContext pc;

	public SpringSecurityNamespaceHandler(Element configElmt, HeraclesSecurityElementParser elmtParser, ParserContext pc) {
		this.configElmt = configElmt;
		this.elmtParser = elmtParser;
		this.pc = pc;
	}

	public void parseSpringSecurity() {
		Document appDoc = configElmt.getOwnerDocument();

		List<Element> noneSecurityHttpElts = buildNoneSecurityElts(elmtParser.getNoneSecurities(), appDoc);

		Element authTypeElmt = buildAuthTypeElmt(elmtParser.getCasAuthTypeModel(), elmtParser.getCustFilterModels(),
				appDoc);

		Element authMgmrElmt = buildAuthcaMgmrElmt(elmtParser.getCasAuthTypeModel(), appDoc);

		BeanDefinitionParserDelegate beanDefDelegate = pc.getDelegate();
		for (Element httpElmt : noneSecurityHttpElts) {
			beanDefDelegate.parseCustomElement(httpElmt);
		}
		log.info("Heracles security - spring security none securities element parse completed!");
		
		beanDefDelegate.parseCustomElement(authTypeElmt);
		log.info("Heracles security - spring security http element parse completed!");
		
		beanDefDelegate.parseCustomElement(authMgmrElmt);
		log.info("Heracles security - spring security authentication manager element parse completed!");
	}

	/**
	 * Construct none security http elements.
	 * 
	 * @param noneSecurities
	 * @param appDoc
	 * @return
	 */
	private List<Element> buildNoneSecurityElts(Set<String> noneSecurities, Document appDoc) {
		List<Element> noneSecurityHttpElts = new ArrayList<Element>(noneSecurities.size());
		for (String patternVal : noneSecurities) {
			Element httpElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "http");
			httpElmt.setAttribute("pattern", patternVal);
			httpElmt.setAttribute("security", "none");
			noneSecurityHttpElts.add(httpElmt);
		}
		return noneSecurityHttpElts;
	}

	/**
	 * Construct auth type element.
	 * 
	 * @param casAuthTypeMdl
	 * @param custFilterMdls
	 * @param pc
	 * @param appDoc
	 * @return
	 */
	private Element buildAuthTypeElmt(CasAuthTypeModel casAuthTypeMdl, List<CustomFilterModel> custFilterMdls,
			Document appDoc) {
		// Now cas auth type is the only support auth type
		Element casAuthTypeElmt = buildCasAuthTypeElts(casAuthTypeMdl, custFilterMdls, appDoc);
		return casAuthTypeElmt;
	}

	/**
	 * Construct cas auth type element.
	 * 
	 * @param casAuthTypeMdl
	 * @param custFilterMdls
	 * @param appDoc
	 * @return
	 */
	private Element buildCasAuthTypeElts(CasAuthTypeModel casAuthTypeMdl, List<CustomFilterModel> custFilterMdls,
			Document appDoc) {
		Element casHttpElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "http");
		casHttpElmt.setAttribute("entry-point-ref", CAS_PROCESSING_FILTER_ENTRY_POINT);

		Element acesDndElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "access-denied-handler");
		acesDndElmt.setAttribute("ref", ACCESS_DENIED_HANDLER);
		casHttpElmt.appendChild(acesDndElmt);
		
		Element logoutElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "logout");
		logoutElmt.setAttribute("logout-success-url", casAuthTypeMdl.getLogoutSuccessUrl());
		casHttpElmt.appendChild(logoutElmt);

		Element casAuthFilterElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "custom-filter");
		casAuthFilterElmt.setAttribute("ref", CAS_AUTHENTICATION_FILTER);
		casAuthFilterElmt.setAttribute("after", "CAS_FILTER");
		casHttpElmt.appendChild(casAuthFilterElmt);

		Element interceptorFilterElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "custom-filter");
		interceptorFilterElmt.setAttribute("ref", FILTER_SECURITY_INTERCEPTROR);
		interceptorFilterElmt.setAttribute("before", "FILTER_SECURITY_INTERCEPTOR");
		casHttpElmt.appendChild(interceptorFilterElmt);

		for (CustomFilterModel customFilterMdl : custFilterMdls) {
			Element custFilterElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "custom-filter");
			custFilterElmt.setAttribute("ref", customFilterMdl.getRef());
			if (null != customFilterMdl.getAfter()) {
				custFilterElmt.setAttribute("after", customFilterMdl.getAfter());
			}
			if (null != customFilterMdl.getBefore()) {
				custFilterElmt.setAttribute("before", customFilterMdl.getBefore());
			}
			if (null != customFilterMdl.getPosition()) {
				custFilterElmt.setAttribute("position", customFilterMdl.getPosition());
			}
			casHttpElmt.appendChild(custFilterElmt);
		}
		return casHttpElmt;
	}

	/**
	 * Construct authentication manager element.
	 * @param casAuthTypeMdl
	 * @param appDoc
	 * @return
	 */
	private Element buildAuthcaMgmrElmt(CasAuthTypeModel casAuthTypeMdl, Document appDoc) {
		Element authMgmrElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "authentication-manager");
		authMgmrElmt.setAttribute("alias", AUTHENTICATION_MANAGER);

		authMgmrElmt.appendChild(buildCasAuthcaPrdrElmt(casAuthTypeMdl, appDoc));
		return authMgmrElmt;
	}

	/**
	 * Construct cas authentication provider element.
	 * 
	 * @param casAuthTypeMdl
	 * @param appDoc
	 * @return
	 */
	private Element buildCasAuthcaPrdrElmt(CasAuthTypeModel casAuthTypeMdl, Document appDoc) {
		// Now cas authentication provider is the only provider.
		Element authPrdrElmt = appDoc.createElementNS(SPRING_SECURITY_NAMESPACE, "authentication-provider");
		authPrdrElmt.setAttribute("ref", CAS_AUTHENTICATION_PROVIDER);
		return authPrdrElmt;
	}

}
