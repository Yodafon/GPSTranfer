package com.gpstransfer.ant.util;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;

public class XMLChecker {

    public static boolean checkXml(FileInputStream xmlString) {

        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(xmlString, new DefaultHandler());
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
