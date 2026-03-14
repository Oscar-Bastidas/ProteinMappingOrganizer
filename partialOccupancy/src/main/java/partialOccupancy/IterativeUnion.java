package partialOccupancy;

import java.sql.*;
import java.io.*;

// This custom class does table unions on specified tables

public class IterativeUnion {
        // Declaring instance fields
        // Total quantity of snapshots to process
        private int windowSize;
        // Username
        private String user;
        // Password
        private String password;
        // Database name
        private String database;
        // Starting snapshot
        private int startIncrementor;
        // Ending snapshot
	private int endIncrementor;
	// Results table index
        private int currentResultsTableIndex;
        // Total quantity of snapshots in the entire dataset (NOT simply window size snapshots)
        private int totalQuantity;
	// Quantity of windows that interactions must be found in for fractional occupancy
	private int windowFraction;
        // Get working directory
	private String cwd = System.getProperty("user.dir");
	// Database URL location (local)
        private String dbURL;
	// Establish connection to the database
	private Connection conn = null;
	// Make a 'PreparedStatement' object
        private Statement statement = null;

	// Constructor to initiate instance field
        public IterativeUnion (String user, String password, String database, int startIncrementor, int endIncrementor, int totalQuantity, int currentResultsTableIndex, int windowFraction) {
                this.user = user;
                this.password = password;
                this.database = database;
                this.startIncrementor = startIncrementor;
                this.endIncrementor = endIncrementor;
                this.currentResultsTableIndex = currentResultsTableIndex;
		this.windowFraction = windowFraction;
                this.dbURL = "jdbc:mariadb://127.0.0.1:3306/?user=" + user + "&password=" + password + "&allowMultiQueries=true&useSSL=false";
                this.windowSize = endIncrementor - startIncrementor + 1;
        } // END constructor

	public void submitUnion () throws SQLException {
		try {
			// Constructed 'UNION' command
			String unionQuery = buildUnion(startIncrementor, endIncrementor, totalQuantity, currentResultsTableIndex, windowFraction);

			// Connect to the database
                        conn = DriverManager.getConnection(dbURL);

                        // Wrap the SQL string command into something SQL can directly process
                        statement = conn.createStatement();

                        // Disable 'AutoCommit' in order to send batch or 'transaction' SQL commands
                        conn.setAutoCommit(false);

                        // Now submit the command to MariaDB
                        statement.execute("USE " + database + ";" + "CREATE TABLE GE_L_b_vs_c_partialOccupancy_50window_50percentOccupancy" + currentResultsTableIndex + " AS " +  unionQuery);
                        // Now actually execute the submitted commands in MariaDB (like hitting the 'Enter' key to execute)
                        conn.commit();
		} // END 'try'
		catch (SQLException e) {
                        System.out.println("\n\n\nNot connected.");
                        e.printStackTrace();

                        // If any of the SQL commands should fail for whatever reason, abort all
                        // commands and rollback to DB's pr3vious state
                        conn.rollback();
			e.printStackTrace();
		} // END 'catch' block
                // 'finally' block is ALWAYS executed, so it serves as a cleanup code repository
                // for closing connections and what not whether an exception is triggered or not
                finally {
                        statement.close();
                        conn.setAutoCommit(true);
                        conn.close();
                } // END 'finally' block
	} // END 'submitUnion' method

// ********************************************************************************************************************************************
// STATIC METHOD SECTION

	public static String buildUnion(int startIncrementor, int endIncrementor, int totalQuantity, int currentResultsTableIndex, int windowFraction) {
		StringBuilder command = new StringBuilder();
		int i = 0;
		command.append("SELECT A_resname, A_resnum, A_resatom, A_atomnum,B_resname, B_resnum, B_resatom, B_atomnum FROM (");

		for (i = startIncrementor; i < endIncrementor; i++) {
			command.append("SELECT A_resname, A_resnum, A_resatom, A_atomnum, B_resname, B_resnum, B_resatom, B_atomnum FROM map");
			command.append(i);
			command.append(" UNION ALL ");
		} // END 'for'

		command.append("SELECT A_resname, A_resnum, A_resatom, A_atomnum, B_resname, B_resnum, B_resatom, B_atomnum FROM map");
		command.append(i);
		command.append(") AS window_union GROUP BY A_resname, A_resnum, A_resatom, A_atomnum, B_resname, B_resnum, B_resatom, B_atomnum ");
		command.append("HAVING COUNT(*) >= ");
		command.append(windowFraction);
		command.append(" AND COUNT(*) < 50"); // Optional operator; the number after the inequality must be the total quantity of snapahots that
		//make up a complete window - either 20 or 50 - and this optional operator is done for compound inequality analyses
		command.append(";");

		String commandAsString = command.toString();
		return commandAsString;
	} // END 'buildUnion' static method
	
} // END 'IterativeLoader' class
