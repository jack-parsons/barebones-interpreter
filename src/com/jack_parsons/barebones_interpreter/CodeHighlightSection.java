package com.jack_parsons.barebones_interpreter;

public class CodeHighlightSection {
	public enum sectionType {
		INSTR, NORM
	}
	private String text;
	private sectionType type;
	
	CodeHighlightSection (String text, sectionType type) {
		this.text = text;
		this.type = type;
	}
	
	public String getText(){
		return text;
	}
	
	public sectionType getType(){
		return type;
	}
}
