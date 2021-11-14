// Created by, Ian Washburn: iwashburn@student.bridgew.edu.

import java.io.IOException;
import java.util.Scanner;

public class GUITester {

	public static void main(String args[]) throws IOException {

		String dataFile = null;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter data file name");
		try{
			String dataFileInput = scanner.nextLine();
			dataFile = ("/projectnb/ct-lensing/kerr_ray_s1/Results/" + dataFileInput + ".txt");
		}
		catch(Exception e){
			System.out.println("Error: Invalid file name");
		}

		scanner.close();

		System.out.println(dataFile);

		GUI gui = new GUI(dataFile);

		gui.dataOrganization();
		gui.setUpGUI();



	}
}