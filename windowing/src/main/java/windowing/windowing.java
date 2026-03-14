package windowing;

import java.sql.*;

// This program serves as a wrapper for the actual PROMAPORG workhorse (modified to make it iterable of course) to find commons accross successive windows
// iteratively incremented in this wrapper and submitted as a system command to call each window's PROMAPORG analysis

// This is the class containing the main method and here, the user must supply the program 5 things as command line parameters: 1) username, 2) password,
// 3) database name, 4) starting snapshot number, 5) ending snapshot number, 6) total quantity of snapshots in the complete da set and 7) starting
// window index, IN THIS ORDER; these credentials are for the user's local SQL installation

// This specific program assumes that there is an ALREADY existing database with the mapping snapshot data already loaded as tables (i.e. "map1," "map2," 
// "map3," etc...)

public class windowing {
	public static void main( String[] args ) {
		// Getting input information from user
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

		// Total quantity of snapshots in whole collection
		int totalQuantity = Integer.parseInt(args[5]);

		// Results table index: there will be multiple results tables for each window analyzed
                // This specific parameter will be iteratively incremented by the app that summons the present PROMAPORG app
                // in order to make a new "results" table for each window analyzed
                int resultsTableIndex = Integer.parseInt(args[6]);

		// Incrementor variables for incrementing individual window start and end values in loop
		int startIncrementor;
		int endIncrementor;
		int currentResultsTableIndex;

		// Loop to iteratively analyze each window one at a time and stopping at the last snapshot in the whole data set
		for (int i = start; i <= totalQuantity - (end - start + 1) + 1; i++) {
			// Incrementing starting snapshot, ending snapshot and results table index variables to advance the window being analyzed
			startIncrementor = start + i - start; // USED TO BE (works): startIncrementor = start + i;
			endIncrementor = end + i - start; // USED TO BE (works): endIncrementor = end + i;
			currentResultsTableIndex = resultsTableIndex + i - start; // USED TO BE (works): currentResultsTableIndex = resultsTableIndex + i;

			IterativeLoader iterativeLoader = new IterativeLoader(user, password, database, startIncrementor, endIncrementor, totalQuantity, currentResultsTableIndex);

			// Join all of the tables to find common interactions across all snapshots
			try {
				iterativeLoader.tableJoins();
			} // END 'try' block
			catch (SQLException e) {
				System.out.println("Not connected from 'main'");
				e.printStackTrace();
			} // END 'catch' block
		} // END 'for'
	} // END 'main'
} // END 'windowing' class
