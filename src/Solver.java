import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;	
import java.util.StringTokenizer;
import java.util.HashSet;
/**
 * Basic Structure of how Solver.java works
 * Constructor:
 * 	Convert filenames for initial and goal trays into Configurations
 * 
 * solve()
 * 	Create a new tray from initial configuration.
 * 
 * solverHelper()
 * 	check if the blocks in goal configuration actually exist in the initial configuration (done by checking area of individual blocks)
 *  check if the initial configuration has reached goal
 *  start a while loop:
 *  	get possible moves from tray
 *  	get the configuration as a result of doing each move
 *  	if this configuration has been seen before, skip this configuration and move on to next move
 * 		if this configuration meets the goal, then we can break from loop
 *  	if this configuration hasn't been seen before and doesn't meet the goal, add the move to evaluatedPossibleMoves, and add the configuration to previousConfigs
 *  	
 *  	after going through every move in possibleMoves, we can now go through our updated evaluatedPossibleMoves
 *  	if evaluatedPossibleMoves is NOT EMPTY,
 *  		get an iterator out of evaluatedPossibleMoves
 *  		grab the first move and set it to nextMove (this will be the next move we will make)
 *  		store the rest of the evaluatedPossibleMoves into a HashMap (restMoves), where configuration is the key and the iterator is the value
 *  			this keeps track of the remaining valid possible moves for each configuration that we've visited
 *  
 *  	if evaluatedPossibleMoves IS EMPTY
 *  		this means that we did not find any moves that can produce a new configuration
 *			start a while loop  
 *  			undo the last move done on the tray
 *  			now that we're one move back, we can look up the remaining valid moves by using the HashMap restMoves
 *  			if there are still valid moves left, then get the next move from iterator and set it to nextMove and break from loop.
 *  			if there are no more valid moves for the tray we're at, meaning the iterator is empty, we have to undo again (so go back to start of while loop)
 *  
 *  	at this point, we have set a move to nextMove
 *  	update the tray with nextMove
 *  	add nextMove to the tray (the tray keeps track of all the moves made so far)
 *  	
 *  	go back to start of loop and repeat until we find goal
 *  
 *  printSolution()
 *   once we solved the puzzle, all we have to do is print out the list of moves that currentT stores (currentT.soFar)
 *
 */
public class Solver {
	int length;
	int width;
	Configuration init;
	Configuration goal;
	Tray currentT;
	boolean unsolved;
	
	boolean draw;
	boolean text;
	boolean time;
	boolean checkOK;
	
	public Solver(String initFileName, String goalFileName) {
            
                System.out.println(initFileName + goalFileName);
		InputSource initsrc = new InputSource(initFileName);
		StringTokenizer st = new StringTokenizer(initsrc.readLine());
		length =  Integer.parseInt(st.nextToken());
		width =  Integer.parseInt(st.nextToken());
		init = new Configuration(initsrc);
		goal = new Configuration(new InputSource(goalFileName));
		
		unsolved = false;
		
		draw = false;
		text = false;
		time = false;
		checkOK = false;
	}
	
	public Solver(String initFileName, String goalFileName, String[] dbug) {
		this(initFileName, goalFileName);
		
		for (String s : dbug) {
			if (s.equals("-otext")) {
				text = true;
			}
			
			else if (s.equals("-odraw")) {
				draw = true;
			}
			
			else if (s.equals("-otime")) {
				time = true;
			}
			
			else if (s.equals("-ocheckOK")) {
				checkOK = true;
			}
			
			else if (s.equals("-oall")) {
				text = true;
				draw = true;
				time = true;
				checkOK = true;
			}
			
			else if (s.equals("-ooptions")) {
				printOptions();
			}
			else {
				throw new IllegalArgumentException("invalid argument: enter -ooptions to see a list of valid options");
			}
		}
	}
	
	public void solve() {
		//solve the puzzle
		solveHelper(new Tray(init, length, width));
	}
	
