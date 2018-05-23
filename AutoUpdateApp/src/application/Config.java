package application;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.Md5HashCode;

public class Config {
	public Config() {
		// TODO Auto-generated constructor stub
	}
	
	public Config(String name,String path, String updatePath,String updateMethod){
		this.name.set(name);
		this.path.set(path);
		this.updatePath.set(updatePath);
		this.updateMethod.set(updateMethod);
		this.hash.set(Md5HashCode.getHashCode(this.getPath()));
	}
	
	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleStringProperty path = new SimpleStringProperty();
	private SimpleStringProperty hash = new SimpleStringProperty();
	private SimpleStringProperty updatePath = new SimpleStringProperty();
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
	public String getUpdatePath() {
		return updatePath.get();
	}
	public void setUpdatePath(String updatePath) {
		this.updatePath.set(updatePath);
	}
	public String getHash() {
		return hash.get();
	}
	public void setHash(String hash) {
		this.hash.set(hash);
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
