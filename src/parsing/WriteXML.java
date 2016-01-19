package parsing;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteXML {
	protected Document _doc;
	protected Element _rootElement;
	protected Map<String, Element> _elementMap;
	protected String _filename;

	public WriteXML() {
		this("save.xml");
	}

	public WriteXML(String fileName) {
		_filename = fileName;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			_doc = docBuilder.newDocument();
			_rootElement = _doc.createElement("slogo");
			_doc.appendChild(_rootElement);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		_elementMap = new HashMap<String, Element>();
	}

	public void addSections(Map<String, Map<String, String>> sections) {
		for (String sectionName : sections.keySet()) {
			Map<String, String> valuesMap = sections.get(sectionName);
			addSection(sectionName, valuesMap);
		}
	}

	public void addSection(String sectionName, Map<String, String> items) {
		Element sectionEl = _doc.createElement(sectionName);
		_rootElement.appendChild(sectionEl);
		for (String itemName : items.keySet()) {
			Element itemEl = _doc.createElement(itemName);
			itemEl.setAttribute("value", items.get(itemName));
			sectionEl.appendChild(itemEl);
		}
	}

	public void writeFile() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(_doc);
			StreamResult result = new StreamResult(new File(_filename));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}