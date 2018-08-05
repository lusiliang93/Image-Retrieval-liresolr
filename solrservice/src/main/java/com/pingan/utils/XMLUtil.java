package com.pingan.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

public class XMLUtil {
    public final static String LT = "&lt;";     // <
    public final static String GT = "&gt;";     // >
    public final static String AND = "&amp;";   // &
    public final static String APOS = "&apos;"; // '
    public final static String QUOT = "&quot;"; // "

    public static int getDocNumber(String xmlPath) throws DocumentException {

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(xmlPath));

        //获取根节点对象
        Element rootElement = document.getRootElement();
        List<Element> elements = rootElement.elements("doc");

        return elements.size();
    }

    public static String filter(String originName) {
        String result = originName;

        if (originName.contains("&"))
            result = result.replaceAll("&", AND);
        if (originName.contains("<"))
            result = result.replaceAll("<", LT);
        if (originName.contains(">"))
            result = result.replaceAll(">", GT);
        if (originName.contains("'"))
            result = result.replaceAll("\'", APOS);
        if (originName.contains("\""))
            result = result.replaceAll("\"", QUOT);

        return result;
    }

    public static void main(String[] args) {
        String originName = "a<b>c&d'e\"";
        String resultName = XMLUtil.filter(originName);
        System.out.println(resultName);
    }

}
