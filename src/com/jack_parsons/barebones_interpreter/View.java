package com.jack_parsons.barebones_interpreter;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.FileWriter;
import javax.swing.JFrame;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.JSplitPane;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
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
import java.awt.Event;

import javax.swing.JSeparator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;

public class View {

	private JFrame frmBarebonesInterpreterIde;
	private final JFileChooser fileExplorerWindow = new JFileChooser();
	private JTextArea consolePane;
	private JTextPane codePane;
	private File currentFile = null;
	private final String windowTitle = "BareBones Interpreter";
	private boolean codeChanged = true;
	private InterpreterController interpreterController;
	private StyledDocument codePaneDocument;
	private final JPanel panel = new JPanel();
	private JButton runButton;
	private JButton stepButton;
	
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
		
		interpreterController = new InterpreterController();

		JMenuBar menuBar = new JMenuBar();
		frmBarebonesInterpreterIde.setJMenuBar(menuBar);
		
		// Create the file button
		JMenu fileButton = new JMenu("File");
		menuBar.add(fileButton);
		
		// Create the open file button
		JButton openFileButton = new JButton("Open File");
		openFileButton.setToolTipText("Shortcut ⌘o");
		openFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openFile();
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
		saveButton.setToolTipText("Shortcut ⌘s");
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
		runButton = new JButton("Run");
		runButton.setToolTipText("Shortcut ⌘r");
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
		
