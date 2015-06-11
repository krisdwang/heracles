package heracles.jdbc.rule;

public class ItemRule {
	private String dbRule;
	private String dbIndexes;
	private String tbRule;
	private String tbSuffixes;
	private boolean isMaster = false;

	public String getDbRule() {
		return dbRule;
	}

	public void setDbRule(String dbRule) {
		this.dbRule = dbRule;
	}

	public String getTbRule() {
		return tbRule;
	}

	public void setTbRule(String tbRule) {
		this.tbRule = tbRule;
	}

	public String getDbIndexes() {
		return dbIndexes;
	}

	public void setDbIndexes(String dbIndexes) {
		this.dbIndexes = dbIndexes;
	}

	public String getTbSuffixes() {
		return tbSuffixes;
	}

	public void setTbSuffixes(String tbSuffixes) {
		this.tbSuffixes = tbSuffixes;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

}
