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
import java.util.logging.Logger;

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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import util.FileManager;
import log.LoggerManager;
import util.Md5HashCode;
import util.MyXMLReader;

public class Controller implements Initializable {
	final static String URLPath = "./UpdateURL";
	final static String localConfigPath = "./Update/Config/";
	final static String versionPath = "./Update/Version/";

	// 配置文件xml文件的哈希码
	String localConfigHash = "", updateConfigHash = "";

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

	// 存放配置文件的列表
	private ArrayList<ObservableList<Config>> configLL = new ArrayList<ObservableList<Config>>();

	// 配置文件列表..左边的
	ObservableList<ConfigListItem> configList = FXCollections.observableArrayList();

	// // 一个配置文件的列表...右边的
	// ObservableList<Config> configData = FXCollections.observableArrayList();

	// map映射tab
	HashMap<String, Tab> tabMap = new HashMap<>();

	public static Logger logger = LoggerManager.getLogger(Controller.class);
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		initWebURL();
		initLoadLocalConfig();
		initTable();
		initButton();
	}

	// 读取本地配置文件
	private void initLoadLocalConfig() {
		// 读取配置文件列表
		// 存放版本的文件夹
		File versionDir = new File(versionPath);
		if (!versionDir.exists()) {
			versionDir.mkdirs();
		}
		// 存放配置文件的文件夹
		File configDir = new File(localConfigPath);
		if (!configDir.exists()) {
			configDir.mkdirs();
		}
		// 读取子目录下的xml文件
		configLL.clear();
		if (configDir.listFiles() != null) {
			for (File child : configDir.listFiles()) {
				// 做正则匹配，xml文件
				System.out.println(child.getName());
				ObservableList<Config> list = FXCollections
						.observableArrayList(MyXMLReader.getXMlFile(child.getPath()));
				configLL.add(list);
				String name = child.getName().substring(0, child.getName().length() - 4);
				configList.add(new ConfigListItem(name));
			}
		}

		// 读取当前版本配置文件

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

	// 初始化table
	private void initTable() {
		itemTable.setItems(configList);

		configItemCol.setMinWidth(100);
		configItemCol.setSortable(false);
		configItemCol.setCellFactory(new ItemCellFactory());
		configItemCol.setCellValueFactory(new PropertyValueFactory<>("item"));
	}

	private void initButton() {
		// 保存按钮响应事件
		ensureButton.setOnAction((ActionEvent e) -> {
			DataTab dataTab = (DataTab) tabPane.getSelectionModel().getSelectedItem();
			String oldName = dataTab.getHisName();
			if (oldName != dataTab.getTextName()) {
				tabMap.remove(oldName);
				tabMap.put(dataTab.getTextName(), dataTab);
			}
			System.out.println("tabName:   " + dataTab.getText());
			dataTab.setHisName(dataTab.getTextName());
			dataTab.setText(dataTab.getTextName());
			int selectIndex = 0;
			for (; selectIndex < itemTable.getItems().size(); selectIndex++) {
				if (itemTable.getItems().get(selectIndex).getItem().equals(oldName)) {
					System.out.println("itemName:   " + dataTab.getTextName());
					itemTable.getItems().get(selectIndex).setItem(dataTab.getTextName());
					break;
				}
			}
			System.out.println("selectindex:    " + selectIndex);
			configLL.get(selectIndex).clear();
			configLL.get(selectIndex).addAll(dataTab.getTableData());

			File cf = new File(localConfigPath + oldName + ".xml");
			cf.delete();
			// 保存数据
			saveXMLFile(localConfigPath + dataTab.getTextName() + ".xml");
			
			logger.info("Config Save : "+dataTab.getTextName() + ".xml");
		});
		// 另存为按钮响应事件
		saveAsButton.setOnAction((ActionEvent e) -> {
			DataTab dataTab = (DataTab) tabPane.getSelectionModel().getSelectedItem();

			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(dataTab.getTextName());
			fileChooser.getExtensionFilters().add(new ExtensionFilter("XML", ".xml"));
			fileChooser.setInitialDirectory(new File("./"));
			File file = fileChooser.showSaveDialog(null);
			if (file != null && !file.exists()) {
				// 显示
				itemTable.getItems().add(new ConfigListItem(dataTab.getTextName()));
				ObservableList<Config> newConfig = FXCollections.observableArrayList();
				newConfig.addAll(dataTab.getTableData());
				configLL.add(newConfig);
				for (Config c : configLL.get(1)) {
					System.out.println(c.getName());
				}
				// 保存文件
				saveXMLFile(file.getPath());

				System.out.println("xml: " + file.getName());
				System.out.println("path: " + file.getPath());
			}
			logger.info("Config Save : "+dataTab.getTextName() + ".xml"+"  Path : "+file.getPath());
		});
		// 取消按钮响应事件
		cancelButton.setOnAction((ActionEvent e) -> {
			System.out.println("cancel");
		});
		// 添加待更新文件按钮响应事件
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
					// 响应事件，将对应的配置文件信息显示。
					int selectIndex = itemTable.getSelectionModel().getSelectedIndex();
					String name = itemTable.getSelectionModel().getSelectedItem().getItem();
					if (!tabMap.containsKey(name)) {
						// for(Config c:configLL.get(selectIndex)) {
						// System.out.println("name: "+c.getName());
						// }
						DataTab dataTab = new DataTab(configLL.get(selectIndex), name);
						tabMap.put(name, dataTab);
						tabPane.getTabs().add(dataTab);
						tabPane.getSelectionModel().select(dataTab);
					}
					tabPane.getSelectionModel().select(tabMap.get(name));
					// U.N
				}
				MouseButton button = t.getButton();
				switch (button) {
				case PRIMARY:
					break;
				case SECONDARY:
					MenuItem delete = new MenuItem("删除");
					MenuItem createVersion = new MenuItem("生成版本");
					// 删除响应函数
					delete.setOnAction((ActionEvent e) -> {
						String fileName = itemTable.getSelectionModel().getSelectedItem().getItem();
						File deleteFile = new File(localConfigPath + fileName + ".xml");
						if (deleteFile.exists()) {
							deleteFile.delete();
							logger.info("Config Delete : "+fileName + ".xml");
						}
						configList.remove(itemTable.getSelectionModel().getSelectedIndex());
						tabPane.getTabs().remove(tabMap.get(fileName));
						tabMap.remove(fileName);
					});

					// 生成版本响应函数
					createVersion.setOnAction((ActionEvent e) -> {
						String fileName = itemTable.getSelectionModel().getSelectedItem().getItem();
						File createFile = new File(localConfigPath + fileName + ".xml");
						if (createFile.exists()) {
							// System.out.println("createVersion!");
							File createVersionDir = new File(versionPath + fileName);
							if (createVersionDir.exists())
								FileManager.deleteFile(createVersionDir);
							createVersionDir.mkdir();
							// 读取配置文件，将对应的选择带更新文件移动到此版本目录下面。
							try {
								// 将配置文件copy到待更新目录下
								Files.copy(createFile.toPath(),
										new File(createVersionDir.getPath() + "/" + createFile.getName()).toPath());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							ArrayList<Config> createVersionConfig = MyXMLReader.getXMlFile(createFile.getPath());
							if (createVersionConfig != null) {
								for (Config config : createVersionConfig) {
									File moveFile = new File(config.getPath());
									if (moveFile.exists()) {
										System.out.println("create");
										try {
											Files.copy(moveFile.toPath(),
													new File(createVersionDir.getPath() + "/" + moveFile.getName())
															.toPath());
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}
								}
							}
						}
						logger.info("Version Create : "+fileName);
					});

					ContextMenu taskContextMenu = new ContextMenu();
					taskContextMenu.getItems().addAll(createVersion, delete);
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

	// 设置更新网址事件
	public void setUpdateURLAction(ActionEvent e) {
		Dialog<String> setURLDialog = new Dialog<>();
		setURLDialog.setTitle("设置更新网址");
		ButtonType ensureButtonType = new ButtonType("确定", ButtonData.OK_DONE);
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
				// 写对象流的对象
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
			logger.info("UpdateURL Change : "+updateURL);
		}
	}

	// 检测更新函数
	private boolean checkUpdate() {
		// 获取本地配置文件哈希码
		File localConfigFile = new File("./Config");
		if (localConfigFile.listFiles() != null)
			for (File f : localConfigFile.listFiles()) {
				if (f.getName().contains(".xml")) {
					localConfigHash = Md5HashCode.getHashCode(f.getPath());
					break;
				}
			}

		// 获取更新文件哈希码
		File updateURLFile = new File(updateURL.substring(8));
		System.out.println(updateURL.substring(8));
		System.out.println(updateURLFile.exists());

		if (updateURLFile.listFiles() != null)
			for (File f : updateURLFile.listFiles()) {
				System.out.println(f.getName());
				if (f.getName().contains(".xml")) {
					updateConfigHash = Md5HashCode.getHashCode(f.getPath());
					break;
				}
			}
		// 对比哈希码
		System.out.println("update   :" + updateConfigHash);
		System.out.println("local    :" + localConfigHash);
		if (updateConfigHash.equals(localConfigHash))
			return false;
		return true;
	}

	//显示更新对话框
	private boolean showUpdateDialog(boolean isNewVersion) {
		Dialog<Boolean> checkUpdateDialog = new Dialog<>();

		checkUpdateDialog.setTitle("检测更新");
		ButtonType ensureButtonType = new ButtonType("确定", ButtonData.OK_DONE);
		checkUpdateDialog.getDialogPane().getButtonTypes().addAll(ensureButtonType, ButtonType.CANCEL);

		Label isNewVer = new Label();
		Label localVer = new Label();
		Label updateVer = new Label();
		String versionCom = "版本ver1.2\n新增功能:\n1.增加功能3\n2.增加功能4\n\n修复....的bug\n";
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
			isNewVer.setText("检测到新版本，是否进行更新?");
			localVer.setText("本地配置文件哈希码: " + localConfigHash);
			updateVer.setText("最新配置文件哈希码: " + updateConfigHash);
		} else {
			isNewVer.setText("没有检测到新版本!");
			localVer.setText("本地配置文件哈希码" + localConfigHash);
			updateVer.setText("最新配置文件哈希码" + updateConfigHash);
		}

		checkUpdateDialog.setResultConverter(dialogButton -> {
			if (dialogButton == ensureButtonType) {
				return true;
			}
			return false;
		});

		Optional<Boolean> result = checkUpdateDialog.showAndWait();
		return result.get()&&isNewVersion;
	}
	
	// 检测更新响应事件
	public void checkUpdateAction(ActionEvent e) throws IOException, InterruptedException {
		// showUpdateDialog(checkUpdate());
		if(!showUpdateDialog(checkUpdate()))
			return ;
		new Thread() {
			public void run() {
				try {
					Process process = Runtime.getRuntime().exec("cmd /c java -jar ./update.jar");
					process.waitFor();
				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		
		System.exit(0);
	}

	// 新建配置文件响应事件
	public void newConfAction(ActionEvent e) throws IOException {
		File file = new File(localConfigPath);
		String path = "新建配置文件";
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
		logger.info("Config new : "+path+".xml");
	}

	// 打开配置文件响应事件
	public void openConfAction(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File("./"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));
		File file = fileChooser.showOpenDialog(null);
		ObservableList<Config> newConfig = FXCollections.observableArrayList();
		newConfig.addAll(MyXMLReader.getXMlFile(file.getPath()));

		configList.add(new ConfigListItem(file.getName().substring(0, file.getName().length() - 4)));
		configLL.add(newConfig);
		logger.info("Config open : "+file.getName());
	}

	// 退出响应事件
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
			if (!cf.exists()) {
				cf.createNewFile();
			}
			// 创建输出流
			FileOutputStream out = new FileOutputStream(cf);
			// createPrettyPrint格式化xml并返回一个OutPutFormat对象
			OutputFormat of = OutputFormat.createPrettyPrint();
			// 创建一个XMLWriter对象
			XMLWriter writer = new XMLWriter(out, of);
			writer.write(document);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
