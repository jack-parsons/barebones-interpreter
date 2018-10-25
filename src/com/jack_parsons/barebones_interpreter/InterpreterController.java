package com.jack_parsons.barebones_interpreter;

// Manages the creation of a new thread to run the interpreter in
public class InterpreterController extends Thread {
	   private Thread thread;
	   private Interpreter interpreter;
	   private boolean running;
	   private boolean stepping;
	   
	   InterpreterController () {
		   stepping = false;
	   }
	   
	   public void setInterpreter(Interpreter interpreter){
		   this.interpreter = interpreter;
	   }
	   
	   public void run() {
		   // This is run in a different thread so that it doesn't cause a hang
		   setRunning(true);
		   interpreter.addListener(new InterpreterListener() {
			   @Override
			   public void finishedEvent () {
				   setRunning(false);
			   }
		   });
		   interpreter.start(stepping);
	   }
	   
	   private void setRunning(boolean value) {
		   running = value;
	   }
	   
	   public boolean isRunning() {
		   return running;
	   }
	   
	   public void stopRunning() {
		   setRunning(false);
		   interpreter.stop();
	   }
	   
	   public void step() {
		   interpreter.step();
	   }
	   
	   public void setStepping(boolean stepping) {
		   this.stepping = stepping;
	   }
	   
	   public int getCurrentLine() {
		   return interpreter.getCurrentLine();
	   }
	   
	   public int getLastLine() {
		   return interpreter.getLastLine();
	   }
	   
	   public void start() {
		   // Called when starting the interpreter
		   this.stepping = stepping;
		   thread = new Thread (this, "Interpreter Thread");
	       thread.start ();
	   }
}
