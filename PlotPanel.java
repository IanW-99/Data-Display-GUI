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
	
	private List<DataPoints> dataPoints;
	
	
	public PlotPanel() throws IOException {
		setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(500, 500));
		
		BufferedReader lineCheck = null;
		dataLines = 0;
		
		try {
			lineCheck = new BufferedReader(new FileReader("C:\\Users\\iwash\\eclipse-workspace\\GUI Testing\\src\\data.txt"));
			String line = lineCheck.readLine();
			
			while(line != null) {
				dataLines++;
				line = lineCheck.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		lineCheck.close();
		
		dataPoints = new ArrayList<DataPoints>(dataLines);
		
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
		final double x2 = width;
		final double y2 = height;

		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.GRAY);
		g2.setStroke(new BasicStroke(1));

		for (double x = spacing; x < width; x += spacing) {

			lines(g2, xaxis + x, y1, xaxis + x, y2);
			lines(g2, xaxis - x, y1, xaxis - x, y2);
		}

		for (double y = spacing; y < height; y += spacing) {

			lines(g2, x1, yaxis + y, x2, yaxis + y);
			lines(g2, x1, yaxis - y, x2, yaxis - y);
		}

		g.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(2));

		g2.draw(new Line2D.Double(x1, yaxis, x2, yaxis));
		g2.draw(new Line2D.Double(xaxis, y1, xaxis, y2));
		
		g.setColor(Color.RED);
		g2.setStroke(new BasicStroke(5));
		
		if(dataPoints.size() != 0) {
			for(int i=0; i<dataPoints.size(); i++) {
				DataPoints currentDataPoint = dataPoints.get(i);
				int xCoord = currentDataPoint.getIndex(0);
				int yCoord = currentDataPoint.getIndex(1);
				g2.draw(new Line2D.Double((scaling * xCoord) + xaxis, yaxis - (scaling * yCoord),
						(scaling * xCoord) + xaxis, yaxis - (scaling * yCoord)));
			}
		}
		
		System.out.println("Done Painting");
		
	}
	
	public void addDataPoint(DataPoints dataPoint) {
		dataPoints.add(dataPoint);
	}
	
	public void clearData() {
		dataPoints = null;
		dataPoints = new ArrayList<DataPoints>(dataLines); 
	}
	
	
	
}