package com.jack_parsons.barebones_interpreter;

/** A base class for interpreter listeners that allow events to travel out of the interpreter
 * 	Methods should be overridden if the listener is interested in a particular event
 * @author Jack Parsons
 *
 */
public class InterpreterListener {
	void outputEvent(String output){}
	void finishedEvent(){}
}
