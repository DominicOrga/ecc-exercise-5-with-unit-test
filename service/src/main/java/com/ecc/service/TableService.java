package com.ecc.service;

import java.util.Optional;
import java.util.List;
import com.ecc.model.TableCell;
import com.ecc.model.TableSearch;

public interface TableService {
	char TABLE_DELIMITER = '/';
	char CELL_DELIMITER = ',';

	boolean isCellNull(int row, int col);
	Optional<TableCell> getCell(int row, int col);
	int getRowCount();
	int getColCount();
	String getTableAsString();
	List<TableSearch> search(String searchString);
	void editCell(int row, int col, boolean isLeftPart, String newString);
}