package br.com.fixturefactory.function;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import br.com.fixturefactory.util.Chainable;
import br.com.fixturefactory.util.ReflectionUtils;

public class AssociationFunction implements RelationFunction, Chainable {

	private FixtureFunction fixtureFunction;
	
	private String targetAttribute;
	
	public AssociationFunction(FixtureFunction fixtureFunction) {
		this.fixtureFunction = fixtureFunction;
	}
	
	public AssociationFunction(FixtureFunction fixtureFunction, String targetAttribute) {
		this(fixtureFunction);
		this.targetAttribute = targetAttribute;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T generateValue(Object owner) {
		Object target = fixtureFunction.generateValue();
		
		if (target instanceof Collection<?>) {
			for (Object item : (Collection<?>) target) {
				this.setField(item, owner);			}
		} else {
			this.setField(target, owner);
		}
		
		return (T) target;
	}
	
	private void setField(Object target, Object value) {
		String fieldName = StringUtils.isBlank(targetAttribute)? this.getField(target.getClass(), value.getClass()).getName() : targetAttribute;
		ReflectionUtils.invokeRecursiveSetter(target, fieldName, value);
	}
	
	private Field getField(Class<?> clazz, Class<?> fieldType) {
		Field searchdField = null;
		
		for (Field field : clazz.getDeclaredFields()) {
			if (ClassUtils.isAssignable(field.getType(), fieldType)) {
				searchdField = field;
				break;
			}
		}
		
		return searchdField;
	}

	@Override
	public Function of(Class<?> clazz, String label) {
		ReflectionUtils.invokeRecursiveSetter(fixtureFunction, "clazz", clazz);
		ReflectionUtils.invokeRecursiveSetter(fixtureFunction, "label", label);
		return this;
	}
	
}
