package heracles.core.beans.mapping.orika;

import heracles.core.beans.mapping.BeanMapper;
import heracles.core.beans.mapping.MapClassScanner;
import heracles.core.beans.mapping.annotation.MapClass;
import heracles.core.beans.mapping.annotation.MapField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.MappingStrategy;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * A bean mapper which implements the {@link BeanMapper}, and base on orika object mapping framework
 * <p>
 * Also it support some enhanced features from orika by implements {@link MapperFacade}
 * 
 * @author kriswang
 *
 */
public class OrikaBeanMapper implements ApplicationContextAware, InitializingBean, MapperFacade, BeanMapper {

	private final Logger logger = LoggerFactory.getLogger(OrikaBeanMapper.class);

	@Getter
	@Setter
	private MapperFactory factory;

	@Getter
	private MapperFacade facade;

	private ApplicationContext applicationContext;

	@Getter
	@Setter
	private Class<? extends Annotation> annotationClass = MapClass.class;

	@Getter
	@Setter
	private Class<? extends Annotation> annotationField = MapField.class;

	@Getter
	@Setter
	private String basePackage;

	private final MapClassScanner scanner = new MapClassScanner();

	public OrikaBeanMapper() {

	}

	protected void init() {

		if (null == factory) {
			DefaultMapperFactory.Builder factoryBuilder = new DefaultMapperFactory.Builder();
			/*
			 * Apply optional user customizations to the factory builder
			 */
			configureFactoryBuilder(factoryBuilder);
			factory = factoryBuilder.build();
		}
		/*
		 * Apply customizations/configurations
		 */
		configure(factory);
	}

	@Override
	public void afterPropertiesSet() {
		Assert.hasText(basePackage, "basePackage must be configured");
		init();
		scanConverter();
		scanMapper();
		scanMapping();
		facade = factory.getMapperFacade();
	}

	/**
	 * used to customize the factory
	 */
	protected void configure(final MapperFactory factory) {
	}

	/**
	 * used to customize the factoryBuilder
	 */
	protected void configureFactoryBuilder(DefaultMapperFactory.Builder factoryBuilder) {

	}

	/**
	 * find out all the customize converters which already be registered as spring bean, then register them in
	 * MapperFactory
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void scanConverter() {
		final Map<String, Converter> converters = applicationContext.getBeansOfType(Converter.class);
		for (final Converter converter : converters.values()) {
			factory.getConverterFactory().registerConverter(converter);
		}
	}

	/**
	 * find out all the customize mappers which already be registered as spring bean, then register them in
	 * MapperFactory
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void scanMapper() {
		final Map<String, Mapper> mappers = applicationContext.getBeansOfType(Mapper.class);
		for (final Mapper mapper : mappers.values()) {
			factory.classMap(mapper.getAType(), mapper.getBType()).byDefault().customize((Mapper) mapper).register();
		}
	}

	/**
	 * scan classes in defined package and find out the {@link MapClass} annotated class
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void scanMapping() throws IllegalArgumentException {

		scanner.addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
		Collection<Class<?>> clazzCollection = scanner.scan(basePackage);
		for (Class<?> clazz : clazzCollection) {
			MapClass mapClassAnnotation = (MapClass) clazz.getAnnotation(annotationClass);
			if (StringUtils.isEmpty(mapClassAnnotation.value())) {
				throw new IllegalArgumentException("MapClass annotation do not have value in class: " + clazz + "");
			}
			Class destinationClass = null;
			try {
				destinationClass = ClassUtils.resolveClassName(mapClassAnnotation.value(),
						ClassUtils.getDefaultClassLoader());
			} catch (IllegalArgumentException iaex) {
				// when no mapping class found, continue
				logger.warn("can not find mapping class [" + mapClassAnnotation.value() + "] for [" + clazz + "]");
				continue;
			}
			// build class map by get the A B class
			ClassMapBuilder classMapBuilder = factory.classMap(clazz, destinationClass);
			logger.info("create mapping: [" + clazz + "] === [" + destinationClass + "]");
			// use reflection to find meta data and define the mapping
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(MapField.class)) {
					MapField mapFieldAnnotation = (MapField) field.getAnnotation(annotationField);
					if (StringUtils.isEmpty(mapFieldAnnotation.value())
							&& StringUtils.isEmpty(mapFieldAnnotation.complexMap())) {
						throw new IllegalArgumentException(
								"MapField annotation must have one of value and complexMap setting on Field: " + field);
					}
					if (!StringUtils.isEmpty(mapFieldAnnotation.value())
							&& !StringUtils.isEmpty(mapFieldAnnotation.complexMap())) {
						throw new IllegalArgumentException(
								"MapField annotation must just only set one of value and complexMap setting on Field: "
										+ field);
					}
					if (!StringUtils.isEmpty(mapFieldAnnotation.value())) {
						String fieldNameA = field.getName();
						String fieldNameB = mapFieldAnnotation.value();
						// add field mapping
						classMapBuilder.field(fieldNameA, fieldNameB);
					} else if (!StringUtils.isEmpty(mapFieldAnnotation.complexMap())) {
						try {
							String complexMap = mapFieldAnnotation.complexMap();
							String[] maps = complexMap.split(MapField.MULTI_MAP_DELIMITERS);
							for (String map : maps) {
								String[] mapping = map.split(MapField.MAP_DELIMITERS);
								String fieldNameA = mapping[0];
								String fieldNameB = mapping[1];
								classMapBuilder.field(fieldNameA, fieldNameB);
							}
						} catch (Exception ex) {
							throw new IllegalArgumentException(
									"MapField annotation's parameter:complexMap must be in wrong format, on Field: "
											+ field, ex);
						}

					}

				}
			}
			// whatever, use default
			classMapBuilder.byDefault();
			// Register this map
			classMapBuilder.register();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Delegate methods for MapperFacade;
	 */

