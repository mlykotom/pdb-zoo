package tasks;

/**
 * An example how to use an AsyncTask.
 *
 * For each action that may have longer execution time create
 * its own class which extends AsyncTask
 *
 * @author Tomas Hanus
 */

//TODO completely remove later

public class QueryTask1 extends AsyncTask {
	public QueryTask1() {
		super();
	}
	@Override
	protected Boolean doInBackground() throws Exception {
		try{
			Thread.sleep(1500); //Simulated long query
			return null;
		} catch(InterruptedException ex){
			ex.printStackTrace();
		}
		return null;
	}
}
