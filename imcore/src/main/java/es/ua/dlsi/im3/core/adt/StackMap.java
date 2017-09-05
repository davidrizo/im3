package es.ua.dlsi.im3.core.adt;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Stack;

public class StackMap<KeyType, ValueType> extends Stack<Map.Entry<KeyType, ValueType>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5077004622389926507L;

	public void add(KeyType key, ValueType value) {
		super.add(new AbstractMap.SimpleImmutableEntry<>(key, value));		
	}
}
