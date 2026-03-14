package promaporg;

import java.sql.*;

// This is the class containing the main method and here, the user must supply the program 5 things as command line parameters: 1) username, 2) password,
// 3) database name, 4) starting snapshot number, 5) ending snapshot number, IN THIS ORDER; these credentials are for the user's local SQL installation

public class promap {
	public static void main (String[] args) {
		// Get inputs from user
		// Username
		String user = args[0];

		// Password
		String password = args[1];

		// Database name
		String database = args[2];

		// Starting snapshot
		int start = Integer.parseInt(args[3]);

		// Ending snapshot
		int end = Integer.parseInt(args[4]);

		//int maxNumOfFiles = Integer.parseInt(args[0]);

		// Iteratively convert multispaced delimiter to csv
		for (int i = start; i <= end; i++) { 
			IterativeFileProcessor convert = new IterativeFileProcessor(i);
			convert.getCsv();
		} // END 'for'
		
		// Now load each and every csv file to MariaDB tables
		IterativeLoader iterativeLoader = new IterativeLoader(user, password, database, start, end);
		try { // Must use 'try' since I'm using method that can throw SQLException
			iterativeLoader.loadFiles();
		} // END 'try' block
		catch (SQLException ex) {
			System.out.println("Not connected from 'main'");
			ex.printStackTrace();
		} // END 'catch' block

		// Join all of the tables to find common interactions across all snapshots
		try {
			iterativeLoader.tableJoins();
		} // END 'try' block
		catch (SQLException e) {
			System.out.println("Not connected from 'main'");
			e.printStackTrace();
		} // END 'catch' block
		
		// Average "metric" values from surviving interactions across all snapshots
		try {
			MetricAverages metricAverages = new MetricAverages(user, password, start, end, database);
			metricAverages.average();
		} // END 'try'
		catch (Exception e) {
			System.out.println("Not connected from 'main'");
                        e.printStackTrace();
		} // END 'catch' block

		// Standard deviation of "metric" values from surviving interactions across all snapshots
                try { 
			MetricStdDevs metricStdDevs = new MetricStdDevs(user, password, start, end, database);
			metricStdDevs.calculateStdDevs();
                } // END 'try'
                catch (Exception e) {
                        System.out.println("Not connected from 'main'");
                        e.printStackTrace();
                } // END 'catch' block
	} // END 'main'
} // END 'promap' class 
