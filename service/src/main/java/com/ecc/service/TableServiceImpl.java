package com.ecc.service;

import com.ecc.util.Utility;
import com.ecc.model.TableCell;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

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

		String[] innerCellArray = unparsedCell.split(CELL_DELIMITER + Utility.EMPTY_STRING);

		if (innerCellArray.length == 1) {
			return Optional.of(new TableCell(innerCellArray[0], Utility.EMPTY_STRING));
		}
		else {
			return Optional.of(new TableCell(innerCellArray[0], innerCellArray[1]));
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