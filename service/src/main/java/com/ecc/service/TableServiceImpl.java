package com.ecc.service;

import com.ecc.util.Utility;
import com.ecc.model.TableCell;
import com.ecc.model.TableSearch;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class TableServiceImpl implements TableService {

	private int tableDelimitersPerRow;
	private File tableFile;

	private List<List<Optional<TableCell>>> rowCells;

	public TableServiceImpl(String tablePath) throws IOException, FileNotFoundException {
		tableFile = new File(tablePath);

		if (!tableFile.exists()) {
			throw new FileNotFoundException("File not found.");
		}

		identifyTableDelimiterCountPerRow();
		checkCellDelimiterCountPerCell();
		checkNoCellDelimiterOnNonEmptyCell();
		parseTable();
	};

	public boolean isCellNull(int row, int col) {
		Optional<TableCell> cell = rowCells.get(row).get(col);
		return !cell.isPresent();
	}

	public Optional<TableCell> getCell(int row, int col) {
		if (row < 0 || col < 0 || row >= getRowCount() || col >= getColCount()) {
			return Optional.empty();
		}

		return rowCells.get(row).get(col);
	}

	public int getRowCount() {
		return this.rowCells.size();
	}

	public int getColCount() {
		if (getRowCount() == 0) {
			return 0;
		}

		return this.rowCells.get(0).size();
	}

	public String getTableAsString() {
		return rowCells.stream()
					   .map((columnCells) ->
					       columnCells.stream()
									  .map((optionalCell) -> optionalCell.isPresent() ? optionalCell.get().toString() : "NULL")
									  .collect(Collectors.joining(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING)))
					   .collect(Collectors.joining("\n"));
	}

	public List<TableSearch> search(String searchString) {
		List<TableSearch> tableSearches = new ArrayList<>();

		for (int i = 0, s = getRowCount(); i < s; i++) {
			for (int j = 0, t = getColCount(); j < t; j++) {
				Optional<TableCell> cell = rowCells.get(i).get(j);

				if (!cell.isPresent()) {
					continue;
				}

				int leftPartCount = Utility.countOccurrence(cell.get().getLeftCell(), searchString);

				if (leftPartCount > 0) {
					TableSearch tableSearch = new TableSearch(i, j, true, leftPartCount);
					tableSearches.add(tableSearch);
				}

				int rightPartCount = Utility.countOccurrence(cell.get().getRightCell(), searchString);

				if (rightPartCount > 0) {
					TableSearch tableSearch = new TableSearch(i, j, false, rightPartCount);
					tableSearches.add(tableSearch);
				}
			}
		}

		return tableSearches;
	}

	public void editCell(int row, int col, boolean isLeftCell, String newString) {
		if (newString.contains(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING) || 
			newString.contains(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)) {
			
			return;
		}

		persistTable();
	}

	private void persistTable() {
		try (FileWriter writer = new FileWriter(this.tableFile)) {
			String tableString = 
				rowCells.stream()
				        .map((columnCells) ->
							     columnCells.stream()
										    .map((optionalCell) -> optionalCell.isPresent() ? optionalCell.get().toString() : "NULL")
						                    .collect(Collectors.joining(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING)))
		                .collect(Collectors.joining("\n"));

			writer.write(tableString);
			writer.flush();

		} catch (IOException e) {
			System.out.println("Data persistence failed.");
		}
	}

	private void parseTable() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(tableFile));
		rowCells = new ArrayList<>();

		String columnCellsStr;

		while ((columnCellsStr = bufferedReader.readLine()) != null && 
			    !columnCellsStr.trim().isEmpty()) {

			List<Optional<TableCell>> columnCells = new ArrayList<>();

			String[] unparsedColumnCells = extractUnparsedColumnCells(columnCellsStr);
			
			for (int i = 0, s = unparsedColumnCells.length; i < s; i++) {
	        	columnCells.add(extractParsedCell(unparsedColumnCells[i]));
            }

            rowCells.add(columnCells);
		}

		if (rowCells.size() == 0) {
			rowCells.add(Arrays.asList(Optional.empty()));
		}
	}

	private String[] extractUnparsedColumnCells(String columnCellsStr) {
		String[] unparsedCells;

		if (columnCellsStr.charAt(columnCellsStr.length() - 1) == TableService.TABLE_DELIMITER) {
			columnCellsStr += " ";
			unparsedCells = 
				columnCellsStr.split(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING);
			unparsedCells[unparsedCells.length - 1] = Utility.EMPTY_STRING;
		}
		else {
			unparsedCells = 
				columnCellsStr.split(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING);
		}

		return unparsedCells;
	}

	private Optional<TableCell> extractParsedCell(String unparsedCell) throws IOException {

		if (unparsedCell.isEmpty()) {
			return Optional.empty();
		}

		String[] cellArray = unparsedCell.split(CELL_DELIMITER + Utility.EMPTY_STRING);

		if (cellArray.length == 0) {
			return Optional.of(new TableCell(Utility.EMPTY_STRING, Utility.EMPTY_STRING));
		}
		else if (cellArray.length == 1) {
			return Optional.of(new TableCell(cellArray[0], Utility.EMPTY_STRING));
		}
		else {
			return Optional.of(new TableCell(cellArray[0], cellArray[1]));
		}
	}

	private void identifyTableDelimiterCountPerRow() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(tableFile));

		String strLine = Utility.EMPTY_STRING;
		int delimiterCount = 0;

		while((strLine = reader.readLine()) != null) {
			int tempCount = StringUtils.countMatches(strLine, TableService.TABLE_DELIMITER + Utility.EMPTY_STRING);

			if (delimiterCount == 0) {
				delimiterCount = tempCount;
			}
			else if (tempCount != delimiterCount) {
				reader.close();
				throw new IOException("Table delimiters per row are not equal.");
			}
		}

		reader.close();
	}

	private void checkCellDelimiterCountPerCell() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(tableFile));

		String strLine = Utility.EMPTY_STRING;

		while ((strLine = reader.readLine()) != null) {
			String[] cellStrings = strLine.split(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING);
			
			for (int i = 0, s = cellStrings.length; i < s; i++) {
				int delimiterCount = 
				StringUtils.countMatches(cellStrings[i], TableService.CELL_DELIMITER + Utility.EMPTY_STRING);
				
				if (delimiterCount > 1) {
					reader.close();
					throw new IOException("Detected multiple cell delimiter in a single cell.");
				} 
			}
		}

		reader.close();
	}

	private void checkNoCellDelimiterOnNonEmptyCell() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(tableFile));

		String strLine = Utility.EMPTY_STRING;

		while ((strLine = reader.readLine()) != null) {
			String[] cellStrings = strLine.split(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING);

			for (int i = 0, s = cellStrings.length; i < s; i++) {
				if (!cellStrings[i].isEmpty() && !cellStrings[i].contains(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)) {
					reader.close();
					throw new IOException("Found non-empty cell with no cell delimiter.");
				} 
			}
		}

		reader.close();
	}
}