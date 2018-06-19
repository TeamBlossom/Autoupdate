package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import util.Md5HashCode;

public class Md5HashCodeTest {
	
	private static Md5HashCode md5 = new Md5HashCode();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetHashCode() {
		String hash = "5dac202ba4816b4c11664d579f473312";
		assertEquals(hash, md5.getHashCode("E:\\Anaconda3\\Library\\RELEASE.txt"));
	}

}
