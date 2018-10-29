package com.ecc.service;

import static org.assertj.core.api.Assertions.*;

import com.ecc.util.Utility;
import com.ecc.model.TableCell;

import org.junit.Test;
import org.junit.Before;

import java.util.Optional;
import java.io.IOException;
import java.io.FileNotFoundException;

public class TableService_ParseCellValueTest {
	
	TableService tableService;

	@Before
	public void parseTable() throws IOException, FileNotFoundException {
		Optional<String> resourcePath = Utility.getResourcePath("mock_table.txt");
		this.tableService = new TableServiceImpl(resourcePath.get());	
	}

	@Test
	public void whenNoCellDelimiterThenValueIsNull() {
		boolean isNull = this.tableService.isCellNull(0, 5);
		assertThat(isNull).isTrue();
	}

	@Test
	public void whenCellBothPartsAreEmptyThenBothPartValuesAreEmpty() {
		
	}

	@Test
	public void whenCellBothPartsAreNotEmptyThenBothPartValuesAreNotEmpty() {
		Optional<TableCell> cell = this.tableService.getCell(0, 0);
		assertThat(cell.get().getLeftCell()).isEqualTo("aaa");
		assertThat(cell.get().getRightCell()).isEqualTo("aaa");
	}

	@Test
	public void whenCellLeftPartIsEmptyThenLeftPartValueIsEmpty() {

	}

	@Test
	public void whenOnlyCellLeftPartIsEmptyThenRightPartValueIsNotEmpty() {

	}

	@Test
	public void whenCellRightPartIsEmptyThenRightPartValueIsEmpty() {

	}

	@Test
	public void whenOnlyCellRightPartIsEmptyThenLeftPartValueIsNotEmpty() {

	}
}