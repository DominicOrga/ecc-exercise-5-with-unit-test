package com.ecc.app;

import com.ecc.service.TableService;
import com.ecc.service.TableServiceImpl;
import com.ecc.util.Utility;
import com.ecc.util.InputUtility;
import com.ecc.model.TableSearch;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class App {
	public static final int OPTION_SEARCH = 0, OPTION_EDIT = 1, OPTION_ADD_ROW = 2, 
		OPTION_ADD_CELL = 3, OPTION_DISPLAY = 4, OPTION_SORT = 5, OPTION_RESET = 6, OPTION_EXIT = 7;

    public static void main(String[] args) {
        TableService tableService = null;

		try {
			boolean isDefaultFile = 
				InputUtility.nextBoolPersistent("Table File [0] Choose File, [1] Use Default:", "1", "0");

			if (!isDefaultFile) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File", "txt");
				fileChooser.setFileFilter(filter);

				int result = fileChooser.showOpenDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {
					tableService = new TableServiceImpl(fileChooser.getSelectedFile().getAbsolutePath());
				}
				else {
					System.out.println("File chooser cancelled. Using default table file.");
					isDefaultFile = true;		
				}
			}
			
			if (isDefaultFile) {
				Optional<String> tableResourcePath = Utility.getResourcePath("table.txt");

				if (!tableResourcePath.isPresent()) {
					throw new FileNotFoundException();
				}

				tableService = new TableServiceImpl(tableResourcePath.get());
			}

		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			return;
		
		} catch (IOException e) {
			System.out.println("Table parsing failed");
			return;
		}

		boolean isExit = false;

		do {
			System.out.println("\n" + tableService.getTableAsString() + "\n\n");

			System.out.println("[0] Search, [1] Edit Cell, [2] Add Row, [3] Add Cell, [4] Display, " + 
				"[5] Sort, [6] Reset, [7] Exit");
			int option = InputUtility.nextIntPersistent("Enter Option:", 0, 7);
			int row, col;
			boolean isValidated;

			switch (option) {
				case OPTION_SEARCH:
					String searchString = InputUtility.nextStringPersistent("Enter Search String:");
					List<TableSearch> tableSearches = tableService.search(searchString);

					String strFormat = "@[row: %d, col: %d, %s] Found occurrences: %d\n";

					for (TableSearch tableSearch : tableSearches) {
						System.out.printf(strFormat, 
							tableSearch.getRow(), 
							tableSearch.getCol(), 
							tableSearch.isLeftPart() ? "LEFT PART" : "RIGHT PART", 
							tableSearch.getMatchCount());
					}

					System.out.println();
					break;

				case OPTION_EDIT: 
					isValidated = false;

					do {
						row = InputUtility.nextIntPersistent("Enter Row:", 0, tableService.getRowCount() - 1);
						col = InputUtility.nextIntPersistent("Enter Column:", 0, tableService.getColCount() - 1);

						if (tableService.isCellNull(row, col) || 
							tableService.isCellOutOfBounds(row, col)) {
							System.out.println("The chosen cell must not be a null cell.");
						}
						else {
							isValidated = true;
						}
					} while (!isValidated);
					
					boolean isLeftPart = 
						InputUtility.nextBoolPersistent("[0] Right Part, [1] Left Part:", "1", "0");

					String[] invalidSubstrings = { 
						TableService.CELL_DELIMITER + Utility.EMPTY_STRING, 
						TableService.TABLE_DELIMITER + Utility.EMPTY_STRING 
					};

					String str = InputUtility.nextStringPersistent(
						"Enter New String:", invalidSubstrings);

					tableService.editCell(row, col, isLeftPart, str);
					break;

				case OPTION_ADD_ROW:
					tableService.addRow();
					break;

				// case OPTION_ADD_CELL: 
				// 	isValidated = false;

				// 	do {
				// 		row = Utility.getIntegerInput("Enter Row:", 0, tableService.getRowCount() - 1);
				// 		col = Utility.getIntegerInput("Enter Column:", 0, tableService.getColumnCount() - 1);

				// 		if (tableService.isCellNull(row, col)) {
				// 			isValidated = true;
				// 		}
				// 		else {
				// 			System.out.println("The chosen cell must be a null cell.");
				// 		}

				// 	} while (!isValidated);

				// 	String leftStr = Utility.getStringInput(
				// 		"Enter Left String:", TableService.INNER_CELL_DELIMITER + Utility.EMPTY_STRING);
				// 	String rightStr = Utility.getStringInput(
				// 		"Enter Right String:", TableService.INNER_CELL_DELIMITER + Utility.EMPTY_STRING);

				// 	tableService.addCell(row, col, leftStr, rightStr);
				// 	tableService.displayTable();
				// 	break;

				// case OPTION_DISPLAY:
				// 	tableService.displayTable();
				// 	break;

				// case OPTION_SORT: 
				// 	row = 
				// 		Utility.getIntegerInput("Enter Row to Sort:", 0, tableService.getRowCount() - 1);

				// 	boolean isAscending = 
				// 		Utility.getBooleanInput("[0] Descending, [1] Ascending:", '1', '0');

				// 	tableService.sortRow(row, isAscending);
				// 	tableService.displayTable();
				// 	break;

				// case OPTION_RESET:
				// 	row = Utility.getIntegerInput("Enter Desired Number of Rows:", 0, 5000);
				// 	col = Utility.getIntegerInput("Enter Desired Number of Columns:", 0, 5000);

				// 	tableService.resetTable(row, col);
				// 	tableService.displayTable();
				// 	break;

				case OPTION_EXIT:
					isExit = true;
			}	

		} while (!isExit);
    }
}
