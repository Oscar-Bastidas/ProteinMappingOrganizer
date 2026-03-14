package promaporg;

import java.sql.*;
import java.io.*;

// This custom class loads formatted mapping files into MariaDB tables and then does table joins on all loaded files

public class IterativeLoader {
	// Declaring instance fields
	// Total quantity of snapshots to process
	private int maxNumOfFiles;
	// Username
	private String user;
	// Password
	private String password;
	// Database name
	private String database;
	// Starting snapshot
	private int start;
	// Ending snapshot
	private int end;
	// Get working directory
	private String cwd = System.getProperty("user.dir");
	// Database URL location (local)
	private String dbURL;
	// Establish connection to the database
	private Connection conn = null;
	// Make a 'PreparedStatement' object
	private PreparedStatement statement = null;

	// Constructor to initiate instance field
	public IterativeLoader(String user, String password, String database, int start, int end) {
		this.user = user;
		this.password = password;
		this.database = database;
		this.start = start;
		this.end = end;
		this.dbURL = "jdbc:mariadb://127.0.0.1:3306/?user=" + user + "&password=" + password + "&allowMultiQueries=true&useSSL=false";
		this.maxNumOfFiles = end - start + 1;
	} // END constructor
	
	// Method to load files into MariaDB tables in a database
	public void loadFiles() throws SQLException {
		try {
			// Connect to the database
			conn = DriverManager.getConnection(dbURL);
			// Disable 'AutoCommit' in order to send batch or 'transaction' SQL commands
			conn.setAutoCommit(false);
			System.out.println("\n\n\nConnected.\n\n\n");

			// Create database
			String createDB = "CREATE DATABASE IF NOT EXISTS " +  database + ";USE " + database + ";";
			// Wrap the SQL string command into something SQLN can directly process
			statement = conn.prepareStatement(createDB);
			// Now submit the command to MariaDB
			statement.executeUpdate();
			// Now actually execute the submitted commands in MariaDB (like hitting the 'Enter' key to execute)
			conn.commit();

			// Iteratively build SQL code for each input file
			for (int i = start; i <= end; i++) {
				// "A" and "B" here are generic terms for the 2 chains that were mapped, the mapping data could be on any two chains
				String createTable = "create table map" + i + "(A_resname varchar(3), A_resnum varchar(6), A_resatom varchar(4), A_atomnum varchar(6), B_resname varchar(4), B_resnum varchar(6), B_resatom varchar(4), B_atomnum varchar(6), metric1 varchar(12), metric2 varchar(12), metric3 varchar(12));";
				
				String filePath = cwd + File.separator + i + "-finedata.csv";
				filePath = filePath.replace("\\", "/"); // Always use forward slashes for SQL: not having this line of code causes problems on Windows computers which use '\' file separators, but SQL installations still expect '/'
				String submitFile = "load data infile '" + filePath + "' into table map" + i + " fields terminated by ',' lines terminated by '\\n'";

				//ORIGINAL
				//String submitFile = "load data infile " + "'" + cwd + File.separator + i + "-finedata.csv' into table map" + i + " fields terminated by \",\" lines terminated by \"\\n\"";

				// Wrap the SQL string command into something SQL can directly process
                        	statement = conn.prepareStatement(createTable + submitFile);
                		// Now submit the command to MariaDB
                        	statement.executeUpdate();
                        	// Now actually execute the submitted commands in MariaDB (like hitting the 'Enter' key to execute)
                        	conn.commit();

				// Add a new column and make it the primary key
				String addIndex = "ALTER TABLE map" + i + " ADD COLUMN dataIndex INT AUTO_INCREMENT PRIMARY KEY FIRST;";
				// Wrap the SQL string command into something SQL can directly process
				statement = conn.prepareStatement(addIndex);
                                // Now submit the command to MariaDB
				statement.executeUpdate();
				// Now actually execute the submitted commands in MariaDB (like hitting the 'Enter' key to execute)
                                conn.commit();
			} // END 'for' loop

		} // END 'try' block
		catch (SQLException e) {
			System.out.println("\n\n\nNot connected.");
			e.printStackTrace();

			// If any of the SQL commands should fail for whatever reason, abort all
			// commands and rollback to DB's pr3vious state
			conn.rollback();
			e.printStackTrace();
		} // END 'catch' block
	} // END method 'loadFiles'
	
	// Method to build & submit table joins - tables must have first been - should previous connection've been closed?  could it have been kept open?
	public void tableJoins () throws SQLException { // 'throws SQLException' MUST explicitly be shown since code that opens DB connection that triggers
	// SQLException was technically not used in this method but in previous method instead, however this method must close the connection which is an
	// action that would throw a SQLException; the open connection remains valid across methods because the connection object is an instance field which
	// is visible/"global" across all methods in this class
			
		// FlexibleJoin myJoin = new FlexibleJoin(maxNumOfFiles); ORIGINAL
		FlexibleJoin myJoin = new FlexibleJoin(maxNumOfFiles, start, end);

		try {
			// !!! Opening a connection to the 'conn' object isn't necessary since connection was opened in another method & 'conn'
			// is an instance field !!!
			// !!! Doing 'conn.setAutoCommit(false);' is not required as its execution in previous method remains valid even here !!!

			// Wrap the SQL string command into something SQL can directly process
                	statement = conn.prepareStatement(myJoin.join());
                	// Now submit the command to MariaDB
                	statement.executeUpdate();
                	// Now actually execute the submitted commands in MariaDB (like hitting the 'Enter' key to execute)
                	conn.commit();
		} // END 'try' block
		catch (SQLException e) {
                        System.out.println("\n\n\nNot connected.");
                        e.printStackTrace();
                                                                                                                                                                                       // If any of the SQL commands should fail for whatever reason, abort all
                        // commands and rollback to DB's pr3vious state
                        conn.rollback();                                                                                                                                               e.printStackTrace();                                                                                                                                   } // END 'catch' block
		// 'finally' block is ALWAYS executed, so it serves as a cleanup code repository
		// for closing connections and what not whether an exception is triggered or not
		finally {
			statement.close();
			conn.setAutoCommit(true);
			conn.close();
		} // END 'finally' block

	} // END method 'tableJoins'
} // END 'IterativeLoader' class
