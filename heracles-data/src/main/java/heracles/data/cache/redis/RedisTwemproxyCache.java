package heracles.data.cache.redis;

import java.util.Arrays;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

@SuppressWarnings("unchecked")
public class RedisTwemproxyCache implements Cache {

	//private static final int PAGE_SIZE = 128;
	private final String name;
	@SuppressWarnings("rawtypes") private final RedisTemplate template;
	private final byte[] prefix;
	//private final byte[] setName;
	//private final byte[] cacheLockName;
	//private long WAIT_FOR_LOCK = 300;
	private final long expiration;
	
	public RedisTwemproxyCache(String name, byte[] prefix, RedisTemplate<? extends Object, ? extends Object> template, long expiration) {

		Assert.hasText(name, "non-empty cache name is required");
		this.name = name;
		this.template = template;
		this.prefix = prefix;
		this.expiration = expiration;

		//StringRedisSerializer stringSerializer = new StringRedisSerializer();

		// name of the set holding the keys
		//this.setName = stringSerializer.serialize(name + "~keys");
		//this.cacheLockName = stringSerializer.serialize(name + "~lock");
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getNativeCache() {
		return template;
	}

	@Override
	public ValueWrapper get(final Object key) {
		return (ValueWrapper) template.execute(new RedisCallback<ValueWrapper>() {

			public ValueWrapper doInRedis(RedisConnection connection) throws DataAccessException {
				waitForLock(connection);
				byte[] bs = connection.get(computeKey(key));
				Object value = template.getValueSerializer() != null ? template.getValueSerializer().deserialize(bs) : bs;
				return (bs == null ? null : new SimpleValueWrapper(value));
			}
		}, true);
	}

	
	@Override
	public <T> T get(Object key, Class<T> type) {
		ValueWrapper wrapper = get(key);
		return wrapper == null ? null : (T) wrapper.get();
	}

	@Override
	public void put(Object key, Object value) {
		final byte[] keyBytes = computeKey(key);
		final byte[] valueBytes = convertToBytesIfNecessary(template.getValueSerializer(), value);

		template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				waitForLock(connection);

				connection.set(keyBytes, valueBytes);

				if (expiration > 0) {
					connection.expire(keyBytes, expiration);
				}
				//connection.exec();
				return null;
			}
		}, true);
	}

	@Override
	public void evict(Object key) {
		final byte[] k = computeKey(key);
		template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.del(k);
				return null;
			}
		}, true);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Failed to clear redis cache, flushAll, flushDb are not supported");
		/*
		template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushAll();
				return null;
			}
		}, true);
		*/

	}
	
	public ValueWrapper putIfAbsent(Object key, final Object value) {

		final byte[] keyBytes = computeKey(key);
		final byte[] valueBytes = convertToBytesIfNecessary(template.getValueSerializer(), value);

		return toWrapper(template.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				waitForLock(connection);

				Object resultValue = value;
				boolean valueWasSet = connection.setNX(keyBytes, valueBytes);
				if (valueWasSet) {
					if (expiration > 0) {
						connection.expire(keyBytes, expiration);
					}
				} else {
					resultValue = deserializeIfNecessary(template.getValueSerializer(), connection.get(keyBytes));
				}

				return resultValue;
			}
		}, true));
	}
	
	private ValueWrapper toWrapper(Object value) {
		return (value != null ? new SimpleValueWrapper(value) : null);
	}
	
	private boolean waitForLock(RedisConnection connection) {

		//boolean retry;
		boolean foundLock = false;
		/*
		do {
			retry = false;
			if (connection.exists(cacheLockName)) {
				foundLock = true;
				try {
					Thread.sleep(WAIT_FOR_LOCK);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				retry = true;
			}
		} while (retry);
		*/
		return foundLock;
	}
	
	private byte[] computeKey(Object key) {

		byte[] keyBytes = convertToBytesIfNecessary(template.getKeySerializer(), key);

		if (prefix == null || prefix.length == 0) {
			return keyBytes;
		}

		byte[] result = Arrays.copyOf(prefix, prefix.length + keyBytes.length);
		System.arraycopy(keyBytes, 0, result, prefix.length, keyBytes.length);

		return result;
	}
	
	private byte[] convertToBytesIfNecessary(RedisSerializer<Object> serializer, Object value) {

		if (serializer == null && value instanceof byte[]) {
			return (byte[]) value;
		}

		return serializer.serialize(value);
	}
	
	private Object deserializeIfNecessary(RedisSerializer<byte[]> serializer, byte[] value) {

		if (serializer != null) {
			return serializer.deserialize(value);
		}

		return value;
	}

}
