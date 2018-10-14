package com.jack_parsons.barebones_interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Interpreter {
	private BufferedReader barebonesBufferedReader;
	private ArrayList<String[]> barebonesCode;
	private int currentLine;
	private HashMap<String, Integer> variableNamespace;
	private HashMap<Integer, Integer> whileJumps;
	private Stack<Integer> whileStack = new Stack<Integer>();
	private float timeTaken;
	
	Interpreter (BufferedReader barebonesBufferedReader) {
		// barebonesBufferedReader is the BufferedReader for the file containing the source code
		this.barebonesBufferedReader = barebonesBufferedReader;
		this.barebonesCode = new ArrayList<String[]>();
		timeTaken = -1; // Initialise timeTaken so if printTimeTaken is called before start it doesn't cause an exception
	}
	
	public void start () {
		// Starts execution of the barebones code
		long startTime = System.nanoTime();
		processFile();
		variableNamespace = new HashMap<String, Integer>();
		currentLine = 0;
		while (currentLine < barebonesCode.size()) {
			executeLine(barebonesCode.get(currentLine), currentLine);
			currentLine ++;
		}
		// Calculate the time taken to execute program
		timeTaken = (float)(System.nanoTime() - startTime)/1000000;
	}
	
	public void printTimeTaken(){
		// Print the time taken for the last program to execute
		System.out.println(String.format("\nExecution finished in %fms", timeTaken));
	}
	
	public void printMemory () {
		// Prints all the variables
		System.out.println("\nMemory:");
		for (String key : variableNamespace.keySet()){
			System.out.println(String.format("%s <- %d", key, variableNamespace.get(key)));
		}
	}
	
	private void errorMessage(String message, int lineNumber){
		/* Prints an error message.
		 * If lineNumber is -1, the line number does not print.
		 * Terminates the program after printing message. */
		if (lineNumber != -1){
			System.out.println("\nError on line:" + (lineNumber + 1));
		}
		System.out.println(message);
		System.exit(1);
	}
	
	private void processWhileJumpPoints() {
		// 'While jump points' are lines containing the start of a while loop which then connects with an end statement
		whileJumps = new HashMap<Integer, Integer>();
		Stack<Integer> whilePositions = new Stack<Integer>();
		for (int lineNumber = 0; lineNumber < barebonesCode.size(); lineNumber++) {
			String[] line = barebonesCode.get(lineNumber);
			if (line[0].equals("while")){
				// Put this while loop line number at the top of the stack
				whilePositions.add(lineNumber);
			} else if (line[0].equals("end")){
				// Add a new jump to the while jumps HashMap and remove the top while loop from the stack
				try {
					whileJumps.put(whilePositions.pop(), lineNumber);
				} catch (EmptyStackException e) {
					errorMessage("while-end mismatch", lineNumber);
				}
			}
		}
		if (whilePositions.size() != 0){
			errorMessage("while-end mismatch", -1);
		}
	}
	
	private void processFile(){
		// Transfer all barebones code into string ArrayList for fast access 
		try {
			String line = barebonesBufferedReader.readLine();
			int lineNumber = 0;
			while (line != null) {
				for (String part : line.split(";")){
					// Do this so semicolon can allow multiple commands on one line
					part = part.trim(); // Trim whitespace such as tabs
					if (part.length() > 0){
						// Check that the line is not empty before adding it to processed code
						// Also split the line into parts
						// TODO add argument checking
						String[] lineParts = splitLineIntoParts(part);
						if (correctArguments(lineParts)){
							barebonesCode.add(lineParts);
							lineNumber ++;
						} else {
							errorMessage("Incorrect arguments", lineNumber);
						}
					}	
				}
				
				line = barebonesBufferedReader.readLine();
			}
			processWhileJumpPoints();
		} catch (IOException e) {
			System.out.println("Error reading text file");
			e.printStackTrace();
		}
	}
	
	private static boolean correctArguments(String[] line) {
		// Check that the arguments given are valid
		boolean valid = false;
		switch (line[0]) {
		case "clear":
		case "incr":
		case "decr":
			valid = (line.length == 2);
			break;
		case "while":
			valid = (line.length == 5);
			break;
		case "end":
			valid = (line.length == 1);
			break;
		}
		return valid;
	}
	
	private static String[] splitLineIntoParts(String line) {
		// Splits the line into parts separated by spaces
		return line.split(" ");
	}
	
	private void executeLine (String[] line, int lineNumber) {
		// Decode the instruction then execute it with any operands
		
		// Find the operation
		switch (line[0]) {
		case "clear":
			clearVar(line[1]);
			break;
		case "incr":
			incrementVar(line[1]);
			break;
		case "decr":
			decrementVar(line[1]);
			break;
		case "while":
			startWhile(line[1], line[2], line[3]);
			break;
		case "end":
			endStatement();
			break;
		default:
			errorMessage(String.format("Invalid operator"), lineNumber);
		}
	}
	
	private Object convertToActualType(String value) {
		try {
			// Try to convert to integer
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			errorMessage("Invalid operand: "+value, -1);
			return null;
		}
	}
	
	private void endStatement(){
		// Return to the line before the last while loop
		currentLine = whileStack.pop()-1;
	}
	
	private void startWhile(String varName, String Operator, String conditionValue) {
		// Start of a while loop
		boolean conditionMet;
		
		if (!variableNamespace.containsKey(varName)){
			// If the variable is not initialised yet, set it to zero
			clearVar(varName);
		}
		
		switch (Operator){
		case "not":
			conditionMet = !variableNamespace.get(varName).equals(convertToActualType(conditionValue));
			break;
		default:
			conditionMet = false;
			System.out.print("Operator not found:" + Operator);
		}
		
		if (conditionMet) {
			whileStack.add(currentLine);
		} else {
			currentLine = whileJumps.get(currentLine);
		}
	}

	private void clearVar(String varName) {
		// Sets the variable to 0
		variableNamespace.put(varName, 0);
	}
	
	private void incrementVar(String varName) {
		// Increment the value of the variable at name
		if (variableNamespace.containsKey(varName)) {
			variableNamespace.put(varName, variableNamespace.get(varName) + 1);
		} else {
			variableNamespace.put(varName, 1); // If undeclared then set to 1
		}
	}
	
	private void decrementVar(String varName) {
		// Decrement the value of the variable at name
		if (variableNamespace.containsKey(varName) && variableNamespace.get(varName) > 0) {
			variableNamespace.put(varName, variableNamespace.get(varName) - 1);
		} else {
			variableNamespace.put(varName, 0); // If undeclared then set to 0 ***
		}
	}
}
