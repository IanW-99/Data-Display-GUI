// Created by, Ian Washburn: iwashburn@student.bridgew.edu.

import java.awt.*;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class PlotPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	final double spacing = 20;
	final int scaling = 20;
	private double xaxis;
	private double yaxis;
	private int dataLines;
	private int type;
	private DataPoint lastSelectedPoint;
	private DataPoint currentSelectedPoint;

	private List<DataPoint> dataPoints;

	public PlotPanel(int type, String dataFile) throws IOException {
		// type 0 is for true values and type 1 is for image
		this.type = type;

		setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(500, 500));

		BufferedReader lineCheck = null;
		dataLines = 0;

		try {
			lineCheck = new BufferedReader(new FileReader(dataFile));
			String line = lineCheck.readLine();

			while(line != null) {
				dataLines++;
				line = lineCheck.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		assert lineCheck != null;
		lineCheck.close();

		dataPoints = new ArrayList<DataPoint>(dataLines);

		lastSelectedPoint = null;

	}

	private void lines(Graphics2D g2, double x1, double y1, double x2, double y2) {

		g2.draw(new Line2D.Double(x1, y1, x2, y2));
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		final double width = getWidth();
		final double height = getHeight();

		xaxis = width / 2.0;
		yaxis = height / 2.0;
		final double x1 = 0;
		final double y1 = 0;


		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(1));

		for (double x = spacing; x < width; x += spacing) {

			lines(g2, xaxis + x, y1, xaxis + x, height);
			lines(g2, xaxis - x, y1, xaxis - x, height);
		}

		for (double y = spacing; y < height; y += spacing) {

			lines(g2, x1, yaxis + y, width, yaxis + y);
			lines(g2, x1, yaxis - y, width, yaxis - y);
		}

		g.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(2));

		g2.draw(new Line2D.Double(x1, yaxis, width, yaxis));
		g2.draw(new Line2D.Double(xaxis, y1, xaxis, height));

		g.setColor(Color.RED);
		g2.setStroke(new BasicStroke(5));

		double xCoord;
		double yCoord;
		DataPoint storedDataPoint = null;

		if(dataPoints.size() != 0) {
			for (DataPoint currentDataPoint : dataPoints) {
				if (this.type == 0) {
					xCoord = currentDataPoint.xTrue;
					yCoord = currentDataPoint.yTrue;
				} else {
					for (int j = 0; j < currentDataPoint.coordinatePoints.length; j++) {
						xCoord = currentDataPoint.coordinatePoints[j][0];
						yCoord = currentDataPoint.coordinatePoints[j][1];
						if (currentDataPoint.getSelectionStatus()) {
							storedDataPoint = currentDataPoint;
						} else {
							//System.out.println(xCoord + ", " + yCoord);
							g2.draw(new Line2D.Double((scaling * xCoord) + xaxis, yaxis - (scaling * yCoord),
									(scaling * xCoord) + xaxis, yaxis - (scaling * yCoord)));
						}
					}
				}

				if (storedDataPoint != null) {
					if (type == 0) {
						xCoord = storedDataPoint.xTrue;
						yCoord = storedDataPoint.yTrue;
						g.setColor(Color.BLUE);
						g2.draw(new Line2D.Double((scaling * xCoord) + xaxis, yaxis - (scaling * yCoord),
								(scaling * xCoord) + xaxis, yaxis - (scaling * yCoord)));
					} else {
						for (int j = 1; j < currentDataPoint.coordinatePoints.length; j++) {
							xCoord = storedDataPoint.coordinatePoints[j][0];
							yCoord = storedDataPoint.coordinatePoints[j][1];
							g.setColor(Color.BLUE);
							g2.draw(new Line2D.Double((scaling * xCoord) + xaxis, yaxis - (scaling * yCoord),
									(scaling * xCoord) + xaxis, yaxis - (scaling * yCoord)));
						}
					}
				}
			}
		}
	}

	public void addDataPoint(DataPoint dataPoint) {
		dataPoints.add(dataPoint);
	}

	public void clearData() {
		dataPoints = null;
		dataPoints = new ArrayList<DataPoint>(dataLines);
	}


	public Double convertXCoord(int intCoord) {
		return (intCoord - xaxis) / scaling;
	}

	public Double convertYCoord(int intCoord) {
		return (yaxis - intCoord) / scaling;
	}

	// checks for point with matching mouse coordinates
	public DataPoint dataCheck(Double xCoord, Double yCoord) {
		DataPoint returnPoint = null;
		double clickError = 0.3;
		Range xRange;
		Range yRange;
		double dummyValue = 25;
		Range xRange2 = new Range(dummyValue, dummyValue); //make secondary ranges larger than plot size to avoid errors while still initializing
		Range yRange2 =new Range(dummyValue, dummyValue);

		for (DataPoint dataPoint : dataPoints) {
			xRange = new Range((dataPoint.xTrue - clickError), (dataPoint.xTrue + clickError));
			yRange = new Range((dataPoint.yTrue - clickError), (dataPoint.yTrue + clickError));

			if ((xRange.contains(xCoord) && yRange.contains(yCoord)) || (xRange2.contains(xCoord) && yRange2.contains(yCoord))) {
				dataPoint.pointSelected();
				if (lastSelectedPoint != null) {
					lastSelectedPoint.pointDeselected();
				}
				lastSelectedPoint = dataPoint;
				returnPoint = dataPoint;
				break;
			}
		}
		return returnPoint;
	}


}