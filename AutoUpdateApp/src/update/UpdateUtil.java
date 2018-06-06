package update;

import java.io.IOException;

import util.FileDownloader;

public class UpdateUtil {

	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String name = FileDownloader.xmlFileDownloader("file:///C:/DATA/Git/Java/Autoupdate/AutoUpdateApp/Update/Version/Newest/");
		System.out.println("name:   "+name);
	}

}
