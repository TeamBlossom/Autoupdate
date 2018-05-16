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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import util.Md5HashCode;
import util.MyXMLReader;

public class Controller implements Initializable {
	final static String URLPath = "./UpdateURL";
	final static String localConfigPath = "./Config/";

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
	private TableColumn<Config, String> updateMethodCol;

	@FXML
	private TableView<ConfigListItem> itemTable;
	@FXML
	private TableColumn<ConfigListItem, String> configItemCol;

	private String updateURL;

	// 存放配置文件的列表
	private ArrayList<ObservableList<Config>> configLL = new ArrayList<ObservableList<Config>>();

	// 配置文件列表..左边的
	ObservableList<ConfigListItem> configList = FXCollections.observableArrayList();

	// 一个配置文件的列表...右边的
	ObservableList<Config> configData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		intiWebURL();
		intiLoadLocalConfig();
		initTable();
		initButton();
	}

	//读取本地配置文件
	private void intiLoadLocalConfig() {
		File configDir = new File(localConfigPath);
		//当文件夹不存在时，新建配置文件文件夹
		if (!configDir.exists()) {
			configDir.mkdir();
		}
		//读取子目录下的xml文件
		configLL.clear();
		if(configDir.listFiles()!=null) {
			for(File child : configDir.listFiles()) {
				//做正则匹配，xml文件
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
		ensureButton.setOnAction((ActionEvent e) -> {
			// 更新列表
			int selectIndex = itemTable.getSelectionModel().getSelectedIndex();
			// 更新列表的
			String fileName = itemTable.getSelectionModel().getSelectedItem().getItem();
			//System.out.println(fileName);
			itemTable.getSelectionModel().getSelectedItem().setItem(version.getText());
			configLL.get(selectIndex).clear();
			configLL.get(selectIndex).addAll(configData);
			
			// 保存数据
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("Info");
			root.addAttribute("Version", version.getText());

			Element filelist = root.addElement("FileList");
			for (Config config : configData) {
				Element file = filelist.addElement("File");
				file.addAttribute("UpdateMethod", config.getUpdateMethod());
				file.addAttribute("Hash", Md5HashCode.getHashCode(config.getPath()));
				file.addAttribute("Path", config.getPath());
				file.addAttribute("Name", config.getName());
			}

			try {
				System.out.println("enter");
				File cf = new File(localConfigPath+fileName+".xml");
				cf.renameTo(new File(localConfigPath+version.getText()+".xml"));
				cf = new File(localConfigPath+version.getText()+".xml");
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
		});
		saveAsButton.setOnAction((ActionEvent e) -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(null);
			if (file != null) {

			}
		});
		cancelButton.setOnAction((ActionEvent e) -> {
			configData.clear();
		});
		addFileButton.setOnAction((ActionEvent e) -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				configData.add(new Config(file.getName(), file.getAbsolutePath(), ""));
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
					// 响应事件，将对应的配置文件信息显示到dateTable中。
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
					MenuItem delete = new MenuItem("删除");
					delete.setOnAction((ActionEvent e) -> {
						configList.remove(itemTable.getSelectionModel().getSelectedIndex());
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

	// 行响应事件(其实是cell的响应事件)
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
					MenuItem delete = new MenuItem("删除");
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

	// 初始化table,两个table
	private void initTable() {
		// configData.add(new Config("name","path",""));
		dateTable.setItems(configData);

//		configList.add(new ConfigListItem("ver1.0"));
//		ObservableList<Config> list = FXCollections.observableArrayList();
//		list.addAll(new Config("file1", "C:\\APP\\file1", "新增"), new Config("file2", "C:\\APP\\file2", "新增"));
//		configLL.add(list);

		itemTable.setItems(configList);

		configItemCol.setMinWidth(100);
		configItemCol.setSortable(false);
		configItemCol.setCellFactory(new ItemCellFactory());
		configItemCol.setCellValueFactory(new PropertyValueFactory<>("item"));

		nameCol.setMinWidth(100);
		nameCol.setSortable(false);
		nameCol.setCellFactory(new TaskCellFactory());
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		pathCol.setMinWidth(100);
		pathCol.setSortable(false);
		pathCol.setCellFactory(new TaskCellFactory());
		pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));

		updateMethodCol.setMinWidth(100);
		updateMethodCol.setSortable(false);
		updateMethodCol.setCellFactory(new Callback<TableColumn<Config, String>, TableCell<Config, String>>() {

			@Override
			public TableCell<Config, String> call(TableColumn<Config, String> param) {
				return new LiveComboBoxTableCell<>(FXCollections.observableArrayList("覆盖", "新增", "删除"));
			}
		});
		updateMethodCol.setCellValueFactory(new PropertyValueFactory<>("updateMethod"));
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
		}
	}

	// 检测更新响应事件
	public void checkUpdateAction(ActionEvent e) throws InterruptedException {
		boolean isNewVersion = true;
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

		String localConfHash = "a4tr12ginalpoca";
		String updateConfHash = "4vjiapask45xna";

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
			isNewVer.setText("检测到新版本，是否进行更新?");
			localVer.setText("本地配置文件哈希码: " + localConfHash);
			updateVer.setText("最新配置文件哈希码: " + updateConfHash);
		} else {
			isNewVer.setText("没有检测到新版本!");
			localVer.setText("本地配置文件哈希码" + localConfHash);
			// 小问题
			updateVer.setText("最新配置文件哈希码" + localConfHash);
		}

		checkUpdateDialog.setResultConverter(dialogButton -> {
			if (dialogButton == ensureButtonType) {
				return true;
			}
			return false;
		});

		Optional<Boolean> result = checkUpdateDialog.showAndWait();

		if (result.get() && isNewVersion) {
			System.out.println("wode ");
			Dialog<Boolean> updatingDialog = new Dialog<>();
			updatingDialog.setTitle("更新中");
			ButtonType ensButtonType = new ButtonType("确定", ButtonData.OK_DONE);
			updatingDialog.getDialogPane().getButtonTypes().addAll(ensureButtonType, ButtonType.CANCEL);

			GridPane grid = new GridPane();
			ProgressBar pbBar = new ProgressBar(0);
			grid.add(pbBar, 0, 0);

			Text t = new Text();

			grid.add(t, 1, 0);
			String tString = "更新中";
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
							sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					t.setText("更新完成");
				};
			}.start();

			updatingDialog.getDialogPane().setContent(grid);
			updatingDialog.setResultConverter(dialogButton -> {
				if (dialogButton == ensButtonType) {
					return true;
				}
				return false;
			});

			Optional<Boolean> res = updatingDialog.showAndWait();
		}
	}

	// 新建配置文件响应事件
	public void newConfAction(ActionEvent e) throws IOException {
		// System.out.println("new");
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
	}

	// 打开配置文件响应事件
	public void openConfAction(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(null);
	}

	// 退出响应事件
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

}
