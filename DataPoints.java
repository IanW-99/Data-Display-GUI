import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;

public class DataPoints {
	
	private int[] data;
	private JCheckBox cb;
	private boolean displayStatus = true;
	
	public DataPoints(String dataString, JCheckBox cb) {
	
		data = new int[4];
		this.cb = cb;
		displayStatus = true;
		
		//formatting data as integer array 
		List<String> seperatedDataString = Arrays.asList(dataString.split(","));
		for(int i=0; i<seperatedDataString.size(); i++) {
			data[i] = Integer.parseInt(seperatedDataString.get(i));
		}	
	}
	
	public int getIndex(int index) {
		return data[index];
	}
	
	public void changeDisplayStatus(boolean selected) {
		displayStatus = selected;
	}
	
	public boolean getDisplayStatus() {
		return displayStatus;
	}
	
	public JCheckBox getCheckBox() {
		return cb;
	}
	
}
