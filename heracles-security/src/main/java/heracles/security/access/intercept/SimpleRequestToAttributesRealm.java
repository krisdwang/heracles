package heracles.security.access.intercept;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Setter;

public class SimpleRequestToAttributesRealm implements RequestToAttributesMapRealm {

	private String name = "requestToAttributesSimpleRealm";

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRequestToAttributesRealm.class);

	@Setter
	private List<InterceptUrlModel> interceptUrlModels;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<String, Map<String, List<String>>> getRequestToAttributesMap() {
		String errMsg = "SimpleRequestToAttributesRealm does not support getRequestToAttributesMap method, please use getSimpleRequestToAttributesMap";
		throw new UnsupportedOperationException(errMsg);
	}

	public List<InterceptUrlModel> getSimpleRequestToAttributesRealm() {
		if (CollectionUtils.isEmpty(interceptUrlModels)) {
			return Collections.emptyList();
		}

		List<InterceptUrlModel> smpRlms = new LinkedList<InterceptUrlModel>();
		for (InterceptUrlModel itctUrlMdl : interceptUrlModels) {
			InterceptUrlModel newMdl = new InterceptUrlModel();
			newMdl.setPattern(itctUrlMdl.getPattern());
			newMdl.setMethod(itctUrlMdl.getMethod());
			newMdl.setAccess(ROLE_PREFIX_AUTH_SYS + itctUrlMdl.getAccess());
			smpRlms.add(newMdl);
			LOGGER.info("Simple realm intercept url: {}", itctUrlMdl);
		}

		return smpRlms;
	}

}
