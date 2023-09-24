
import javax.swing.JColorChooser;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

//import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.SwingConstants;
//import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
//import javax.swing.JTextPane;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FixedCosts_Tab extends JFrame {

	private JPanel contentPane;
	public static DefaultTableModel dtm = new DefaultTableModel();
	public static JTable varCosts;
	public static JScrollPane scrollPane_1;

	public static String[] savefiles = new String[4];
	public Object[] columns = { "Capital/Equipment", "Qty", "Unit Cost", "Misc Fees", "Total Cost", "Date Purchased",
			"Supplier" };

	public JTextField search;

	public int totalTotal;
	public JLabel lblSumVariableCost_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FixedCosts_Tab frame = new FixedCosts_Tab();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */

	private void filter(String query) {
		TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(dtm);
		varCosts.setRowSorter(tr);
		tr.setRowFilter(RowFilter.regexFilter("(?i)" + query));
	}

	public FixedCosts_Tab() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(175, 3, 1017, 685);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		dtm.setColumnIdentifiers(columns);

		// TABLE SET-UP
		// https://www.quora.com/How-can-I-call-a-class-from-another-class-in-Java
		FileReading_Methods fileMethods = new FileReading_Methods();
		savefiles = fileMethods.loadFileDestinations();
		if(varCosts == null) { // To make sure information is not put into table a second time
			// (if you started from this window, went to another window, then went back
			// here)
			fileMethods.readIntoTable(savefiles[3], columns, dtm, varCosts);
		}
			
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(212, 108, 763, 354);
		contentPane.add(scrollPane_1);

		varCosts = new JTable();
		scrollPane_1.setViewportView(varCosts);
		varCosts.setModel(dtm);
		varCosts.setRowHeight(20); // https://www.codejava.net/java-se/swing/setting-column-width-and-row-height-for-jtable

		JButton Add2 = new JButton("Add (+)");
		Add2.setToolTipText(
				"Fill out all the entry fields above (especially the name text fields), then click this button to add an entry to the table above.");
		Add2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				final Object[] row = new Object[columns.length];

				row[0] = "???";
				row[1] = String.valueOf(0);
				row[2] = String.valueOf(0);
				row[3] = String.valueOf(0);
				row[4] = String.valueOf(0);
				row[5] = "MM/DD/YYYY";
				row[6] = "???";

				dtm.addRow(row);
			}

		});

		Add2.setFont(new Font("Tahoma", Font.BOLD, 11));
		Add2.setBounds(355, 503, 133, 45);
		contentPane.add(Add2);

		JButton Delete2 = new JButton("Delete (-)");
		Delete2.setToolTipText("Select a row from the table above, then click this button to delete it.");
		Delete2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int delete = JOptionPane.showConfirmDialog(null,
						"Would you like to delete your selected row/s from the table?");

				if (delete == JOptionPane.YES_OPTION) {
					int selRow = varCosts.getSelectedRow();
					dtm.removeRow(selRow);
				}
			}
		});
		Delete2.setFont(new Font("Tahoma", Font.BOLD, 11));
		Delete2.setBounds(519, 503, 133, 45);
		contentPane.add(Delete2);

		JButton Save2 = new JButton("Save");
		Save2.setToolTipText(
				"Click this button to save the data currently in the table into a text file (click the \"See Current Save Destination\" JButton to see the name of the text file where data from the table is currently being saved).");
		Save2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		Save2.setFont(new Font("Tahoma", Font.BOLD, 11));
		Save2.setBounds(689, 503, 126, 45);
		contentPane.add(Save2);

		JButton btnChangeSaveDestination = new JButton("Change Save Destination");
		btnChangeSaveDestination.setToolTipText(
				"Write the data currently in the table into a new text file or an existing text file of your choice");
		btnChangeSaveDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String option = JOptionPane.showInputDialog(
						"Would you like to save to a new file (type 'N') or an existing file (type 'E')?");

				if (option.equalsIgnoreCase("N") || option.equalsIgnoreCase("n")) {
					int filecreation = JOptionPane.showConfirmDialog(null,
							"Are you sure you would like to make the current table a new .txt file?");

					if (filecreation == JOptionPane.YES_OPTION) {
						String filename = JOptionPane.showInputDialog("Type a name for the .txt file");
						int numOfRows = dtm.getRowCount();

						try {
							File f1 = new File(filename + ".txt"); // identifies text file
							if (f1.createNewFile()) {
								FileOutputStream in = new FileOutputStream(f1); // prepares text file for printing
								PrintWriter w = new PrintWriter(in); // allows for text file to be printed

								for (int x = 0; x < numOfRows; x++) {
									for (int y = 0; y < 5; y++) {
										w.println((String) dtm.getValueAt(x, y));
									}
								}

								w.close();
								savefiles[3] = filename;

								File f2 = new File("SaveFiles.txt");
								FileOutputStream in2 = new FileOutputStream(f2); // prepares text file for printing
								PrintWriter w2 = new PrintWriter(in2); // allows for text file to be printed

								for (int r = 0; r < 4; r++) {
									System.out.println(savefiles[r]);
									w2.println((String) savefiles[r]);
								}
								w2.close();

								JOptionPane.showMessageDialog(null,
										"Your data has been saved into your new file '" + filename + ".txt'");
								System.out.println("File is created!");
							} else {
								System.out.println("File already exists.");
							}
						}

						catch (Exception e2) {
							System.out.println("File could not be created or process did not work");
						}
					}
				}

				if (option.equalsIgnoreCase("E") || option.equalsIgnoreCase("e")) {
					JFileChooser fc = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
					fc.setFileFilter(filter);

					String pathSeparator = System.getProperty("file.separator");

					int returnVal = fc.showOpenDialog(null);
					File selectedFile = null;
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						selectedFile = fc.getSelectedFile();
						String filename = selectedFile.getAbsolutePath();
						String actualfilename = filename.substring(filename.lastIndexOf(pathSeparator) + 1);
						int numOfRows = dtm.getRowCount();

						if (selectedFile.getName().toLowerCase().endsWith(".txt")) {
							try {
								File f1 = new File(actualfilename);

								FileOutputStream in = new FileOutputStream(f1); // prepares text file for printing
								PrintWriter w = new PrintWriter(in); // allows for text file to be printed

								for (int x = 0; x < numOfRows; x++) {
									for (int y = 0; y < 5; y++) {
										w.println((String) dtm.getValueAt(x, y));
									}
								}

								w.close();
								String finalfilename = actualfilename.replace(".txt", "");
								savefiles[3] = finalfilename;

								File f2 = new File("SaveFiles.txt");
								FileOutputStream in2 = new FileOutputStream(f2); // prepares text file for printing
								PrintWriter w2 = new PrintWriter(in2); // allows for text file to be printed

								for (int r = 0; r < 4; r++) {
									System.out.println(savefiles[r]);
									w2.println((String) savefiles[r]);
								}
								w2.close();

								JOptionPane.showMessageDialog(null,
										"Your data has been saved into your new file '" + filename + ".txt'");
								System.out.println("File is created!");
							}

							catch (Exception e2) {
								System.out.println("File could not be created or process did not work");
							}
						}
					}
				}

			}
		});
		btnChangeSaveDestination.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 11));
		btnChangeSaveDestination.setBounds(732, 583, 204, 54);
		contentPane.add(btnChangeSaveDestination);

		search = new JTextField();
		search.setToolTipText(
				"Type your search term in here to filter the table accordingly (casing of letters doesn't matter). Delete the text in here to show all table entries again.");
		search.setHorizontalAlignment(SwingConstants.CENTER);
		search.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String query = search.getText();
				filter(query);
			}
		});
		search.setFont(new Font("Tahoma", Font.BOLD, 14));
		search.setColumns(10);
		search.setBounds(446, 72, 369, 30);
		contentPane.add(search);

		JLabel labeel = new JLabel("Fixed Costs");
		labeel.setHorizontalAlignment(SwingConstants.CENTER);
		labeel.setFont(new Font("Tahoma", Font.BOLD, 35));
		labeel.setBounds(175, 22, 733, 45);
		contentPane.add(labeel);

