package test;

import java.io.IOException;

public class Test5{

	public static void main(String[] args){
		// TODO Auto-generated method stub
			
		new Thread() {
			public void run() {
				Process process;
				try {
					sleep(1000);
					process = Runtime.getRuntime().exec("./autoUpdateApp.exe");
					process.waitFor();
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			};
		}.start();		
	}

}
