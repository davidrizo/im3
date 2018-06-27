package es.ua.dlsi.im3.analysis.hierarchical.io;

import es.ua.dlsi.im3.core.adt.tree.ITreeLabel;
import es.ua.dlsi.im3.core.io.ImportException;

import java.util.HashMap;
import java.util.Map.Entry;


public class AttributesLabel implements ITreeLabel {
	String tag;
	HashMap<String, String> attributes;
	/**
	 * <tag>text content</tag>
	 */
	String textContent;
	
	public AttributesLabel(String tag) {
		attributes = new HashMap<>();
		this.tag = tag;
	}
	
	
	public String getTag() {
		return tag;
	}


	@Override
	public String getStringLabel() {
		return attributes.toString();
	}

	@Override
	public String getColor() throws Exception {
		return null;
	}

	@Override
	public ITreeLabel clone() {
		AttributesLabel result = new AttributesLabel(tag);
		result.setTextContent(textContent);
		for (Entry<String, String> entry: attributes.entrySet()) {
			result.attributes.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

    @Override
    public Double getPredefinedHorizontalPosition() {
        return null;
    }

    public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}
	
	public String getAttribute(String name) throws ImportException {
		String result = attributes.get(name);
		if (result == null) {
			throw new ImportException("Missing attribute named '" + name + "'" + " in tag " + tag + " among attributes: " + attributes.toString());
		}
		return result;
	}

	public String getOptionalAttribute(String name) {
		return attributes.get(name);
	}


	public String getTextContent() {
		return textContent;
	}


	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	
	
	
}
