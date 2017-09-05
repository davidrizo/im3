/*
 * Created on 21-mar-2005
 */
package es.ua.dlsi.im3.core.utils;

import java.util.Iterator;
import java.util.Map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Class for constructing histograms when the number of classes is not known
 *
 * @author drizo
 * @param <ContentType>
 *            The type must implement the hashCode method
 */
public class Histogram<ContentType extends Comparable<ContentType>> {

	/**
	 * Table of element + occurrences. The element must contain an hashCode
	 * operator
	 */
	private final HashMap<ContentType, Long> hashMap;
	/**
	 * Total of symbols
	 */
	private long total;

	/**
	 * Constructor
	 */
	public Histogram() {
		hashMap = new HashMap<>();
		total = 0;
	}

	public Histogram(Collection<ContentType> initialContent) {
		hashMap = new HashMap<>();
		total = 0;
		for (ContentType object : initialContent) {
			addElement(object);
		}
	}

	/**
	 * Constructor Use it when you want to have always the same keys. Their
	 * values are initialized to 1
	 *
	 * @param keys
	 */
	public Histogram(ContentType[] keys) {
		hashMap = new HashMap<>();
		total = 0;
		for (int i = 0; i < keys.length; i++) {
			addElement(keys[i]);
		}
	}

	/**
	 * Register an element. If the element already exists, the count is
	 * incremented. If not, a new element is added and initialized to 1
	 *
	 * @param element
	 */
	public final void addElement(ContentType element) {
		if (element == null) {
			throw new RuntimeException("The element cannot be null");
		}
		addElement(element, 1);
	}

	/**
	 * Register an element. If the element already exists, the count is
	 * incremented by count. If not, a new element is added and initialized to
	 * count
	 *
	 * @param element
	 * @param count
	 */
	public final void addElement(ContentType element, long count) {
		if (element == null) {
			throw new RuntimeException("The element cannot be null");
		}

		Long value = hashMap.get(element);
		if (value == null) {
			Long icount = count;
			hashMap.put(element, icount);
		} else {
			hashMap.put(element, value + count);
		}
		total += count;
	}

	/**
	 * It returns the number of times the element is added
	 *
	 * @param element
	 * @return
	 */
	public long getCountOfElement(ContentType element) {
		Long value = hashMap.get(element);
		if (value == null) {
			return 0;
		} else {
			return (value);
		}
	}

	/**
	 * Print percentages
	 *
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (Iterator<ContentType> iter = hashMap.keySet().iterator(); iter.hasNext();) {
			sb.append(iter.next().toString());
			sb.append('\t');
		}
		sb.append('\n');
		for (Iterator<ContentType> iter = hashMap.keySet().iterator(); iter.hasNext();) {
			ContentType key = iter.next();
			Long ivalue = hashMap.get(key);
			// long value = (long) (100.0 * (float)ivalue.longValue() /
			// (float)total);
			long value = ivalue;
			sb.append(value);
			sb.append('\t');
		}
		sb.append('\n');

		return sb.toString();
	}

	public String toOrderedDecreasingString() {
		return toOrderedDecreasingString(true);
	}
	public String toOrderedDecreasingString(boolean printAsColumns) {
		StringBuilder sb = new StringBuilder();

		List<Map.Entry<ContentType, Long>> sorted = new ArrayList<>();
		sorted.addAll(hashMap.entrySet());

		Collections.sort(sorted, new Comparator<Map.Entry<ContentType, Long>>() {
			@Override
			public int compare(Map.Entry<ContentType, Long> o1, Map.Entry<ContentType, Long> o2) {
				if (o1.getValue() > o2.getValue()) {
					return -1;
				} else if (o1.getValue() < o2.getValue()) {
					return 1;
				} else {
					return o1.getKey().compareTo(o2.getKey());
				}
			}
		});

		if (printAsColumns) {
			for (Iterator<Map.Entry<ContentType, Long>> iter = sorted.iterator(); iter.hasNext();) {
				sb.append(iter.next().getKey());
				sb.append('\t');
			}
			sb.append('\n');
			for (Iterator<Map.Entry<ContentType, Long>> iter = sorted.iterator(); iter.hasNext();) {
				Long ivalue = iter.next().getValue();
				// long value = (long) (100.0 * (float)ivalue.longValue() /
				// (float)total);
				sb.append(ivalue);
				sb.append('\t');
			}
			sb.append('\n');
		} else {
			for (Iterator<Map.Entry<ContentType, Long>> iter = sorted.iterator(); iter.hasNext();) {
				Map.Entry<ContentType, Long> entry = iter.next();
				// long value = (long) (100.0 * (float)ivalue.longValue() /
				// (float)total);
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
				sb.append('\n');
			}
			sb.append('\n');
				
		}

		return sb.toString();
	}

	/**
	 * Prlong percentages
	 *
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toStringGNUPlot() {
		StringBuilder sb = new StringBuilder();

		for (Iterator<ContentType> iter = hashMap.keySet().iterator(); iter.hasNext();) {
			ContentType key = iter.next();

			sb.append(key.toString());
			sb.append('\t');
			Long ivalue = hashMap.get(key);
			// long value = (long) (100.0 * (float)ivalue.longValue() /
			// (float)total);
			long value = ivalue;
			sb.append(value);

			sb.append('\n');

		}
		sb.append('\n');

		return sb.toString();
	}

	/**
	 * Print percentages
	 *
	 * @see java.lang.Object#toString()
	 */
	public void printHistogram() {
		for (Iterator<ContentType> iter = hashMap.keySet().iterator(); iter.hasNext();) {
			ContentType key = iter.next();
			Long ivalue = hashMap.get(key);
			long value;
			value = ivalue;
			System.out.println(value + ";" + key.toString());
		}
	}
	
