package application;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Config {
	public Config() {
		// TODO Auto-generated constructor stub
	}
	
	public Config(String name,String path,String updateMethod){
		this.name.set(name);
		this.path.set(path);
		this.updateMethod.set(updateMethod);
	}
	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleStringProperty path = new SimpleStringProperty();
	private SimpleStringProperty updateMethod = new SimpleStringProperty();
	
	
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name); 
	}
	public String getPath() {
		return path.get();
	}
	public void setPath(String path) {
		this.path.set(path);
	}
	public String getUpdateMethod() {
		return updateMethod.get();
	}
	
	public void setUpdateMethod(String updateMethod) {
		this.updateMethod.set(updateMethod); 
	}
	
	public final StringProperty updateMethodProperty() {
        return this.updateMethod;
    }
	
	
}
