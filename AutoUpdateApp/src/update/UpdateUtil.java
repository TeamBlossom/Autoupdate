package update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;

import application.Config;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.Client;
import util.FileDownloader;
import util.FileManager;
import util.Md5HashCode;
import util.MyXMLReader;

public class UpdateUtil extends Application{

	final static String URLPath = "./UpdateURL";
	final static String localConfigPath = "./Update/Config/";
	final static String versionPath = "./Update/Version/";
	
	//�����ļ�xml�ļ��Ĺ�ϣ��
	String localConfigHash="",updateConfigHash="";
	private String updateURL;
	
	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

		initWebURL();
        showUpdateDialog(checkUpdate());
	}
	
	private void initWebURL() {
		try {
			if (new File(URLPath).exists()) {
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(URLPath)));
				updateURL = in.readLine();
				in.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//�����º���
	private boolean checkUpdate() {
		//��ȡ���������ļ���ϣ��
		File localConfigFile = new File("./Config");
		if(localConfigFile.listFiles()!=null)
			for(File f:localConfigFile.listFiles()) {
				if(f.getName().contains(".xml")) {
					localConfigHash = Md5HashCode.getHashCode(f.getPath());
					break;
				}
			}
		
		//��ȡ�����ļ���ϣ��
		File updateURLFile = new File(updateURL.substring(8));
		System.out.println(updateURL.substring(8));
		System.out.println(updateURLFile.exists());
		
		if(updateURLFile.listFiles()!=null)
		for(File f: updateURLFile.listFiles()) {
			System.out.println(f.getName());
			if(f.getName().contains(".xml")) {
				updateConfigHash = Md5HashCode.getHashCode(f.getPath());
				break;
			}
		}
		//�Աȹ�ϣ��
		System.out.println("update   :"+updateConfigHash);
		System.out.println("local    :"+localConfigHash);
		if(updateConfigHash.equals(localConfigHash))
			return false;
		return true;
	}
	
	//��ʾ���¶Ի���
	private void showUpdateDialog(boolean isNewVersion) {
		//boolean isNewVersion = true;
		Dialog<Boolean> checkUpdateDialog = new Dialog<>();

		checkUpdateDialog.setTitle("������");
		ButtonType ensureButtonType = new ButtonType("ȷ��", ButtonData.OK_DONE);
		checkUpdateDialog.getDialogPane().getButtonTypes().addAll(ensureButtonType, ButtonType.CANCEL);

		Label isNewVer = new Label();
		Label localVer = new Label();
		Label updateVer = new Label();
		String versionCom = "�汾ver1.2\n��������:\n1.���ӹ���3\n2.���ӹ���4\n\n�޸�....��bug\n";
		TextArea textArea = new TextArea(versionCom);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		GridPane gridPane = new GridPane();
		gridPane.add(isNewVer, 0, 0);
		gridPane.add(localVer, 0, 1);
		gridPane.add(updateVer, 0, 2);

		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		checkUpdateDialog.getDialogPane().setExpandableContent(textArea);
		checkUpdateDialog.getDialogPane().setContent(gridPane);

		if (isNewVersion) {
			isNewVer.setText("��⵽�°汾���Ƿ���и���?");
			localVer.setText("���������ļ���ϣ��: " + localConfigHash);
			updateVer.setText("���������ļ���ϣ��: " + updateConfigHash);
		} else {
			isNewVer.setText("û�м�⵽�°汾!");
			localVer.setText("���������ļ���ϣ��" + localConfigHash);
			updateVer.setText("���������ļ���ϣ��" + updateConfigHash);
		}

		checkUpdateDialog.setResultConverter(dialogButton -> {
			if (dialogButton == ensureButtonType) {
				return true;
			}
			return false;
		});

		Optional<Boolean> result = checkUpdateDialog.showAndWait();

		if (result.get() && isNewVersion) {
			Client client = new Client();
			client.init();
			client.sendMessage("END");
			client.exit();
			
			Dialog<Boolean> updatingDialog = new Dialog<>();
			updatingDialog.setTitle("������");
			ButtonType ensButtonType = new ButtonType("ȷ��", ButtonData.OK_DONE);
			updatingDialog.getDialogPane().getButtonTypes().addAll(ensureButtonType, ButtonType.CANCEL);

			GridPane grid = new GridPane();
			ProgressBar pbBar = new ProgressBar(0);
			grid.add(pbBar, 0, 0);

			Text t = new Text();

			grid.add(t, 1, 0);
			String tString = "������";
			new Thread() {
				public void run() {
					for (int i = 0, j = 0; i <= 100; i++, j++) {
						pbBar.setProgress((double) i / 100);
						if (j % 10 == 0)
							t.setText(tString + ".");
						else if (j % 10 == 1)
							t.setText(tString + "..");
						else if (j % 10 == 2)
							t.setText(tString + "...");
						try {
							sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					t.setText("�������");
				};
			}.start();

			updatingDialog.getDialogPane().setContent(grid);
			updatingDialog.setResultConverter(dialogButton -> {
				if (dialogButton == ensButtonType) {
					return true;
				}
				return false;
			});
			downloadUpdateFiles();
			Optional<Boolean> res = updatingDialog.showAndWait();
			System.out.println(res.get());
			

			new Thread() {
				public void run() {
					Process process;
					try {
						sleep(1000);
						process = Runtime.getRuntime().exec("./autoUpdateAPP.exe");
						process.waitFor();
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.start();
			
			new Thread() {
				public void run() {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.exit(0);	
				};
			}.start();
			
		}
	}
	
	//���ظ����ļ�
	public void downloadUpdateFiles() {
		try {
			String xmlName = FileDownloader.xmlFileDownloader(updateURL);
			//ɾ���ɵı��������ļ�
			File oldconfig = new File("./Config/");
			FileManager.deleteFile(oldconfig);
			FileDownloader.downLoadFromUrl(updateURL+xmlName, xmlName, "./Config/");
			//File newConfig = new File();
			ArrayList<Config> tempConfigList = MyXMLReader.getXMlFile("./Config/"+xmlName);
			for(Config c:tempConfigList) {
				String name = c.getName();
				FileDownloader.downLoadFromUrl(updateURL+name, name, "./temp/");
				String tempUpdateMethod = c.getUpdateMethod();
				String tempUpdatePath = c.getUpdatePath();
				//System.out.println("file_path:   "+tempUpdatePath+"/"+name);
				File tempLocalFile = new File(tempUpdatePath+File.separator+name);
				if(tempLocalFile.exists())
					tempLocalFile.delete();
				File tempDir = new File(tempUpdatePath);
				if(!tempDir.exists())
					tempDir.mkdirs();
				switch (tempUpdateMethod) {
				case "����":
				case "����":
					File tempFile = new File("./temp/"+name);
					tempFile.renameTo(tempLocalFile);
					break;
				case "ɾ��": break;
				default: break;
				}
				FileManager.deleteFile(new File("./temp/"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
