// Created by, Ian Washburn: iwashburn@student.bridgew.edu.

import javax.swing.*;

import java.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;

public class GUI{

	private final JButton refresh;
	private final JButton enableCheckBoxes;
	private final JButton disableCheckBoxes;
	private final JLabel clickCoords;
	private final JLabel clickData;
	private final JFrame frame;
	private final JMenuBar menuBar;
	private final JMenu dataMenu;
	private List<DataPoint> dataPoints;
	private DataPoint selectedDataPoint;

	// change data text file here
	private final String dataFile;

	//private Insets frameInsets;

	private PlotPanel truePlotPanel;
	private PlotPanel imagePlotPanel;


	public GUI(String dataFile) throws IOException {

		this.dataFile = dataFile;

		frame = new JFrame("Plot");
		refresh = new JButton("Refresh Graph");
		disableCheckBoxes = new JButton("Disable All");
		enableCheckBoxes = new JButton("Enable All");
		clickCoords = new JLabel("....................");
		clickData = new JLabel("Click Data");

		menuBar = new JMenuBar();
		dataMenu = new JMenu("Data CheckBoxes");

	}

	public void setUpGUI() {

		frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int frameWidth = 1200;
		int frameHeight = 700;
		frame.setSize(new Dimension(frameWidth, frameHeight));
		frame.setResizable(false);
		frame.setBackground(Color.BLUE);

		// forcing coordinate label to not resize
		Dimension labelSize = new Dimension(150, 100);
		clickCoords.setFont(new Font("Tahoma", Font.BOLD, 14));
		clickCoords.setMinimumSize(labelSize);
		clickCoords.setPreferredSize(labelSize);
		clickCoords.setMaximumSize(labelSize);

		Dimension secondaryLabelSize = new Dimension(250, 100);
		clickData.setFont(new Font("Tahoma", Font.BOLD, 14));
		clickData.setMinimumSize(secondaryLabelSize);
		clickData.setPreferredSize(secondaryLabelSize);
		clickData.setMaximumSize(secondaryLabelSize);

		//Organizing check boxes
		double previousXTrue = Integer.MAX_VALUE;
		JMenu newMenu = null;

		for (DataPoint dataPoint : dataPoints) {
			double xTrue = dataPoint.xTrue;
			if ((int) xTrue != (int) previousXTrue) {
				newMenu = new JMenu("xTrue = " + (int) xTrue);
				dataMenu.add(newMenu);
			} else {
				assert newMenu != null;
			}
			newMenu.add(dataPoint.getCheckBox());
			previousXTrue = xTrue;
		}

		menuBar.add(dataMenu);
		menuBar.add(enableCheckBoxes);
		menuBar.add(disableCheckBoxes);
		menuBar.add(refresh);
		frame.setJMenuBar(menuBar);

		JPanel backPanel = new JPanel(new BorderLayout(200, 50));

		try {
			truePlotPanel = new PlotPanel(0, dataFile);
			imagePlotPanel = new PlotPanel(1, dataFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		backPanel.add(truePlotPanel, BorderLayout.LINE_START);
		backPanel.add(imagePlotPanel, BorderLayout.LINE_END);
		//backPanel.add(clickCoords, BorderLayout.PAGE_START);
		backPanel.add(clickData, BorderLayout.NORTH);

		truePlotPanel.addMouseListener(trueMouseLocationListener);
		//imagePlotPanel.addMouseListener(imageMouseLocationListener);


		frame.add(backPanel);
		frame.setVisible(true);

		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Refresh Pushed");
				refresh();
			}
		});

