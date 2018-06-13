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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.FileDownloader;
import util.FileManager;
import util.MyXMLReader;

public class UpdateUtil extends Application {

	final static String URLPath = "./UpdateURL";

	// 配置文件xml文件的哈希码
	String localConfigHash = "", updateConfigHash = "";
	private String updateURL;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		initWebURL();
		showUpdateDialog();
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

	// 显示更新对话框
	private void showUpdateDialog() {
		Dialog<Boolean> updatingDialog = new Dialog<>();
		updatingDialog.setTitle("更新中");
		ButtonType ensureButtonType = new ButtonType("确定", ButtonData.OK_DONE);
		ButtonType ensButtonType = new ButtonType("确定", ButtonData.OK_DONE);
		updatingDialog.getDialogPane().getButtonTypes().addAll(ensureButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		ProgressBar pbBar = new ProgressBar(0);
		grid.add(pbBar, 0, 0);

		new Thread() {
			public void run() {
				for (int i = 0, j = 0; i <= 100; i++, j++) {
					pbBar.setProgress((double) i / 100);
					try {
						sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
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
		
		new Thread() {
			public void run() {
				Process process;
				try {
					sleep(1000);
					process = Runtime.getRuntime().exec("cmd /c java -jar ./app.jar");
					process.waitFor();
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();

	}

	// 下载更新文件
	public void downloadUpdateFiles() {
		try {
			String xmlName = FileDownloader.xmlFileDownloader(updateURL);
			// 删除旧的本地配置文件
			File oldconfig = new File("./Config/");
			FileManager.deleteFile(oldconfig);
			FileDownloader.downLoadFromUrl(updateURL + xmlName, xmlName, "./Config/");
			// File newConfig = new File();
			ArrayList<Config> tempConfigList = MyXMLReader.getXMlFile("./Config/" + xmlName);
			for (Config c : tempConfigList) {
				String name = c.getName();
				FileDownloader.downLoadFromUrl(updateURL + name, name, "./temp/");
				String tempUpdateMethod = c.getUpdateMethod();
				String tempUpdatePath = c.getUpdatePath();
				// System.out.println("file_path: "+tempUpdatePath+"/"+name);
				File tempLocalFile = new File(tempUpdatePath + File.separator + name);
				if (tempLocalFile.exists())
					tempLocalFile.delete();
				File tempDir = new File(tempUpdatePath);
				if (!tempDir.exists())
					tempDir.mkdirs();
				switch (tempUpdateMethod) {
				case "新增":
				case "覆盖":
					File tempFile = new File("./temp/" + name);
					tempFile.renameTo(tempLocalFile);
					break;
				case "删除":
					break;
				default:
					break;
				}
				FileManager.deleteFile(new File("./temp/"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
