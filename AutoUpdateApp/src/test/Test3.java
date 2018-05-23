package test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Test3 {

    /**
     * ������Url�������ļ�
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException {
        URL url = new URL(urlStr);
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
/*        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //���ó�ʱ��Ϊ3��
        conn.setConnectTimeout(3*1000);
        //��ֹ���γ���ץȡ������403����
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");*/

        //�õ�������
        InputStream inputStream = urlConnection.getInputStream();
        //��ȡ�Լ�����
        byte[] getData = readInputStream(inputStream);

        //�ļ�����λ��
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
        System.out.println("info:"+url+" download success");
    }
    /**
     * ���������л�ȡ�ֽ�����
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) throws IOException {
            downLoadFromUrl("file:///C:/DATA/Git/Java/Autoupdate/AutoUpdateApp/Update/Version/Newest/",
                    "1","C://Download/");
//    	for(String s:new Test4().getAllUrls("file:///C:/DATA/Git/Java/Autoupdate/AutoUpdateApp/Update/Version/Newest/")) {
//    		System.out.println(s);
//    	}
    	
    }
}
