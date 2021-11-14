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

	private JButton refresh;
	private JButton enableCheckBoxes;
	private JButton disableCheckBoxes;
	private JLabel clickCoords;
	private JLabel clickData;
	private Dimension labelSize;
	private Dimension secondaryLabelSize;
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu dataMenu;
	private List<DataPoint> dataPoints;
	private DataPoint selectedDataPoint;

	// change data text file here
	private String dataFile;

	private int frameWidth = 1200;
	private int frameHeight = 700;
	//private Insets frameInsets;

	private PlotPanel truePlotPanel;
	private PlotPanel imagePlotPanel;
	private JPanel backPanel;


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
		frame.setSize(new Dimension(frameWidth, frameHeight));
		frame.setResizable(false);
		frame.setBackground(Color.BLUE);

		// forcing coordinate label to not resize
		labelSize = new Dimension(150, 100);
		clickCoords.setFont(new Font("Tahoma", Font.BOLD, 14));
		clickCoords.setMinimumSize(labelSize);
		clickCoords.setPreferredSize(labelSize);
		clickCoords.setMaximumSize(labelSize);

		secondaryLabelSize = new Dimension(250, 100);
		clickData.setFont(new Font("Tahoma", Font.BOLD, 14));
		clickData.setMinimumSize(secondaryLabelSize);
		clickData.setPreferredSize(secondaryLabelSize);
		clickData.setMaximumSize(secondaryLabelSize);

		//Organizing check boxes
		double previousXTrue = Integer.MAX_VALUE;
		JMenu newMenu = null;

		for(int i=0; i<dataPoints.size(); i++) {
			double xTrue = dataPoints.get(i).xTrue;
			if((int) xTrue != (int) previousXTrue) {
				newMenu = new JMenu("xTrue = " + (int) xTrue);
				dataMenu.add(newMenu);
				newMenu.add(dataPoints.get(i).getCheckBox());
			} else {
				newMenu.add(dataPoints.get(i).getCheckBox());
			}
			previousXTrue = xTrue;
		}

		menuBar.add(dataMenu);
		menuBar.add(enableCheckBoxes);
		menuBar.add(disableCheckBoxes);
		menuBar.add(refresh);
		frame.setJMenuBar(menuBar);

		backPanel = new JPanel(new BorderLayout(200, 50));

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
		imagePlotPanel.addMouseListener(imageMouseLocationListener);


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

		lineCheck.close();


		dataPoints = new ArrayList<DataPoint>(dataLines);

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			String line = reader.readLine();

			while(line != null) {
				List<String> seperatedDataString = Arrays.asList(line.split(","));
				double xTrue = Double.valueOf(seperatedDataString.get(0));
				double yTrue = Double.valueOf(seperatedDataString.get(1));
				boolean found = false;
				DataPoint foundDataPoint = null;
				for(int i = 0; i < dataPoints.size(); i++) {
					if(dataPoints.get(i).xTrue == xTrue && dataPoints.get(i).yTrue == yTrue) {
						found = true;
						foundDataPoint = dataPoints.get(i);
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
					foundDataPoint.setImageData(Double.valueOf(seperatedDataString.get(2)), Double.valueOf(seperatedDataString.get(3)), Double.valueOf(seperatedDataString.get(4)));
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
				System.out.println(xTrue + " " + yTrue);
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

		for(int i=0; i<dataPoints.size(); i++) {
			if(dataPoints.get(i).getDisplayStatus()) {
				plotPanel.addDataPoint(dataPoints.get(i));
			}
		}
		System.out.println("Done");
		plotPanel.repaint();
	}

	public void enableCheckBoxes(){
		for(int i=0; i<dataPoints.size(); i++){
			dataPoints.get(i).changeDisplayStatus(true);
			dataPoints.get(i).getCheckBox().setSelected(true);
		}
	}

	public void disableCheckBoxes(){
		for(int i=0; i<dataPoints.size(); i++){
			dataPoints.get(i).changeDisplayStatus(false);
			dataPoints.get(i).getCheckBox().setSelected(false);
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
			while(found == false) {
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

	MouseAdapter trueMouseLocationListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			Double xCoord = truePlotPanel.convertXCoord(e.getX());
			Double yCoord = truePlotPanel.convertYCoord(e.getY());
			clickCoords.setText("X = " + xCoord + "; Y = " + yCoord);
			selectedDataPoint = truePlotPanel.dataCheck(xCoord, yCoord);
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


}

