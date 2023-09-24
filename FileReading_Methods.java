import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class FileReading_Methods {

	public static DefaultTableModel dtm = new DefaultTableModel();
	public static JTable memberslist;
	public static JScrollPane scrollPane_2;

	public String[] savefiles = new String[6];

	public String[] loadFileDestinations() {
		try {
			File f1 = new File("SaveFiles.txt"); // identifies text file
			if (f1.createNewFile()) {
				System.out.println("'SaveFiles.txt' is created!");
			} else {
				System.out.println("'SaveFiles.txt' already exists.");
			}
			FileReader in = new FileReader(f1); // prepares to read file
			BufferedReader r = new BufferedReader(in); // actually reads and extracts from the file

			for(int i = 0; i < savefiles.length; i++) {
				String line = r.readLine();

				if (line == null) {
					break;
				}

				savefiles[i] = line;
			}

			r.close();
			System.out.println("Success!");
		}

		catch (Exception e2) {
			System.out.println("SaveFiles.txt Reading doesn't work: " + e2);
		}

		return savefiles;
	}

	public void setUpComboBoxCells(JTable table, TableColumn columnName, String filename) {
		// READ FROM FILE TO UPLOAD EXISTING DATA INTO THE TABLE

		JComboBox comboBox = new JComboBox();
		comboBox.addItem("???");

		try {
			File f1 = new File(filename + ".txt"); // identifies text file
			if (f1.createNewFile()) {
				System.out.println(filename + ".txt" + " is created!");
			} else {
				System.out.println(filename + ".txt" + " already exists.");
			}
			FileReader in = new FileReader(f1); // prepares to read file
			BufferedReader r = new BufferedReader(in); // actually reads and extracts from the file

			while (true) {
				String choice = r.readLine();

				if (choice == null)
					break;

				comboBox.addItem(choice);

			}

			r.close();
		}

		catch (Exception e2) {
			System.out.println(filename + "reading doesn't work: " + e2);
		}

		columnName.setCellEditor(new DefaultCellEditor(comboBox));

		columnName.setCellRenderer(new TableCellRenderer() {
			JComboBox box = new JComboBox();

			// https://stackoverflow.com/questions/30744524/how-to-make-the-jcombobox-dropdown-always-visible-in-a-jtable
			@Override
			public JComboBox getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				box.removeAllItems();
				box.addItem(value.toString());
				return box;
			}
		});

	}

	public void readIntoTable(String filename, Object[] colNames, DefaultTableModel dtm, JTable tab1e) {
		Object[] columns = colNames;
		int arrLength = columns.length;

		// READ FROM FILE TO UPLOAD EXISTING DATA INTO THE TABLE
		try {
			File f1 = new File(filename + ".txt"); // identifies text file
			if (f1.createNewFile()) {
				System.out.println(filename + ".txt" + " is created!");
			} else {
				System.out.println(filename + ".txt" + " already exists.");
			}
			FileReader in = new FileReader(f1); // prepares to read file
			BufferedReader r = new BufferedReader(in); // actually reads and extracts from the file

			outerloop: // label to be able to break the whole loop while inside the for-loop
			// https://stackoverflow.com/questions/886955/how-do-i-break-out-of-nested-loops-in-java
			while (true) {
				String tempArr[] = new String[arrLength];

				for (int i = 0; i < arrLength; i++) {
					tempArr[i] = r.readLine();
				}

				for (int j = 0; j < arrLength; j++) {
					if (tempArr[j] == null) {
						break outerloop;
					}
				}

				Object[] row = new Object[arrLength];

				if (memberslist == null) {
					for (int i = 0; i < arrLength; i++) {
						row[i] = tempArr[i];
					}

					dtm.addRow(row);
				}

			}

			r.close();
		}

		catch (Exception e2) {
			System.out.println(filename + "reading doesn't work: " + e2);
		}
	}

//	public void data_validation(JTable table, Object[] columnNums) {
//		if(table.getCellEditor() != null) {
//			
//			if(Arrays.asList(columnNums).contains(table.getEditingColumn())) {
//				
//			}
//			
//		}
//	}

	public float getTotal(JTable table, int columnNum) {
		float total = 0;

		for (int i = 0; i < table.getRowCount(); i++) {

			// Check for empty rows that can cause errors (SEPT 4: WHEN YOU SAVE a table
			// with an empty row, close, then reopen
			// the program, it clears ALL data???)
//			if(cell.length() == 0) {
//				total += 0;
//			} else {
			float value = Float.parseFloat(table.getValueAt(i, columnNum).toString());
			System.out.println(value);
			total += value;
//			}

		}

		return total;
	}
	
	public void save(JTextField search, Object[] columns, String fileName) {
		if(search.getText().length() < 0) {
			JOptionPane.showMessageDialog(null, "Please clear the search bar before saving your data.\n(So that you don't save a table only showing your search results)");
		} else {
			int save = JOptionPane.showConfirmDialog(null, "Are you sure you would like to save your data table?");

			if (save == JOptionPane.YES_OPTION) {

				int numOfRows = dtm.getRowCount();

				try {
					File f = new File(fileName + ".txt"); // creates text file
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
					System.out.println("Saving doesn't work!: " + e2);
					JOptionPane.showMessageDialog(null, e2, "Table Data DID NOT save properly", JOptionPane.WARNING_MESSAGE, null);
				}
			}
		}

	}

}