	public void solveHelper(Tray t) {
		//a list of configurations visited so far
		HashSet<Configuration> previousConfigs = new HashSet<Configuration>();
		
		//a possible configuration for evaluation
		Configuration possibleConfig;
		
		//a storage to keep an iterator of valid moves (values) for each configuration (key)
		HashMap<Configuration, Iterator<Move>> restMoves = new HashMap<Configuration, Iterator<Move>>();
		
		//a list of moves that produce a new configuration not seen before
		HashSet<Move> evaluatedPossibleMoves;
		
		//iterator variable to hold an iterator
		Iterator<Move> iter;
		
		
		//set the tray to currentT
		currentT = t;
		
		//DEBUGGING PURPOSES
		int run = 0;
		if (draw) {
			System.out.println("initial tray: ");
			currentT.print();
			System.out.println();
		}
		
		//see if the blocks in goal configuration actually exist in the initial configuration
		  //this will catch impossible puzzles like if a goal tray has a 5x5 block, but the initial tray only has 1x1 blocks
		if (!goal.existsIn(currentT.myConfig)) {
			if (text) {
				System.out.println("Goal blocks not found in initial tray");
			}
			unsolved = true;
			return;
		}

		
		//see if current tray has reached the goal already
		if (currentT.myConfig.reachedGoal(goal)) {
			if (text) {
				System.out.println("Initial configuration has already reached goal");
			}
			return;
		}
		
		//add the current Configuration to previousConfigs
		previousConfigs.add(new Configuration(currentT.myConfig));
		
		//a list of possibleMoves from a tray
		HashSet<Move> possibleMoves;
		
		//the next move we will make
		Move nextMove = null;
		
		
		
		while (true) {
			//DEBUGGING PURPOSES
			if (text) {
				System.out.println("\nrun #: " + run);
				run++;
			}
			
			//get the possible moves from the tray
			possibleMoves = currentT.possibleMoves();
			
			//create a new list for evaluatedPossibleMoves
			evaluatedPossibleMoves= new HashSet<Move>();
			
			//go through every move in possibleMoves
			for (Move m : possibleMoves) {
				//get the configuration as a result of doing move m
				possibleConfig = currentT.myConfig.move(m);
				
				//see if this configuration has been seen before
				if (!previousConfigs.contains(possibleConfig)) {
					
					//if it has not been seen before, check if it reached the goal
					if (possibleConfig.reachedGoal(goal)) {
						//DEBUGGING PURPOSES
						if (text) {
							System.out.println("Goal found");
						}
						
						//add the move to soFar in currentT
						currentT.addMove(m);
						//end method, solution was found
						return;
					}
					
					//if the configuration has not been seen before, add this move to evaluatedPossibleMoves
					evaluatedPossibleMoves.add(m);
					//add the configuration to evaluatedPossibleMoves
					previousConfigs.add(possibleConfig);
					
				}
			}		
			
			//check if evaluatedPossibleMoves is empty
			if (evaluatedPossibleMoves.isEmpty()) {
				//if it is empty, keep undoing moves until we find a valid move to make
				while(true) {
					//DEBUGGING PURPOSES
					if (text) {
						System.out.println("\nNo possible moves for current tray.");
					}
					
					//undo the last move made
					if (currentT.undoMove(text)) {
						//DEBUGGING PURPOSES
						if (checkOK) {
							if (!currentT.isOK(t)) {
								System.out.println("Tray is not OK.");
								return;
							}
						}
						if (draw) {
							System.out.println("\nTray after undoing: ");
							currentT.print();
						}
						
						//get the iterator of valid moves associated with the curront configuration
						iter = restMoves.get(currentT.myConfig);
						if (iter != null) {
							//get the next move from iter
							if (iter.hasNext()) {
								//we found the next move to make, set nextMove to iter.next and break from loop
								nextMove = iter.next();
								break;
							}
							
							//iterator is empty, we have to keep undoing moves
							else {
							}
						}
						//if iterator is null, that means that currentT.myConfig does not exist in restMoves, return error/unsolved
						else {
							//DEBUGGING PURPOSES
							if (text) {
								System.out.println("iter is null, could not find configuration in restMoves. unsolved");
							}
							unsolved = true;
							return;
						}
					}
					
					//if currentT.undo() returns false, that means that the list of moves was empty
					//this means that no valid moves were found and we've undo'd until we reached the initial tray
					//puzzle is unsolvable
					else {
						if (text) {
							System.out.println("currentT.soFar is empty, cannot undo. unsolved");
						}
						unsolved = true;
						return;
					}
				}
			}
			
			//if evaluatedPossibleMoves is NOT empty
			else {
				//DEBUGGING PURPOSES
				if (text) {
					System.out.println("# of possible moves: " + evaluatedPossibleMoves.size());
					for (Move m : evaluatedPossibleMoves) {
						System.out.println("\t" + m);
					}
				}
				
				//make an iterator out of evaluatedPossibleMoves
				iter = evaluatedPossibleMoves.iterator();
				
				//get the next move, and set it to nextMove
				nextMove = iter.next();
				
				//store the rest of valid moves to the current configuration
				restMoves.put(new Configuration(currentT.myConfig), iter);
			}
			
			//DEBUGGING PURPOSES
			if (text) {
				System.out.println("\nmy next move: " + nextMove);
			}			
			if (draw) {
				System.out.println("\nbefore: ");
				currentT.print();
			}
			
			//make the next move
			currentT.move(nextMove);
			//add the move to currentT.soFar
			currentT.addMove(nextMove);
			
			//now go back to start of while loop
			
			
			
			//DEBUGGING PURPOSES
			if (checkOK) {
				if (!currentT.isOK(t)) {
					System.out.println("Tray is not OK.");
					return;
				}
			}
			if (draw) {
				System.out.println("\nafter: ");
				currentT.print();
			}
		}
	}
	
