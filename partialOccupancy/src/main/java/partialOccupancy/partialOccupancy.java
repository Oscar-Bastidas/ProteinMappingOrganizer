package partialOccupancy;

import java.sql.*;

// This program finds interactions that are found in a certain fraction of snapshots in a pre-defined window.
// Variable named "end" indicates window size and variable named "windowFraction" indicates the fractional
// quantity of windows that the interactions must be found in.  Whether the interactions should be found in
// that specific quantity of windows, or "less than or equal to" that fractional quantity is indicated in the ancillary
// custom class, "IterativeUnion."

public class partialOccupancy {
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

                // Ending snapshot - this value indicates window size
                int end = Integer.parseInt(args[4]);

                // Total quantity of snapshots in whole collection
                int totalQuantity = Integer.parseInt(args[5]);

                // Results table index: there will be multiple results tables for each window analyzed
                // This specific parameter will be iteratively incremented by the app that summons the present PROMAPORG app
                // in order to make a new "results" table for each window analyzed
                int resultsTableIndex = Integer.parseInt(args[6]);

		// Quantity of windows that interactions must be found in which will dictate the occupancy fraction
		int windowFraction = Integer.parseInt(args[7]);

                // Incrementor variables for incrementing individual window start and end values in loop
                int startIncrementor;
                int endIncrementor;
                int currentResultsTableIndex;

		// Loop to iteratively analyze each window one at a time and stopping at the last snapshot in the whole data set
                for (int i = 0; i < totalQuantity - (end - start + 1) + 1; i++) {
                        // Incrementing starting snapshot, ending snapshot and results table index variables to advance the window being analyzed
                        startIncrementor = start + i;
                        endIncrementor = end + i;
                        currentResultsTableIndex = resultsTableIndex + i;

                        IterativeUnion iterativeUnion = new IterativeUnion(user, password, database, startIncrementor, endIncrementor, totalQuantity, currentResultsTableIndex, windowFraction);

                        // Join all of the tables to find common interactions across all snapshots
                        try {
                                iterativeUnion.submitUnion();
                        } // END 'try' block
                        catch (SQLException e) {
                                System.out.println("Not connected from 'main'");
                                e.printStackTrace();
                        } // END 'catch' block
                } // END 'for'
	} // END 'main'
} // END 'partialOccupancy' class
