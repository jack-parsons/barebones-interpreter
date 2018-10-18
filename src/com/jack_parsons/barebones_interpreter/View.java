package com.jack_parsons.barebones_interpreter;

import java.awt.EventQueue;
import java.io.FileWriter;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JSplitPane;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;
import javax.swing.JSeparator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class View {

	private JFrame frmBarebonesInterpreterIde;
	private final JFileChooser fileExplorerWindow = new JFileChooser();
	private JTextArea consolePane;
	private JTextPane codePane;
	private File currentFile = null;
	private final String windowTitle = "BareBones Interpreter";
	private boolean codeChanged = true;
	private InterpreterController interpeterController;
	private StyledDocument codePaneDocument;
	private final JPanel panel = new JPanel();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View window = new View();
					window.frmBarebonesInterpreterIde.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public View() {
		initialize();
	}
	
	private void initialize() {
		// Initialize the contents of the frame.
		frmBarebonesInterpreterIde = new JFrame();
		frmBarebonesInterpreterIde.setTitle(windowTitle);
		frmBarebonesInterpreterIde.setBounds(100, 100, 900, 600);
		frmBarebonesInterpreterIde.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		interpeterController = new InterpreterController();

		JMenuBar menuBar = new JMenuBar();
		frmBarebonesInterpreterIde.setJMenuBar(menuBar);
		
		// Create the file button
		JMenu fileButton = new JMenu("File");
		menuBar.add(fileButton);
		
		// Create the open file button
		JButton openFileButton = new JButton("Open File");
		openFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fileExplorerWindow.setCurrentDirectory(new File(System.getProperty("user.dir")+"/barebones code/"));
				int returnVal = fileExplorerWindow.showOpenDialog(frmBarebonesInterpreterIde);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fileExplorerWindow.getSelectedFile();
		            updateEditorManager(file);
		            currentFile = file;
		    		frmBarebonesInterpreterIde.setTitle(file.toString() + " - " + windowTitle);
				}
			}
		});
		openFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		fileButton.add(openFileButton);
		
		// Create the save as button
		JButton saveAsButton = new JButton("Save As");
		saveAsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveAs();
			}
		});
		saveAsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		fileButton.add(saveAsButton);
		
		// Create the save button
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (currentFile == null){
					saveAs();
				} else {
					saveFile();
				}
			}
		});
		fileButton.add(saveButton);
		
		// Create the run button
		JButton runButton = new JButton("Run");
		runButton.setToolTipText("");
		runButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startInterpreting();
			}
		});
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		// Create the menu bar and add the elements to it
		JSeparator separator = new JSeparator();
		menuBar.add(separator);
		menuBar.add(runButton);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setBackground(Color.LIGHT_GRAY);
		splitPane.setResizeWeight(0.9);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmBarebonesInterpreterIde.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		// Create the code editor
		codePane = new JTextPane();
		codePane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				codeChanged = true;  // Now prompt will appear asking to save
				
				refreshDocument();
			}
		});
        codePaneDocument = codePane.getStyledDocument();
        // Set up different styling for code
        Style instructionStyle = codePane.addStyle("instruction style", null);
        StyleConstants.setForeground(instructionStyle, Color.BLUE);
        
        // Create the console
		JScrollPane scrollPlane2 = new JScrollPane(codePane);
		splitPane.setLeftComponent(scrollPlane2);
		consolePane = new JTextArea();
		consolePane.setEditable(false);
		consolePane.setText("waiting...");
		JScrollPane scrollPlane1 = new JScrollPane(consolePane);
		splitPane.setRightComponent(scrollPlane1);
	}
	
	private void refreshDocument() {
		// Refresh all the colouring of the code
		try {
			String docText = codePaneDocument.getText(0, codePaneDocument.getLength());
			ArrayList<CodeHighlightSection> sections = Interpreter.sytaxHighlighingProcessing(docText);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private void updateEditorManager(File file) {
		// Update the code pane with the contents of the current file
		try {
			BufferedReader bareboneBufferedReader = new BufferedReader(new FileReader(file));
			String line;
			StringBuilder fileText = new StringBuilder();
			do {
				line = bareboneBufferedReader.readLine();
				if (line != null){
					fileText.append(line+"\n");
				}
			} while (line != null);
			codePane.setText(fileText.toString());
			bareboneBufferedReader.close();
		} catch (IOException e) {
			System.out.println("Error reading file");
		}
	}
	
	private void startInterpreting(){
		// Starts interpreting the code when the run button is clicked
		try {
			boolean saved;
			if (codeChanged) {
				// Create dialogue window to save file
				Object[] options = {"Save", "Cancel"};
				int optionSelected = JOptionPane.showOptionDialog(frmBarebonesInterpreterIde, "File needs to be saved before execution",
				    "Save file", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (optionSelected == 0) {
					// If save file option chosen
					saveFile();
					saved = true;
				} else {
					saved = false;
				}
			} else {
				saved = true;
			}
			
			if (currentFile != null && saved) {  // Needs to be saved first to run
				Interpreter interpreter = new Interpreter(new BufferedReader(new FileReader(currentFile)));
				// The listener is listening for output from the interpreter
				interpreter.addListener(new InterpreterListener(){
					@Override
					void outputEvent(String output) {
						printToConsole(output);
					}
					@Override
					void finishedEvent() {
						// When the code has finished, print the memory and the time taken
						printToConsole(interpreter.printMemory());
						printToConsole(interpreter.printTimeTaken());
					}
				});
				consolePane.setText("");  // Reset text
				interpeterController.setInterpreter(interpreter);
				interpeterController.start();
			}
		} catch (IOException e) {
			consolePane.setText("\nError starting interpreter");
			System.out.println("Note");
		}
	}
	
	private void printToConsole(String output) {
		// Prints a string to the console
		consolePane.append(output);
	}
	
	private void saveAs () {
		// Save the code to where the use selects using the 
		fileExplorerWindow.setCurrentDirectory(new File(System.getProperty("user.dir")+"/barebones code/"));
		// Bring up the file explorer dialogue
		int returnVal = fileExplorerWindow.showSaveDialog(frmBarebonesInterpreterIde);
		if (returnVal == JFileChooser.APPROVE_OPTION) { // Only save if user selected approve button
            File file = fileExplorerWindow.getSelectedFile();
            currentFile = file;
            // Update the window title to show the file name currently being looked at
    		frmBarebonesInterpreterIde.setTitle(file.toString() + " - " + windowTitle);
            saveFile();
		}
	}
	
	private void saveFile() {
		// Save the code in the editor to the current file
		try {
			if (currentFile != null){
				FileWriter writer = new FileWriter(currentFile);
				writer.write(codePane.getText());
				writer.close();
				codeChanged = false; // Reset so prompt asking to save does not appear
			} else {
				// If not saved yet, then bring up save as dialogue
				saveAs();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
