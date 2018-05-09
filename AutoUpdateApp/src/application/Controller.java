package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.sun.corba.se.impl.util.Version;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class Controller implements Initializable{

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
	
	//存放配置文件的列表
	private ArrayList<ObservableList<Config>> configLL = new ArrayList<ObservableList<Config>>();
	
	//配置文件列表
	ObservableList<ConfigListItem> configList = FXCollections.observableArrayList();
	
	//一个配置文件的列表
	ObservableList<Config> configData = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		initTable();
		initButton();
	}

	private void initButton()
	{
		ensureButton.setOnAction((ActionEvent e)->{
			int selectIndex =itemTable.getSelectionModel().getSelectedIndex();
			itemTable.getSelectionModel().getSelectedItem().setItem(version.getText());
			configLL.get(selectIndex).clear();
			configLL.get(selectIndex).addAll(configData);
		});
		saveAsButton.setOnAction((ActionEvent e)->{
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(null);
            if (file != null) {
            	
            }
		});
		cancelButton.setOnAction((ActionEvent e)->{
			configData.clear();
		});
		addFileButton.setOnAction((ActionEvent e)->{
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                 configData.add(new Config(file.getName(),file.getAbsolutePath(),""));
            }
		});
	}
	
	private class ItemCellFactory implements Callback<TableColumn<ConfigListItem, String>, TableCell<ConfigListItem, String>> {
	    @Override
	    public TableCell<ConfigListItem, String> call(TableColumn<ConfigListItem, String> param) {
	        TextFieldTableCell<ConfigListItem, String> cell = new TextFieldTableCell<>();
	        cell.setOnMouseClicked((MouseEvent t) -> {
	            if(t.getClickCount()==2)
	            {
	            	//响应事件，将对应的配置文件信息显示到dateTable中。
	            	int selectIndex = itemTable.getSelectionModel().getSelectedIndex();
	            	configData.clear();
	            	configData.addAll(configLL.get(selectIndex));
	            }
	            MouseButton button = t.getButton();
	            switch(button) {
                case PRIMARY:  break;
                case SECONDARY: 
                	MenuItem delete = new MenuItem("删除");
                	delete.setOnAction((ActionEvent e)->{
                		configList.remove(itemTable.getSelectionModel().getSelectedIndex());
                	});
                	ContextMenu taskContextMenu = new ContextMenu();
                	taskContextMenu.getItems().add(delete);
					cell.setContextMenu(taskContextMenu );
					break;
                case MIDDLE:  break;
                default: ;
              }
	        });
	        return cell;
	    }
	}

	
	//行响应事件(其实是cell的响应事件)
	private class TaskCellFactory implements Callback<TableColumn<Config, String>, TableCell<Config, String>> {
		    @Override
		    public TableCell<Config, String> call(TableColumn<Config, String> param) {
		        TextFieldTableCell<Config, String> cell = new TextFieldTableCell<>();
		        cell.setOnMouseClicked((MouseEvent t) -> {
		            MouseButton button = t.getButton();
		            switch(button) {
                    case PRIMARY:  break;
                    case SECONDARY: 
                    	MenuItem delete = new MenuItem("删除");
                    	delete.setOnAction((ActionEvent e)->{
                    		configData.remove(dateTable.getSelectionModel().getSelectedIndex());
                    	});
                    	ContextMenu taskContextMenu = new ContextMenu();
                    	taskContextMenu.getItems().add(delete);
						cell.setContextMenu(taskContextMenu );
						break;
                    case MIDDLE:  break;
                    default: ;
                  }
		        });
		        return cell;
		    }
		}
	
	//初始化table,两个table
	private void initTable()
	{
		//configData.add(new Config("name","path",""));
		dateTable.setItems(configData);
		
		configList.add(new ConfigListItem("ver1.0"));
		ObservableList<Config> list = FXCollections.observableArrayList();
		list.addAll(new Config("file1","C:\\APP\\file1","新增"),new Config("file2","C:\\APP\\file2","新增"));
		configLL.add(list);
		
		itemTable.setItems(configList);
		
		configItemCol.setMinWidth(100);
		configItemCol.setSortable(false);
		configItemCol.setCellFactory(new ItemCellFactory());
		configItemCol.setCellValueFactory(
	               new PropertyValueFactory<>("item"));
		
		nameCol.setMinWidth(100);
	    nameCol.setSortable(false);
	    nameCol.setCellFactory(new TaskCellFactory());
	    nameCol.setCellValueFactory(
	               new PropertyValueFactory<>("name"));
	     
	    pathCol.setMinWidth(100);
	    pathCol.setSortable(false);
	    pathCol.setCellFactory(new TaskCellFactory());
	    pathCol.setCellValueFactory(
	               new PropertyValueFactory<>("path"));
	    
	    updateMethodCol.setMinWidth(100);
	    updateMethodCol.setSortable(false);
	    updateMethodCol.setCellFactory(new Callback<TableColumn<Config, String>, TableCell<Config, String>>() {

            @Override
            public TableCell<Config, String> call(TableColumn<Config, String> param) {
                return new LiveComboBoxTableCell<>(FXCollections.observableArrayList("覆盖", "新增","删除"));
            }
        });
	    updateMethodCol.setCellValueFactory(
	               new PropertyValueFactory<>("updateMethod"));
	}
	
	
	//设置更新网址事件
	public void setUpdateURLAction(ActionEvent e)
	{
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
		if(!result.get().isEmpty())
			updateURL = result.get();
	}
	
	
	//检测更新响应事件
	public void checkUpdateAction(ActionEvent e) throws InterruptedException
	{
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
		//gridPane.add(textArea, 0, 4);
		//gridPane.add(updateInfo, 0, 5);
		
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		
		checkUpdateDialog.getDialogPane().setExpandableContent(textArea);
		checkUpdateDialog.getDialogPane().setContent(gridPane);
		
		if(isNewVersion)
		{
			isNewVer.setText("检测到新版本，是否进行更新?");
			localVer.setText("本地配置文件哈希码: "+localConfHash);
			updateVer.setText("最新配置文件哈希码: "+updateConfHash);
		}
		else
		{
			isNewVer.setText("没有检测到新版本!");
			localVer.setText("本地配置文件哈希码"+localConfHash);
			//小问题
			updateVer.setText("最新配置文件哈希码"+localConfHash);
		}
		
		checkUpdateDialog.setResultConverter(dialogButton -> {
		    if (dialogButton == ensureButtonType) {
		    	return true;
		    }
		    return false;
		});
		
		Optional<Boolean> result = checkUpdateDialog.showAndWait();	
		
		if(result.get()&&isNewVersion)
		{
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
					for(int i=0,j=0;i<=100;i++,j++)
					{
						pbBar.setProgress((double)i/100);
						if(j%10==0)
							t.setText(tString+".");
						else if(j%10==1)
							t.setText(tString+"..");
						else if(j%10==2)
							t.setText(tString+"...");
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
	
	//新建配置文件响应事件
	public void newConfAction(ActionEvent e)
	{
		System.out.println("new");
		configLL.add(FXCollections.observableArrayList());
		configList.add(new ConfigListItem("新建配置文件"));
	}
	
	//打开配置文件响应事件
	public void openConfAction(ActionEvent e)
	{
		
	}
	
	//退出响应事件
	public void exitAction(ActionEvent e)
	{
		System.exit(0);
	}
	
	//ComboBoxTableCell
	public static class LiveComboBoxTableCell<S,T> extends TableCell<S, T> {

	        private final ComboBox<T> comboBox ;

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
