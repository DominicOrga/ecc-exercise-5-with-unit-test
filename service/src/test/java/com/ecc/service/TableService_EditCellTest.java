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

public class TableService_EditCellTest {

	Optional<String> resourcePath;
	TableService tableService;

	@Before
	public void setupTableService() throws IOException, FileNotFoundException {
		this.resourcePath = Utility.getResourcePath("edit_cell_test_table.txt");
		this.tableService = new TableServiceImpl(this.resourcePath.get());
	}

	@Test
	public void whenStringHasTableDelimiterThenDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(0, 0, true, "abcde/");

		this.tableService = new TableServiceImpl(this.resourcePath.get());
		Optional<TableCell> cell = this.tableService.getCell(0, 0);
		assertThat(cell.get().getLeftCell()).isEqualTo("a");
	}

	@Test
	public void whenStringHasCellDelimiterEditCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(0, 0, true, "ab,cde");

		this.tableService = new TableServiceImpl(this.resourcePath.get());
		Optional<TableCell> cell = this.tableService.getCell(0, 0);
		assertThat(cell.get().getLeftCell()).isEqualTo("a");			
	}

	@Test
	public void whenStringIsValidThenUpdateValueAndPersist() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(0, 1, true, "abcde");

		this.tableService = new TableServiceImpl(this.resourcePath.get());
		Optional<TableCell> cell = this.tableService.getCell(0, 1);
		assertThat(cell.get().getLeftCell()).isEqualTo("abcde");
	}

	@Test
	public void whenEditingNullCellThenDoNothing() 
		throws IOException, FileNotFoundException {

		this.tableService.editCell(2, 1, true, "abcde");

		this.tableService = new TableServiceImpl(this.resourcePath.get());
		Optional<TableCell> cell = this.tableService.getCell(2, 1);

		assertThat(cell.isPresent()).isFalse();
	}
}