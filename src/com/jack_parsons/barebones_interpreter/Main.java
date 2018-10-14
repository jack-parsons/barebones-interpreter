package com.jack_parsons.barebones_interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {
	
	static BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));  // Create buffer reader to read user input

	public static void main(String[] args) {
		
		try {
			System.out.println("Working Directory = " + System.getProperty("user.dir"));
			System.out.print("Enter file name of barebones code: ");
			String fileName = inputReader.readLine();
			BufferedReader bareboneBufferedReader = new BufferedReader(new FileReader(fileName));
			Interpreter interpreter = new Interpreter(bareboneBufferedReader);
			interpreter.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
