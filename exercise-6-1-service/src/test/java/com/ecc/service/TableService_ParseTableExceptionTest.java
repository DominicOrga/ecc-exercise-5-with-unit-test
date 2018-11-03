package com.ecc.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.ecc.util.Utility;

import java.util.Optional;
import java.util.function.Supplier;

import java.io.IOException;
import java.io.FileNotFoundException;

public class TableService_ParseTableExceptionTest {

	@Test
	public void whenTableFilePathDoesNotExistThenThrowIOException() 
		throws IOException, FileNotFoundException {

		Throwable thrown = catchThrowable(() -> {
			TableService tableService = new TableServiceImpl("non-existing-file.txt");
		});

		assertThat(thrown).isInstanceOf(FileNotFoundException.class)
                          .hasMessageMatching("File not found.");
	}

	@Test
	public void whenTableDelimiterCountPerRowAreNotEqualThenThrowIOException() 
		throws IOException, FileNotFoundException {	

		Throwable thrown = catchThrowable(() -> {
			Optional<String> resourcePath = 
				Utility.getResourcePath("table_delimiter_count_per_row_are_not_equal.txt");
			TableService tableService = new TableServiceImpl(resourcePath.get());	
		});

		assertThat(thrown).isInstanceOf(IOException.class)
		                  .hasMessageMatching("Table delimiters per row are not equal.");
	}

	@Test
	public void whenCellDelimiterCountPerRowIsMoreThanOneThenThrowIOException() 
		throws IOException, FileNotFoundException {
			
		Throwable thrown = catchThrowable(() -> {
            Optional<String> resourcePath = 
            	Utility.getResourcePath("cell_delimiter_per_row_is_more_than_one.txt");
            TableService tableService = new TableServiceImpl(resourcePath.get());		
		});

		assertThat(thrown).isInstanceOf(IOException.class)
		                  .hasMessageMatching("Detected multiple cell delimiter in a single cell.");
	}

	@Test
	public void whenNoCellDelimiterOnNonEmptyCellThenThrowIOException() 
		throws IOException, FileNotFoundException {

		Throwable thrown = catchThrowable(() -> {
            Optional<String> resourcePath = 
            	Utility.getResourcePath("no_cell_delimiter_on_non_empty_cell.txt");
            TableService tableService = new TableServiceImpl(resourcePath.get());		
		});

		assertThat(thrown).isInstanceOf(IOException.class)
						  .hasMessageMatching("Found non-empty cell with no cell delimiter.");
	}
}