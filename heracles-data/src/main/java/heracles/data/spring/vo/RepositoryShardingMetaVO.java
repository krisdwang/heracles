package heracles.data.spring.vo;

import java.util.ArrayList;
import java.util.List;

public class RepositoryShardingMetaVO {

	private String strategiesPackage;
	private List<String> beanNames = new ArrayList<String>();

	public void addBeanName(String beanName) {
		beanNames.add(beanName);
	}

	public String getStrategiesPackage() {
		return strategiesPackage;
	}

	public void setStrategiesPackage(String strategiesPackage) {
		this.strategiesPackage = strategiesPackage;
	}

	public List<String> getBeanNames() {
		return beanNames;
	}

	public void setBeanNames(List<String> beanNames) {
		this.beanNames = beanNames;
	}
}
