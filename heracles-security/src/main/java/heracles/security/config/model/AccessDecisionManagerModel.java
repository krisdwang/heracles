package heracles.security.config.model;

import lombok.Data;

import org.springframework.beans.factory.support.ManagedList;

@Data
public class AccessDecisionManagerModel {

	private String clazzName;
	
	private ManagedList<Object> springBeans;
}
