package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.LoginForm;
import cz.vutbr.fit.pdb.ateam.gui.MainFrame;
import cz.vutbr.fit.pdb.ateam.observer.AppStateChangedObservable;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

/**
 * Class controls all events occurred in MainFrame's MenuBar.
 *
 * @author Jakub Tutko
 */
public class MainFrameController extends Controller {
	private MainFrame mainFrame;

	/**
	 * Constructor saves instance of the MainFrame class.
	 *
	 * @param mainFrame instance of the MainFrame
	 */
	public MainFrameController(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
		AppStateChangedObservable.getInstance().subscribe(this);
	}

	/**
	 * LogoutMenu in MenuBar logout user and opens LoginForm.
	 */
	public void logoutMenuAction() {
		AsyncTask task = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				if (success) {
					mainFrame.dispose();
					new LoginForm().setVisible(true);
				} else {
					showDialog(ERROR_MESSAGE, "Cannot logout from the database!");
				}
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					dataManager.clearCache();
					dataManager.disconnectDatabase();
					return true;
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
				}
				return false;
			}
		};

		task.start();
	}

	/**
	 * Initialize database from the initialization script.
	 */
	public void initDBMenuAction() {
		AsyncTask task = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				//reloadAllData();
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					dataManager.initDatabase();
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
					return false;
				}
				return true;
			}
		};

		task.start();
	}

	public void appStateChangedListener(String state) {
		mainFrame.getAppStateLabel().setText(state);
	}
}
