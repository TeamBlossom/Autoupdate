package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
		//System.out.println(xmlName);
		//获取ver.*.xml文件名称
		return xmlName.substring(xmlName.indexOf("ver"), xmlName.indexOf(".xml")+4);
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
		File file = new File(saveDir + File.separator + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
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
	
	
//	public static void main(String[] args) throws IOException {
////           downLoadFromUrl("file:///C:/DATA/Git/Java/Autoupdate/AutoUpdateApp/Update/Version/Newest/",
////                   "1","C://Download/");
//           xmlFileDownloader("file:///C:/DATA/Git/Java/Autoupdate/AutoUpdateApp/Update/Version/Newest/",
//                   "1","C://Download/");
//   }
}
