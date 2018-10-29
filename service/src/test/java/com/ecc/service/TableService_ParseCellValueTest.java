package com.ecc.service;

import static org.assertj.core.api.Assertions.*;

import com.ecc.util.Utility;

import org.junit.Test;

import java.util.Optional;
import java.io.IOException;
import java.io.FileNotFoundException;

public class TableService_ParseCellValueTest {
	
	@Test
	public void whenNoCellDelimiterThenCellIsNull() throws IOException, FileNotFoundException {
		Optional<String> resourcePath = 
			Utility.getResourcePath("parse_cell_value_test.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());	

		boolean isNull1 = tableService.isCellNull(0, 0);
		boolean isNull2 = tableService.isCellNull(1, 1);
		boolean isNull3 = tableService.isCellNull(2, 1);

		assertThat(isNull1).isTrue();
		assertThat(isNull2).isTrue();
		assertThat(isNull3).isTrue();
	}

	@Test
	public void whenCellBothPartsAreEmptyThenBothPartsAreEmpty() {

	}

	@Test
	public void whenCellBothPartsAreNotEmptyThenBothPartsAreNotEmpty() {

	}

	@Test
	public void whenCellLeftPartIsEmptyThenLeftPartIsEmpty() {

	}

	@Test
	public void whenOnlyCellLeftPartIsEmptyThenRightPartIsNotEmpty() {

	}

	@Test
	public void whenCellRightPartIsEmptyThenRightPartIsEmpty() {

	}

	@Test
	public void whenOnlyCellRightPartIsEmptyThenLeftPartIsNotEmpty() {

	}
}