	public <S, D> D map(S sourceObject, Class<D> destinationClass) {
		return facade.map(sourceObject, destinationClass);
	}

	public <S, D> D map(S sourceObject, Class<D> destinationClass, MappingContext context) {
		return facade.map(sourceObject, destinationClass, context);
	}

	public <S, D> void map(S sourceObject, D destinationObject) {
		facade.map(sourceObject, destinationObject);
	}

	public <S, D> void map(S sourceObject, D destinationObject, MappingContext context) {
		facade.map(sourceObject, destinationObject, context);
	}

	public <S, D> void map(S sourceObject, D destinationObject, Type<S> sourceType, Type<D> destinationType) {
		facade.map(sourceObject, destinationObject, sourceType, destinationType);
	}

	public <S, D> void map(S sourceObject, D destinationObject, Type<S> sourceType, Type<D> destinationType,
			MappingContext context) {
		facade.map(sourceObject, destinationObject, sourceType, destinationType, context);
	}

	public <S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass) {
		return facade.mapAsSet(source, destinationClass);
	}

	public <S, D> Set<D> mapAsSet(Iterable<S> source, Class<D> destinationClass, MappingContext context) {
		return facade.mapAsSet(source, destinationClass, context);
	}

	public <S, D> Set<D> mapAsSet(S[] source, Class<D> destinationClass) {
		return facade.mapAsSet(source, destinationClass);
	}

	public <S, D> Set<D> mapAsSet(S[] source, Class<D> destinationClass, MappingContext context) {
		return facade.mapAsSet(source, destinationClass, context);
	}

	public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
		return facade.mapAsList(source, destinationClass);
	}

	public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass, MappingContext context) {
		return facade.mapAsList(source, destinationClass, context);
	}

	public <S, D> List<D> mapAsList(S[] source, Class<D> destinationClass) {
		return facade.mapAsList(source, destinationClass);
	}

	public <S, D> List<D> mapAsList(S[] source, Class<D> destinationClass, MappingContext context) {
		return facade.mapAsList(source, destinationClass, context);
	}

	public <S, D> D[] mapAsArray(D[] destination, Iterable<S> source, Class<D> destinationClass) {
		return facade.mapAsArray(destination, source, destinationClass);
	}

	public <S, D> D[] mapAsArray(D[] destination, S[] source, Class<D> destinationClass) {
		return facade.mapAsArray(destination, source, destinationClass);
	}

	public <S, D> D[] mapAsArray(D[] destination, Iterable<S> source, Class<D> destinationClass, MappingContext context) {
		return facade.mapAsArray(destination, source, destinationClass, context);
	}

	public <S, D> D[] mapAsArray(D[] destination, S[] source, Class<D> destinationClass, MappingContext context) {
		return facade.mapAsArray(destination, source, destinationClass, context);
	}

	public <S, D> D map(S sourceObject, Type<S> sourceType, Type<D> destinationType) {
		return facade.map(sourceObject, sourceType, destinationType);
	}

	public <S, D> D map(S sourceObject, Type<S> sourceType, Type<D> destinationType, MappingContext context) {
		return facade.map(sourceObject, sourceType, destinationType, context);
	}

	public <S, D> Set<D> mapAsSet(Iterable<S> source, Type<S> sourceType, Type<D> destinationType) {
		return facade.mapAsSet(source, sourceType, destinationType);
	}

	public <S, D> Set<D> mapAsSet(Iterable<S> source, Type<S> sourceType, Type<D> destinationType,
			MappingContext context) {
		return facade.mapAsSet(source, sourceType, destinationType, context);
	}

	public <S, D> Set<D> mapAsSet(S[] source, Type<S> sourceType, Type<D> destinationType) {
		return facade.mapAsSet(source, sourceType, destinationType);
	}

	public <S, D> Set<D> mapAsSet(S[] source, Type<S> sourceType, Type<D> destinationType, MappingContext context) {
		return facade.mapAsSet(source, sourceType, destinationType, context);
	}

	public <S, D> List<D> mapAsList(Iterable<S> source, Type<S> sourceType, Type<D> destinationType) {
		return facade.mapAsList(source, sourceType, destinationType);
	}

	public <S, D> List<D> mapAsList(Iterable<S> source, Type<S> sourceType, Type<D> destinationType,
			MappingContext context) {
		return facade.mapAsList(source, sourceType, destinationType, context);
	}

	public <S, D> List<D> mapAsList(S[] source, Type<S> sourceType, Type<D> destinationType) {
		return facade.mapAsList(source, sourceType, destinationType);
	}

	public <S, D> List<D> mapAsList(S[] source, Type<S> sourceType, Type<D> destinationType, MappingContext context) {
		return facade.mapAsList(source, sourceType, destinationType, context);
	}

	public <S, D> D[] mapAsArray(D[] destination, Iterable<S> source, Type<S> sourceType, Type<D> destinationType) {
		return facade.mapAsArray(destination, source, sourceType, destinationType);
	}

	public <S, D> D[] mapAsArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType) {
		return facade.mapAsArray(destination, source, sourceType, destinationType);
	}

	public <S, D> D[] mapAsArray(D[] destination, Iterable<S> source, Type<S> sourceType, Type<D> destinationType,
			MappingContext context) {
		return facade.mapAsArray(destination, source, sourceType, destinationType, context);
	}

	public <S, D> D[] mapAsArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType,
			MappingContext context) {
		return facade.mapAsArray(destination, source, sourceType, destinationType, context);
	}

	public <S, D> void mapAsCollection(Iterable<S> source, Collection<D> destination, Class<D> destinationClass) {
		facade.mapAsCollection(source, destination, destinationClass);
	}

	public <S, D> void mapAsCollection(Iterable<S> source, Collection<D> destination, Class<D> destinationClass,
			MappingContext context) {
		facade.mapAsCollection(source, destination, destinationClass, context);
	}

	public <S, D> void mapAsCollection(S[] source, Collection<D> destination, Class<D> destinationCollection) {
		facade.mapAsCollection(source, destination, destinationCollection);
	}

	public <S, D> void mapAsCollection(S[] source, Collection<D> destination, Class<D> destinationCollection,
			MappingContext context) {
		facade.mapAsCollection(source, destination, destinationCollection, context);
	}

	public <S, D> void mapAsCollection(Iterable<S> source, Collection<D> destination, Type<S> sourceType,
			Type<D> destinationType) {
		facade.mapAsCollection(source, destination, sourceType, destinationType);
	}

	public <S, D> void mapAsCollection(S[] source, Collection<D> destination, Type<S> sourceType,
			Type<D> destinationType) {
		facade.mapAsCollection(source, destination, sourceType, destinationType);
	}

	public <S, D> void mapAsCollection(Iterable<S> source, Collection<D> destination, Type<S> sourceType,
			Type<D> destinationType, MappingContext context) {
		facade.mapAsCollection(source, destination, sourceType, destinationType, context);
	}

	public <S, D> void mapAsCollection(S[] source, Collection<D> destination, Type<S> sourceType,
			Type<D> destinationType, MappingContext context) {
		facade.mapAsCollection(source, destination, sourceType, destinationType, context);
	}

	public <S, D> D convert(S source, Type<S> sourceType, Type<D> destinationType, String converterId) {
		return facade.convert(source, sourceType, destinationType, converterId);
	}

	public <S, D> D convert(S source, Class<D> destinationClass, String converterId) {
		return facade.convert(source, destinationClass, converterId);
	}

	public <S, D> D newObject(S source, Type<? extends D> destinationClass, MappingContext context) {
		return facade.newObject(source, destinationClass, context);
	}

	public <Sk, Sv, Dk, Dv> Map<Dk, Dv> mapAsMap(Map<Sk, Sv> source, Type<? extends Map<Sk, Sv>> sourceType,
			Type<? extends Map<Dk, Dv>> destinationType) {
		return facade.mapAsMap(source, sourceType, destinationType);
	}

	public <Sk, Sv, Dk, Dv> Map<Dk, Dv> mapAsMap(Map<Sk, Sv> source, Type<? extends Map<Sk, Sv>> sourceType,
			Type<? extends Map<Dk, Dv>> destinationType, MappingContext context) {
		return facade.mapAsMap(source, sourceType, destinationType, context);
	}

	public <S, Dk, Dv> Map<Dk, Dv> mapAsMap(Iterable<S> source, Type<S> sourceType,
			Type<? extends Map<Dk, Dv>> destinationType) {
		return facade.mapAsMap(source, sourceType, destinationType);
	}

	public <S, Dk, Dv> Map<Dk, Dv> mapAsMap(Iterable<S> source, Type<S> sourceType,
			Type<? extends Map<Dk, Dv>> destinationType, MappingContext context) {
		return facade.mapAsMap(source, sourceType, destinationType, context);
	}

	public <S, Dk, Dv> Map<Dk, Dv> mapAsMap(S[] source, Type<S> sourceType, Type<? extends Map<Dk, Dv>> destinationType) {
		return facade.mapAsMap(source, sourceType, destinationType);
	}

	public <S, Dk, Dv> Map<Dk, Dv> mapAsMap(S[] source, Type<S> sourceType,
			Type<? extends Map<Dk, Dv>> destinationType, MappingContext context) {
		return facade.mapAsMap(source, sourceType, destinationType, context);
	}

	public <Sk, Sv, D> List<D> mapAsList(Map<Sk, Sv> source, Type<? extends Map<Sk, Sv>> sourceType,
			Type<D> destinationType) {
		return facade.mapAsList(source, sourceType, destinationType);
	}

	public <Sk, Sv, D> List<D> mapAsList(Map<Sk, Sv> source, Type<? extends Map<Sk, Sv>> sourceType,
			Type<D> destinationType, MappingContext context) {
		return facade.mapAsList(source, sourceType, destinationType, context);
	}

	public <Sk, Sv, D> Set<D> mapAsSet(Map<Sk, Sv> source, Type<? extends Map<Sk, Sv>> sourceType,
			Type<D> destinationType) {
		return facade.mapAsSet(source, sourceType, destinationType);
	}

	public <Sk, Sv, D> Set<D> mapAsSet(Map<Sk, Sv> source, Type<? extends Map<Sk, Sv>> sourceType,
			Type<D> destinationType, MappingContext context) {
		return facade.mapAsSet(source, sourceType, destinationType, context);
	}

	public <Sk, Sv, D> D[] mapAsArray(D[] destination, Map<Sk, Sv> source, Type<? extends Map<Sk, Sv>> sourceType,
			Type<D> destinationType) {
		return facade.mapAsArray(destination, source, sourceType, destinationType);
	}

	public <Sk, Sv, D> D[] mapAsArray(D[] destination, Map<Sk, Sv> source, Type<? extends Map<Sk, Sv>> sourceType,
			Type<D> destinationType, MappingContext context) {
		return facade.mapAsArray(destination, source, sourceType, destinationType, context);
	}

	public <S, D> MappingStrategy resolveMappingStrategy(S sourceObject, java.lang.reflect.Type rawAType,
			java.lang.reflect.Type rawBType, boolean mapInPlace, MappingContext context) {
		return facade.resolveMappingStrategy(sourceObject, rawAType, rawBType, mapInPlace, context);
	}

	public <S, D> BoundMapperFacade<S, D> dedicatedMapperFor(Type<S> sourceType, Type<D> destinationType) {
		return factory.getMapperFacade(sourceType, destinationType);
	}

	public <S, D> BoundMapperFacade<S, D> dedicatedMapperFor(Type<S> sourceType, Type<D> destinationType,
			boolean containsCycles) {
		return factory.getMapperFacade(sourceType, destinationType, containsCycles);
	}

	public <A, B> BoundMapperFacade<A, B> dedicatedMapperFor(Class<A> aType, Class<B> bType) {
		return factory.getMapperFacade(aType, bType);
	}

	public <A, B> BoundMapperFacade<A, B> dedicatedMapperFor(Class<A> aType, Class<B> bType, boolean containsCycles) {
		return factory.getMapperFacade(aType, bType, containsCycles);
	}

	public void factoryModified(MapperFactory factory) {
		facade.factoryModified(factory);
	}

}
