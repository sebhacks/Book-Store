package package1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Gui implements ActionListener{

//specifying the layout type 
GridLayout gridLayout = new GridLayout(8,2);	
	
//creating the frame 
private JFrame frame = new JFrame("Book Store");

//keeping track of the time and date 
Date date = new Date();

//keeps track of the order total 
public double orderTotal = 0; 

//keeps track of the current item number 
public int curItem = 1; 



	//user entries for books
	private JLabel numItemsLabel = new JLabel("Enter number of items in this order: ");
	public JTextField numItemsText = new JTextField(20);

	private JLabel bookIDLabel = new JLabel("Enter book ID for item: " + curItem);	
	private JTextField bookIDText = new JTextField(20);

	private JLabel quantityLabel = new JLabel("Enter quantity for item: " + curItem);
	private JTextField quantityText = new JTextField(20);

	private JLabel itemInfoLabel = new JLabel("Item " + curItem + " info: ");
	private JTextField itemInfoText = new JTextField(100);

	private JLabel subtotalLabel = new JLabel("Order subtotal for items: ");
	private JTextField subtotalText = new JTextField(20);

	//buttons
	private JButton processItem = new JButton("Process item " + curItem);
	private JButton confirmItem = new JButton("Confirm item " + curItem);
	private JButton viewOrder = new JButton("View Order");
	private JButton finishOrder = new JButton("Finish Order");
	private JButton newOrder = new JButton("New Order");
	private JButton exit = new JButton("Exit");
	

	
	

//constructor
public Gui() {
	
		//set dimensions and default close operation
		frame.setSize(700, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(gridLayout);
		 									
								
		//add action listeners and set action commands 
		processItem.setActionCommand(processItem.getName());
		processItem.addActionListener(this);	
		confirmItem.setActionCommand(confirmItem.getName());
		confirmItem.addActionListener(this);	
		viewOrder.setActionCommand(viewOrder.getName());
		viewOrder.addActionListener(this);	
		finishOrder.setActionCommand(finishOrder.getName());
		finishOrder.addActionListener(this);	
		newOrder.setActionCommand(newOrder.getName());
		newOrder.addActionListener(this);
		exit.setActionCommand(newOrder.getName());
		exit.addActionListener(this);
		
		
		//create container for items
		Container c = frame.getContentPane();
		
		//adding items to container
		c.add(numItemsLabel);
		c.add(numItemsText); 		 
		c.add(bookIDLabel);
		c.add(bookIDText);
		c.add(quantityLabel);	//text area and labels
		c.add(quantityText);
		c.add(itemInfoLabel);  
		c.add(itemInfoText);
		c.add(subtotalLabel);
		c.add(subtotalText);
		
		c.add(processItem); 	 
		c.add(confirmItem);
		c.add(viewOrder);		//buttons at the bottom
		c.add(finishOrder);
		c.add(newOrder);
		c.add(exit);
		
		//making sure certain buttons are disabled initially 
		confirmItem.setEnabled(false);
		viewOrder.setEnabled(false);
		finishOrder.setEnabled(false);
		//making sure last 2 labels can't be editted 
		itemInfoText.setEditable(false);
		subtotalText.setEditable(false);
		
		//display frame 
		frame.setVisible(true);
}
 


//action event handler 
public void actionPerformed(ActionEvent e) {

	
    if (e.getSource() == processItem) {
    	
    	//updating buttons and labels
    	confirmItem.setEnabled(true);
    	processItem.setEnabled(false); 
    	numItemsText.setEditable(false);
    	
    	//reading in the number of items and storing it in numItems 
    	String str = numItemsText.getText();
    	int numItems = Integer.parseInt(str);
    	//reading in book ID and storing it in bookID 
    	String str2 = bookIDText.getText(); 
    	int bookID = Integer.parseInt(str2);
        //reading in quantity and storing it in quantity
      	String str3 = quantityText.getText(); 
      	int quantity = Integer.parseInt(str3);
    	
		// reading in input file 
		File file = new File("./src/package1/inventory.txt"); 	
		try {
			Scanner sc = new Scanner(file);
			String input = "";
			while (sc.hasNext()) {
				input = sc.nextLine();
				if(input.contains(str2)) { 
					
					//scans the line and grabs the third string (aka price)
					Scanner scanLine = new Scanner(input);
  					scanLine.useDelimiter(", *");
  					scanLine.next();
  					scanLine.next();
  					String amnt = scanLine.next();
  					double amount = Double.parseDouble(amnt);
  					amount = amount * quantity; 
  					scanLine.reset();
  					
  					//this stores the total with discount applied 
  					double total = applyDiscount(amount, calculateDiscount(quantity));
  					total = roundDecimal(total); 
					
					itemInfoText.setText(input + " " + quantity + " " + calculateDiscount(quantity) + "% $" + total);
					
					//resets variable holding bookID back to empty 
					str2 = "";
					break; 
				}
			}
			//handling error if book ID is not found in file
			if((sc.hasNext() == false) && (input.contains(str2) == false) ){
				JOptionPane.showMessageDialog(null, "Book ID " + str2 + " is not in file.");
				confirmItem.setEnabled(false);
				finishOrder.setEnabled(false);
				processItem.setEnabled(true);
			}
		} catch(FileNotFoundException exception) {
			exception.printStackTrace();			
		}
		
		
    	
        
      } else if (e.getSource() == confirmItem) {
    	  //updating buttons
    	  confirmItem.setEnabled(false);
    	  viewOrder.setEnabled(true);
    	  finishOrder.setEnabled(true);
    	  processItem.setEnabled(true);
    	  
      	//reading in book ID and storing it in str  
      	String str = bookIDText.getText(); 
      //reading in quantity and storing it in quantity
    	String str2 = quantityText.getText(); 
    	int quantity = Integer.parseInt(str2);
    	//reading in the number of items and storing it in numItems 
    	String str3 = numItemsText.getText();
    	int numItems = Integer.parseInt(str3);
    	  
    	// reading in input file 
  		File file = new File("./src/package1/inventory.txt"); 	
  		try {
  			Scanner sc = new Scanner(file);
  			String input = "";
  			while (sc.hasNext()) {
  				input = sc.nextLine();
  				if(input.contains(str)) {
  					
  				//Scans line, sets delimiter and skips to third line, with amount holding the
 					// book's price. Amount is then added to the orderTotal. 
  					Scanner scanLine = new Scanner(input);
  					scanLine.useDelimiter(", *");
  					scanLine.next();
  					scanLine.next();
  					String amnt = scanLine.next();
  					double amount = Double.parseDouble(amnt);
  					amount = amount * quantity;
  					double total = applyDiscount(amount, calculateDiscount(quantity));
  					total = roundDecimal(total); 
  					
  					//adds to total 
  					orderTotal += total; 
  					
  					
  					//tells user the subtotal 
  					subtotalText.setText("$" + orderTotal);
  					
  					
  					//resets scanner to default 
  					scanLine.reset();
  					
  				}
  			}
  		} catch(FileNotFoundException exception) {
  			exception.printStackTrace();			
  		}
    	  
    	  
			
    	  
    	  String tmpText = itemInfoText.getText(); 
    	  appendStrToFile("./src/package1/logger.txt", tmpText); 
    	  
    	  JOptionPane.showMessageDialog(null, "Item accepted.");
    	  
    	  //updates the current item 
    	  curItem++; 
    	  
    	  if(curItem > numItems) {
    		  processItem.setEnabled(false);
    		  bookIDText.setEditable(false);
    		  bookIDText.setEditable(false);
    		  quantityText.setEditable(false);
    	  } else {
        	  //reset necessry text fields 
        	  bookIDText.setText("");
        	  quantityText.setText("");
        	  itemInfoText.setText("");
        	  
        	  //updating labels and buttons
        	  bookIDLabel.setText("Enter book ID for item: " + curItem);
        	  quantityLabel.setText("Enter quantity for item: " + curItem);
        	  itemInfoLabel.setText("Item " + curItem + " info: ");
        	  processItem.setText("Process item " + curItem);
        	  confirmItem.setText("Confirm item " + curItem);
    	  }
    	  

    	  
    	  
    	  
      	} else if (e.getSource() == viewOrder) {
      	  
      			String message = "";
	    		File file = new File("./src/package1/logger.txt"); 	
	    		try {
	    			Scanner sc = new Scanner(file);
	    			while (sc.hasNext()) {
	    				String input2 = sc.nextLine(); 
	    				message += input2 + "\n"; 
	    			}
	    		} catch(FileNotFoundException exception) {
	    			exception.printStackTrace();			
	    		}
	    		 
	    		JOptionPane.showMessageDialog(null, message);
	      		
      	  
        			} else if (e.getSource() == finishOrder) {
          	  
		      			String message = "";
		      			int counter = 0; 
			    		File file = new File("./src/package1/logger.txt"); 	
			    		try {
			    			Scanner sc = new Scanner(file);
			    			while (sc.hasNext()) {
			    				String input2 = sc.nextLine(); 
			    				message += input2 + "\n";
			    				counter += 1; 
			    			}
			    		} catch(FileNotFoundException exception) {
			    			exception.printStackTrace();			
			    		}
			    		
			    		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
			    		
			    		orderTotal = roundDecimal(orderTotal); 
			    		
			    		String dateMessage = "Date: " + sdf.format(date) + "\n" + "\n";
			    		
			    		String numLinesMessage = "Number of line items: " + counter + "\n" + "\n";
			    		
			    		String message2 = "\n" + "\n" + "Your order subtotal is: $" + orderTotal;
			    	
			    		String message3 = "\n" + "\n" + "Thanks for shopping! ";
			    		 
			    		JOptionPane.showMessageDialog(null, dateMessage+numLinesMessage+message+message2+message3);
			    		
			    		
			    		//exit application 
			    		System.exit(0);
        		
        		
          	  
            	} else if (e.getSource() == newOrder) {
              	  //updating buttons and labels 
            		processItem.setEnabled(true);
            		confirmItem.setEnabled(false);
            		viewOrder.setEnabled(false);
            		finishOrder.setEnabled(false);
            		numItemsText.setEditable(true);
            		subtotalText.setText("");
            		bookIDText.setEditable(true);
            		bookIDText.setText("");
            		quantityText.setEditable(true);
            		quantityText.setEnabled(true);
            		itemInfoText.setText("");
            		numItemsText.setText("");
            		quantityText.setText("");
              	  	
            		//update counter to 1 again 
            		curItem = 1; 
            		
            		//update labels and buttons 
            		bookIDLabel.setText("Enter book ID for item: " + curItem);	
            		quantityLabel.setText("Enter quantity for item: " + curItem);
            		itemInfoLabel.setText("Item " + curItem + " info: ");
            		processItem.setText("Process item " + curItem);
            		confirmItem.setText("Confirm item " + curItem);
            		
            		
            		//empty out the contents of the txt file 
        			try { 
                		PrintWriter writer = new PrintWriter("./src/package1/logger.txt");
                		writer.print("");
                		writer.close();
        			} catch (IOException exception) { 
        				System.out.println(exception); 
        			}
        			
        			
              	  
        			
		                	} else if (e.getSource() == exit) {
		                    	  //exit the application 
		                    	  System.exit(0);
		                    	  
		                      	}
				
}

		public static void appendStrToFile(String fileName, String str) {
			
			Date date = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmm");
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat sdf3 = new SimpleDateFormat("hh:mm:ss a z");
			
			try { 
				// Open given file in append mode. 
				BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true)); 
				out.write(sdf.format(date) + ", " + str + ", " + sdf2.format(date) + ", " + sdf3.format(date) + "\n"); 
				out.close(); 
			} catch (IOException e) { 
				System.out.println(e); 
			} 
		
		} 
		
		public int calculateDiscount(int quantity) {
			
			int percentDiscount = 0; 
			if(quantity>4 && quantity<10) {
				percentDiscount = 10; 
			} else if(quantity>9 && quantity<15) {
				percentDiscount = 15; 
				} else if(quantity>=15) {
					percentDiscount = 20; 
					}
			return percentDiscount; 
			
		}
		
		public double applyDiscount(double amount, int discount) {
			double discountPercent = 0; 
			double total = 0; 
					if(discount == 0) {
						return amount; 
					} else if(discount == 10) {
						discountPercent = 0.1; 
						} else if(discount == 15) {
							discountPercent = 0.15; 
							} else if(discount == 20) {
									discountPercent = 0.2; 
								}
			total = amount - (discountPercent * amount);  
			return total; 
		}
		
		public double roundDecimal(double value) {
			value = Math.round(value * 100.0) / 100.0;
			return value; 
		}

}

