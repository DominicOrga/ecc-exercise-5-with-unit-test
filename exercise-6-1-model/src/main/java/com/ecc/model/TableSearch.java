package com.ecc.model;

public class TableSearch {

	private int row;
	private int col;
	private boolean isLeftPart;
	private int matchCount;

	public TableSearch(int row, int col, boolean isLeftPart, int matchCount) {
		this.row = row;
		this.col = col;
		this.isLeftPart = isLeftPart;
		this.matchCount = matchCount;
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public boolean isLeftPart() {
		return this.isLeftPart;
	}

	public int getMatchCount() {
		return this.matchCount;
	}
}