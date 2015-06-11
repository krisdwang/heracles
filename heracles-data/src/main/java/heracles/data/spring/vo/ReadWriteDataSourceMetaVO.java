package heracles.data.spring.vo;

import heracles.data.datasource.strategy.RandomLoadBalanceStrategy;
import heracles.data.datasource.strategy.RoundRobinLoadBalanceStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ReadWriteDataSourceMetaVO {

	private String name;
	private String loadBalance = "roundRobin";
	private String weight;
	private AtomDataSourceMetaVO writeDataSourceMetaVO;
	private List<AtomDataSourceMetaVO> readDataSourceMetaVOs = new ArrayList<AtomDataSourceMetaVO>();

	public void addReadDataSourceMetaVO(AtomDataSourceMetaVO readDataSourceMetaVO) {
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

	public Map<String, String> getReadKeys() {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < readDataSourceMetaVOs.size(); i++) {
			String name = this.name + "_read" + i;
			map.put(name, name);
		}
		return map;
	}

	public String getWriteKey() {
		return this.name + "_write";
	}

	public Object getLoadBalanceArgs() {
		if (this.loadBalance.equalsIgnoreCase("random")) {
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < readDataSourceMetaVOs.size(); i++) {
				list.add(this.name + "_read" + i);
			}
			return list;
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		int i = 0;
		for (String weight : getWeights()) {
			map.put(this.name + "_read" + i++, Integer.parseInt(weight));
		}
		return map;
	}

	public Object getMatrixLoadBalanceArgs() {
		if (this.loadBalance.equalsIgnoreCase("random")) {
			List<String> list = new ArrayList<String>();
			for (int i = 1; i <= readDataSourceMetaVOs.size(); i++) {
				if (i > 9) {
					list.add("read" + i);
				} else {
					list.add("read0" + i);
				}
			}
			return list;
		}

		Map<String, Integer> map = new HashMap<String, Integer>();
		int i = 1;
		for (String weight : getWeights()) {
			int index = i++;
			if (index > 9) {
				map.put("read" + index, Integer.parseInt(weight));
			} else {
				map.put("read0" + index, Integer.parseInt(weight));
			}
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

	public String getReadWriteKeyBeanDefinitionName() {
		return this.name + "_key";
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

	public AtomDataSourceMetaVO getWriteDataSourceMetaVO() {
		return writeDataSourceMetaVO;
	}

	public void setWriteDataSourceMetaVO(AtomDataSourceMetaVO writeDataSourceMetaVO) {
		this.writeDataSourceMetaVO = writeDataSourceMetaVO;
	}

	public List<AtomDataSourceMetaVO> getReadDataSourceMetaVOs() {
		return readDataSourceMetaVOs;
	}

	public void setReadDataSourceMetaVOs(List<AtomDataSourceMetaVO> readDataSourceMetaVOs) {
		this.readDataSourceMetaVOs = readDataSourceMetaVOs;
	}

	public Class<?> getLoadBalanceClass() {
		if (this.loadBalance.equalsIgnoreCase("random")) {
			return RandomLoadBalanceStrategy.class;
		}
		return RoundRobinLoadBalanceStrategy.class;
	}

}
