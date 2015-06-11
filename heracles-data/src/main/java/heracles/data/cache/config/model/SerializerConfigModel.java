package heracles.data.cache.config.model;

import lombok.Data;

@Data
public class SerializerConfigModel {

	private SerializerType keySerializerType;
	private String keySerializerClass;
	
	private SerializerType valueSerializerType;
	private String valueSerializerClass;
	
	private SerializerType hashKeySerializerType;
	private String hashKeySerializerClass;
	
	private SerializerType hashValueSerializerType;
	private String hashValueSerializerClass;
}
