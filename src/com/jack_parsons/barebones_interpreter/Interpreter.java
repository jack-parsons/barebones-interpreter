package com.jack_parsons.barebones_interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Interpreter {
	private BufferedReader barebonesBufferedReader;
	private ArrayList<String[]> barebonesCode;
	private int currentLine;
	private HashMap<String, Integer> variableNamespace;
	private HashMap<Integer, Integer> whileJumps;
	private Stack<Integer> whileStack = new Stack<Integer>();
	private long timeTaken;
	
	Interpreter (BufferedReader barebonesBufferedReader) {
		// barebonesBufferedReader is the BufferedReader for the file containing the source code
		this.barebonesBufferedReader = barebonesBufferedReader;
		this.barebonesCode = new ArrayList<String[]>();
	}
	
	public void start () {
		// Starts execution of the barebones code
		long startTime = System.nanoTime();
		processFile();
		variableNamespace = new HashMap<String, Integer>();
		currentLine = 0;
		while (currentLine < barebonesCode.size()) {
			executeLine(barebonesCode.get(currentLine));
			currentLine ++;
		}
		// Print the execution time
		timeTaken = (System.nanoTime() - startTime)/1000000;
	}
	
	public void printTimeTaken(){
		System.out.println(String.format("\nExecution finished in %dms", timeTaken));
	}
	
	public void printMemory () {
		// Prints all the variables
		System.out.println("\nMemory:");
		for (String key : variableNamespace.keySet()){
			System.out.println(String.format("%s <- %d", key, variableNamespace.get(key)));
		}
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
				whileJumps.put(whilePositions.pop(), lineNumber);
			}
		}
	}
	
	private void processFile(){
		// Transfer all barebones code into string ArrayList for fast access 
		try {
			String line = barebonesBufferedReader.readLine();
			while (line != null) {
				for (String part : line.split(";")){
					// Do this so semicolon can allow multiple commands on one line
					part = part.trim(); // Trim whitespace such as tabs
					if (part.length() > 0){
						// Check that the line is not empty before adding it to processed code
						// Also split the line into parts
						// TODO add argument checking
						barebonesCode.add(splitLineIntoParts(part));
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
	
	private static String[] splitLineIntoParts(String line) {
		// Splits the line into parts separated by spaces
		return line.split(" ");
	}
	
	private void executeLine (String[] line) {
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
			System.out.println(String.format("Invalid operator"));
		}
	}
	
	private Object convertToActualType(String value) {
		try {
			// Try to convert to integer
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			// If not it is string
			return value;
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
