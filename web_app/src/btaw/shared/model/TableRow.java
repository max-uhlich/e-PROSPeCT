package btaw.shared.model;

import java.io.Serializable;
import java.util.HashMap;



public class TableRow implements Serializable
{
	HashMap<String, String> hemp;
	HashMap<String, Boolean> dupes;
	
	public TableRow() {
		hemp = new HashMap<String, String>();
		dupes = new HashMap<String, Boolean>();
	}

	public String getRowData(String columnName)
	{
		// TODO: Throw error if they somehow supply a column name that is not supplied in patient.
		return hemp.get(columnName.toLowerCase());
	}
	public void addEntry(String key,String value){
		this.hemp.put(key.toLowerCase(), value);
	}
	public void setDuplicate(String key) {
		this.dupes.put(key.toLowerCase(), new Boolean(true));
	}
	public void clearDuplicates() {
		this.dupes.clear();
	}
	public boolean isDuplicate(String key) {
		if(this.dupes.get(key) == null)
			return false;
		else
			return this.dupes.get(key);
	}
	public HashMap<String, String> getRowHM() {
		return hemp;
	}

}
