package website;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
public class Html {
	Document doc = null;
	Element body = null;
	Element headNode;
	
	public Html(String title, String linkPrefix, String lang) {
		String sPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
		String sSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			DOMImplementation domImpl = builder.getDOMImplementation();
			DocumentType doctype = domImpl.createDocumentType("html", sPublicId, sSystemId); 
			doc = domImpl.createDocument("http://www.w3.org/1999/xhtml","html",doctype);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		doc.getDocumentElement().setAttribute("xmlns","http://www.w3.org/1999/xhtml");
		doc.getDocumentElement().setAttribute("lang", lang);
		doc.getDocumentElement().setAttribute("xml:lang", lang);

		
		
		headNode = doc.createElement("head");
		Element titleNode = doc.createElement("title");
		titleNode.appendChild(doc.createTextNode(title));
		body = doc.createElement("body");
		doc.getDocumentElement().appendChild(headNode);
		headNode.appendChild(titleNode);
		doc.getDocumentElement().appendChild(body);

		Element cssLink = doc.createElement("link");
		cssLink.setAttribute("href", linkPrefix + "style.css");
		cssLink.setAttribute("rel", "stylesheet");
		cssLink.setAttribute("type", "text/css");
		
		headNode.appendChild(cssLink);
		
		Element meta = doc.createElement("meta");
		meta.setAttribute("http-equiv", "Content-Type");
		meta.setAttribute("content", "text/html; charset=utf-8");
		headNode.appendChild(meta);
	}

	public void addJSFile(String url) {
		Element script = doc.createElement("script");
		script.setAttribute("type", "text/javascript");
		script.setAttribute("src", url);
		script.appendChild(text(" "));
		headNode.appendChild(script);
	}
	
	public void setDescription(String description) {
		Element meta = doc.createElement("meta");
		meta.setAttribute("name", "description");
		meta.setAttribute("content", description);
		headNode.appendChild(meta);
	}

	public Document getDoc() {
		return doc;
	}

	public Element getBody() {
		return body;
	}

	public Element form(String id, String action, String onsubmit) {
		Element form = doc.createElement("form");
		if (id != null) form.setAttribute("id", id);
		if (action != null) form.setAttribute("action", action);
		if (onsubmit != null) form.setAttribute("onsubmit", onsubmit);
		return form;
	}

	public Element input(String id, String name) {
		Element input = doc.createElement("input");
		input.setAttribute("id", id);
		input.setAttribute("name", name);
		return input;
	}

	public Element input(String id, String name, int size) {
		Element input = input(id, name);
		input.setAttribute("size", Integer.toString(size));
		return input;
	}

	public Element button(String value, String onclick) {
		Element input = doc.createElement("input");
		input.setAttribute("type", "button");
		input.setAttribute("value", value);
		input.setAttribute("onclick", onclick);
		return input;
	}

	public Element anchor(String anchor) {
		Element link = doc.createElement("a");
		link.setAttribute("name", anchor);
		return link;
	}

	public Element link(String href, String text) {
		return link(href, doc.createTextNode(text));
	}

	public Element link(String href, Node linkNode) {
		Element link = doc.createElement("a");
		link.setAttribute("href", href);
		link.appendChild(linkNode);
		return link;
	}

	public Element img(String src, String desc) {
		Element img = doc.createElement("img");
		img.setAttribute("src", src);
		img.setAttribute("alt", desc);
		img.setAttribute("title", desc);
		return img;
	}

	public Element span(String className) {
		Element span = tag("span");
		span.setAttribute("class", className);
		return span;
	}

	public Element span(String className, String text) {
		Element span = span(className);
		span.appendChild(text(text));
		return span;
	}

	public Element spanNewlines(String className, String text) {
		Element span = span(className);
		handleNewlines(span, text);
		return span;
	}
	
	public void handleNewlines(Element appendTo, String text) {
		String[] lines = text.split("\r\n|\r|\n");
		for (String line : lines) {
			appendTo.appendChild(text(line));
			appendTo.appendChild(br());
		}		
	}
	
	public Element divId(String id) {
		Element div = tag("div");
		div.setAttribute("id", id);
		return div;
	}

	public Element div(String className) {
		Element div = tag("div");
		div.setAttribute("class", className);
		return div;
	}

	public Element div(String className, String content) {
		Element div = tag("div");
		div.setAttribute("class", className);
		div.appendChild(text(content));
		return div;
	}

	public Element sup(String className) {
		Element sup = tag("sup");
		sup.setAttribute("class", className);
		return sup;
	}

	public Element br() {
		return tag("br");
	}

	public Element p() {
		return tag("p");
	}

	public Element p(String text) {
		return tag("p", text);
	}
	public Element pNewlines(String text) {
		Element p = tag("p");
		handleNewlines(p, text);		
		return p;
	}

	public Element p(Node node) {
		return tag("p", node);
	}

	public Element h1(String text) {
		return tag("h1", text);
	}

	public Element h2(String text) {
		return tag("h2", text);
	}

	public Element h2(String imageUrl, String text) {
		Element h2 = tag("h2", img(imageUrl, ""));
		h2.appendChild(text(" " + text));
		return h2;
	}

	public Element li(String text) {
		return tag("li", text);
	}

	public Element ul() {
		return tag("ul");
	}

	public Node text(String text) {
		return doc.createTextNode(text);
	}



	private Element tag(String tagname) {
		return doc.createElement(tagname);
	}
	private Element tag(String tagname, Node node) {
		Element tag = tag(tagname);
		tag.appendChild(node);
		return tag;
	}
	private Element tag(String tagname, String text) {
		return tag(tagname, doc.createTextNode(text));
	}

	public void toFile(File file, boolean omitXmlDeclaration) {
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD XHTML 1.0 Strict//EN");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
			if (omitXmlDeclaration) transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StreamResult result = new StreamResult(file);
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
