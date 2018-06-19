package test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import util.FileManager;

public class FileManagerTest {
	
	private static FileManager fm = new FileManager();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDeleteFile() {
		File file = new File("G:\\Hosts\\log.txt");
		assertEquals(!file.exists(), fm.deleteFile(file));
	}

}
