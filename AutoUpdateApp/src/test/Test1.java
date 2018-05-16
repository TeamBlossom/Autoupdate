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
        //����CountryRegion��name����ֵ  
        String countryName = "";  
        //����CountryRegion��code����ֵ  
        String countryCode = "";  
        //����city��state�ڵ��name����ֵ  
        String stateName = "";  
        //����city��state�ڵ��code����ֵ  
        String stateCode = "";  
        //����InputStream���󲢳�ʼ������null  
        InputStream in = null;  
        SAXReader reader = new SAXReader();  
        //����ļ����ַ�������  
        InputStreamReader isr =null;  
        try {  
            in =new FileInputStream("LocList.xml");  
            //������������ֱ������ʽ  
            isr = new InputStreamReader(in, "UTF-8");  
            //ʹ��read���������������ص�SAXBuilder�л��xml��Document����  
            Document document = reader.read(isr);  
            //��ȡ���ڵ�Location  
            Element rootElement = document.getRootElement();  
            //��ȡ���ڵ�Location���ӽڵ�  
            @SuppressWarnings("unchecked")  
            List<Element> coutryList = rootElement.elements();  
            //�����ӽڵ�  
            for(Element country : coutryList){  
                if (country.attributes()!=null && country.attributes().size()>0){  
                    countryName = country.attributeValue("Name");  
                    countryCode = country.attributeValue("Code");  
                    System.out.println("���ң�" + countryName + "  ����:" + countryCode);  
                    //��ȡcountry���ӽڵ�state  
                    @SuppressWarnings("unchecked")  
                    List<Element> stateList = country.elements();  
                    //����state  
                    for (Element state : stateList){  
                        //�ж��Ƿ�ӵ�����������state�ڵ㶼��û�����Եģ���  
                        if(state.attributes()!=null && state.attributes().size()>0){  
                            stateName = state.attributeValue("Name");  
                            stateCode = state.attributeValue("Code");  
                            //�����й�ʱ�ű���state�µ��ӽڵ�  
                            if("�л����񹲺͹�".equals(countryName)){  
                                //��ȡstate���ӽڵ�city  
                                @SuppressWarnings("unchecked")  
                                List<Element> cityList = state.elements();  
                                //����city  
                                for (Element city : cityList){  
                                    if(city.attributes()!=null && city.attributes().size()>0){  
                                        String cityName = city.attributeValue("Name");  
                                        String cityCode = city.attributeValue("Code");  
                                        System.out.println(countryName + stateName + "�ĳ��л������ " + cityName + "  ����:" + cityCode);  
                                    }  
                                }  
                            }else{  
                                System.out.println(countryName + "��һ�����У� " + stateName + "  ����:" + stateCode);  
                            }  
                        }else{  
                            //��ȡstate���ӽڵ�city  
                            @SuppressWarnings("unchecked")  
                            List<Element> cityList = state.elements();  
                            //����city  
                            for (Element city : cityList){  
                                if(city.attributes()!=null && city.attributes().size()>0){  
                                    String cityName = city.attributeValue("Name");  
                                    String cityCode = city.attributeValue("Code");  
                                    System.out.println(countryName + "��һ�����У� " + cityName + "  ����:" + cityCode);  
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
            //�ر���  
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
		// �����ĵ��������ĵ��ĸ�Ԫ�ؽڵ�   
		dom4jParseTest();
	}

}
