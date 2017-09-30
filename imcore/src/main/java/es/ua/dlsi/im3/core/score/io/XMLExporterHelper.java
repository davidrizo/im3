package es.ua.dlsi.im3.core.score.io;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import es.ua.dlsi.im3.core.IM3RuntimeException;


public class XMLExporterHelper {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	/**
	 * 
	 * @param tabs
	 * @param tag
	 * @param pairsAttributeValue Eg. role="composer" id="3" should be specified as the list "role" "composer" "id" "3"
	 */
	public static void start(StringBuilder sb, int tabs, String tag, String ... pairsAttributeValue) {
		if (pairsAttributeValue == null) {
			add(sb, tabs, "<", tag, ">\n");		
		} else {
			if (pairsAttributeValue.length % 2 != 0) {
				throw new IM3RuntimeException("Cannot build non-pair attributes");
			}
			StringBuilder sbAttributes = new StringBuilder();
			for (int i=0; i<pairsAttributeValue.length; i+=2) {
				sbAttributes.append(' ');
				sbAttributes.append(pairsAttributeValue[i]);
				sbAttributes.append('=');
				sbAttributes.append('"');
				sbAttributes.append(pairsAttributeValue[i+1]);
				sbAttributes.append('"');
			}
			add(sb, tabs, "<", tag, sbAttributes.toString(), ">\n");
		}				
	}
	
	public static void start(StringBuilder sb, int tabs, String tag, ArrayList<String> pairsAttributeValue) {
		start(sb, tabs, tag, pairsAttributeValue, ">\n");
	}
	public static void start(StringBuilder sb, int tabs, String tag, ArrayList<String> pairsAttributeValue, String tagTerminator) {
		if (pairsAttributeValue == null || pairsAttributeValue.isEmpty()) {
			add(sb, tabs, "<", tag, tagTerminator);		
		} else {
			if (pairsAttributeValue.size() % 2 != 0) {
				throw new IM3RuntimeException("Cannot build non-pair attributes");
			}
			StringBuilder sbAttributes = new StringBuilder();
			for (int i=0; i<pairsAttributeValue.size(); i+=2) {
				sbAttributes.append(' ');
				sbAttributes.append(pairsAttributeValue.get(i));
				sbAttributes.append('=');
				sbAttributes.append('"');
				sbAttributes.append(pairsAttributeValue.get(i+1));
				sbAttributes.append('"');
			}
			add(sb, tabs, "<", tag, sbAttributes.toString(), tagTerminator);
		}	
	}
	
	public static void start(StringBuilder sb, int tabs, String string, String [] pairsAttributeValue, String tagTerminator) {
		if (pairsAttributeValue == null || pairsAttributeValue.length == 0) {
			add(sb, tabs, "<", string, tagTerminator);		
		} else {
			if (pairsAttributeValue.length % 2 != 0) {
				throw new IM3RuntimeException("Cannot build non-pair attributes with empty or non-pair (" + pairsAttributeValue.length + ") number of attributes");
			}
			StringBuilder sbAttributes = new StringBuilder();
			for (int i=0; i<pairsAttributeValue.length; i+=2) {
				sbAttributes.append(' ');
				sbAttributes.append(pairsAttributeValue[i]);
				sbAttributes.append('=');
				sbAttributes.append('"');
				sbAttributes.append(pairsAttributeValue[i+1]);
				sbAttributes.append('"');
			}
			add(sb, tabs, "<", string, sbAttributes.toString(), tagTerminator);
		}	
	}	
	public static void startEnd(StringBuilder sb, int tabs, String tag, ArrayList<String> pairsAttributeValue) {
		start(sb, tabs, tag, pairsAttributeValue, "/>\n");
	}

	public static void startEnd(StringBuilder sb, int tabs, String tag, String ... pairsAttributeValue) {
		start(sb, tabs, tag, pairsAttributeValue, "/>\n");
	}

	public static void startEndTextContent(StringBuilder sb, int tabs, String tag, String textContent, String ... pairsAttributeValue) {
		start(sb, tabs, tag, pairsAttributeValue, ">\n");
		add(sb, tabs+1, textContent + "\n");
		end(sb, tabs, tag);
	}

	public static void startEndTextContentSingleLine(StringBuilder sb, int tabs, String tag, String textContent, String ... pairsAttributeValue) {
		start(sb, tabs, tag, pairsAttributeValue, ">");
		add(sb, 0, textContent);
		end(sb, 0, tag);
	}


	public static void end(StringBuilder sb, int tabs, String tag) {
		add(sb, tabs, "</", tag, ">\n");		
	}
	

	/**
	 * 
	 * @param tabs
	 * @param tag
	 * @param content
	 * @param pairsAttributeValue Eg. role="composer" id="3" should be specified as the list "role" "composer" "id" "3"
	 */
	public static void text(StringBuilder sb, int tabs, String tag, String content, String ... pairsAttributeValue) {
		if (pairsAttributeValue == null) {
			add(sb, tabs, "<", tag, ">", content, "</", tag, ">\n");		
		} else {
			if (pairsAttributeValue.length % 2 != 0) {
				throw new IM3RuntimeException("Cannot build non-pair attributes");
			}
			StringBuilder sbAttributes = new StringBuilder();
			for (int i=0; i<pairsAttributeValue.length; i+=2) {
				sbAttributes.append(' ');
				sbAttributes.append(pairsAttributeValue[i]);
				sbAttributes.append('=');
				sbAttributes.append('"');
				sbAttributes.append(pairsAttributeValue[i+1]);
				sbAttributes.append('"');
			}
			add(sb, tabs, "<", tag, sbAttributes.toString(), ">", content, "</", tag, ">\n");
		}
	}


    public static void add(StringBuilder sb, int tabs, String ...content) {
		for (int i=0; i<tabs; i++) {
			sb.append('\t');			
		}
		for (int i=0; i<content.length; i++) {
			sb.append(content[i]);
		}
	}

}
