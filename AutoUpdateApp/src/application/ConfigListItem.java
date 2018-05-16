package application;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConfigListItem {
//	private Config confItem = new Config();
	private SimpleStringProperty name = new SimpleStringProperty();
	public ConfigListItem() {
		// TODO Auto-generated constructor stub
	}
	public ConfigListItem(String name) {
		// TODO Auto-generated constructor stub
		this.name.set(name);
	}
	public String getItem()
	{
		return name.get();
	}
	
	public void setItem(String itemName)
	{
		name.set(itemName);
	}
	
	public final StringProperty itemProperty() {
        return this.name;
    }
}
