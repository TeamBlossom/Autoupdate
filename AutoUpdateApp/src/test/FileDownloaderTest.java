package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import util.FileDownloader;

public class FileDownloaderTest {
	
	private static FileDownloader fd = new FileDownloader();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testXmlFileDownloader() {
		String name = "ver1.0.xml";
		try {
			assertEquals(name, fd.xmlFileDownloader("file:///G:/learngit/test/"));
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	@Test
	public void testDownLoadFromUrl() {
		File file = new File("G:\\learngit\\test.txt");
		try {
			assertEquals(file.exists(), fd.downLoadFromUrl("file:///G:/learngit/test.txt", "test.txt", "G:\\learngit\\test\\"));
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}
