package heracles.jdbc.spring.vo;

import heracles.jdbc.group.strategy.RandomLoadBalanceStrategy;
import heracles.jdbc.group.strategy.RoundRobinLoadBalanceStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class AllInOneReadWriteDataSourceVO {

	private String name;
	private String loadBalance = "roundRobin";
	private String weight;
	private AllInOneAtomDataSourceVO writeDataSourceMetaVO;
	private List<AllInOneAtomDataSourceVO> readDataSourceMetaVOs = new ArrayList<AllInOneAtomDataSourceVO>();

	public void addReadDataSourceMetaVO(AllInOneAtomDataSourceVO readDataSourceMetaVO) {
		readDataSourceMetaVOs.add(readDataSourceMetaVO);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoadBalance() {
		return loadBalance;
	}

	public Object getMatrixLoadBalanceArgs() {
		if (this.loadBalance.equalsIgnoreCase("random")) {
			List<String> list = new ArrayList<String>();
			if (readDataSourceMetaVOs != null && readDataSourceMetaVOs.size() > 0) {
				for (int i = 1; i <= readDataSourceMetaVOs.size(); i++) {
					if (i > 9) {
						list.add("read" + i);
					} else {
						list.add("read0" + i);
					}
				}
			} else {
				list.add("write");
			}
			return list;
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		if (readDataSourceMetaVOs != null && readDataSourceMetaVOs.size() > 0) {
			int i = 1;
			for (String weight : getWeights()) {
				int index = i++;
				if (index > 9) {
					map.put("read" + index, Integer.parseInt(weight));
				} else {
					map.put("read0" + index, Integer.parseInt(weight));
				}
			}
		} else {
			map.put("write", 10);
		}
		return map;
	}

	public void setLoadBalance(String loadBalance) {
		this.loadBalance = loadBalance;
	}

	public String getWeight() {
		return this.weight;
	}

	public String getWriteBeanDefinitionName() {
		return this.name + "_write";
	}

	public String getAtomWriteBeanDefinitionName() {
		return this.name + "_atomwrite";
	}

	public String getReadBeanDefinitionName(int i) {
		return this.name + "_read" + i;
	}

	public String getAtomReadBeanDefinitionName(int i) {
		return this.name + "_atomread" + i;
	}

	public String getLoadBalanceBeanDefinitionName() {
		return this.name + "_lb";
	}

	public String[] getWeights() {
		if (StringUtils.isNotBlank(weight)) {
			String[] strs = weight.split(":");
			if (ArrayUtils.isNotEmpty(strs) && strs.length == readDataSourceMetaVOs.size()) {
				return strs;
			}
		}

		String[] strs = new String[readDataSourceMetaVOs.size()];
		for (int i = 0; i < readDataSourceMetaVOs.size(); i++) {
			strs[i] = "10";
		}
		return strs;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public AllInOneAtomDataSourceVO getWriteDataSourceMetaVO() {
		return writeDataSourceMetaVO;
	}

	public void setWriteDataSourceMetaVO(AllInOneAtomDataSourceVO writeDataSourceMetaVO) {
		this.writeDataSourceMetaVO = writeDataSourceMetaVO;
	}

	public List<AllInOneAtomDataSourceVO> getReadDataSourceMetaVOs() {
		return readDataSourceMetaVOs;
	}

	public Class<?> getJdbcLoadBalanceClass() {
		if (this.loadBalance.equalsIgnoreCase("random")) {
			return RandomLoadBalanceStrategy.class;
		}
		return RoundRobinLoadBalanceStrategy.class;
	}
}
