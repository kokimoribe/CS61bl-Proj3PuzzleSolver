
public class Move {
	public static final byte UP = 1;
	public static final byte DOWN = 2;
	public static final byte LEFT = 3;
	public static final byte RIGHT = 4;
	
	Block start;
	Block end;
	final int myHashCode;
	final byte direction;
	
	final int newRow;
	final int newCol;
	final int oldRow;
	final int oldCol;
	String myString;
	
	public Move(Block s, Block e, byte d) {
		start = s;
		end = e;
		direction = d;
		
		myString = String.format("%d %d %d %d", s.startRow, s.startCol, e.startRow, e.startCol);
		
		int temp = 0;
		String temp1 = myString.replaceAll("\\s+", "");
		try {
			temp = Integer.parseInt(temp1);
		}
		
		catch (NumberFormatException err) {
			temp = temp1.hashCode();			
		}
		
		finally {
			myHashCode = temp;
		}
		
		switch(direction) {
			case UP:	newRow = end.startRow;
							oldRow = start.endRow;
							newCol = 0;
							oldCol = 0;					
							break;
			case DOWN:	newRow = end.endRow;
							oldRow = start.startRow;
							newCol = 0;
							oldCol = 0;
							break;
			case LEFT:	newCol = end.startCol;
							oldCol = start.endCol;
							newRow = 0;
							oldRow = 0;
							break;
			case RIGHT:	newCol = end.endCol;
							oldCol = start.startCol;
							newRow = 0;
							oldRow = 0;
							break;
						
			default:	newRow = 0;
						oldRow = 0;
						newCol = 0;
						oldCol = 0;
		}
	}
	
	public int hashCode() {
		return myHashCode;
	}
	
	public String toString() {
		return myString;
	}
}
