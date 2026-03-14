package promaporg;

import java.io.*;
import java.util.regex.Pattern;

// Custom class to convert the variable space delimiters to a single character (",") of OpenContact mapping results
public class IterativeFileProcessor {
	// Instance field of index for iteration
	private int fileIndex;

	// Constructor initializing instance field
	public IterativeFileProcessor(int fileIndex) {
		// "this" means the instance variable which is also named "fileIndex" (on the left hand side)) is the one that's supposed to assume
		// the value of "fileIndex" from the parameter (on the right hand side); w/o the "this" Java would think I'm redundantly assigning
		// the parameter's value back to the parameter, a useless operation that would leave the actual instance variable undeclared
		this.fileIndex = fileIndex;
	}

	// Custom method for the custom class to run the logic
	public void getCsv() {
		// Get working directory
		String cwd = System.getProperty("user.dir");

		String inputFile = String.valueOf(fileIndex) + "-finedata.txt";
		String outputFile = String.valueOf(fileIndex) + "-finedata.csv";
		String delimiter = ",";

		try {
			String line;

			FileReader fileReader = new FileReader(cwd + File.separator + inputFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			PrintWriter writer = new PrintWriter(cwd + File.separator + outputFile, "UTF-8");
			
			while ((line = bufferedReader.readLine()) != null) {
				// Acquire data from OpenContact output file according to columns
				String resName_A = line.substring(0,3) + ",";
				String resNum_A = line.substring(3,9) + ",";
				String atomName_A = line.substring(9,13) + ",";
				String atomNum_A = line.substring(13,19) + ",";
				// Skipping column number 19 since it's guaranteed to be blank
				// String resName_B = line.substring(20,23) + ","; ORIGINAL
				String resName_B = line.substring(19,23) + ",";
				String resNum_B = line.substring(23,29) + ",";
				String atomName_B = line.substring(29,33) + ",";
				String atomNum_B = line.substring(33, 39) + ",";
				String metric1 = line.substring(39,51) + ",";
				String metric2 = line.substring(51,63) + ",";
				String metric3 = line.substring(63,75);

				// Concatenate above substrings to print to new csv file
				writer.println(resName_A + resNum_A + atomName_A + atomNum_A + resName_B + resNum_B +
				atomName_B + atomNum_B + metric1 + metric2 + metric3);
			} // END 'while'

			// Close stuff that beeds to be closed
			fileReader.close();
			bufferedReader.close();
			writer.close();
	
		} // END 'try' 
		catch (IOException e) {
			e.printStackTrace();
		} // END 'catch'
	} // END custom 'getCsv' method
} // END class
