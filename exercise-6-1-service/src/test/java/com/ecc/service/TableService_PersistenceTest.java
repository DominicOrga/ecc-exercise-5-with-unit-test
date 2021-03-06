package com.ecc.service;

import static org.assertj.core.api.Assertions.*;

import com.ecc.util.Utility;
import com.ecc.model.TableCell;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;
import org.apache.commons.io.FileUtils;

import java.util.Optional;
import java.util.function.Consumer;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public class TableService_PersistenceTest {

	String fileCache;
	Optional<String> resourcePath;
	TableService tableService;

	@Before
	public void setupTableService() throws IOException, FileNotFoundException {
		this.resourcePath = Utility.getResourcePath("persistence_test_table.txt");
		this.fileCache = FileUtils.readFileToString(new File(this.resourcePath.get()), "UTF-8");
		this.tableService = new TableServiceImpl(this.resourcePath.get());
	}

	@Test
	public void givenStringWithTableDelimiterWhenEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(0, 0, true, "abcde/");

		assertOnTableBufferThenTableFilePersistence(() -> {
			Optional<TableCell> cell = this.tableService.getCell(0, 0);
			assertThat(cell.get().getLeftCell()).isEqualTo("a");
		});
	}

	@Test
	public void givenStringWithCellDelimiterWhenEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(0, 0, true, "ab,cde");

		assertOnTableBufferThenTableFilePersistence(() -> {
			Optional<TableCell> cell = this.tableService.getCell(0, 0);
			assertThat(cell.get().getLeftCell()).isEqualTo("a");
		});		
	}

	@Test
	public void givenValidStringWhenEditCellThenPersistValue() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(0, 1, true, "abcde"); 

		assertOnTableBufferThenTableFilePersistence(() -> {
			Optional<TableCell> cell = this.tableService.getCell(0, 1);
			assertThat(cell.get().getLeftCell()).isEqualTo("abcde");
		});
	}

	@Test
	public void givenNullCellWhenEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(2, 1, true, "abcde");

		assertOnTableBufferThenTableFilePersistence(() -> {
			Optional<TableCell> cell = this.tableService.getCell(2, 1);
			assertThat(cell.isPresent()).isFalse();
		});
	}

	@Test
	public void whenAddRowThenAddRowWithValueOnFirstColOnly() 
		throws IOException, FileNotFoundException {

		int rowCount = this.tableService.getRowCount();
		this.tableService.addRow();

		assertOnTableBufferThenTableFilePersistence(() -> {
			int newRowCount = this.tableService.getRowCount();
			assertThat(newRowCount).isEqualTo(rowCount + 1);
			assertThat(this.tableService.isCellNull(rowCount, 0)).isFalse();
			assertThat(this.tableService.isCellNull(rowCount, 1)).isTrue();
			assertThat(this.tableService.isCellNull(rowCount, 2)).isTrue();
		});
	}

	@Test
	public void givenANullCellWhenAddCellThenAdd()
		throws IOException, FileNotFoundException {

		this.tableService.addCell(2, 1, "leftString", "rightString");

		assertOnTableBufferThenTableFilePersistence(() -> {
			assertThat(this.tableService.isCellNull(2, 1)).isFalse();
		});
	}

	@Test
	public void givenANonNullCellWhenAddCellTheDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.addCell(0, 0, "leftString", "rightString");

		assertOnTableBufferThenTableFilePersistence(() -> {
			Optional<TableCell> cell = this.tableService.getCell(0, 0);
			assertThat(cell.get().getLeftCell()).isNotEqualTo("leftString");
			assertThat(cell.get().getRightCell()).isNotEqualTo("rightString");
		});	
	}

	@Test
	public void sortRowAscending() 
		throws IOException, FileNotFoundException {

		this.tableService.sortRow(0, true);

		assertOnTableBufferThenTableFilePersistence(() -> {
			Optional<TableCell> col0 = this.tableService.getCell(0, 0);
			Optional<TableCell> col1 = this.tableService.getCell(0, 1);
			Optional<TableCell> col2 = this.tableService.getCell(0, 2); 
			Optional<TableCell> col3 = this.tableService.getCell(0, 3); 

			assertThat(col0.get().toString(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)).isEqualTo("a,a");
			assertThat(col1.get().toString(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)).isEqualTo("bb,bb");
			assertThat(col2.get().toString(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)).isEqualTo("ccc,ccc");
			assertThat(col3.isPresent()).isFalse();	
		});			
	}

	@Test
	public void sortRowDescending() 
		throws IOException, FileNotFoundException {

		this.tableService.sortRow(0, false);
	
		assertOnTableBufferThenTableFilePersistence(() -> {
			Optional<TableCell> col0 = this.tableService.getCell(0, 0);
			Optional<TableCell> col1 = this.tableService.getCell(0, 1);
			Optional<TableCell> col2 = this.tableService.getCell(0, 2); 
			Optional<TableCell> col3 = this.tableService.getCell(0, 3); 

			assertThat(col0.get().toString(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)).isEqualTo("ccc,ccc");
			assertThat(col1.get().toString(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)).isEqualTo("bb,bb");
			assertThat(col2.get().toString(TableService.CELL_DELIMITER + Utility.EMPTY_STRING)).isEqualTo("a,a");
			assertThat(col3.isPresent()).isFalse();		
		});				
	}

	@Test
	public void whenResetTableThenResetTableWithRandomValues() 
		throws IOException, FileNotFoundException {

		this.tableService.resetTable(2, 2);

		assertOnTableBufferThenTableFilePersistence(() -> {
			Optional<TableCell> cell00 = this.tableService.getCell(0, 0);
			Optional<TableCell> cell01 = this.tableService.getCell(0, 1);
			Optional<TableCell> cell10 = this.tableService.getCell(1, 0); 
			Optional<TableCell> cell11 = this.tableService.getCell(1, 1); 

			assertThat(cell00.isPresent()).isTrue();
			assertThat(cell01.isPresent()).isTrue();
			assertThat(cell10.isPresent()).isTrue();
			assertThat(cell11.isPresent()).isTrue();	
		});		
	}

	@Test
	public void givenLessThanOneRowOrColWhenResetTableThenDoNothing() 
		throws IOException, FileNotFoundException {
		
		this.tableService.resetTable(0, 0);

		assertThat(tableService.getRowCount()).isEqualTo(3);
		assertThat(tableService.getColCount()).isEqualTo(4);

		assertOnTableBufferThenTableFilePersistence(() -> {
			assertThat(tableService.getRowCount()).isEqualTo(3);
			assertThat(tableService.getColCount()).isEqualTo(4);
		});	
	}

	@After
	public void RestoreTable() throws IOException {
		FileUtils.writeStringToFile(new File(this.resourcePath.get()), fileCache);		
	}

	public void assertOnTableBufferThenTableFilePersistence(Runnable runnable) 
		throws IOException, FileNotFoundException{
		
		runnable.run();
		this.tableService = new TableServiceImpl(this.resourcePath.get());
		runnable.run();
	}
}