package frc.CheeseLog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class BufferedFileWriter {

	static String folderPath;
	static String delimiter;
	private String table;
	private boolean loggingStarted;
	private int tempFileIndex; // used to indicate how many temporary files we have created
	private long fileCounter;
	public BufferedWriter primaryFile;
	private HashMap<String, Loggable> loggables;
	private HashMap<Loggable, BufferedWriter> extraFiles;

	/**
	 * 
	 * @param table The name of the table this file will represent
	 * @throws Exception if the fileString is invalid
	 */
	public BufferedFileWriter(String table) throws Exception {
		this.table = table;
		tempFileIndex = 0;
		loggingStarted = false;
		primaryFile = new BufferedWriter(new FileWriter(new File(folderPath + table + ".log")));
		loggables = new HashMap<String, Loggable>();
		extraFiles = new HashMap<Loggable, BufferedWriter>();
	}

	/**
	 * Adds a new column if logging has not started, or adds an additional file otherwise
	 * 
	 * @param name     The name of the loggable
	 * @param loggable The object to be logged
	 * @throws Exception If there is an error creating the temporary file
	 */
	public void add(String name, Loggable loggable) throws Exception {
		System.out.println(loggingStarted + " " + name);
		if (loggingStarted && !loggables.containsKey(name)) {
			String tempFilepath = folderPath + table + tempFileIndex + ".log";
			extraFiles.put(loggable, new BufferedWriter(new FileWriter(new File(tempFilepath))));
			extraFiles.get(loggable).write("Iteration"+ delimiter+name + delimiter+"\n");
			tempFileIndex++;
		} else if (loggable.logToFile) {
			loggables.put(name, loggable);
		}
	}

	/**
	 * Logs all relevant values
	 * 
	 * @param counter The current iteration (used as an index)
	 * @throws IOException If there is an extraneous error with one of the files
	 */
	public void log(long counter) throws IOException {
		this.fileCounter = counter;
		if (!loggingStarted) {
			loggingStarted = true;
			printHeaders();
		}
		primaryFile.write(fileCounter + delimiter);
		for (String name : loggables.keySet()) {
			primaryFile.write((loggables.get(name).fileCheck() ? loggables.get(name).getLogValue() : "") + delimiter);
		}
		for (Loggable l : extraFiles.keySet()) {
			extraFiles.get(l).write(fileCounter + delimiter);
			extraFiles.get(l).write((l.fileCheck() ? l.getLogValue() : "") + delimiter + "\n");

		}
		primaryFile.write("\n");
	}

	/**
	 * Prints the headers of the file
	 */
	private void printHeaders() throws IOException {
		primaryFile.write("Iteration" + delimiter);
		for (String name : loggables.keySet()) {
			primaryFile.write(name + delimiter);
		}
		primaryFile.write("\n");
	}

	/**
	 * Closes the writer, cleaning up any additional files
	 * 
	 * @throws IOException If, for example, you lose file ownership as the program
	 *                     is running.
	 */
	public void close() throws IOException {
		for (Loggable l : extraFiles.keySet()) {
			extraFiles.get(l).close();
		}
		primaryFile.close();

	}
}
