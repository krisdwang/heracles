package heracles.data.cache.serializer;

import heracles.data.cache.redis.serializer.HeraclesRedisSerializer;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class HeraclesJdkSerializer implements HeraclesRedisSerializer<Object> {

	private RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer();

	@Override
	public byte[] serialize(Object t) throws SerializationException {
		return serializer.serialize(t);
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		return serializer.deserialize(bytes);
	}

}
