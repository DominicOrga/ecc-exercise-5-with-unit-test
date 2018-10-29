package com.ecc.service;

public interface TableService {
	char TABLE_DELIMITER = '/';
	char CELL_DELIMITER = ',';

	boolean isCellNull(int row, int col);
}