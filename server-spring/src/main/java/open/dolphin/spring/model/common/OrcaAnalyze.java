/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package open.dolphin.spring.model.common;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

/**
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 */
public class OrcaAnalyze {
    /**
     * コンストラクタ
     */
    public OrcaAnalyze() {

    }

    public void analisisSampleXml(String statement) {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc;

        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(statement.getBytes("UTF-8")));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpathAPI = xpathFactory.newXPath();
            Node pNode = (Node) xpathAPI.compile("/xmlio2/patientinfores/Patient_Information").evaluate(doc,
                    XPathConstants.NODE);
            if (pNode != null) {
                Node node;
                // 患者ID
                node = (Node) xpathAPI.compile("Patient_ID").evaluate(pNode, XPathConstants.NODE);
                String pid = (node != null) ? node.getFirstChild().getNodeValue() : null;

                // 保険情報
                Node hNode;
                NodeIterator ite = (NodeIterator) xpathAPI
                        .compile("HealthInsurance_Information/HealthInsurance_Information_child").evaluate(pNode,
                                XPathConstants.NODE);
                while ((hNode = ite.nextNode()) != null) {
                    // 保険の種類
                    node = (Node) xpathAPI.compile("InsuranceProvider_Class").evaluate(hNode, XPathConstants.NODE);
                    // String insuranceProviderClass = (node != null) ?
                    // node.getFirstChild().getNodeValue() : null;
                }
            }
        } catch (SAXException ex) {
            Logger.getLogger(OrcaAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OrcaAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OrcaAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(OrcaAnalyze.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
