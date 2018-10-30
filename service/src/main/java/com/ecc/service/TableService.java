package com.ecc.service;

import java.util.Optional;
import com.ecc.model.TableCell;

public interface TableService {
	char TABLE_DELIMITER = '/';
	char CELL_DELIMITER = ',';

	boolean isCellNull(int row, int col);
	Optional<TableCell> getCell(int row, int col);
	int getRowCount();
	int getColCount();
	String getTableAsString();
}