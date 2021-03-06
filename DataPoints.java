// Created by, Ian Washburn: iwashburn@student.bridgew.edu.

import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;

class DataPoint {

	private final JCheckBox cb;
	private boolean displayStatus = true;
	private boolean selected = false;
	public boolean manyCoords = false;

	public double xTrue;
	public double yTrue;


	public double[][] coordinatePoints; // i is a counter of image pairs stored
	private int i = 0;


	public DataPoint(String dataString, JCheckBox cb) {

		this.cb = cb;

		//formatting data

		List<String> seperatedDataString = Arrays.asList(dataString.split(","));
		xTrue = Double.parseDouble(seperatedDataString.get(0));
		yTrue = Double.parseDouble(seperatedDataString.get(1));


		coordinatePoints = new double[10000][3];
		double xImage = Double.parseDouble(seperatedDataString.get(2));
		double yImage = Double.parseDouble(seperatedDataString.get(3));
		double dist = Double.parseDouble(seperatedDataString.get(4));
		setImageData(xImage, yImage, dist);



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

	public void pointSelected(){
		selected = true;
	}

	public void pointDeselected(){
		selected = false;
	}

	public boolean getSelectionStatus(){
		return selected;
	}

	public void setImageData(double xCoord, double yCoord, double dist){
		coordinatePoints[i][0] = xCoord;
		coordinatePoints[i][1] = yCoord;
		coordinatePoints[i][2] = dist;
		i++;
		manyCoords = true;
	}

}