	public void printSolution() {
		
		if (unsolved || currentT.getsoFar().size() == 0) {
			return;
		}
		
		else {
			ListIterator<Move> mIter = currentT.getsoFar().listIterator(0);
			
			Tray tray = null;
			
			if (text) {
				System.out.println("PRINTING SOLUTION:");
			}
			if (draw) {
				System.out.println("Starting tray:");
				tray = new Tray(init, length, width);
				tray.print();
				System.out.println();
			}
			while(mIter.hasNext()) {
				Move m = mIter.next();
				System.out.println(m);
				if (draw) {
					System.out.println();
					tray.move(m);
					tray.print();
					System.out.println();
				}
			}
		}
	}
	
	public static void printOptions() {
		System.out.println("[-oall]\tEnables all options.");
		System.out.println("[-ocheckOK]\tChecks isOK on tray after every change is made to tray.");
		System.out.println("[-odraw]\tDraws a representation of tray after every change is made to tray.");
		System.out.println("[-otext]\tDisplays useful text that tells user what the code is doing.");
		System.out.println("[-otime]\tDisplays the time it took to find and print solution.");
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			throw new IllegalArgumentException("Need at least the initConfigFile and goalConfigFile");
		}
		
		if (args.length == 1) {
			if (args[0].equals("-ooptions")) {
				Solver.printOptions();
				return;
			}
			else {
				throw new IllegalArgumentException("Correct format is: options initConfigFile goalConfigFile");
			}
		}
		
		Solver sr;
		
		if (args.length == 2) {
			sr = new Solver(args[0], args[1]);
		}
		
		else {
			sr = new Solver(args[args.length - 2], args[args.length - 1], Arrays.copyOfRange(args, 0, args.length - 2));
		}
		
		long start = 0;
		long stop = 0;
		
		if (sr.time) {
			start = System.nanoTime();
		}
		sr.solve();
		sr.printSolution();
		
		if (sr.time) {
			stop = System.nanoTime();
			double duration = ((double)(stop - start)) / 1000000000.0;
			System.out.println("Total duration: " + duration + "s");
		}
	}
}
