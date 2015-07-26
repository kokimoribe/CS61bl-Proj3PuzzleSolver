import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Tray {
		
	int length;
	int width;
	Configuration myConfig;
	Tray previous;
	
	//true if cell is part of block, false if empty space
	boolean[][] booleanOccupancy;
	
	
	//a list of moves that this tray has made so far
	private LinkedList<Move> soFar;
	
	public Tray(Configuration c, int l, int w) {
		//initialization				
		myConfig = c;						
		length = l;
		width = w;
		booleanOccupancy = new boolean[length][width];
		soFar = new LinkedList<Move>();

		for (Block b : c.config()) {
			//assign entire block area with boolean
			for (int row = b.startRow; row <= b.endRow; row++) {
				Arrays.fill(booleanOccupancy[row], b.startCol, b.endCol + 1, true);
			}
		}		
	}
	
	public Tray(Tray t) {		
		//copy myConfig
		myConfig = new Configuration(t.myConfig);
		
		//copy length/width
		length = t.length;
		width = t.width;
		
		//initialize array and prepare for copy
		booleanOccupancy = new boolean[length][];
		
		//copy array
		for (int row = 0; row < length; row++) {
			booleanOccupancy[row] = Arrays.copyOf(t.booleanOccupancy[row], width);
		}
		
		soFar = new LinkedList<Move>(t.soFar);
	}
	
	public boolean undoMove(boolean text) {
		if (soFar.size() == 0) {
			return false;
		}
		undoMoveHelper(soFar.removeLast(), text);
		return true;
	}
	
	private void undoMoveHelper(Move m, boolean text) {
		
		if (text) {
			System.out.println("undoing move: " + m);
		}
		//remove the end block
		myConfig.config().remove(m.end);
		//add the start block
		myConfig.config().add(m.start);
		
		myConfig.updateHash();
		
		//for vertical movement
		if (m.direction == Move.UP || m.direction == Move.DOWN) {
			//the old row will be turned to true
			Arrays.fill(booleanOccupancy[m.oldRow], m.start.startCol, m.start.endCol + 1, true);
			//the new row will be turned to false
			Arrays.fill(booleanOccupancy[m.newRow], m.start.startCol, m.start.endCol + 1, false);
		}
		
		//for horizontal movement
		else {
			for (int row = m.start.startRow; row <= m.start.endRow; row++) {
				//the old column will be turned to true
				booleanOccupancy[row][m.oldCol] = true;
				//the new column will turned to false
				booleanOccupancy[row][m.newCol] = false;
			}
		}
	}
	//Tray with Move m taken place
	public void move(Move m) {
		
		if (m == null) {
			return;
		}
		//update with the move m
		update(m);
	}
	
	//update the tray's attributes when Move m is taken place
	private void update(Move m) {
		
		//remove the start block
		myConfig.config().remove(m.start);
		//add the end block
		myConfig.config().add(m.end);
		
		myConfig.updateHash();
		
		//for vertical movement
		if (m.direction == Move.UP || m.direction == Move.DOWN) {
			//the old row will be turned to false
			Arrays.fill(booleanOccupancy[m.oldRow], m.start.startCol, m.start.endCol + 1, false);
			//the new row that is going to occupy the row of blank space will be turned to true
			Arrays.fill(booleanOccupancy[m.newRow], m.start.startCol, m.start.endCol + 1, true);
		}
		
		//for horizontal movement
		else {
			for (int row = m.start.startRow; row <= m.start.endRow; row++) {
				//the old column will be turned to false
				booleanOccupancy[row][m.oldCol] = false;
				//the new column that is going to occupy the col of blank space will be turned to true
				booleanOccupancy[row][m.newCol] = true;
			}
		}
	}
		
	public HashSet<Move> possibleMoves() {
		
		//make a list of moves that will be returned at the end
		HashSet<Move> moves = new HashSet<Move>();
		
		//make an iterator over the list of blocks
		Iterator<Block> blockIter = myConfig.config().iterator();
		Block b;
		int startR;
		int startC;
		int endR;
		int endC;
				
		while (blockIter.hasNext()) {
			b = blockIter.next();
			startR = b.startRow;
			startC = b.startCol;
			endR = b.endRow;
			endC = b.endCol;
			
			//check if block can move up, down, left, or right
			
			//up
			//starting row must not be the first row of the tray
			if (startR > 0) {
				//if the row above the block is unoccupied
				if (rowUnoccupied(startR - 1, startC, endC)) {
					//add the move to the list moves
					moves.add(new Move(b, new Block(startR - 1, startC, endR - 1, endC), Move.UP));
				}
			}
			
			//down
			//ending row must not be the last row of the tray
			if (endR < length - 1) {
				//if the row below the block is unoccupied
				if (rowUnoccupied(endR + 1, startC, endC)) {
					//add the move to the list moves
					moves.add(new Move(b, new Block(startR + 1, startC, endR + 1, endC), Move.DOWN));
				}
			}
			
			//left
			//starting col must not be the first col of the tray
			if (startC > 0) {
				if (colUnoccupied(startC - 1, startR, endR)) {
					//add the move to the list moves
					moves.add(new Move(b, new Block(startR, startC - 1, endR, endC - 1), Move.LEFT));
				}
			}
			
			//right
			//ending col must not be the last col of the tray
			if (endC < width - 1) {
				//add the move to the list moves
				if (colUnoccupied(endC + 1, startR, endR)) {
					moves.add(new Move(b, new Block(startR, startC + 1, endR, endC + 1), Move.RIGHT));
				}
			}
		}
		
		return moves;		
	}
	
	//checks if the row is unoccupied from col start to stop
	private boolean rowUnoccupied(int row, int start, int stop) {
		for (int col = start; col <= stop; col++) {
			if (booleanOccupancy[row][col]) {
				return false;
			}
		}
		
		return true;
	}
	
	//checks if the col is unoccupied from row start to stop
	private boolean colUnoccupied(int col, int start, int stop) {
		for (int row = start; row <= stop; row++) {
			if (booleanOccupancy[row][col]) {
				return false;
			}
		}
		
		return true;
	}
	
	public void addMove(Move m) {
		soFar.add(m);
	}
	
	public LinkedList<Move> getsoFar() {
		return soFar;
	}
	public void print() {
		
		int[][] blocks = new int[length][width];
		int count = 1;
		for (Block b: myConfig.config()) {
			for (int row = b.startRow; row <= b.endRow; row++) {
				for (int col = b.startCol; col <= b.endCol; col++) {
					blocks[row][col] = count;
				}
			}
			count++;
		}
		
		for (int[] ar : blocks) {
			for (int i : ar) {
				System.out.print(i + " ");
			}
			System.out.print("\n");
		}
	}
	
	public boolean isOK(Tray init) {
		if (this.myConfig.existsIn(init.myConfig)) {
			return true;
		}
		
		return false;
	}
}
