package pkg;

public class Main {

	public static void main(String[] args) {
	    /**
	     * Let's escape from the Static world as fast as we can, 
	     * and make an instance of our class.
	     * 
	     * If no command-line argument is used, start with 1 thread.
	     */
		int threads;
		
		if(args.length > 0){
			threads = Integer.parseInt(args[0]);
		}else{
			threads = 1;
		}
		MTCounter mtc = new MTCounter( threads );
		mtc.fnc01();		
		
	}
	
}
