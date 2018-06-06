package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FileDownloader {
	public static String xmlFileDownloader(String urlStr) throws IOException  {
		URL url = new URL(urlStr);
		URLConnection urlConnection = url.openConnection();
		urlConnection.connect();
		// 得到输入流
		InputStream inputStream = urlConnection.getInputStream();
		// 获取自己数组
		byte[] getData = readInputStream(inputStream);
		String xmlName = new String(getData);
		
		System.out.println("xmlName:   "+xmlName);
		
		int i;
		while((i=xmlName.indexOf('\n'))>0) {
			//System.out.println(xmlName.substring(0,i));
			String name = xmlName.substring(0,i);
			if(name.indexOf("ver")>=0&&name.indexOf(".xml")>=0)
				return name;
			xmlName = xmlName.substring(i+1,xmlName.length());
		}
		//System.out.println("xmlName:  "+xmlName);
		//获取ver.*.xml文件名称
		return null;
	}
	
	
	public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
		URL url = new URL(urlStr);
		URLConnection urlConnection = url.openConnection();
		urlConnection.connect();
		// 得到输入流
		InputStream inputStream = urlConnection.getInputStream();
		// 获取自己数组
		byte[] getData = readInputStream(inputStream);

		// 文件保存位置
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}
		File file = new File(savePath + fileName);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		
		fos.close();
		
		inputStream.close();
		
		System.out.println("info:" + url + " download success");
	}

	private static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}
	
}
