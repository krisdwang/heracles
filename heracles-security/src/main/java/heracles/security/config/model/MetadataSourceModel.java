package heracles.security.config.model;

import heracles.security.access.intercept.InterceptUrlModel;

import java.util.List;

import lombok.Data;

import org.springframework.beans.factory.support.ManagedList;


@Data
public class MetadataSourceModel {

	private String clazzName;
	
	private ManagedList<Object> springBeans;
	
	private List<InterceptUrlModel> interceptUrls;
	
	
}
