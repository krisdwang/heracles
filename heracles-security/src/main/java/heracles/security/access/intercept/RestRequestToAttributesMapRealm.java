package heracles.security.access.intercept;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RestRequestToAttributesMapRealm implements
		RequestToAttributesMapRealm {

	private String name = "restRequestToAttributesMapRealm";

	@Getter
	@Setter
	private RestTemplate requestToAttributesMapRestTemplate;

	@Getter
	@Setter
	private String restRealmUrl;

	private MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 */
	public Map<String, Map<String, List<String>>> getRequestToAttributesMap() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(
				parameters, headers);
		try {
			ResponseEntity<Map<String, Map<String, List<String>>>> responseEntity = requestToAttributesMapRestTemplate
					.exchange(
							restRealmUrl,
							HttpMethod.POST,
							requestEntity,
							new ParameterizedTypeReference<Map<String, Map<String, List<String>>>>() {
							});
			return responseEntity.getBody();
		} catch (Exception e) {
			throw new RuntimeException("get metadata from ACCT error");
		}

	}

	public Map<String, String> getParameters() {
		return parameters.toSingleValueMap();
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters.setAll(parameters);
	}

}
