package com.jack_parsons.barebones_interpreter;

/** A base class for interpreter listeners that allow events to travel out of the interpreter
 * 	Methods should be overridden if the listener is interested in a particular event
 * @author Jack Parsons
 *
 */
public class InterpreterListener {
	void outputEvent(String output){} // Called when there is some output from the interpreter
	void finishedEvent(){} // Called when the interpreter has finished
}
