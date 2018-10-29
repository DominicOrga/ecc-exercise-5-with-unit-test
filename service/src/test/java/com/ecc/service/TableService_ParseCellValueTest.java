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
			Utility.getResourcePath("table_service_parse_cell_value_test.txt");
		TableService tableService = new TableServiceImpl(resourcePath.get());	

		boolean isNull = tableService.isCellNull(0, 0);

		assertThat(isNull).isTrue();
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