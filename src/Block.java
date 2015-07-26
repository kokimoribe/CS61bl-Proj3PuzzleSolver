public final class Block {

	final int length;
	final int width;
	final int area;
	
	final int startRow;
	final int startCol;
	final int endRow;
	final int endCol;
	final int myHashCode;
	final String myString;		
	
	public Block(int sr, int sc, int er, int ec) {
		startRow = sr;
		startCol = sc;
		endRow = er;
		endCol = ec;
		
		length = endRow - startRow + 1;
		width = endCol - startCol + 1;
		area = length * width;
		
		myString = String.format("%d %d %d %d", startRow, startCol, endRow, endCol);
		
		int temp = 0;
		String temp1 = myString.replaceAll("\\s+", "");
		try {
			temp = Integer.parseInt(temp1);
		}
		catch (NumberFormatException e) {
			temp = temp1.hashCode();
		}
		finally {
			myHashCode = temp;
		}
	}

	public String toString() {
		return myString;
	}
	
	@Override
	public int hashCode() {	
		return myHashCode;
	}
	
	@Override
	public boolean equals(Object o) {
		
		Block b = (Block) o;
		return (startRow == b.startRow && startCol == b.startCol) && (endRow == b.endRow && endCol == b.endCol);
	}
}