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
	public final String windowTitle = "BareBones Interpreter";
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
		frmBarebonesInterpreterIde.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_META) {
					System.out.println(1);
				}
			}
		});
		frmBarebonesInterpreterIde.setBounds(100, 100, 720, 540);
		frmBarebonesInterpreterIde.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
			if (currentFile != null) {
				Interpreter interpreter = new Interpreter(new BufferedReader(new FileReader(currentFile)));
				interpreter.start();
				txtrWaiting.setText("");  // Reset text
				txtrWaiting.append(interpreter.printMemory());
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
			FileWriter writer = new FileWriter(currentFile);
			writer.write(editorPane.getText());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
