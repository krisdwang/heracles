package heracles.data.cache.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class OrderModel implements Serializable {

	private static final long serialVersionUID = -290261355037258056L;

	private BigInteger id;

	private BigInteger productId;

	private BigInteger customerId;

	private String productName;

	private BigDecimal price;

	private Date updateTime;

	private Date createTime;

	private Map<KeyModel, ValueModel> map = new HashMap<KeyModel, ValueModel>(1) {
		private static final long serialVersionUID = 7732641334327342986L;

		{
			put(new KeyModel(new BigInteger("9001"), "key9001"), new ValueModel(new BigInteger("6002"), "value6002"));
		}
	};

	static class KeyModel implements Serializable {
		private static final long serialVersionUID = 1209158816629005029L;

		private BigInteger id;
		private String key;

		public KeyModel(BigInteger id, String key) {
			super();
			this.id = id;
			this.key = key;
		}

	}

	static class ValueModel implements Serializable {
		private static final long serialVersionUID = -5903392626273253005L;

		private BigInteger id;

		private String value;

		public ValueModel(BigInteger id, String value) {
			super();
			this.id = id;
			this.value = value;
		}

	}

}
