package com.ecc.service;

import static org.assertj.core.api.Assertions.*;

import com.ecc.util.Utility;
import com.ecc.model.TableCell;
import com.ecc.model.TableSearch;

import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

import java.util.Optional;
import java.util.List;
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

	@Test
	public void givenASearchStringwhenMatchesFoundThenReturnCountandCoordinates()
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("mock_table.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		List<TableSearch> tableSearches = tableService.search("ee");

		TableSearch testCase1 = tableSearches.get(0);
		TableSearch testCase2 = tableSearches.get(1);

		assertThat(testCase1.getRow()).isEqualTo(0);
		assertThat(testCase1.getCol()).isEqualTo(4);
		assertThat(testCase1.isLeftPart()).isTrue();
		assertThat(testCase1.getMatchCount()).isEqualTo(2);

		assertThat(testCase2.getRow()).isEqualTo(4);
		assertThat(testCase2.getCol()).isEqualTo(2);
		assertThat(testCase2.isLeftPart()).isFalse();
		assertThat(testCase2.getMatchCount()).isEqualTo(2);		
	}

	@Test
	public void givenAStringWithATableDelimiterWhenEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("edit_cell_test_table.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		tableService.editCell(0, 0, true, "abcde/");

		tableService = new TableServiceImpl(resourcePath.get());
		Optional<TableCell> cell = tableService.getCell(0, 0);
		assertThat(cell.get().getLeftCell()).isEqualTo("a");
	}

	@Test
	public void givenAStringWithACellDelimiterWhenEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("edit_cell_test_table.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		tableService.editCell(0, 0, true, "ab,cde");

		tableService = new TableServiceImpl(resourcePath.get());
		Optional<TableCell> cell = tableService.getCell(0, 0);
		assertThat(cell.get().getLeftCell()).isEqualTo("a");			
	}

	@Test
	public void givenValidStringWhenEditCellThenUpdateValueAndPersist() 
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("edit_cell_test_table.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		tableService.editCell(0, 1, true, "abcde");

		tableService = new TableServiceImpl(resourcePath.get());
		Optional<TableCell> cell = tableService.getCell(0, 1);
		assertThat(cell.get().getLeftCell()).isEqualTo("abcde");
	}

	@Test
	public void givenANullCellWhenEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		Optional<String> resourcePath = Utility.getResourcePath("edit_cell_test_table.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());

		tableService.editCell(2, 1, true, "abcde");

		tableService = new TableServiceImpl(resourcePath.get());
		Optional<TableCell> cell = tableService.getCell(2, 1);

		assertThat(cell.isPresent()).isFalse();
	}
}