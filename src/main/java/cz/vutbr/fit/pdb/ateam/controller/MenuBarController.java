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
public class MenuBarController extends Controller {
	private MainFrame mainFrame;

	/**
	 * Constructor saves instance of the MainFrame class.
	 *
	 * @param mainFrame instance of the MainFrame
	 */
	public MenuBarController(MainFrame mainFrame) {
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
				if(success) {
					mainFrame.dispose();
					new LoginForm().setVisible(true);
				} else {
					showDialog(ERROR_MESSAGE, "Cannot logout from the database!");
				}
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
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

	public void appStateChangedListener(String state) {
		mainFrame.getAppStateLabel().setText(state);
	}
}
