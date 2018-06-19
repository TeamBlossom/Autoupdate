package util;

import java.io.File;

public class FileManager {
	public static boolean deleteFile(File file) {
		if(file.exists()) {
			if(file.isDirectory()) {
				if(file.listFiles()!=null) {
					for(File f:file.listFiles()) {
						deleteFile(f);
					}
				}
			}
			if(file.delete())
				return true;
			else 
				return false;
		}
		return true;
	}

}
