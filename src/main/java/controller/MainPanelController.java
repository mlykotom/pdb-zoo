package controller;

import gui.MainFrame;
import tasks.AsyncTask;
import tasks.QueryTask1;

/**
 * Controller for Main screen of the application.
 *
 * @Author Tomas Hanus
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
			protected Boolean doInBackground() throws Exception {
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
			protected Boolean doInBackground() throws Exception {
				Thread.sleep(2000);
				return null;
			}
		};
	}

}
