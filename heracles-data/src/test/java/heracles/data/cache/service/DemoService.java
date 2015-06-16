package heracles.data.cache.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public class DemoService {

	private Map<String, String> orderMap = new HashMap<String, String>();
	private Map<String, OrderModel> orderObjectMap = new HashMap<String, OrderModel>();
	{
		orderMap.put("2101", "order1");
		orderMap.put("2102", "order2");
		orderMap.put("2103", "order3");
		
		OrderModel orderObj1 = new OrderModel();
		orderObjectMap.put("3101", orderObj1);
		orderObjectMap.put("4101", orderObj1);
		orderObj1.setId(new BigInteger("2101"));
		orderObj1.setProductId(new BigInteger("21011"));
		orderObj1.setCustomerId(new BigInteger("21012"));
		orderObj1.setProductName("orderObj1 product");
		orderObj1.setPrice(new BigDecimal("45.88"));
		orderObj1.setCreateTime(new Date());
		orderObj1.setUpdateTime(new Date());
		
		OrderModel orderObj2 = new OrderModel();
		orderObjectMap.put("3102", orderObj2);
		orderObjectMap.put("4102", orderObj2);
		orderObj2.setId(new BigInteger("2102"));
		orderObj2.setProductId(new BigInteger("21021"));
		orderObj2.setCustomerId(new BigInteger("21022"));
		orderObj2.setProductName("orderObj2 product");
		orderObj2.setPrice(new BigDecimal("45.89"));
		orderObj2.setCreateTime(new Date());
		orderObj2.setUpdateTime(new Date());
		
		OrderModel orderObj3 = new OrderModel();
		orderObjectMap.put("3103", orderObj3);
		orderObjectMap.put("4103", orderObj3);
		orderObj3.setId(new BigInteger("2103"));
		orderObj3.setProductId(new BigInteger("21031"));
		orderObj3.setCustomerId(new BigInteger("21032"));
		orderObj3.setProductName("orderObj3 product");
		orderObj3.setPrice(new BigDecimal("45.90"));
		orderObj3.setCreateTime(new Date());
		orderObj3.setUpdateTime(new Date());
	}
	
	public Map<String, String> getOrderMap() {
		return orderMap;
	}

	public Map<String, OrderModel> getOrderObjectMap() {
		return orderObjectMap;
	}

	@Cacheable(value = "orderCacheCluster")
	public String findOrder(String orderId) {
		return orderMap.get(orderId);
	}

	//@CacheEvict(value = "orderCacheCluster")
	public void deleteOrder(String orderId) {
		//orderMap.remove(orderId);
	}
	
	@Cacheable(value = "orderCacheCluster")
	public OrderModel findOrderObject(String orderId) {
		return orderObjectMap.get(orderId);
	}
	
	@CacheEvict(value = "orderCacheCluster")
	public void deleteOrderObject(String orderId) {
		//orderObjectMap.remove(orderId);
	}
}
