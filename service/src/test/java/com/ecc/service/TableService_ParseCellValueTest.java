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
		Optional<TableCell> cell = this.tableService.getCell(2, 5);

		assertThat(cell.get().getLeftCell()).isEmpty();
		assertThat(cell.get().getRightCell()).isEmpty();
	}

	@Test
	public void whenCellBothPartsAreNotEmptyThenBothPartValuesAreNotEmpty() {
		Optional<TableCell> cell = this.tableService.getCell(0, 0);

		assertThat(cell.get().getLeftCell()).isEqualTo("aaa");
		assertThat(cell.get().getRightCell()).isEqualTo("aaa");
	}

	@Test
	public void whenOnlyCellLeftPartIsEmptyThenRightPartValueIsNotEmpty() {
		Optional<TableCell> cell = this.tableService.getCell(1, 0);

		assertThat(cell.get().getLeftCell()).isEmpty();
		assertThat(cell.get().getRightCell()).isEqualTo("fff");
	}

	@Test
	public void whenOnlyCellRightPartIsEmptyThenLeftPartValueIsNotEmpty() {
		Optional<TableCell> cell = this.tableService.getCell(0, 4);

		assertThat(cell.get().getLeftCell()).isEqualTo("eee");
		assertThat(cell.get().getRightCell()).isEmpty();
	}
}