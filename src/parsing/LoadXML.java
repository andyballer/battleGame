package parsing;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadXML {
	String _filename;

	public LoadXML(String filename) {
		_filename = filename;
	}

	/**
	 * 
	 * @param sections
	 *            - Section names
	 * @return A map mapping section name to a List of the Elements
	 */
	public Map<String, List<Element>> parse(String... sections) {
		Map<String, List<Element>> ret = new HashMap<String, List<Element>>();
		try {
			File fXmlFile = new File(_filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			for (String sectionName : sections) {
				NodeList sectionsFound = doc.getElementsByTagName(sectionName);
				List<Element> nodeList = new ArrayList<Element>();
				if (sectionsFound.getLength() > 0) {
					Node sectionNode = sectionsFound.item(0);
					NodeList sectionChildren = sectionNode.getChildNodes();
					for (int temp = 0; temp < sectionChildren.getLength(); temp++) {
						Node nNode = sectionChildren.item(temp);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							nodeList.add((Element) nNode);
						}
					}
				}
				ret.put(sectionName, nodeList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
