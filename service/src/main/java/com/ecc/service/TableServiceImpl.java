package com.ecc.service;

import com.ecc.util.Utility;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class TableServiceImpl implements TableService {

	private int tableDelimitersPerRow;
	private File tableFile;

	public TableServiceImpl(String tablePath) throws IOException {
		tableFile = new File(tablePath);

		if (!tableFile.exists()) {
			throw new IOException("File not found.");
		}

		identifyTableDelimiterCountPerRow();
		checkCellDelimiterCountPerCell();
	};


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

			System.out.println(Arrays.toString(strSplit));
			
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