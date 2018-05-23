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
import java.util.Optional;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import util.FileDownloader;
import util.FileManager;
import util.Md5HashCode;
import util.MyXMLReader;

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
	TextField version;

	@FXML
	private TableView<Config> dateTable;
	@FXML
	private TableColumn<Config, String> nameCol;
	@FXML
	private TableColumn<Config, String> pathCol;
	@FXML
	private TableColumn<Config, String> hashCol;
	@FXML
	private TableColumn<Config, String> updatePathCol;
	@FXML
	private TableColumn<Config, String> updateMethodCol;

	@FXML
	private TableView<ConfigListItem> itemTable;
	@FXML
	private TableColumn<ConfigListItem, String> configItemCol;

	private String updateURL;

	// ��������ļ����б�
	private ArrayList<ObservableList<Config>> configLL = new ArrayList<ObservableList<Config>>();

	// �����ļ��б�..��ߵ�
	ObservableList<ConfigListItem> configList = FXCollections.observableArrayList();

	// һ�������ļ����б�...�ұߵ�
	ObservableList<Config> configData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		intiWebURL();
		intiLoadLocalConfig();
		initTable();
		initButton();
		if(checkUpdate()) {
			showUpdateDialog(true);
		}
	}

	//��ȡ���������ļ�
	private void intiLoadLocalConfig() {
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
	}
	
	private void intiWebURL() {
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

	private void initButton() {
		//���水ť��Ӧ�¼�
		ensureButton.setOnAction((ActionEvent e) -> {
			// �����б�
			int selectIndex = itemTable.getSelectionModel().getSelectedIndex();
			// �����б��
			String fileName = itemTable.getSelectionModel().getSelectedItem().getItem();
			//System.out.println(fileName);
			itemTable.getSelectionModel().getSelectedItem().setItem(version.getText());
			configLL.get(selectIndex).clear();
//			int updatePathColNum = dateTable.getColumns().indexOf(updatePathCol);
//			for(Config config: configData) {
//				System.out.println(dateTable.getColumns().get(updatePathColNum).getCellData(0));
//				//config.setUpdatePath((String));
//			}
//			
			configLL.get(selectIndex).addAll(configData);
			
			// ��������
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("Info");
			root.addAttribute("Version", version.getText());

			Element filelist = root.addElement("FileList");
			for (Config config : configData) {
				Element file = filelist.addElement("File");
				file.addAttribute("UpdateMethod", config.getUpdateMethod());
				file.addAttribute("UpdatePath", config.getUpdatePath());
				file.addAttribute("Hash", config.getHash());
				file.addAttribute("Path", config.getPath());
				file.addAttribute("Name", config.getName());
			}

			try {
				System.out.println("enter");
				File cf = new File(localConfigPath+fileName+".xml");
				cf.renameTo(new File(localConfigPath+version.getText()+".xml"));
				cf = new File(localConfigPath+version.getText()+".xml");
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
		});
		//���Ϊ��ť��Ӧ�¼�
		saveAsButton.setOnAction((ActionEvent e) -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(null);
			if (file != null) {

			}
		});
		//ȡ����ť��Ӧ�¼�
		cancelButton.setOnAction((ActionEvent e) -> {
			configData.clear();
		});
		//��Ӵ������ļ���ť��Ӧ�¼�
		addFileButton.setOnAction((ActionEvent e) -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				configData.add(new Config(file.getName(), file.getAbsolutePath(), "", ""));
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
					// ��Ӧ�¼�������Ӧ�������ļ���Ϣ��ʾ��dateTable�С�
					int selectIndex = itemTable.getSelectionModel().getSelectedIndex();
					configData.clear();
					configData.addAll(configLL.get(selectIndex));
					version.setText(itemTable.getSelectionModel().getSelectedItem().getItem());
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

	// ����Ӧ�¼�(��ʵ��cell����Ӧ�¼�)
	private class TaskCellFactory implements Callback<TableColumn<Config, String>, TableCell<Config, String>> {
		@Override
		public TableCell<Config, String> call(TableColumn<Config, String> param) {
			TextFieldTableCell<Config, String> cell = new TextFieldTableCell<>();
			cell.setOnMouseClicked((MouseEvent t) -> {
				MouseButton button = t.getButton();
				switch (button) {
				case PRIMARY:
					break;
				case SECONDARY:
					MenuItem delete = new MenuItem("ɾ��");
					delete.setOnAction((ActionEvent e) -> {
						configData.remove(dateTable.getSelectionModel().getSelectedIndex());
					});
					ContextMenu taskContextMenu = new ContextMenu();
					taskContextMenu.getItems().add(delete);
					cell.setContextMenu(taskContextMenu);
					break;
				case MIDDLE:
					break;
				default:
					;
				}
			});
			return cell;
		}
	}

	// ��ʼ��table,����table
	private void initTable() {
		// configData.add(new Config("name","path",""));
		dateTable.setItems(configData);
		dateTable.setEditable(true);
//		configList.add(new ConfigListItem("ver1.0"));
//		ObservableList<Config> list = FXCollections.observableArrayList();
//		list.addAll(new Config("file1", "C:\\APP\\file1", "����"), new Config("file2", "C:\\APP\\file2", "����"));
//		configLL.add(list);

		itemTable.setItems(configList);

		configItemCol.setMinWidth(100);
		configItemCol.setSortable(false);
		configItemCol.setCellFactory(new ItemCellFactory());
		configItemCol.setCellValueFactory(new PropertyValueFactory<>("item"));

		nameCol.setMinWidth(100);
		nameCol.setSortable(false);
		nameCol.setEditable(false);
		nameCol.setCellFactory(new TaskCellFactory());
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		pathCol.setMinWidth(100);
		pathCol.setSortable(false);
		pathCol.setEditable(false);
		pathCol.setCellFactory(new TaskCellFactory());
		pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
		
		hashCol.setMinWidth(100);
		hashCol.setSortable(false);
		hashCol.setEditable(false);
		hashCol.setCellFactory(new TaskCellFactory());
		hashCol.setCellValueFactory(new PropertyValueFactory<>("hash"));
		
		updatePathCol.setMinWidth(100);
		updatePathCol.setSortable(false);
		updatePathCol.setEditable(true);
		//��ӱ༭�ύ��Ӧ����
		updatePathCol.setOnEditCommit(
                (CellEditEvent<Config, String> t) -> {
                    ( t.getTableView().getItems()
                            .get(t.getTablePosition().getRow()))
                            .setUpdatePath(t.getNewValue());
                });
		updatePathCol.setCellFactory(TextFieldTableCell.forTableColumn());
		updatePathCol.setCellValueFactory(new PropertyValueFactory<>("updatePath"));

		updateMethodCol.setMinWidth(100);
		updateMethodCol.setSortable(false);
		updateMethodCol.setCellFactory(new Callback<TableColumn<Config, String>, TableCell<Config, String>>() {

			@Override
			public TableCell<Config, String> call(TableColumn<Config, String> param) {
				return new LiveComboBoxTableCell<>(FXCollections.observableArrayList("����", "����", "ɾ��"));
			}
		});
		updateMethodCol.setCellValueFactory(new PropertyValueFactory<>("updateMethod"));
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
	public void checkUpdateAction(ActionEvent e) throws InterruptedException {
		System.out.println("work");
		showUpdateDialog(checkUpdate());
	}

	// �½������ļ���Ӧ�¼�
	public void newConfAction(ActionEvent e) throws IOException {
		// System.out.println("new");
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
	}

	// �������ļ���Ӧ�¼�
	public void openConfAction(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(null);
	}

	// �˳���Ӧ�¼�
	public void exitAction(ActionEvent e) {
		System.exit(0);
	}

	// ComboBoxTableCell
	public static class LiveComboBoxTableCell<S, T> extends TableCell<S, T> {

		private final ComboBox<T> comboBox;

		public LiveComboBoxTableCell(ObservableList<T> items) {
			this.comboBox = new ComboBox<>(items);

			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			
			comboBox.valueProperty().addListener(new ChangeListener<T>() {
				@Override
				public void changed(ObservableValue<? extends T> obs, T oldValue, T newValue) {
					// attempt to update property:
					ObservableValue<T> property = getTableColumn().getCellObservableValue(getIndex());
					if (property instanceof WritableValue) {
						((WritableValue<T>) property).setValue(newValue);
					}
				}
			});
		}

		@Override
		public void updateItem(T item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setGraphic(null);
			} else {
				comboBox.setValue(item);
				setGraphic(comboBox);
			}
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
		//System.out.println(updateURL.exists());
		if(updateURLFile.listFiles()!=null)
		for(File f: updateURLFile.listFiles()) {
			//System.out.println(f.getName());
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

//		String localConfHash = "a4tr12ginalpoca";
//		String updateConfHash = "4vjiapask45xna";

		GridPane gridPane = new GridPane();
		gridPane.add(isNewVer, 0, 0);
		gridPane.add(localVer, 0, 1);
		gridPane.add(updateVer, 0, 2);
		// gridPane.add(textArea, 0, 4);
		// gridPane.add(updateInfo, 0, 5);

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
