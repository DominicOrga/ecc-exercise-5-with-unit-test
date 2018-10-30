package com.ecc.service;

import static org.assertj.core.api.Assertions.*;

import com.ecc.util.Utility;

import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

import java.util.Optional;
import java.io.IOException;
import java.io.FileNotFoundException;

public class TableServiceTest {

	@Test
	public void whenGetTableAsStringThenNullValuesAreNULL() 
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
	@Ignore
	public void whenGetTableAsStringThenNonNullValuesArePersisted() 
		throws IOException, FileNotFoundException {
			
	}
}