package heracles.data.cache.serializer;

import heracles.data.cache.redis.serializer.HeraclesRedisSerializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class HeraclesStringSerializer implements HeraclesRedisSerializer<String> {

	private RedisSerializer<String> serializer = new StringRedisSerializer();

	@Override
	public byte[] serialize(String t) throws SerializationException {
		return serializer.serialize(t);
	}

	@Override
	public String deserialize(byte[] bytes) throws SerializationException {
		return serializer.deserialize(bytes);
	}
}