//		JLabel label_15 = new JLabel("");
//		label_15.setIcon(new ImageIcon(varCosts.class.getResource("/bin/horizontal line.png")));
//		label_15.setBounds(0, 440, 140, 14);
//		contentPane.add(label_15);

		JButton btnRemoveSort = new JButton("Enable Sort");
		btnRemoveSort.setToolTipText(
				"Click this button, then click one of the column headers to sort the table from A to Z according to that column. Click that header again to sort the table from Z to A according to that column.");
		btnRemoveSort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(varCosts.getModel());
				varCosts.setRowSorter(sorter);

				List<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
				sorter.setSortKeys(sortKeys);

			}
		});
		btnRemoveSort.setBounds(825, 50, 133, 23);
		contentPane.add(btnRemoveSort);

		JButton button = new JButton("Disable Sort");
		button.setToolTipText(
				"Disable the ability to sort the table from A to Z or Z to A according to one of the table columns. The table will revert back to sorting entries from the earliest entry to the most recent entry.");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				varCosts.setRowSorter(null);

			}
		});
		button.setBounds(825, 79, 133, 23);
		contentPane.add(button);

//		JLabel label = new JLabel("");
//		label.setIcon(new ImageIcon(ChangeAccountDetails.class.getResource("/bin/horizontal line.png")));
//		label.setBounds(0, 506, 140, 14);
//		contentPane.add(label);

