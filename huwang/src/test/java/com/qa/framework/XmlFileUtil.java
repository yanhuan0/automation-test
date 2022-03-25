package com.qa.framework;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlFileUtil {
	
	public static void main(String[] args) {
		String originFile = args[0];
		String changeFile = args[1];
		String action = args[2];
		XmlFileUtil xmlFileUtil=new XmlFileUtil();
		xmlFileUtil.changeXmlFile(originFile,changeFile,action);
	}

	/**
	 * Update xml file according to content of another xml file
	 * 
	 * @param orginFile
	 *            , file need to be changed
	 * @param ChangFile
	 *            , the change content
	 * @param action
	 *            , delete or update
	 */
	public void changeXmlFile(String orginFile, String ChangFile, String action) {
		try {
			Document doc;
			if (action.equals("delete")){
				doc=updateElement(readXmlFileToDoc(orginFile),
						readXmlFileToDoc(ChangFile), true);	
				writeDocToXmlFile(doc,orginFile);
			}			
			else if (action.equals("update")){
				doc=updateElement(readXmlFileToDoc(orginFile),
						readXmlFileToDoc(ChangFile), false);
				writeDocToXmlFile(doc,orginFile);
			}
			else
				System.out
						.print("Wrong action parameter, it must be delete or update");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document readXmlFileToDoc(String fileName) throws Exception{
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
		.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.parse(fileName);		
	}
	
	public void writeDocToXmlFile(Document doc, String fileName) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileName));
		transformer.transform(source, result);
	}

	public Document updateElement(Document originDoc, Document changeDoc, boolean delete)
			throws Exception {
		System.out.println("Action="+delete);
		NodeList changeRootList = changeDoc.getFirstChild().getChildNodes();// <MessageProcessor>
																			// <XMLP>
		Node changeRootNode;
		Node originRootNode;// <MessageProcessor> <XMLP>
		NodeList tempOriginList;
		Element changeRootElement;
		Element originRootElement;
		for (int j = 0; j < changeRootList.getLength(); j++) {// <MessageProcessor>
																// <XMLP>
			if (changeRootList.item(j).getNodeType() == Node.ELEMENT_NODE) {
				changeRootNode=changeRootList.item(j);
				changeRootElement=(Element)changeRootNode;
				System.out.println("Change Root Node Name:"+changeRootNode.getNodeName());
				tempOriginList=originDoc.getElementsByTagName(
						changeRootNode.getNodeName());
			
				originRootNode = tempOriginList.item(0);
				if(tempOriginList.getLength() > 1){
					for(int i = 0; i < tempOriginList.getLength(); i++) {
						originRootNode=tempOriginList.item(i);
						originRootElement=(Element)originRootNode;
						if(originRootElement.getAttribute("id").equals(changeRootElement.getAttribute("id"))){
							break;
						}						
					}					
				}
				updatedNode(originDoc, originRootNode, changeRootNode, delete);
			}
		}
		return originDoc;
	}
	/**
	 * 
	 * @param originDoc
	 * @param originRootNode, with the same id and tag name as changeRootNode
	 * @param changeRootNode, the node under root node
	 * @param delete
	 */
	public void updatedNode(Document originDoc, Node originRootNode, Node changeRootNode, boolean delete) {
		System.out.println("In sub function Action="+delete);

		NodeList changeChildList = changeRootNode.getChildNodes();// <InputStreamLimitInKB>
																	// <ReadRequestBeforeProcessing>
		NodeList OriginChildList = originRootNode.getChildNodes();
		Node childItem;
		Node orginItem;
		for (int i = 0; i < changeChildList.getLength(); i++) {
			childItem = changeChildList.item(i);
			if (childItem.getNodeType() == Node.ELEMENT_NODE) {

				for (int k = 0; k < OriginChildList.getLength(); k++) {
					orginItem = OriginChildList.item(k);
					if (orginItem.getNodeName().equals(childItem.getNodeName())) {
						originRootNode.removeChild(orginItem);
						System.out.println("Delete node:"
								+ childItem.getNodeName());
					}
				}
				if (!delete) {
					originRootNode.appendChild(originDoc.importNode(childItem,
							true));
					System.out.println("Add node:" + childItem.getNodeName());
				}
			}
		}

	}
}
