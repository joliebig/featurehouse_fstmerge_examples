package net.sf.jabref.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.transform.TransformerException;

import net.sf.jabref.AuthorList;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.Util;

import org.jempbox.xmp.XMPMetadata;
import org.jempbox.xmp.XMPSchema;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMPSchemaBibtex extends XMPSchema {

	
	public static final String NAMESPACE = "http://jabref.sourceforge.net/bibteXMP/";

	public static final String KEY = "bibtex";

	
	public XMPSchemaBibtex(XMPMetadata parent) {
		super(parent, KEY, NAMESPACE);
	}

	
	public XMPSchemaBibtex(Element e, String namespace) {
		super(e, KEY);
	}

	protected String makeProperty(String propertyName) {
		return KEY + ":" + propertyName;
	}

	
	public List getPersonList(String field) {
		return getSequenceList(field);
	}

	
	public void setPersonList(String field, String value) {
		AuthorList list = AuthorList.getAuthorList(value);

		int n = list.size();
		for (int i = 0; i < n; i++) {
			addSequenceValue(field, list.getAuthor(i).getFirstLast(false));
		}
	}

	public String getTextProperty(String field) {
		return super.getTextProperty(makeProperty(field));
	}

	public void setTextProperty(String field, String value) {
		super.setTextProperty(makeProperty(field), value);
	}

	public List getBagList(String bagName) {
		return super.getBagList(makeProperty(bagName));
	}

	public void removeBagValue(String bagName, String value) {
		super.removeBagValue(makeProperty(bagName), value);
	}

	public void addBagValue(String bagName, String value) {
		super.addBagValue(makeProperty(bagName), value);
	}

	public List getSequenceList(String seqName) {
		return super.getSequenceList(makeProperty(seqName));
	}

	public void removeSequenceValue(String seqName, String value) {
		super.removeSequenceValue(makeProperty(seqName), value);
	}

	public void addSequenceValue(String seqName, String value) {
		super.addSequenceValue(makeProperty(seqName), value);
	}

	public List getSequenceDateList(String seqName) throws IOException {
		return super.getSequenceDateList(makeProperty(seqName));
	}

	public void removeSequenceDateValue(String seqName, Calendar date) {
		super.removeSequenceDateValue(makeProperty(seqName), date);
	}

	public void addSequenceDateValue(String field, Calendar date) {
		super.addSequenceDateValue(makeProperty(field), date);
	}

	public static String getContents(NodeList seqList) {

		Element seqNode = (Element) seqList.item(0);
		StringBuffer seq = null;

		NodeList items = seqNode.getElementsByTagName("rdf:li");
		for (int j = 0; j < items.getLength(); j++) {
			Element li = (Element) items.item(j);
			if (seq == null) {
				seq = new StringBuffer();
			} else {
				seq.append(" and ");
			}
			seq.append(getTextContent(li));
		}
		if (seq != null) {
			return seq.toString();
		}
		return null;
	}

	
	public static Map getAllProperties(XMPSchema schema, String namespaceName) {
		NodeList nodes = schema.getElement().getChildNodes();

		Map<String, String> result = new HashMap<String, String>();

		if (nodes == null) {
			return result;
		}

		
		int n = nodes.getLength();

		for (int i = 0; i < n; i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() != Node.ATTRIBUTE_NODE
				&& node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String nodeName = node.getNodeName();

			String[] split = nodeName.split(":");

			if (split.length == 2 && split[0].equals(namespaceName)) {
				NodeList seqList = ((Element) node).getElementsByTagName("rdf:Seq");
				if (seqList.getLength() > 0) {

					String seq = getContents(seqList);

					if (seq != null) {
						result.put(split[1], seq);
					}
				} else {
					NodeList bagList = ((Element) node).getElementsByTagName("rdf:Bag");
					if (bagList.getLength() > 0) {

						String seq = getContents(bagList);

						if (seq != null) {
							result.put(split[1], seq);
						}
					} else {
						result.put(split[1], getTextContent(node));
					}
				}
			}
		}

		
		NamedNodeMap attrs = schema.getElement().getAttributes();
		int m = attrs.getLength();
		for (int j = 0; j < m; j++) {
			Node attr = attrs.item(j);

			String nodeName = attr.getNodeName();
			String[] split = nodeName.split(":");
			if (split.length == 2 && split[0].equals(namespaceName)) {
				result.put(split[1], attr.getNodeValue());
			}
		}

		
		
		for (Map.Entry<String, String> entry : result.entrySet()){
			String key = entry.getKey();
			if (preserveWhiteSpace.contains(key))
				continue;
			entry.setValue(((String) entry.getValue()).replaceAll("\\s+", " ").trim());
		}

		return result;
	}

	public static HashSet<String> preserveWhiteSpace = new HashSet<String>();
	static {
		preserveWhiteSpace.add("abstract");
		preserveWhiteSpace.add("note");
		preserveWhiteSpace.add("review");
	}

	public void setBibtexEntry(BibtexEntry entry, BibtexDatabase database) {
		
		Object[] fields = entry.getAllFields();
		Object[] results;
		int resultsSize;
		
		JabRefPreferences prefs = JabRefPreferences.getInstance();
		if (prefs.getBoolean("useXmpPrivacyFilter")) {
			TreeSet<String> filters = new TreeSet<String>(Arrays.asList(prefs.getStringArray("xmpPrivacyFilter")));
			results = new Object[fields.length];
			resultsSize = 0;
			for (int i = 0; i < fields.length; i++) {
				if (!filters.contains(fields[i])) {
					results[resultsSize++] = fields[i];
				}
			}
		} else {
			results = fields;
			resultsSize = fields.length;
		}
		
		for (int i = 0; i < resultsSize; i++){
			String field = results[i].toString();
			String value = BibtexDatabase.getResolvedField(field, entry, database);
			if (field.equals("author") || field.equals("editor")) {
				setPersonList(field, value);
			} else {
				setTextProperty(field, value);
			}
		}
		setTextProperty("entrytype", entry.getType().getName());
	}

	public BibtexEntry getBibtexEntry() {

		String type = getTextProperty("entrytype");
		BibtexEntryType t;
		if (type != null)
			t = BibtexEntryType.getStandardType(type);
		else
			t = BibtexEntryType.OTHER;

		BibtexEntry e = new BibtexEntry(Util.createNeutralId(), t);

		
		Map text = getAllProperties(this, "bibtex");
		text.remove("entrytype");
		e.setField(text);
		return e;
	}

	
	public static String getTextContent(Node node) {
		boolean hasTextContent = false;
		StringBuffer buffer = new StringBuffer();
		NodeList nlist = node.getChildNodes();
		for (int i = 0; i < nlist.getLength(); i++) {
			Node child = nlist.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				buffer.append(child.getNodeValue());
				hasTextContent = true;
			}
		}
		return (hasTextContent ? buffer.toString() : "");
	}

}
