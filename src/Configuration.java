import java.util.HashSet;
import java.util.StringTokenizer;

public class Configuration {
	
	private HashSet<Block> config;
	private int myhashCode;
	
	public Configuration(InputSource src) {
		config = new HashSet<Block>();
		String line;
		StringTokenizer st;
		Block b;
		myhashCode = 0;
		while(true) {
			line = src.readLine();
			if (line == null) {
				break;
			}
			st = new StringTokenizer(line);
			b = new Block(	Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), 
							Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
			myhashCode = myhashCode + b.hashCode();
			config.add(b);
		}
	}
	
	public Configuration(Configuration c) {
		config = new HashSet<Block>(c.config);
		myhashCode = c.hashCode();
	}
	
	public Configuration move(Move m) {
		Configuration rtn = new Configuration(this);
		rtn.update(m.start, m.end);
		return rtn;
	}
	
	private void update(Block start, Block end) {
		config.remove(start);
		config.add(end);
		
		updateHash();
	}
	
	public void updateHash() {
		myhashCode = 0;
		for (Block b : config) {
			myhashCode += b.hashCode();
		}
	}
	@Override
	public boolean equals(Object o) {
		Configuration cfg = (Configuration) o;
		//assumes HashSet.equals works as intended
		return config.equals(cfg.config);
	}
	
	public boolean reachedGoal(Configuration goal) {		
		//assumes HashSet.containsAll works as intended
		return config.containsAll(goal.config);
	}
	
	public boolean existsIn(Configuration c) {
		
		int exists = 0;
		
		for (Block b : this.config) {
			for (Block bk : c.config) {
				if (bk.area == b.area) {
					exists += 1;
					break;
				}
			}
		}
		
		return exists == this.config.size();
	}
	
	
	@Override
	public int hashCode() {
		updateHash();
		return myhashCode;
	}
	
	public void print() {
		for (Block b : config) {
			System.out.println(b);
		}
	}
	
	public HashSet<Block> config() {
		return config;
	}
}
