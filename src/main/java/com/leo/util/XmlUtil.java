package com.leo.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {
	public static Map<String, String> xmlToMap(String data) {
		try {
			Map<String, String> map = new HashMap<>();
			SAXReader reader = new SAXReader();
			try(InputStream ins=new ByteArrayInputStream(data.getBytes("UTF-8"))) {
				Document doc=reader.read(ins);
				Element root = doc.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> list = root.elements();

				for (Element e : list) {
					map.put(e.getName(), e.getText());
				}

				return map;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static Map<String, String> xmlToMap(HttpServletRequest request) {
		try {
			Map<String, String> map = new HashMap<>();
			SAXReader reader = new SAXReader();
			try(InputStream ins=request.getInputStream()) {
				Document doc=reader.read(ins);
				Element root = doc.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> list = root.elements();

				for (Element e : list) {
					map.put(e.getName(), e.getText());
				}

				return map;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String mapToXml(Map<String, String> map) {
		try {
	        Document document = DocumentHelper.createDocument();
	        Element nodeElement = document.addElement("xml");
	        for (Object obj : map.keySet()) {
	        	Element keyElement = nodeElement.addElement(String.valueOf(obj));
	            keyElement.setText(String.valueOf(map.get(obj)));
	        }
	        return docToStr(document);
		} catch (Exception ex) {
            return null;
        }
    }

	private static String docToStr(Document document) throws IOException {
        // 使用输出流来进行转化
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// 使用UTF-8编码
		OutputFormat format = new OutputFormat("    ", true, "UTF-8");
		XMLWriter writer = new XMLWriter(out, format);
		writer.write(document);
		return out.toString("UTF-8");
    }
}
