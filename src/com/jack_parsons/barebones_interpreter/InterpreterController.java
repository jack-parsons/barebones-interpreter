package com.jack_parsons.barebones_interpreter;

public class InterpreterController extends Thread {
	   private Thread thread;
	   private Interpreter interpreter;
	   
	   public void setInterpreter(Interpreter interpreter){
		   this.interpreter = interpreter;
	   }
	   
	   public void run() {
		   interpreter.start();
	   }
	   
	   public void start() {
		   thread = new Thread (this, "Interpreter Thread");
	       thread.start ();
	   }
}
