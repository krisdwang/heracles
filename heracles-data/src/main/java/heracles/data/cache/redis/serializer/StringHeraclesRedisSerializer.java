package heracles.data.cache.redis.serializer;

import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class StringHeraclesRedisSerializer implements HeraclesRedisSerializer<Object> {

	private StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

	@Override
	public byte[] serialize(Object obj) throws SerializationException {
		if (null == obj) {
			return null;
		}
		
		String target = null;
		if (obj instanceof String) {
			target = (String) obj;
		} else {
			target = obj.toString();
		}
		return stringRedisSerializer.serialize(target);
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		return (Object) stringRedisSerializer.deserialize(bytes);
	}

}
