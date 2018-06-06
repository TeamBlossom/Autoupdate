package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


import java.io.OutputStreamWriter;

import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import util.FileManager;
import util.MyXMLReader;
import util.Server;

public class Controller implements Initializable {
	final static String URLPath = "./UpdateURL";
	final static String localConfigPath = "./Update/Config/";
	final static String versionPath = "./Update/Version/";
	
	//�����ļ�xml�ļ��Ĺ�ϣ��
	String localConfigHash="",updateConfigHash="";
	
	@FXML
	MenuItem checkUpdateMenuItem;
	@FXML
	MenuItem setUpdateURLMenuItem;
	@FXML
	MenuItem newConfMenuItem;
	@FXML
	MenuItem openConfMenuItem;
	@FXML
	MenuItem exitMenuItem;
	@FXML
	AnchorPane contentPane;

	@FXML
	Button ensureButton;
	@FXML
	Button saveAsButton;
	@FXML
	Button cancelButton;
	@FXML
	Button addFileButton;

	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private TableView<ConfigListItem> itemTable;
	@FXML
	private TableColumn<ConfigListItem, String> configItemCol;

	private String updateURL;

	// ��������ļ����б�
	private ArrayList<ObservableList<Config>> configLL = new ArrayList<ObservableList<Config>>();

	// �����ļ��б�..��ߵ�
	ObservableList<ConfigListItem> configList = FXCollections.observableArrayList();

//	// һ�������ļ����б�...�ұߵ�
//	ObservableList<Config> configData = FXCollections.observableArrayList();

