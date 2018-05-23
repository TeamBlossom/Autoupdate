package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import application.Config;

public class MyXMLReader {
	
	public static ArrayList<Config> getXMlFile(String path){  
		ArrayList<Config> configList = new ArrayList<>();
        //创建InputStream对象并初始化变量null  
        InputStream in = null;  
        SAXReader reader = new SAXReader();  
        //获得文件的字符输入流  
        InputStreamReader isr =null;
        try {
            in =new FileInputStream(path);
            isr = new InputStreamReader(in, "UTF-8");  
            Document document = reader.read(isr);  
            Element rootElement = document.getRootElement();  
            for(Element file :  rootElement.element("FileList").elements()){  
                if (file.attributes()!=null && file.attributes().size()>0){
                	Config config = new Config();
                	config.setName(file.attributeValue("Name"));
                	config.setPath(file.attributeValue("Path"));
                	config.setHash(file.attributeValue("Hash"));
                	config.setUpdatePath(file.attributeValue("UpdatePath"));
                	config.setUpdateMethod(file.attributeValue("UpdateMethod"));
                	configList.add(config);
                }  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }catch (DocumentException e) {  
            e.printStackTrace();  
        }catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } finally{  
            //关闭流  
            try {  
                if(in != null)  
                    in.close();  
                if (isr != null)  
                    isr.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
              
        }
		return configList;  
    }  
//	public static void main(String[] args) throws Exception {
//		// TODO Auto-generated method stub
//		// 创建文档并设置文档的根元素节点   
//		ArrayList<Config> configs = dom4jParseTest("./Config/ver1.0.xml");
//		for(Config config :configs) {
//			System.out.println(config.getName());
//		}
//	}

}
