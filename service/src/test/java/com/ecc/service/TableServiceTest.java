package com.ecc.service;

import static org.assertj.core.api.Assertions.*;

import com.ecc.util.Utility;
import com.ecc.model.TableCell;

import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

import java.util.Optional;
import java.io.IOException;
import java.io.FileNotFoundException;

public class TableServiceTest {

	@Test
	public void givenANullTableWhenGetTableAsStringThenValuesAreNULL() 
		throws IOException, FileNotFoundException {
		
		Optional<String> resourcePath = Utility.getResourcePath("pure_null_table.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		String tableString = tableService.getTableAsString();
		String expectedValue = "NULL/NULL/NULL/NULL\n" +
							   "NULL/NULL/NULL/NULL\n" +
							   "NULL/NULL/NULL/NULL";

		assertThat(tableString).isEqualTo(expectedValue);
	}

	@Test
	public void givenANonNullTableWhenGetTableAsStringThenValuesArePersisted() 
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("non_null_table.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		String tableString = tableService.getTableAsString();
		String expectedValue = "a,a/aa,aa/aaa,aaa\n" +
							   "b,b/bb,bb/bbb,bbb\n" +
							   "c,c/cc,cc/ccc,ccc";

		assertThat(tableString).isEqualTo(expectedValue);
	}

	@Test
	public void givenAnEmptyFileWhenGetRowCountThenReturnOne() 
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("empty_file.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		int rowCount = tableService.getRowCount();

		assertThat(rowCount).isEqualTo(1);
	}

	@Test
	public void givenAnEmptyFileWhenGetColCountThenReturnOne() 
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("empty_file.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		int rowCount = tableService.getColCount();

		assertThat(rowCount).isEqualTo(1);			
	}

	@Test
	public void givenOutOfBoundsRowOrColWhenGetCellThenReturnNull() 
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("empty_file.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		Optional<TableCell> testCase1 = tableService.getCell(-1, 0);
		Optional<TableCell> testCase2 = tableService.getCell(0, -1);
		Optional<TableCell> testCase3 = tableService.getCell(10, 0);
		Optional<TableCell> testCase4 = tableService.getCell(0, 10);

		assertThat(testCase1.isPresent()).isFalse();
		assertThat(testCase2.isPresent()).isFalse();
		assertThat(testCase3.isPresent()).isFalse();
		assertThat(testCase4.isPresent()).isFalse();
	}
}