		enableCheckBoxes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Enable CheckBoxes Pushed");
				enableCheckBoxes();
			}
		});

		disableCheckBoxes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Disable CheckBoxes Pushed");
				disableCheckBoxes();
			}
		});




		setPoints(truePlotPanel);
		setPoints(imagePlotPanel);
	}

	// organizes data from text file & creates check boxes
	public void dataOrganization() throws IOException {

		BufferedReader lineCheck = null;
		int dataLines = 0;

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

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			String line = reader.readLine();

			while(line != null) {
				List<String> seperatedDataString = Arrays.asList(line.split(","));
				double xTrue = Double.parseDouble(seperatedDataString.get(0));
				double yTrue = Double.parseDouble(seperatedDataString.get(1));
				boolean found = false;
				DataPoint foundDataPoint = null;
				for (DataPoint dataPoint : dataPoints) {
					if (dataPoint.xTrue == xTrue && dataPoint.yTrue == yTrue) {
						found = true;
						foundDataPoint = dataPoint;
						break;
					}
				}
				if(!found) {
					JCheckBox newCheckBox = new JCheckBox("placeholder");
					newCheckBox.setSelected(true);
					DataPoint dataBeingCreated = new DataPoint(line, newCheckBox);
					dataPoints.add(dataBeingCreated);
					newCheckBox.setText("(" + dataBeingCreated.xTrue + ", " + dataBeingCreated.yTrue + ")");
					newCheckBox.addActionListener(checkBoxListener);

				} else{
					foundDataPoint.setImageData(Double.parseDouble(seperatedDataString.get(2)), Double.parseDouble(seperatedDataString.get(3)), Double.parseDouble(seperatedDataString.get(4)));
				}
				line = reader.readLine();
			};

			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//Selection sort of xTrue
		double minXTrue;
		double xTrue;
		double yTrue;
		int indexToBeSwapped = 0;
		double minYTrue;
		DataPoint temp;

		for(int i=0; i<dataPoints.size(); i++) {
			minXTrue = Integer.MAX_VALUE;
			minYTrue = Integer.MAX_VALUE;
			for(int j = i; j<dataPoints.size(); j++) {
				xTrue = dataPoints.get(j).xTrue;
				yTrue = dataPoints.get(j).yTrue;
				//System.out.println(xTrue + " " + yTrue);
				if(xTrue < minXTrue) {
					minXTrue = xTrue;
					minYTrue = yTrue;
					indexToBeSwapped = j;
				}
				else if(xTrue == minXTrue) {
					if(yTrue < minYTrue) {
						minXTrue = xTrue;
						minYTrue = yTrue;
						indexToBeSwapped = j;
					}
				}
			}
			temp = dataPoints.get(i);
			dataPoints.set(i, dataPoints.get(indexToBeSwapped));
			dataPoints.set(indexToBeSwapped, temp);
			temp = null;
		}

	}

	// sets the on the plot
	public void setPoints(PlotPanel plotPanel) {

		for (DataPoint dataPoint : dataPoints) {
			if (dataPoint.getDisplayStatus()) {
				plotPanel.addDataPoint(dataPoint);
			}
		}
		System.out.println("Done");
		plotPanel.repaint();
	}

	public void enableCheckBoxes(){
		for (DataPoint dataPoint : dataPoints) {
			dataPoint.changeDisplayStatus(true);
			dataPoint.getCheckBox().setSelected(true);
		}
	}

	public void disableCheckBoxes(){
		for (DataPoint dataPoint : dataPoints) {
			dataPoint.changeDisplayStatus(false);
			dataPoint.getCheckBox().setSelected(false);
		}
	}

	//refreshes graphics to update any changes
	public void refresh() {
		truePlotPanel.clearData();
		imagePlotPanel.clearData();
		setPoints(truePlotPanel);
		setPoints(imagePlotPanel);
	}


	// action listener for check boxes
	ActionListener checkBoxListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AbstractButton abstractButton = (AbstractButton) e.getSource();
			boolean selected = abstractButton.getModel().isSelected();

			boolean found = false;
			int index = 0;
			while(!found) {
				if(dataPoints.get(index).getCheckBox() ==  abstractButton) {
					dataPoints.get(index).changeDisplayStatus(selected);
					found = true;
				} else {
					index++;
				}
			}

			System.out.println(selected);
		}
	};

	// action listener for mouse clicks
	/*
	MouseAdapter imageMouseLocationListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			Double xCoord = imagePlotPanel.convertXCoord(e.getX());
			Double yCoord = imagePlotPanel.convertYCoord(e.getY());
			clickCoords.setText("X = " + xCoord + "; Y = " + yCoord);
			selectedDataPoint = imagePlotPanel.dataCheck(xCoord, yCoord);
			if(selectedDataPoint != null) {
				clickData.setText("<html>Xtrue = " + selectedDataPoint.xTrue + "; Ytrue = " + selectedDataPoint.yTrue +
						"<br/> Primary (Ximage, Yimage) = " + selectedDataPoint.primaryXImage + ", " + selectedDataPoint.primaryYImage+ "; Distance = " + selectedDataPoint.primaryCoordDistance +
						"<br/> Secondary (Ximage, Yimage) = " + selectedDataPoint.secondaryXImage + ", " + selectedDataPoint.secondaryYImage + "; Distance = " + selectedDataPoint.secondaryCoordDistance + "</html>");
				refresh();
			} else {
				clickData.setText(null);
			}
		}
	};
	 */

	MouseAdapter trueMouseLocationListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			Double xCoord = truePlotPanel.convertXCoord(e.getX());
			Double yCoord = truePlotPanel.convertYCoord(e.getY());
			clickCoords.setText("<html>XTrue = " + xCoord + "; YTrue = " + yCoord);
			selectedDataPoint = truePlotPanel.dataCheck(xCoord, yCoord);
			if(selectedDataPoint != null) {
				double[][] imageCoords = new double[selectedDataPoint.coordinatePoints.length][2];
				for(int i = 1; i < selectedDataPoint.coordinatePoints.length; i++){
					imageCoords[i-1][0] = selectedDataPoint.coordinatePoints[i][0];
					imageCoords[i-1][1] = selectedDataPoint.coordinatePoints[i][1];
					imageCoords[i-1][2] = selectedDataPoint.coordinatePoints[i][2];
				}
				for(double[] images: imageCoords){
					clickCoords.setText("<html>" + clickCoords.getText()+ "<br/>" + images[0] + ", " +
							images[1] + ", " + images[2] + "</html");
				}
				refresh();
			}else {
				clickData.setText(null);
			}
		}
	};


}

