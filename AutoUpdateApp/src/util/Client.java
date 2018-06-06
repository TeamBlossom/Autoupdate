package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private Socket socket = null;
	private PrintWriter pw = null;
	public void init() {
		try {
			socket = new Socket("127.0.0.1", util.Server.PORT);
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMessage(String message) {
		System.out.println("message"+message);
		pw.println(message);
		pw.flush();
	}
	
	public void exit() {
		try {
			socket.close();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
