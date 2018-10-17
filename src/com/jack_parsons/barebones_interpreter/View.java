package com.jack_parsons.barebones_interpreter;

import java.awt.EventQueue;
import java.io.FileWriter;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.JLayeredPane;
import javax.swing.JInternalFrame;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import net.miginfocom.swing.MigLayout;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.Panel;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JScrollBar;
import java.awt.Color;
import javax.swing.JSeparator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class View {

	private JFrame frmBarebonesInterpreterIde;
	private final JFileChooser fileExplorerWindow = new JFileChooser();
	private JTextArea txtrWaiting;
	private JEditorPane editorPane;
	private File currentFile = null;
	private final String windowTitle = "BareBones Interpreter";
	private boolean textChanged = true;
	private InterpreterController interpeterController;
	
	/**
	 * @wbp.nonvisual location=-30,181
	 */
	private final JPanel panel = new JPanel();

	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the application.
	 */
	public View() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBarebonesInterpreterIde = new JFrame();
		frmBarebonesInterpreterIde.setTitle(windowTitle);
		frmBarebonesInterpreterIde.setBounds(100, 100, 900, 600);
		frmBarebonesInterpreterIde.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		interpeterController = new InterpreterController();
		
		JMenuBar menuBar = new JMenuBar();
		frmBarebonesInterpreterIde.setJMenuBar(menuBar);
		
		JMenu mnV = new JMenu("File");
		menuBar.add(mnV);
		
		JButton btnOpenFile_1 = new JButton("Open File");
		btnOpenFile_1.addMouseListener(new MouseAdapter() {
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
		btnOpenFile_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		mnV.add(btnOpenFile_1);
		
		JButton btnSaveAs = new JButton("Save As");
		btnSaveAs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveAs();
			}
		});
		btnSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		mnV.add(btnSaveAs);
		
		JButton btnSaveFile = new JButton("Save");
		btnSaveFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (currentFile == null){
					saveAs();
				} else {
					saveFile();
				}
			}
		});
		mnV.add(btnSaveFile);
		
		JButton btnNewButton = new JButton("Run");
		btnNewButton.setToolTipText("");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				startInterpreting();
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		JSeparator separator = new JSeparator();
		menuBar.add(separator);
		menuBar.add(btnNewButton);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setBackground(Color.LIGHT_GRAY);
		splitPane.setResizeWeight(0.9);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmBarebonesInterpreterIde.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		editorPane = new JEditorPane();
		editorPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				textChanged = true;  // Now prompt will appear asking to save
			}
		});
		JScrollPane scrollPlane2 = new JScrollPane(editorPane);
		splitPane.setLeftComponent(scrollPlane2);
		
		txtrWaiting = new JTextArea();
		txtrWaiting.setEditable(false);
		txtrWaiting.setText("waiting...");
		JScrollPane scrollPlane1 = new JScrollPane(txtrWaiting);
		splitPane.setRightComponent(scrollPlane1);
	}
	
	private void updateEditorManager(File file) {
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
			editorPane.setText(fileText.toString());
			bareboneBufferedReader.close();
		} catch (IOException e) {
			System.out.println("Error reading file");
		}
	}
	
	private void startInterpreting(){
		try {
			boolean saved;
			if (textChanged) {
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
			
			if (currentFile != null && saved) {  // Needs to be saved to run
				Interpreter interpreter = new Interpreter(new BufferedReader(new FileReader(currentFile)));
				interpreter.addListener(new InterpreterListener(){
					@Override
					void outputEvent(String output) {
						txtrWaiting.append(output);
					}
					@Override
					void finishedEvent() {
						txtrWaiting.append(interpreter.printMemory());
						txtrWaiting.append(interpreter.printTimeTaken());
					}
				});
				txtrWaiting.setText("");  // Reset text
				interpeterController.setInterpreter(interpreter);
				interpeterController.start();
			}
		} catch (IOException e) {
			txtrWaiting.setText("\nError starting interpreter");
			System.out.println("Note");
		}
	}
	
	private void saveAs () {
		fileExplorerWindow.setCurrentDirectory(new File(System.getProperty("user.dir")+"/barebones code/"));
		int returnVal = fileExplorerWindow.showSaveDialog(frmBarebonesInterpreterIde);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileExplorerWindow.getSelectedFile();
            currentFile = file;
    		frmBarebonesInterpreterIde.setTitle(file.toString() + " - " + windowTitle);
            saveFile();
		}
	}
	
	private void saveFile() {
		try {
			if (currentFile != null){
				FileWriter writer = new FileWriter(currentFile);
				writer.write(editorPane.getText());
				writer.close();
				textChanged = false; // Reset so prompt asking to save does not appear
			} else {
				// If not saved yet, then bring up save as dialogue
				saveAs();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
