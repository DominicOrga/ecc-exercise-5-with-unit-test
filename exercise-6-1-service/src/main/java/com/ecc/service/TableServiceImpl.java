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
import java.util.Map;
import java.util.Comparator;
import java.util.Random;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Supplier;

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

	public boolean isCellOutOfBounds(int row, int col) {
		return row < 0 || col < 0 || row >= getRowCount() || col >= getColCount();
	}

	public boolean isCellNull(int row, int col) {
		Optional<TableCell> cell = rowCells.get(row).get(col);
		return !cell.isPresent();
	}

	public Optional<TableCell> getCell(int row, int col) {
		if (isCellOutOfBounds(row, col)) {
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
									  .map((optionalCell) -> optionalCell.isPresent() ? 
									      optionalCell.get().toString(TableService.CELL_DELIMITER + Utility.EMPTY_STRING) : "NULL")
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

	public void editCell(int row, int col, boolean isLeftPart, String newString) {
		if (newString.contains(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING) || 
			newString.contains(TableService.CELL_DELIMITER + Utility.EMPTY_STRING) || 
			isCellOutOfBounds(row, col)) {
			
			return;
		}

		Optional<TableCell> cell = this.rowCells.get(row).get(col);

		if (cell.isPresent()) {
			if (isLeftPart) {
				cell.get().setLeftCell(newString);
			}
			else {
				cell.get().setRightCell(newString);
			}
		}

		persistTable();
	}

	public void addCell(int row, int col, String leftString, String rightString) {
		if (isCellOutOfBounds(row, col) || !isCellNull(row, col)) {
			return;
		}

		this.rowCells.get(row).set(col, Optional.of(new TableCell(leftString, rightString)));

		persistTable();
	}

	public void addRow() {
		List<Optional<TableCell>> columnCells = new ArrayList<>();

		for (int j = 0, s = getColCount(); j < s; j++) {
			columnCells.add((j == 0) ? 
				Optional.of(new TableCell(generateRandomString(), generateRandomString())) : 
				Optional.empty());
		}

		this.rowCells.add(columnCells);

		persistTable();
	}

	public void sortRow(int row, boolean isAscending) {
		if (row >= getRowCount() || row < 0) {
			return;
		}

		Map<Boolean, List<Optional<TableCell>>> presentCells = 
			this.rowCells.get(row).stream().collect(Collectors.partitioningBy(Optional::isPresent));

		List<Optional<TableCell>> sortedPresentCells = 
			presentCells.get(true)
			            .stream()
			            .map(Optional::get)
			            .sorted(isAscending ? Comparator.naturalOrder() : Comparator.reverseOrder())
	                    .map(innerCell -> Optional.ofNullable(innerCell))
	                    .collect(Collectors.toList());
   
       	this.rowCells.set(
       		row, 
       		Stream.concat(sortedPresentCells.stream(), presentCells.get(false).stream())
       		      .collect(Collectors.toList())
   		);

       	persistTable();
	}
	
	public void resetTable(int rowCount, int colCount) {
		if (rowCount <= 0 || colCount <= 0) {
			return;
		}

		Supplier<List<Optional<TableCell>>> rowCellSupplier = 
		() -> {
			return Stream.generate(() -> 
						     Optional.of(new TableCell(generateRandomString(), generateRandomString())))
						 .limit(colCount)
						 .collect(Collectors.toList());
		};

		this.rowCells = Stream.generate(rowCellSupplier)
							  .limit(rowCount)
							  .collect(Collectors.toList());

		persistTable();
	}

	private void persistTable() {
		try (FileWriter writer = new FileWriter(this.tableFile)) {
			String tableString = 
				rowCells.stream()
				        .map((columnCells) ->
							     columnCells.stream()
										    .map((optionalCell) -> optionalCell.isPresent() ? 
										        optionalCell.get().toString(TableService.CELL_DELIMITER + Utility.EMPTY_STRING) : Utility.EMPTY_STRING)
						                    .collect(Collectors.joining(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING)))
		                .collect(Collectors.joining("\n"));

			writer.write(tableString);
			writer.flush();
			writer.close();

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
				if (cellStrings[i] != null && 
					!cellStrings[i].isEmpty() && 
					!cellStrings[i].contains(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)) {
					
					reader.close();
					throw new IOException("Found non-empty cell with no cell delimiter.");
				} 
			}
		}

		reader.close();
	}

	private String generateRandomString() {
		StringBuilder sb = new StringBuilder();

		Random rnd = new Random();

		for (int i = 0; i < 5; i++) {

			int ascii = -1;

			do {
				// ascii character between 0 ~ 31 represent symbols not found in the keyboard. Also, 
				// 127 ascii is an empty character. Hence, allowed ascii range is between 32 ~ 126 
				// only.
				ascii = rnd.nextInt(127 - 32) + 32; 
			} while (ascii == TABLE_DELIMITER || ascii == CELL_DELIMITER);
			

			sb.append((char) ascii);	
		}

		return sb.toString();
	}
}