package textcollage;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * A panel that contains a large drawing area where strings
 * can be drawn.  The strings are represented by objects of
 * type DrawTextItem.  An input box under the panel allows
 * the user to specify what string will be drawn when the
 * user clicks on the drawing area.
 */
public class DrawTextPanel extends JPanel  {
	
	// As it now stands, this class can only show one string at at
	// a time!  The data for that string is in the DrawTextItem object
	// named theString.  (If it's null, nothing is shown.  This
	// variable should be replaced by a variable of type
	// ArrayList<DrawStringItem> that can store multiple items.
	
	//private DrawTextItem theString;  // change to an ArrayList<DrawTextItem> !
	//My input.
	//Array list is declared and used instead of single element.
	
	private ArrayList<DrawTextItem> listOfItems = new ArrayList<DrawTextItem>();//working with array, solve it with array list

	
	private Color currentTextColor = Color.BLACK;  // Color applied to new strings.

	private Canvas canvas;  // the drawing area.
	private JTextField input;  // where the user inputs the string that will be added to the canvas
	private SimpleFileChooser fileChooser;  // for letting the user select files
	private JMenuBar menuBar; // a menu bar with command that affect this panel
	private MenuHandler menuHandler; // a listener that responds whenever the user selects a menu command
	private JMenuItem undoMenuItem;  // the "Remove Item" command from the edit menu
	private JTextField angleInput; // To input text angle
	private JTextField transparency; // To input text transparency
	
