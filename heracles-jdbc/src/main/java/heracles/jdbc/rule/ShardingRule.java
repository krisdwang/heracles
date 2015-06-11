package heracles.jdbc.rule;

import java.util.ArrayList;
import java.util.List;

public class ShardingRule {
	private String tbName;

	private List<ItemRule> itemRules = new ArrayList<ItemRule>();

	public String getTbName() {
		return tbName;
	}

	public void setTbName(String tbName) {
		this.tbName = tbName;
	}

	public List<ItemRule> getItemRules() {
		return itemRules;
	}

	public void setItemRules(List<ItemRule> itemRules) {
		this.itemRules = itemRules;
	}

	public void addItemRule(ItemRule itemRule) {
		this.itemRules.add(itemRule);
	}

}
