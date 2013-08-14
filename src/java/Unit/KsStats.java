package Unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.db.ColumnFamilyStoreMBean;

public class KsStats {
	private String ksName;
	private Map<String,String> statics = new HashMap<String,String>();
	private List<ColumnFamilyStoreMBean> columnFamilies;
	
	public KsStats(String ksName)
	{
		this.ksName = ksName;
	}
	
	public String getKsName() {
		return ksName;
	}
	public void setKsName(String ksName) {
		this.ksName = ksName;
	}
	public Map<String, String> getStatics() {
		return statics;
	}
	public void setStatics(Map<String, String> statics) {
		this.statics = statics;
	}
	public List<ColumnFamilyStoreMBean> getColumnFamilies() {
		return columnFamilies;
	}
	public void setColumnFamilies(List<ColumnFamilyStoreMBean> columnFamilies) {
		this.columnFamilies = columnFamilies;
	}
	
}
