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
		Optional<String> resourcePath = Utility.getResourcePath("parse_cell_value_test.txt");
		this.tableService = new TableServiceImpl(resourcePath.get());	
	}

	@Test
	public void whenNoCellDelimiterThenValueIsNull() {
		boolean isNull1 = this.tableService.isCellNull(0, 0);
		boolean isNull2 = this.tableService.isCellNull(1, 1);
		boolean isNull3 = this.tableService.isCellNull(2, 1);

		assertThat(isNull1).isTrue();
		assertThat(isNull2).isTrue();
		assertThat(isNull3).isTrue();
	}

	@Test
	public void whenCellBothPartsAreEmptyThenBothPartValuesAreEmpty() {
		
	}

	@Test
	public void whenCellBothPartsAreNotEmptyThenBothPartValuesAreNotEmpty() {
		Optional<TableCell> cell = this.tableService.getCell(0, 1);

		assertThat(cell.get().getLeftCell()).isEqualTo("bbb");
		assertThat(cell.get().getRightCell()).isEqualTo("ccc");
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