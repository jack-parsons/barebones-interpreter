package com.jack_parsons.barebones_interpreter;

// Manages the creation of a new thread to run the interpreter in
public class InterpreterController extends Thread {
	   private Thread thread;
	   private Interpreter interpreter;
	   
	   public void setInterpreter(Interpreter interpreter){
		   this.interpreter = interpreter;
	   }
	   
	   public void run() {
		   // This is run in a different thread so that it doesn't cause a hang
		   interpreter.start();
	   }
	   
	   public void start() {
		   // Called when starting the interpreter
		   thread = new Thread (this, "Interpreter Thread");
	       thread.start ();
	   }
}
