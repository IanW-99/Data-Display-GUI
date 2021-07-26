import javax.swing.*;

import java.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class GUI{
	
	private JButton refresh;
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu dataMenu;
	private List<DataPoints> dataPoints;
	
	private int frameWidth = 900;
	private int frameHeight = 900;
	//private Insets frameInsets;
	
	private PlotPanel plotPanel;
	private JPanel backPanel;
	
	
	public GUI() throws IOException {
		
		
		frame = new JFrame("Plot");
		refresh = new JButton("Refresh Graph");
		
		menuBar = new JMenuBar();
		dataMenu = new JMenu("Data CheckBoxes");
		
		}
		
	public void setUpGUI() {	
		
		frame.getContentPane();			
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(frameWidth, frameHeight));
		frame.setResizable(false);
		frame.setBackground(Color.BLUE);
		
		//Organizing check boxes
		int previousXTrue = Integer.MAX_VALUE;
		JMenu newMenu = null;
		
		for(int i=0; i<dataPoints.size(); i++) {
			int xTrue = dataPoints.get(i).getIndex(0);
			if(xTrue != previousXTrue) {
				newMenu = new JMenu("xTrue = " + xTrue);
				dataMenu.add(newMenu);
				newMenu.add(dataPoints.get(i).getCheckBox());
			} else {
				newMenu.add(dataPoints.get(i).getCheckBox());
			}
			previousXTrue = xTrue;
		}
		
		menuBar.add(dataMenu);
		frame.setJMenuBar(menuBar);
		
		backPanel = new JPanel();
		backPanel.add(refresh);
		
		try {
			plotPanel = new PlotPanel();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		backPanel.add(plotPanel);
		
	
		frame.add(backPanel);
		frame.setVisible(true);
		
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Refresh Pushed");
				refresh();				
			}
		});
		
		setPoints();
	}
	
	// organizes data from text file & creates check boxes
	public void dataOrganization() throws IOException {
		
		BufferedReader lineCheck = null;
		int dataLines = 0;
		
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
		
		
		//Gathering and organizing data
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\iwash\\eclipse-workspace\\GUI Testing\\src\\data.txt"));
			String line = reader.readLine();
			
			while(line != null) { 
				JCheckBox newCheckBox = new JCheckBox("placeholder");
				newCheckBox.setSelected(true);
				DataPoints dataBeingCreated = new DataPoints(line, newCheckBox);
				dataPoints.add(dataBeingCreated);
				newCheckBox.setText("(" + dataBeingCreated.getIndex(0) + ", " + dataBeingCreated.getIndex(1) + ")");
				newCheckBox.addActionListener(checkBoxListener);
				
				line = reader.readLine();
			};
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Selection sort of xTrue
		int minXTrue;
		int xTrue;
		int yTrue;
		int indexToBeSwapped = 0;
		int minYTrue;
		DataPoints temp;
		
		for(int i=0; i<dataPoints.size(); i++) {
			minXTrue = Integer.MAX_VALUE;
			minYTrue = Integer.MAX_VALUE;
			for(int j = i; j<dataPoints.size(); j++) {
				xTrue = dataPoints.get(j).getIndex(0);
				yTrue = dataPoints.get(j).getIndex(1);
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
	
	public void setPoints() {
		
		for(int i=0; i<dataPoints.size(); i++) {
			if(dataPoints.get(i).getDisplayStatus()) {
				plotPanel.addDataPoint(dataPoints.get(i));
			}
		}
		System.out.println("Done");
		plotPanel.repaint();
	}
	
	public void refresh() {
		plotPanel.clearData();
		setPoints();
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
	
	
	
					
}