//		JLabel label_2 = new JLabel("");
//		label_2.setIcon(new ImageIcon(varCosts.class.getResource("/bin/horizontal line.png")));
//		label_2.setBounds(0, 573, 140, 14);
//		contentPane.add(label_2);

		JLabel lblTypeYourSearch = new JLabel("Type your search term in here ->");
		lblTypeYourSearch.setHorizontalAlignment(SwingConstants.CENTER);
		lblTypeYourSearch.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblTypeYourSearch.setBounds(173, 61, 276, 45);
		contentPane.add(lblTypeYourSearch);

		// FILE NEEDS TO BE IN SAME FOLDER AS EXISTING .TXT FILES
		JButton btnReadAnotherFile = new JButton("Read Another File");
		btnReadAnotherFile.setToolTipText(
				"Choose a text file and display its contents in the table above (only works if it has a number of lines divisible by 5).");
		btnReadAnotherFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
				fc.setFileFilter(filter);

				int returnVal = fc.showOpenDialog(null);
				File selectedFile = null;

				String pathSeparator = System.getProperty("file.separator");

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					selectedFile = fc.getSelectedFile();
					String filename = selectedFile.getAbsolutePath();
					String actualfilename = filename.substring(filename.lastIndexOf(pathSeparator) + 1);
					String lastfilename = actualfilename.replaceAll(".txt", "");

					if (selectedFile.getName().toLowerCase().endsWith(".txt")) {
						dtm.setRowCount(0);

						fileMethods.readIntoTable(lastfilename, columns, dtm, varCosts);
					}
					scrollPane_1.setViewportView(varCosts);
					varCosts.setModel(dtm);
					contentPane.add(scrollPane_1);

					int choice = JOptionPane.showConfirmDialog(null,
							"Would you like to make this file your new save destination?");

					if (choice == JOptionPane.YES_OPTION) {
						try {
							String finalfilename = actualfilename.replace(".txt", "");
							savefiles[3] = finalfilename;

							File f2 = new File("SaveFiles.txt");
							FileOutputStream in2 = new FileOutputStream(f2); // prepares text file for printing
							PrintWriter w2 = new PrintWriter(in2); // allows for text file to be printed

							for (int r = 0; r < 4; r++) {
								System.out.println(savefiles[r]);
								w2.println((String) savefiles[r]);
							}
							w2.close();
						}

						catch (Exception e2) {
							System.out.println("oops!!!!!!!!!");
						}
					}
				}
			}
		});
		btnReadAnotherFile.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 11));
		btnReadAnotherFile.setBounds(488, 583, 204, 54);
		contentPane.add(btnReadAnotherFile);

		JButton btnViewCurrentSave = new JButton("View Current Save Destination");
		btnViewCurrentSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"The data in the table above is currently being saved to '" + savefiles[3] + ".txt'");
			}
		});
		btnViewCurrentSave.setToolTipText(
				"Click this button to see a prompt that tells you where data from the table above is currently being saved.");
		btnViewCurrentSave.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 11));
		btnViewCurrentSave.setBounds(229, 583, 204, 54);
		contentPane.add(btnViewCurrentSave);

		JLabel lblSumVariableCost = new JLabel("Sum Fixed Cost:");
		lblSumVariableCost.setHorizontalAlignment(SwingConstants.CENTER);
		lblSumVariableCost.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblSumVariableCost.setBounds(519, 462, 194, 45);
		contentPane.add(lblSumVariableCost);

		lblSumVariableCost_1 = new JLabel("\u20B1");
		lblSumVariableCost_1.setForeground(Color.RED);
		lblSumVariableCost_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblSumVariableCost_1.setFont(new Font("Tahoma", Font.BOLD, 25));
		lblSumVariableCost_1.setBounds(697, 462, 194, 45);
		contentPane.add(lblSumVariableCost_1);
		lblSumVariableCost_1.setText("\u20B1" + String.valueOf(getTotal()));
		// --------------------------------------------

		JButton btnLifetime = new JButton("Lifetime Sales");
		btnLifetime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				if(contentPane.isDisplayable() == false) {
					Lifetime_Sales_Tab frame = new Lifetime_Sales_Tab();
					frame.setVisible(true);
				}
			}
		});
		btnLifetime.setBackground(Color.WHITE);
		btnLifetime.setBounds(-2, 122, 178, 41);
		contentPane.add(btnLifetime);

		JButton btnTotal = new JButton("Total Initial Inventory");
		btnTotal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				if(contentPane.isDisplayable() == false) {
					TotalInitialInventory_Tab frame = new TotalInitialInventory_Tab();
					frame.setVisible(true);
				}
			}
		});
		btnTotal.setBackground(Color.WHITE);
		btnTotal.setBounds(-2, 194, 178, 41);
		contentPane.add(btnTotal);

		JButton btnFixed = new JButton("Fixed Costs");
		btnFixed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnFixed.setForeground(Color.BLUE);
		btnFixed.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 12));
		btnFixed.setBackground(Color.WHITE);
		btnFixed.setBounds(-2, 269, 178, 41);
		contentPane.add(btnFixed);

		JButton btnVariable = new JButton("Variable Costs");
		btnVariable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				if(contentPane.isDisplayable() == false) {
					VariableCosts_Tab frame = new VariableCosts_Tab();
					frame.setVisible(true);
				}
			}
		});
		btnVariable.setBackground(Color.WHITE);
		btnVariable.setBounds(-2, 331, 178, 41);
		contentPane.add(btnVariable);

		JButton btnDuringCon = new JButton("Inventory DURING Con");
		btnDuringCon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				// SEPT 18:
				// https://stackoverflow.com/questions/34185479/was-jframe-disposed#:~:text=Although%20you%20should%20probably%20avoid,you%20could%20use%20for%20this.&text=You%20will%20want%20to%20use,check%20if%20it%20is%20disposed.
				if(contentPane.isDisplayable() == false) {
					Inventory_Tab frame = new Inventory_Tab();
					frame.setVisible(true);
				}
			}
		});
		btnDuringCon.setBackground(Color.WHITE);
		btnDuringCon.setBounds(-2, 399, 178, 41);
		contentPane.add(btnDuringCon);
		// --------------------------------------------

		// SEPT 4: https://stackoverflow.com/questions/5766175/word-wrap-in-jbuttons

		// Ensures that when you click the "red cross" to close the window, it will have
		// the confirmation screen
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// FOR AUTOMATIC CALCULATIONS:
		addTableListener();

	}

	// SEPT 4:
	// https://stackoverflow.com/questions/16295942/java-swing-adding-action-listener-for-exit-on-close?noredirect=1&lq=1
	// Second solution
	@Override
	public void dispose() {
		int exit = JOptionPane.showConfirmDialog(null, "Would you like to save your data AND close this window?");

		if (exit == JOptionPane.YES_OPTION) {
			save();
			super.dispose();
		}
		if (exit == JOptionPane.NO_OPTION) {
			super.dispose();
		}
	}

	// SEPT 4:
	// https://stackoverflow.com/questions/6889694/jtable-detect-cell-data-change?rq=3
	private void addTableListener() {
		dtm.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent tme) {
//                if (tme.getType() == TableModelEvent.UPDATE) {
//                    System.out.println("");
//                    System.out.println("Cell " + tme.getFirstRow() + ", "
//                            + tme.getColumn() + " changed. The new value: "
//                            + dtm.getValueAt(tme.getFirstRow(),
//                            tme.getColumn()));
//                }

				// If you edit the price or unit cost
				if (tme.getColumn() == 1 || tme.getColumn() == 2 || tme.getColumn() == 3) {

					float newTotal = 0;
					float newUnitCost = 0;

					float pcs = Float.parseFloat(varCosts.getValueAt(tme.getFirstRow(), 1).toString());
					float unitCost = Float.parseFloat(varCosts.getValueAt(tme.getFirstRow(), 2).toString());
					float miscCost = Float.parseFloat(varCosts.getValueAt(tme.getFirstRow(), 3).toString());
					float totalCost = Float.parseFloat(varCosts.getValueAt(tme.getFirstRow(), 4).toString());

//					if(varCosts.getValueAt(0, tme.getColumn()).toString().contains("Sticker")) {
//						
//					} else {
					float rawCost = pcs * unitCost;
					// raw cost + miscellaneous cost
					newTotal = rawCost + miscCost;
//					}

					varCosts.setValueAt(String.valueOf(newTotal), tme.getFirstRow(), 4);

					// Update text total with peso symbol outside
					lblSumVariableCost_1.setText("\u20B1" + String.valueOf(getTotal()));
				}

//				//Formula changes ONLY for sticker products: (total - misc)/pcs = unit cost
//				if(tme.getColumn() == 5 && varCosts.getValueAt(0, tme.getColumn()).toString().contains("Sticker")) {
//			
//				}
			}
		});
	}

	private float getTotal() {
		float total = 0;

		for (int i = 0; i < varCosts.getRowCount(); i++) {

			// Check for empty rows that can cause errors (SEPT 4: WHEN YOU SAVE a table
			// with an empty row, close, then reopen
			// the program, it clears ALL data???)
//			if(cell.length() == 0) {
//				total += 0;
//			} else {
			total += Float.parseFloat(varCosts.getValueAt(i, 4).toString());
//			}

		}

		return total;
	}

	private boolean checkEmptyRows() {
		boolean emptyRowFound = false;

		for (int i = 0; i < varCosts.getRowCount(); i++) {

		}

		return emptyRowFound;
	}

	public void save() {
		if (search.getText().length() < 0) {
			JOptionPane.showMessageDialog(null,
					"Please clear the search bar before saving your data.\n(So that you don't save a table only showing your search results)");
		} else {
			int save = JOptionPane.showConfirmDialog(null, "Are you sure you would like to save your data table?");

			if (save == JOptionPane.YES_OPTION) {

				int numOfRows = dtm.getRowCount();

				try {
					File f = new File(savefiles[3] + ".txt"); // creates text file
					FileOutputStream in = new FileOutputStream(f); // prepares text file for printing
					PrintWriter w = new PrintWriter(in); // allows for text file to be printed

					for (int x = 0; x < numOfRows; x++) {
						for (int y = 0; y < columns.length; y++) {
							w.println((String) dtm.getValueAt(x, y));
						}
					}

					w.close();
					JOptionPane.showMessageDialog(null, "Your data has been saved.");
				}

				catch (Exception e2) {
					System.out.println("Saving doesn't work!");
				}
			}
		}

	}
}