	public void printHistogramProbabilities() {
		for (Iterator<ContentType> iter = hashMap.keySet().iterator(); iter.hasNext();) {
			ContentType key = iter.next();
			double prob = getProbability(key);
			System.out.println(prob + ";" + key.toString());
		}
	}
	

	/**
	 * Prlong percentages
	 *
	 * @param keys
	 * @return
	 * @throws IMException
	 * @see java.lang.Object#toStringWithoutHeader()
	 */
	public String toStringWithoutHeader(ContentType[] keys) throws IM3Exception {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < keys.length; i++) {
			if (sb.length() > 0) {
				sb.append(',');
			}
			Long ivalue = hashMap.get(keys[i]);
			if (ivalue == null) {
				throw new IM3Exception("The key " + keys[i].toString() + " is not found in the histogram");
			}
			// long value = (long) (100.0 * (float)ivalue.longValue() /
			// (float)total);
			long value = ivalue;
			sb.append(value);
		}
		return sb.toString();
	}

	/**
	 * Return the most repeated element (the one with the highest value)
	 *
	 * @return
	 */
	public ContentType getMaxElement() {
		long max = -1;
		ContentType maxKey = null;
		for (Iterator<Map.Entry<ContentType, Long>> iter = hashMap.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<ContentType, Long> entry = iter.next();
			Long ivalue = entry.getValue();
			long value = ivalue;
			if (value > max) {
				max = value;
				maxKey = entry.getKey();
			}
		}
		return maxKey;
	}

	/**
	 * It returns the number of distinct symbols
	 *
	 * @return
	 */
	public long size() {
		return hashMap.size();
	}

	/**
	 * Return the total num of symbols
	 *
	 * @return
	 */
	public long getCountOfElements() {
		/*
		 * long count = 0; for (Iterator iter = hash.entrySet().iterator();
		 * iter.hasNext();) { Map.Entry entry = (Map.Entry) iter.next(); Long
		 * ivalue = (Long) entry.getValue(); long value = ivalue.longValue();
		 * count += value; }
		 * 
		 * return count;
		 */
		return total;
	}

	/**
	 * True if element is contained
	 *
	 * @param element
	 * @return
	 */
	public boolean contains(ContentType element) {
		return hashMap.containsKey(element);
	}

	/**
	 * This method should be used to improve the performance, not to manipulate
	 * the objects directly
	 * 
	 * @return
	 */
	public Iterator<Map.Entry<ContentType, Long>> entrySetIterator() {
		return hashMap.entrySet().iterator();
	}

	public double getProbability(ContentType element) {
		long count = getCountOfElement(element);
		return (double) count / (double) total;
	}

	public double getAverage() {
		double count = 0;
		double sum = 0;
		for (Iterator<Map.Entry<ContentType, Long>> iter = hashMap.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<ContentType, Long> entry = iter.next();
			sum += entry.getValue();
			count++;
		}
		return sum / count;
	}

	public Set<ContentType> getKeys() {
		return hashMap.keySet();
	}

}
