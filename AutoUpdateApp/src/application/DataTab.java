package application;


import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class DataTab extends Tab{
	private ObservableList<Config> configData = FXCollections.observableArrayList();
	private AnchorPane dataPane;
	private TextField textField = new TextField();
	//记录之前的name
	private 	String oldName;
	private Label label = new Label("配置版本");
	private TableView<Config> dataTable;
	private TableColumn<Config, String> nameCol;
	private TableColumn<Config, String> pathCol;
	private TableColumn<Config, String> hashCol;
	private TableColumn<Config, String> updatePathCol;
	private TableColumn<Config, String> updateMethodCol;
	
	public DataTab(String name) {
		this.setClosable(true);
		this.setText(name);
		initTableView();
		initMenu();
		initPane();
		dataTable.setEditable(true);
		textField.setText(name);
		oldName = name;
		//关联数据和显示
		dataTable.setItems(configData);
		this.setContent(dataPane);
		
	}
	
	public DataTab(ObservableList<Config> data,String name) {
		this(name);
		configData.addAll(data);
		for(Config c:configData) {
			System.out.println("Config:  "+c.getName());
		}
	}
	
	//table初始化
	private void initTableView() {	
		nameCol = new TableColumn<>("文件名称");
		nameCol.setMinWidth(100);
		nameCol.setSortable(false);
		nameCol.setEditable(false);
		nameCol.setCellFactory(new TaskCellFactory());
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		pathCol = new TableColumn<>("文件路径");
		pathCol.setMinWidth(100);
		pathCol.setSortable(false);
		pathCol.setEditable(false);
		pathCol.setCellFactory(new TaskCellFactory());
		pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
		
		hashCol = new TableColumn<>("哈希码");
		hashCol.setMinWidth(100);
		hashCol.setSortable(false);
		hashCol.setEditable(false);
		hashCol.setCellFactory(new TaskCellFactory());
		hashCol.setCellValueFactory(new PropertyValueFactory<>("hash"));
		
		updatePathCol = new TableColumn<>("更新路径");
		updatePathCol.setMinWidth(100);
		updatePathCol.setSortable(false);
		updatePathCol.setEditable(true);
		//添加编辑提交相应函数
		updatePathCol.setOnEditCommit(
                (CellEditEvent<Config, String> t) -> {
                    ( t.getTableView().getItems()
                            .get(t.getTablePosition().getRow()))
                            .setUpdatePath(t.getNewValue());
                });
		updatePathCol.setCellFactory(TextFieldTableCell.forTableColumn());
		updatePathCol.setCellValueFactory(new PropertyValueFactory<>("updatePath"));
		
		updateMethodCol = new TableColumn<>("更新方法");
		updateMethodCol.setMinWidth(100);
		updateMethodCol.setSortable(false);
		updateMethodCol.setCellFactory(new Callback<TableColumn<Config, String>, TableCell<Config, String>>() {

			@Override
			public TableCell<Config, String> call(TableColumn<Config, String> param) {
				return new LiveComboBoxTableCell<>(FXCollections.observableArrayList("覆盖", "新增", "删除"));
			}
		});
		updateMethodCol.setCellValueFactory(new PropertyValueFactory<>("updateMethod"));
	
		dataTable = new TableView<>();
		dataTable.getColumns().addAll(nameCol,pathCol,hashCol,
				updatePathCol,updateMethodCol);
	}
	
	//menu初始化
	private void initMenu() {
		//设置为可关闭
		this.setClosable(true);
		
		ContextMenu menu = new ContextMenu();
		MenuItem closeItem = new MenuItem("close");
		//关闭当前Tab
		closeItem.setOnAction((ActionEvent e) -> {
			System.out.println("CLOSE");
		});
		//MenuItem closeAllItem = new MenuItem("close");
		menu.getItems().addAll(closeItem);
		this.setContextMenu(menu);
	}
	
	//pane初始化
	private void initPane() {
		dataPane = new AnchorPane();
		dataPane.getChildren().addAll(label,textField,dataTable);
		AnchorPane.setTopAnchor(label, 5.0);
		AnchorPane.setLeftAnchor(label, 10.0);
		AnchorPane.setTopAnchor(textField, 5.0);
		AnchorPane.setLeftAnchor(textField, 100.0);
		AnchorPane.setTopAnchor(dataTable, 30.0);
		AnchorPane.setLeftAnchor(dataTable, 0.0);
		AnchorPane.setRightAnchor(dataTable, 0.0);
		AnchorPane.setBottomAnchor(dataTable, 40.0);
	}
	
	//获取table数据
	public ObservableList<Config> getTableData(){
		return configData;
	}
	
	//增加数据
	public void addTableData(Config config) {
		configData.add(config);
	}
	
	//删除数据
	public void deleteTableData(int index) {
		configData.remove(index);
	}
	
	//获取textField的text
	public String getTextName() {
		return textField.getText();
	}
	
	//获取旧name
	public String getHisName() {
		return oldName;
	}
	
	public void setHisName(String oldName) {
		this.oldName = oldName;
	}
	
	//普通的cell
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
						configData.remove(dataTable.getSelectionModel().getSelectedIndex());
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

	//ComboBoxTableCell
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
