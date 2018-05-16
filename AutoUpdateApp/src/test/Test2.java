package test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Test2 {
	public static void main(String[] args)  {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./hello.txt", false)));
		    out.write("hello");
		    out.close();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

}