		// Create step button
		stepButton = new JButton("Step");
		stepButton.setToolTipText("Shortcut: ⌘→");
		stepButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!interpreterController.isRunning()){
					startStepping();
				} else {
					interpreterController.step();
				}
			}
		});
		stepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		// Create stop button
		JButton stopButton = new JButton("Stop");
		stopButton.setToolTipText("Shortcut ⌘s");
		stopButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Stop the execution
				interpreterController.stopRunning();
			}
		});
		menuBar.add(stopButton);
		menuBar.add(stepButton);
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
        
        addKeyboardShortcuts();
        
        // Create the console
		JScrollPane scrollPlane2 = new JScrollPane(codePane);
		splitPane.setLeftComponent(scrollPlane2);
		consolePane = new JTextArea();
		consolePane.setEditable(false);
		consolePane.setText("waiting...");
		JScrollPane scrollPlane1 = new JScrollPane(consolePane);
		splitPane.setRightComponent(scrollPlane1);
	}
	
	private void openFile() {
		// Opens the current file and puts it onto the code panel
		fileExplorerWindow.setCurrentDirectory(new File(System.getProperty("user.dir")+"/barebones code/"));
		int returnVal = fileExplorerWindow.showOpenDialog(frmBarebonesInterpreterIde);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileExplorerWindow.getSelectedFile();
            updateEditorManager(file);
            currentFile = file;
    		frmBarebonesInterpreterIde.setTitle(file.toString() + " - " + windowTitle);
		}
	}
	
	private void addKeyboardShortcuts() {
        // Add keyboard shortcuts
		
		// Save shortcut
        InputMap inputMap = codePane.getInputMap();
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_S,
        		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(key, new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		saveFile();
        	}
        });

		// Run shortcut
        inputMap = codePane.getInputMap();
        key = KeyStroke.getKeyStroke(KeyEvent.VK_R,
        		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(key, new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		startInterpreting();
        	}
        });

		// Open shortcut
        inputMap = codePane.getInputMap();
        key = KeyStroke.getKeyStroke(KeyEvent.VK_O,
        		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(key, new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		openFile();
        	}
        });

		// Stop shortcut
        inputMap = codePane.getInputMap();
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S,
        		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(key, new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		interpreterController.stopRunning();
        	}
        });
		// Step shortcut
        inputMap = codePane.getInputMap();
        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
        		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(key, new AbstractAction() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (!interpreterController.isRunning()){
        			startStepping();
        		} else {
        			interpreterController.step();
        		}
        	}
        });
	}
	
	private ArrayList<Integer> getlineNumberPositions(String text) {
		// Find the positions of all the line 
		ArrayList<Integer> lineNumberPositions = new ArrayList<Integer>();
		lineNumberPositions.add(0); // Line 0 at beginning
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\n') {
				if (length > 0){
					lineNumberPositions.add(i);
				}
			} else if (text.charAt(i) != ' ' && text.charAt(i) != '\t' ) {
				length ++; // Check that line has text on it
			}
		}
		return lineNumberPositions;
	}
	
	private void updateCurrentLine(int currentLine, int oldLine) {
		// Change swap the currently running line highlighting
		try {
			String text = codePaneDocument.getText(0, codePaneDocument.getLength());
			ArrayList<Integer> lineNumberPositions = getlineNumberPositions(text);
			
			// Remove the old background
			String temp1 = text.substring(lineNumberPositions.get(oldLine), lineNumberPositions.get(oldLine+1));
			codePaneDocument.remove(lineNumberPositions.get(oldLine), lineNumberPositions.get(oldLine+1) - lineNumberPositions.get(oldLine));
	        Style style1 = codePane.addStyle("instruction style", null);
	        StyleConstants.setBackground(style1, Color.WHITE);
			codePaneDocument.insertString(lineNumberPositions.get(oldLine), temp1, style1);
			
			// Add the new background
			if (currentLine != -1){
				// -1 represents no fill
				String temp2 = text.substring(lineNumberPositions.get(currentLine), lineNumberPositions.get(currentLine+1));
				codePaneDocument.remove(lineNumberPositions.get(currentLine), lineNumberPositions.get(currentLine+1) - lineNumberPositions.get(currentLine));
		        Style style2 = codePane.addStyle("instruction style", null);
		        StyleConstants.setBackground(style2, Color.BLUE);
				codePaneDocument.insertString(lineNumberPositions.get(currentLine), temp2, style2);
			}
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
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
	
	private boolean checkSaved () {
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
		return saved;
	}
	
	private void startStepping(){
		try {
			boolean saved = checkSaved();
			if (currentFile != null && saved) {  // Needs to be saved first to run
				runButton.setEnabled(false); // Disable run button
//				stepButton.setEnabled(false); // Disable run button
				Interpreter interpreter = new Interpreter(new BufferedReader(new FileReader(currentFile)));
				interpreter.addListener(new InterpreterListener(){
					@Override
					void outputEvent(String output) {
						printToConsole(output);
					}
					@Override
					void finishedEvent() {
						// When the code has finished, print the memory and the time taken
						printToConsole(interpreter.printMemory());
						runButton.setEnabled(true);
						updateCurrentLine(-1, interpreterController.getLastLine());
						
					}
					@Override
					void finishedStepEvent() {
						// When the code has finished a step, print the memory and the time taken
						stepButton.setEnabled(true);
						updateCurrentLine(interpreterController.getCurrentLine(), interpreterController.getLastLine());
					}
				});
//				updateCurrentLine(0, 0);
				consolePane.setText("");  // Reset text
				interpreterController.setInterpreter(interpreter);
				interpreterController.setStepping(true);
				interpreterController.start();
			}
		} catch (IOException e) {
			consolePane.setText("\nError starting interpreter");
		}
	}
	
	private void startInterpreting(){
		// Starts interpreting the code when the run button is clicked
		try {
			boolean saved = checkSaved();
//			stepButton.setEnabled(false);
			
			runButton.setEnabled(false);
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
						stepButton.setEnabled(true);
						runButton.setEnabled(true);
					}
				});
				consolePane.setText("");  // Reset text
				interpreterController.setInterpreter(interpreter);
				interpreterController.setStepping(false);
				interpreterController.start();
			}
		} catch (IOException e) {
			consolePane.setText("\nError starting interpreter");
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
