package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Test1 {
	
	public static void dom4jParseTest(){  
        //缓存CountryRegion的name属性值  
        String countryName = "";  
        //缓存CountryRegion的code属性值  
        String countryCode = "";  
        //缓存city或state节点的name属性值  
        String stateName = "";  
        //缓存city或state节点的code属性值  
        String stateCode = "";  
        //创建InputStream对象并初始化变量null  
        InputStream in = null;  
        SAXReader reader = new SAXReader();  
        //获得文件的字符输入流  
        InputStreamReader isr =null;  
        try {  
            in =new FileInputStream("LocList.xml");  
            //给设置输入流直射编码格式  
            isr = new InputStreamReader(in, "UTF-8");  
            //使用read方法将输入流加载到SAXBuilder中获得xml的Document对象  
            Document document = reader.read(isr);  
            //获取根节点Location  
            Element rootElement = document.getRootElement();  
            //获取根节点Location的子节点  
            @SuppressWarnings("unchecked")  
            List<Element> coutryList = rootElement.elements();  
            //遍历子节点  
            for(Element country : coutryList){  
                if (country.attributes()!=null && country.attributes().size()>0){  
                    countryName = country.attributeValue("Name");  
                    countryCode = country.attributeValue("Code");  
                    System.out.println("国家：" + countryName + "  代码:" + countryCode);  
                    //获取country的子节点state  
                    @SuppressWarnings("unchecked")  
                    List<Element> stateList = country.elements();  
                    //遍历state  
                    for (Element state : stateList){  
                        //判断是否拥有属性外国的state节点都是没有属性的，有  
                        if(state.attributes()!=null && state.attributes().size()>0){  
                            stateName = state.attributeValue("Name");  
                            stateCode = state.attributeValue("Code");  
                            //当是中国时才遍历state下的子节点  
                            if("中华人民共和国".equals(countryName)){  
                                //获取state的子节点city  
                                @SuppressWarnings("unchecked")  
                                List<Element> cityList = state.elements();  
                                //遍历city  
                                for (Element city : cityList){  
                                    if(city.attributes()!=null && city.attributes().size()>0){  
                                        String cityName = city.attributeValue("Name");  
                                        String cityCode = city.attributeValue("Code");  
                                        System.out.println(countryName + stateName + "的城市或地区： " + cityName + "  代码:" + cityCode);  
                                    }  
                                }  
                            }else{  
                                System.out.println(countryName + "的一级城市： " + stateName + "  代码:" + stateCode);  
                            }  
                        }else{  
                            //获取state的子节点city  
                            @SuppressWarnings("unchecked")  
                            List<Element> cityList = state.elements();  
                            //遍历city  
                            for (Element city : cityList){  
                                if(city.attributes()!=null && city.attributes().size()>0){  
                                    String cityName = city.attributeValue("Name");  
                                    String cityCode = city.attributeValue("Code");  
                                    System.out.println(countryName + "的一级城市： " + cityName + "  代码:" + cityCode);  
                                }  
                            }  
                        }  
                    }  
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
    }  
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// 创建文档并设置文档的根元素节点   
		dom4jParseTest();
	}

}
