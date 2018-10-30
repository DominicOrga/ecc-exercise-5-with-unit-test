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

		this.tableService = new TableServiceImpl(this.resourcePath.get());
		Optional<TableCell> cell = this.tableService.getCell(0, 0);
		assertThat(cell.get().getLeftCell()).isEqualTo("a");
	}

	@Test
	public void givenStringWithCellDelimiterWhenEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(0, 0, true, "ab,cde");

		this.tableService = new TableServiceImpl(this.resourcePath.get());
		Optional<TableCell> cell = this.tableService.getCell(0, 0);
		assertThat(cell.get().getLeftCell()).isEqualTo("a");			
	}

	@Test
	public void givenValidStringWhenEditCellThenPersistValue() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(0, 1, true, "abcde");

		this.tableService = new TableServiceImpl(this.resourcePath.get());
		Optional<TableCell> cell = this.tableService.getCell(0, 1);
		assertThat(cell.get().getLeftCell()).isEqualTo("abcde");
	}

	@Test
	public void givenNullCellWhenEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(2, 1, true, "abcde");

		this.tableService = new TableServiceImpl(this.resourcePath.get());
		Optional<TableCell> cell = this.tableService.getCell(2, 1);

		assertThat(cell.isPresent()).isFalse();
	}

	@After
	public void RestoreTable() throws IOException {
		FileUtils.writeStringToFile(new File(this.resourcePath.get()), fileCache);		
	}
}