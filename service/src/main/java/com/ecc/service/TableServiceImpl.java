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
	};

	public boolean isCellNull(int row, int col) {
		return true;
	}

	private void parseTable() throws IOException {

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
			String[] strSplit = strLine.split(TableService.TABLE_DELIMITER + Utility.EMPTY_STRING);
			
			for (int i = 0, s = strSplit.length; i < s; i++) {
				int delimiterCount = StringUtils.countMatches(strSplit[i], TableService.CELL_DELIMITER + Utility.EMPTY_STRING);
				
				if (delimiterCount > 1) {
					reader.close();
					throw new IOException("Detected multiple cell delimiter in a single cell.");
				} 
			}
		}

		reader.close();
	}
}