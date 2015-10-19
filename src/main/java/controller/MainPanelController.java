package controller;

import gui.MainFrame;
import tasks.AsyncTask;
import tasks.QueryTask1;

/**
 * Created by Tomas on 10/14/2015.
 */
public class MainPanelController extends Controller {
	private final MainFrame frame;

	public MainPanelController(MainFrame mainFrame) {
		super();
		this.frame = mainFrame;
	}

	public void db_InitAction() {
		QueryTask1 qt1 = new QueryTask1();
	}

	public void manageAnimalsAction(){
		//TODO Redirect to manage users screen
		//TODO Remove temporary AsyncTask example
		AsyncTask task = new AsyncTask() {
			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(1500);
				return null;
			}
		};
	}

	public void manageUsersAction(){
		//TODO Redirect to manage users screen
		//TODO Remove temporary AsyncTask example
		AsyncTask task = new AsyncTask() {
			@Override
			protected Void doInBackground() throws Exception {
				Thread.sleep(2000);
				return null;
			}
		};
	}

}
