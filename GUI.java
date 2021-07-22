import javax.swing.*;

import java.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
//import java.awt.Insets;
import java.awt.event.ActionEvent;

public class GUI{
	
	private JButton refresh;
	private JFrame frame;
	private JMenuBar menuBar;
	private JMenu menu1;
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
		menu1 = new JMenu("Menu 1");
		
		}
		
	public void setUpGUI() {	
		
		frame.getContentPane();			
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(frameWidth, frameHeight));
		frame.setResizable(false);
		//frameInsets = frame.getInsets();
		
		frame.setBackground(Color.BLUE);
		
		for(int i=0; i<dataPoints.size(); i++) {
			menu1.add(dataPoints.get(i).getCheckBox());
			dataPoints.get(i).getCheckBox().setVisible(true);
		}
		
		menuBar.add(menu1);
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
				JCheckBox newCheckBox = new JCheckBox("checkbox " + (dataPoints.size() + 1));
				newCheckBox.setSelected(true);
				dataPoints.add(new DataPoints(line, newCheckBox));
				newCheckBox.addActionListener(checkBoxListener);
				
				line = reader.readLine();
			};
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