	//mapӳ��tab
	HashMap<String, Tab> tabMap = new HashMap<>();
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		initWebURL();
		initLoadLocalConfig();
		initTable();
		initButton();
		new Thread() {
			public void run() {
				System.out.println("start");
				new Server().start();
				System.out.println("end");
			};
		}.start();
	}

	//��ȡ���������ļ�
	private void initLoadLocalConfig() {
		//��ȡ�����ļ��б�
		//��Ű汾���ļ���
		File versionDir = new File(versionPath);
		if(!versionDir.exists()) {
			versionDir.mkdirs();
		}
		//��������ļ����ļ���
		File configDir = new File(localConfigPath);
		if (!configDir.exists()) {
			configDir.mkdirs();
		}
		//��ȡ��Ŀ¼�µ�xml�ļ�
		configLL.clear();
		if(configDir.listFiles()!=null) {
			for(File child : configDir.listFiles()) {
				//������ƥ�䣬xml�ļ�
				System.out.println(child.getName());
				ObservableList<Config> list = FXCollections.observableArrayList(MyXMLReader.getXMlFile(child.getPath()));
				configLL.add(list);
				String name = child.getName().substring(0, child.getName().length()-4);
				configList.add(new ConfigListItem(name));
			}
		}
		
		//��ȡ��ǰ�汾�����ļ�
		
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

	// ��ʼ��table
	private void initTable() {
		itemTable.setItems(configList);

		configItemCol.setMinWidth(100);
		configItemCol.setSortable(false);
		configItemCol.setCellFactory(new ItemCellFactory());
		configItemCol.setCellValueFactory(new PropertyValueFactory<>("item"));
	}
	
	
	private void initButton() {
		//���水ť��Ӧ�¼�
		ensureButton.setOnAction((ActionEvent e) -> {
			DataTab dataTab = (DataTab) tabPane.getSelectionModel().getSelectedItem();
			String oldName = dataTab.getHisName();
			if(oldName!=dataTab.getTextName()) {
				tabMap.remove(oldName);
				tabMap.put(dataTab.getTextName(), dataTab);
			}
			System.out.println("tabName:   "+dataTab.getText());
			dataTab.setHisName(dataTab.getTextName());
			dataTab.setText(dataTab.getTextName());
			int selectIndex = 0;
			for(;selectIndex<itemTable.getItems().size();selectIndex++) {
				if(itemTable.getItems().get(selectIndex).getItem().equals(oldName))
				{
					System.out.println("itemName:   "+dataTab.getTextName());
					itemTable.getItems().get(selectIndex).setItem(dataTab.getTextName());
					break;
				}
			}
			System.out.println("selectindex:    "+selectIndex);
			configLL.get(selectIndex).clear();
			configLL.get(selectIndex).addAll(dataTab.getTableData());
			
			File cf = new File(localConfigPath+oldName+".xml");
			cf.delete();
			// ��������
			saveXMLFile(localConfigPath+dataTab.getTextName()+".xml");	
		});
		//���Ϊ��ť��Ӧ�¼�
		saveAsButton.setOnAction((ActionEvent e) -> {
			DataTab dataTab = (DataTab) tabPane.getSelectionModel().getSelectedItem();
			
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(dataTab.getTextName());
			fileChooser.getExtensionFilters().add(new ExtensionFilter("XML", ".xml"));
			fileChooser.setInitialDirectory(new File("./"));
			File file = fileChooser.showSaveDialog(null);
			if (file != null&&!file.exists()) {
				//��ʾ
				itemTable.getItems().add(new ConfigListItem(dataTab.getTextName()));	
				ObservableList<Config> newConfig = FXCollections.observableArrayList();
				newConfig.addAll(dataTab.getTableData());
				configLL.add(newConfig);
				for(Config c:configLL.get(1)) {
					System.out.println(c.getName());
				}
				//�����ļ�
				saveXMLFile(file.getPath());
				
				System.out.println("xml: "+file.getName());
				System.out.println("path: "+file.getPath());
			}
		});
		//ȡ����ť��Ӧ�¼�
		cancelButton.setOnAction((ActionEvent e) -> {
			System.out.println("cancel");
		});
		//��Ӵ������ļ���ť��Ӧ�¼�
		addFileButton.setOnAction((ActionEvent e) -> {
			DataTab dataTab = (DataTab) tabPane.getSelectionModel().getSelectedItem();
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				dataTab.addTableData(new Config(file.getName(), file.getAbsolutePath(), "", ""));
			}
		});
	}

	private class ItemCellFactory
			implements Callback<TableColumn<ConfigListItem, String>, TableCell<ConfigListItem, String>> {
		@Override
		public TableCell<ConfigListItem, String> call(TableColumn<ConfigListItem, String> param) {
			TextFieldTableCell<ConfigListItem, String> cell = new TextFieldTableCell<>();
			cell.setOnMouseClicked((MouseEvent t) -> {
				if (t.getClickCount() == 2) {
					// ��Ӧ�¼�������Ӧ�������ļ���Ϣ��ʾ��
					int selectIndex = itemTable.getSelectionModel().getSelectedIndex();
					String name = itemTable.getSelectionModel().getSelectedItem().getItem();
					if(!tabMap.containsKey(name)){
//						for(Config c:configLL.get(selectIndex)) {
//							System.out.println("name:   "+c.getName());
//						}
						DataTab dataTab = new DataTab(configLL.get(selectIndex), name);
						tabMap.put(name, dataTab);
						tabPane.getTabs().add(dataTab);
						tabPane.getSelectionModel().select(dataTab);
					}
					tabPane.getSelectionModel().select(tabMap.get(name));
					//U.N
				}
				MouseButton button = t.getButton();
				switch (button) {
				case PRIMARY:
					break;
				case SECONDARY:
					MenuItem delete = new MenuItem("ɾ��");
					MenuItem createVersion = new MenuItem("���ɰ汾");
					//ɾ����Ӧ����
					delete.setOnAction((ActionEvent e) -> {
						String fileName = itemTable.getSelectionModel().getSelectedItem().getItem();
						File deleteFile = new File(localConfigPath+fileName+".xml");
						if(deleteFile.exists()) {
							deleteFile.delete();
						}
						configList.remove(itemTable.getSelectionModel().getSelectedIndex());
						tabPane.getTabs().remove(tabMap.get(fileName));
						tabMap.remove(fileName);
					});
					
					//���ɰ汾��Ӧ����
					createVersion.setOnAction((ActionEvent e) -> {
						String fileName = itemTable.getSelectionModel().getSelectedItem().getItem();
						File createFile = new File(localConfigPath+fileName+".xml");
						if(createFile.exists()) {
							//System.out.println("createVersion!");
							File createVersionDir = new File(versionPath+fileName);
							if(createVersionDir.exists())
								FileManager.deleteFile(createVersionDir);
							createVersionDir.mkdir();
							//��ȡ�����ļ�������Ӧ��ѡ��������ļ��ƶ����˰汾Ŀ¼���档
							try {
								//�������ļ�copy��������Ŀ¼��
								Files.copy(createFile.toPath(),new File(createVersionDir.getPath()+"/"+createFile.getName()).toPath());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							ArrayList<Config> createVersionConfig = MyXMLReader.getXMlFile(createFile.getPath());
							if(createVersionConfig!=null) {
								for(Config config :createVersionConfig) {
									File moveFile = new File(config.getPath());
									if(moveFile.exists()) {
										System.out.println("create");
										try {
											Files.copy(moveFile.toPath(),new File(createVersionDir.getPath()+"/"+moveFile.getName()).toPath());
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}
								}
							}
						}
					});
					
					ContextMenu taskContextMenu = new ContextMenu();
					taskContextMenu.getItems().addAll(createVersion,delete);
					cell.setContextMenu(taskContextMenu);
					break;
				case MIDDLE:
					break;
				default: ;
				}
			});
			return cell;
		}
	}


	// ���ø�����ַ�¼�
	public void setUpdateURLAction(ActionEvent e) {
		Dialog<String> setURLDialog = new Dialog<>();
		setURLDialog.setTitle("���ø�����ַ");
		ButtonType ensureButtonType = new ButtonType("ȷ��", ButtonData.OK_DONE);
		setURLDialog.getDialogPane().getButtonTypes().addAll(ensureButtonType, ButtonType.CANCEL);

		GridPane gridPane = new GridPane();
		TextField URLTextFile = new TextField(updateURL);
		gridPane.add(URLTextFile, 0, 0);
		setURLDialog.getDialogPane().setContent(gridPane);

		setURLDialog.setResultConverter(dialogButton -> {
			if (dialogButton == ensureButtonType) {
				return URLTextFile.getText();
			}
			return "";
		});

		Optional<String> result = setURLDialog.showAndWait();
		if (!result.get().isEmpty()) {
			updateURL = result.get();
			try {
				// д�������Ķ���
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(URLPath, false)));
				out.write(updateURL);
				out.close();
			} catch (FileNotFoundException exception) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			} catch (IOException exception) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			}
		}
	}

	// ��������Ӧ�¼�
	public void checkUpdateAction(ActionEvent e) throws IOException, InterruptedException {
		//showUpdateDialog(checkUpdate());
		Process process = Runtime.getRuntime().exec("./update.exe");
		process.waitFor();
	}

	// �½������ļ���Ӧ�¼�
	public void newConfAction(ActionEvent e) throws IOException {
		File file = new File(localConfigPath);
		String path = "�½������ļ�";
		int i = 1;
		File createFile = new File(localConfigPath + path + ".xml");
		if (!createFile.exists()) {
			createFile.createNewFile();
		} else {
			while (true) {
				createFile = new File(localConfigPath + path + i + ".xml");
				if (!createFile.exists()) {
					createFile.createNewFile();
					path = path + i;
					break;
				}
				i++;
			}
		}
		configLL.add(FXCollections.observableArrayList());
		configList.add(new ConfigListItem(path));
		System.out.println("yessss");
	}

	// �������ļ���Ӧ�¼�
	public void openConfAction(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File("./"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));
		File file = fileChooser.showOpenDialog(null);
		ObservableList<Config> newConfig = FXCollections.observableArrayList();
		newConfig.addAll(MyXMLReader.getXMlFile(file.getPath()));
		
		configList.add(new ConfigListItem(file.getName().substring(0, file.getName().length()-4)));
		configLL.add(newConfig);
	}

	// �˳���Ӧ�¼�
	public void exitAction(ActionEvent e) {
		System.exit(0);
	}

	private void saveXMLFile(String path) {
		DataTab dataTab = (DataTab) tabPane.getSelectionModel().getSelectedItem();
		
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("Info");
		root.addAttribute("Version", dataTab.getTextName());

		Element filelist = root.addElement("FileList");
		for (Config config : dataTab.getTableData()) {
			Element file = filelist.addElement("File");
			file.addAttribute("UpdateMethod", config.getUpdateMethod());
			file.addAttribute("UpdatePath", config.getUpdatePath());
			file.addAttribute("Hash", config.getHash());
			file.addAttribute("Path", config.getPath());
			file.addAttribute("Name", config.getName());
		}

		try {
			
			File cf = new File(path);
			if(!cf.exists()) {
				cf.createNewFile();
			}
			// ���������
			FileOutputStream out = new FileOutputStream(cf);
			// createPrettyPrint��ʽ��xml������һ��OutPutFormat����
			OutputFormat of = OutputFormat.createPrettyPrint();
			// ����һ��XMLWriter����
			XMLWriter writer = new XMLWriter(out, of);
			writer.write(document);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	//�����º���
//	private boolean checkUpdate() {
//		//��ȡ���������ļ���ϣ��
//		File localConfigFile = new File("./Config");
//		if(localConfigFile.listFiles()!=null)
//			for(File f:localConfigFile.listFiles()) {
//				if(f.getName().contains(".xml")) {
//					localConfigHash = Md5HashCode.getHashCode(f.getPath());
//					break;
//				}
//			}
//		
//		//��ȡ�����ļ���ϣ��
//		File updateURLFile = new File(updateURL.substring(8));
//		System.out.println(updateURL.substring(8));
//		System.out.println(updateURLFile.exists());
//		
//		if(updateURLFile.listFiles()!=null)
//		for(File f: updateURLFile.listFiles()) {
//			System.out.println(f.getName());
//			if(f.getName().contains(".xml")) {
//				updateConfigHash = Md5HashCode.getHashCode(f.getPath());
//				break;
//			}
//		}
//		//�Աȹ�ϣ��
//		System.out.println("update   :"+updateConfigHash);
//		System.out.println("local    :"+localConfigHash);
//		if(updateConfigHash.equals(localConfigHash))
//			return false;
//		return true;
//	}
//	
//	//��ʾ���¶Ի���
//	private void showUpdateDialog(boolean isNewVersion) {
//		//boolean isNewVersion = true;
//		Dialog<Boolean> checkUpdateDialog = new Dialog<>();
//
//		checkUpdateDialog.setTitle("������");
//		ButtonType ensureButtonType = new ButtonType("ȷ��", ButtonData.OK_DONE);
//		checkUpdateDialog.getDialogPane().getButtonTypes().addAll(ensureButtonType, ButtonType.CANCEL);
//
//		Label isNewVer = new Label();
//		Label localVer = new Label();
//		Label updateVer = new Label();
//		String versionCom = "�汾ver1.2\n��������:\n1.���ӹ���3\n2.���ӹ���4\n\n�޸�....��bug\n";
//		TextArea textArea = new TextArea(versionCom);
//		textArea.setEditable(false);
//		textArea.setWrapText(true);
//
//		GridPane gridPane = new GridPane();
//		gridPane.add(isNewVer, 0, 0);
//		gridPane.add(localVer, 0, 1);
//		gridPane.add(updateVer, 0, 2);
//
//		GridPane.setVgrow(textArea, Priority.ALWAYS);
//		GridPane.setHgrow(textArea, Priority.ALWAYS);
//
//		checkUpdateDialog.getDialogPane().setExpandableContent(textArea);
//		checkUpdateDialog.getDialogPane().setContent(gridPane);
//
//		if (isNewVersion) {
//			isNewVer.setText("��⵽�°汾���Ƿ���и���?");
//			localVer.setText("���������ļ���ϣ��: " + localConfigHash);
//			updateVer.setText("���������ļ���ϣ��: " + updateConfigHash);
//		} else {
//			isNewVer.setText("û�м�⵽�°汾!");
//			localVer.setText("���������ļ���ϣ��" + localConfigHash);
//			updateVer.setText("���������ļ���ϣ��" + updateConfigHash);
//		}
//
//		checkUpdateDialog.setResultConverter(dialogButton -> {
//			if (dialogButton == ensureButtonType) {
//				return true;
//			}
//			return false;
//		});
//
//		Optional<Boolean> result = checkUpdateDialog.showAndWait();
//
//		if (result.get() && isNewVersion) {
//			Dialog<Boolean> updatingDialog = new Dialog<>();
//			updatingDialog.setTitle("������");
//			ButtonType ensButtonType = new ButtonType("ȷ��", ButtonData.OK_DONE);
//			updatingDialog.getDialogPane().getButtonTypes().addAll(ensureButtonType, ButtonType.CANCEL);
//
//			GridPane grid = new GridPane();
//			ProgressBar pbBar = new ProgressBar(0);
//			grid.add(pbBar, 0, 0);
//
//			Text t = new Text();
//
//			grid.add(t, 1, 0);
//			String tString = "������";
//			new Thread() {
//				public void run() {
//					for (int i = 0, j = 0; i <= 100; i++, j++) {
//						pbBar.setProgress((double) i / 100);
//						if (j % 10 == 0)
//							t.setText(tString + ".");
//						else if (j % 10 == 1)
//							t.setText(tString + "..");
//						else if (j % 10 == 2)
//							t.setText(tString + "...");
//						try {
//							sleep(10);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					t.setText("�������");
//				};
//			}.start();
//
//			updatingDialog.getDialogPane().setContent(grid);
//			updatingDialog.setResultConverter(dialogButton -> {
//				if (dialogButton == ensButtonType) {
//					return true;
//				}
//				return false;
//			});
//			downloadUpdateFiles();
//			Optional<Boolean> res = updatingDialog.showAndWait();
//			System.out.println(res.get());
//			
//
//			new Thread() {
//				public void run() {
//					Process process;
//					try {
//						process = Runtime.getRuntime().exec("./update.exe");
//						process.waitFor();
//					} catch (IOException | InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				};
//			}.start();
//			
//			new Thread() {
//				public void run() {
//					try {
//						sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					System.exit(0);	
//				};
//			}.start();
//			
//		}
//	}
//	
//	//���ظ����ļ�
//	public void downloadUpdateFiles() {
//		try {
//			String xmlName = FileDownloader.xmlFileDownloader(updateURL);
//			//ɾ���ɵı��������ļ�
//			File oldconfig = new File("./Config/");
//			FileManager.deleteFile(oldconfig);
//			FileDownloader.downLoadFromUrl(updateURL+xmlName, xmlName, "./Config/");
//			//File newConfig = new File();
//			ArrayList<Config> tempConfigList = MyXMLReader.getXMlFile("./Config/"+xmlName);
//			for(Config c:tempConfigList) {
//				String name = c.getName();
//				FileDownloader.downLoadFromUrl(updateURL+name, name, "./temp/");
//				String tempUpdateMethod = c.getUpdateMethod();
//				String tempUpdatePath = c.getUpdatePath();
//				//System.out.println("file_path:   "+tempUpdatePath+"/"+name);
//				File tempLocalFile = new File(tempUpdatePath+File.separator+name);
//				if(tempLocalFile.exists())
//					tempLocalFile.delete();
//				File tempDir = new File(tempUpdatePath);
//				if(!tempDir.exists())
//					tempDir.mkdirs();
//				switch (tempUpdateMethod) {
//				case "����":
//				case "����":
//					File tempFile = new File("./temp/"+name);
//					tempFile.renameTo(tempLocalFile);
//					break;
//				case "ɾ��": break;
//				default: break;
//				}
//				FileManager.deleteFile(new File("./temp/"));
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
}