	/**
	 * An object of type Canvas is used for the drawing area.
	 * The canvas simply displays all the DrawTextItems that
	 * are stored in the ArrayList, strings.
	 */
	private class Canvas extends JPanel {
		Canvas() {
			setPreferredSize( new Dimension(800,600) );
			setBackground(Color.LIGHT_GRAY);
			setFont( new Font( "Serif", Font.BOLD, 24 ));
		}
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			// my input
			if (listOfItems != null){
			for (DrawTextItem element : listOfItems){
			if (element != null)
				element.draw(g);
		}}
			//if (theString != null)
				//theString.draw(g);
		}
	}
	
	/**
	 * An object of type MenuHandler is registered as the ActionListener
	 * for all the commands in the menu bar.  The MenuHandler object
	 * simply calls doMenuCommand() when the user selects a command
	 * from the menu.
	 */
	private class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			doMenuCommand( evt.getActionCommand());
		}
	}

	/**
	 * Creates a DrawTextPanel.  The panel has a large drawing area and
	 * a text input box where the user can specify a string.  When the
	 * user clicks the drawing area, the string is added to the drawing
	 * area at the point where the user clicked.
	 */
	public DrawTextPanel() {
		fileChooser = new SimpleFileChooser();
		undoMenuItem = new JMenuItem("Remove Item");
		undoMenuItem.setEnabled(false);
		menuHandler = new MenuHandler();
		setLayout(new BorderLayout(3,3));
		setBackground(Color.BLACK);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel();
		bottom.add(new JLabel("Text to add: "));
		input = new JTextField("Hello World!", 40);
		bottom.add(input);
		
		bottom.add(new JLabel("Set angle: "));
		angleInput = new JTextField("0", 4);
		bottom.add(angleInput);
		
		bottom.add(new JLabel("Set Transparency: "));
		transparency = new JTextField("0", 4);
		bottom.add(transparency);
		
		add(bottom, BorderLayout.SOUTH);
		canvas.addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				doMousePress( e );
			}
		} );
	}
		
	/**
	 * This method is called when the user clicks the drawing area.
	 * A new string is added to the drawing area.  The center of
	 * the string is at the point where the user clicked.
	 * @param e the mouse event that was generated when the user clicked
	 */
	public void doMousePress( MouseEvent e ) {
		String text = input.getText().trim();
		if (text.length() == 0) {
			input.setText("Hello World!");
			text = "Hello World!";
		}
		String angle = angleInput.getText().trim();
		Double rotationAngle = 0.0;
		try {
			rotationAngle = Double.parseDouble(angle);
		}
		catch (Exception e1){
			JOptionPane.showMessageDialog(this, "Angle must be a valid number.");
			return;
		}
		String trans = transparency.getText().trim();

		Double textTransparency = 0.0;
		try {
			textTransparency = Double.parseDouble(trans);
			if (textTransparency<0 || textTransparency>1){
				JOptionPane.showMessageDialog(this, "Transparency must be a valid number, between 0 & 1.");
				return;
				}
		}
		catch (Exception e1){
			JOptionPane.showMessageDialog(this, "Transparency must be a valid number.");
			return;
		}
		
		DrawTextItem s = new DrawTextItem( text, e.getX(), e.getY() );
		s.setTextColor(currentTextColor);  // Default is null, meaning default color of the canvas (black).
		// Adding these two parameters settings
		s.setRotationAngle(rotationAngle);
		s.setTextTransparency(textTransparency);
//   SOME OTHER OPTIONS THAT CAN BE APPLIED TO TEXT ITEMS:
//		s.setFont( new Font( "Serif", Font.ITALIC + Font.BOLD, 12 ));  // Default is null, meaning font of canvas.
//		s.setMagnification(3);  // Default is 1, meaning no magnification.
//		s.setBorder(true);  // Default is false, meaning don't draw a border.
//		s.setRotationAngle(25);  // Default is 0, meaning no rotation.
//		s.setTextTransparency(0.3); // Default is 0, meaning text is not at all transparent.
//		s.setBackground(Color.BLUE);  // Default is null, meaning don't draw a background area.
//		s.setBackgroundTransparency(0.7);  // Default is 0, meaning background is not transparent.
		
		//theString = s;  // Set this string as the ONLY string to be drawn on the canvas!
		if (listOfItems == null){ // To prevent errors when emptying the array list
			listOfItems = new ArrayList<DrawTextItem>();
			//System.out.println("I am here in the clear condition");
			
		}
		listOfItems.add(s);
		undoMenuItem.setEnabled(true);
		canvas.repaint();
	}
	
	/**
	 * Returns a menu bar containing commands that affect this panel.  The menu
	 * bar is meant to appear in the same window that contains this panel.
	 */
	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			
			String commandKey; // for making keyboard accelerators for menu commands
			if (System.getProperty("mrj.version") == null)
				commandKey = "control ";  // command key for non-Mac OS
			else
				commandKey = "meta ";  // command key for Mac OS
			
			JMenu fileMenu = new JMenu("File");
			menuBar.add(fileMenu);
			JMenuItem saveItem = new JMenuItem("Save...");
			saveItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "N"));
			saveItem.addActionListener(menuHandler);
			fileMenu.add(saveItem);
			JMenuItem openItem = new JMenuItem("Open...");
			openItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "O"));
			openItem.addActionListener(menuHandler);
			fileMenu.add(openItem);
			fileMenu.addSeparator();
			JMenuItem saveImageItem = new JMenuItem("Save Image...");
			saveImageItem.addActionListener(menuHandler);
			fileMenu.add(saveImageItem);
			
			JMenu editMenu = new JMenu("Edit");
			menuBar.add(editMenu);
			undoMenuItem.addActionListener(menuHandler); // undoItem was created in the constructor
			undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "Z"));
			editMenu.add(undoMenuItem);
			editMenu.addSeparator();
			JMenuItem clearItem = new JMenuItem("Clear");
			clearItem.addActionListener(menuHandler);
			editMenu.add(clearItem);
			
			JMenu optionsMenu = new JMenu("Options");
			menuBar.add(optionsMenu);
			JMenuItem colorItem = new JMenuItem("Set Text Color...");
			colorItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "T"));
			colorItem.addActionListener(menuHandler);
			optionsMenu.add(colorItem);
			JMenuItem bgColorItem = new JMenuItem("Set Background Color...");
			bgColorItem.addActionListener(menuHandler);
			optionsMenu.add(bgColorItem);
			
		}
		return menuBar;
	}
	
	/**
	 * Carry out one of the commands from the menu bar.
	 * @param command the text of the menu command.
	 */
	private void doMenuCommand(String command) {
		if (command.equals("Save...")) { // save all the string info to a file

			File dataFile = fileChooser.getOutputFile(this, "Select Data File Name", "textimage.txt");
			if (dataFile == null)
				return;
			//JOptionPane.showMessageDialog(this, "Sorry, the Save command is not implemented.");

			try {
				// Image data is saved in a text file
				//File text format is: first line 3 integers represent background colors
				// Red, Green and Blue
				PrintWriter result = new PrintWriter(dataFile);
				
				result.println(canvas.getBackground().getRed()+ "\t" +canvas.getBackground().getGreen()
						+ "\t" +canvas.getBackground().getBlue());
				// For each element, first font is recorded, then X and Y position
				// Following is the text color (red, green and blue)
				// then the text border, rotation angle, magnification, transparency, background transparency
				// and finally the string. THe string is left to the end since it can be a sentence. 
				
				for (DrawTextItem element : listOfItems)
				{
					result.print(element.getX()+ "\t" + element.getY()
					+ "\t" + element.getTextColor().getRed() + "\t" + element.getTextColor().getGreen() 
					+ "\t" + element.getTextColor().getBlue()+ "\t" + element.getRotationAngle() 
					+ "\t" + element.getMagnification()+ "\t" + element.getTextTransparency()
					+ "\t" + element.getBackgroundTransparency()+ "\t" + element.getString());
					
					result.println("");					
				
				}
				result.flush();
				result.close();

				}
			catch (IOException e){
				JOptionPane.showMessageDialog(this,"Cannot open file" + dataFile.toString());
				return;
			}			catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Sorry, an error occurred while trying to save the file:\n" + e);
			}

			}
		else if (command.equals("Open...")) { // read a previously saved file, and reconstruct the list of strings
			File dataFile = fileChooser.getInputFile(this, "Select Data File Name to import");
			if (dataFile == null)
				return;
			try{
				Scanner inputFile = new Scanner(dataFile);
				try {
					String backGround = inputFile.nextLine();
					int backGroundRed, backGroundGreen, backGroundBlue;
					backGroundRed = Integer.parseInt(backGround.split("\t")[0]);
					backGroundGreen = Integer.parseInt(backGround.split("\t")[1]);
					backGroundBlue = Integer.parseInt(backGround.split("\t")[2]);
					
					ArrayList<DrawTextItem> listOfItemsImported = new ArrayList<DrawTextItem>();
					while (inputFile.hasNextLine()){
						String string = inputFile.nextLine();
						String[] items = string.split("\t");
						int elementX = Integer.parseInt(items[0]);
						int elementY = Integer.parseInt(items[1]);
						int elementRed = Integer.parseInt(items[2]);
						int elementGreen = Integer.parseInt(items[3]);
						int elementBlue = Integer.parseInt(items[4]);
						double elementAngle = Double.parseDouble(items[5]);
						double elementMag = Double.parseDouble(items[6]);
						double elementTrans = Double.parseDouble(items[7]);
						double elementBackTrans = Double.parseDouble(items[8]);
						String text = items[9];
						
						DrawTextItem element = new DrawTextItem(text, elementX, elementY);
						element.setBackgroundTransparency(elementBackTrans);
						element.setTextTransparency(elementTrans);
						element.setRotationAngle(elementAngle);
						element.setMagnification(elementMag);
						Color textColor = new Color(elementRed, elementGreen, elementBlue);
						element.setTextColor(textColor);
						listOfItemsImported.add(element);						
					}
					listOfItems = listOfItemsImported;
					inputFile.close();
					Color backgroundColor = new Color(backGroundRed, backGroundGreen, backGroundBlue);
					canvas.setBackground(backgroundColor);
				}
				catch (Exception e){
					JOptionPane.showMessageDialog(this, "Error occured. File not valid.");
					return;
				}
			}
			catch (FileNotFoundException e){
				JOptionPane.showMessageDialog(this, "File not found.");
				return;
			}
		
			canvas.repaint(); // (you'll need this to make the new list of strings take effect)
		}
		else if (command.equals("Clear")) {  // remove all strings
			listOfItems = null;//theString = null;   // Remove the ONLY string from the canvas.
 			if (listOfItems == null){
				undoMenuItem.setEnabled(false);
				canvas.repaint();}
			else {

				listOfItems.clear();
				undoMenuItem.setEnabled(false);
				canvas.repaint();
			}

		}
		else if (command.equals("Remove Item")) { // remove the most recently added string
			//theString = null;   // Remove the ONLY string from the canvas.
			if (listOfItems.size()>0)
				listOfItems.remove(listOfItems.size()-1);
			if (listOfItems.size()==0)
				undoMenuItem.setEnabled(false);
			canvas.repaint();
		}
		else if (command.equals("Set Text Color...")) {
			Color c = JColorChooser.showDialog(this, "Select Text Color", currentTextColor);
			if (c != null)
				currentTextColor = c;
		}
		else if (command.equals("Set Background Color...")) {
			Color c = JColorChooser.showDialog(this, "Select Background Color", canvas.getBackground());
			if (c != null) {
				canvas.setBackground(c);
				canvas.repaint();
			}
		}
		else if (command.equals("Save Image...")) {  // save a PNG image of the drawing area
			File imageFile = fileChooser.getOutputFile(this, "Select Image File Name", "textimage.png");
			if (imageFile == null)
				return;
			try {
				// Because the image is not available, I will make a new BufferedImage and
				// draw the same data to the BufferedImage as is shown in the panel.
				// A BufferedImage is an image that is stored in memory, not on the screen.
				// There is a convenient method for writing a BufferedImage to a file.
				BufferedImage image = new BufferedImage(canvas.getWidth(),canvas.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics g = image.getGraphics();
				g.setFont(canvas.getFont());
				canvas.paintComponent(g);  // draws the canvas onto the BufferedImage, not the screen!
				boolean ok = ImageIO.write(image, "PNG", imageFile); // write to the file
				if (ok == false)
					throw new Exception("PNG format not supported (this shouldn't happen!).");
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(this, 
						"Sorry, an error occurred while trying to save the image:\n" + e);
			}
		}
	}
	

}
