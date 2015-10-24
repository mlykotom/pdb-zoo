package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.gui.dialog.LoadingDialog;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;

/**
 * Controller for LoadingDialog of AsyncTask
 *
 * @Author Tomas Hanus
 */
public class LoadingDialogController extends Controller {
	/** Constant for setting true in order to cancel process even if it is already working
	 * on calling cancel method of AsyncTask  */
	private static final boolean CANCEL_IF_RUNNING = true;

	private AsyncTask asyncTask;
	private LoadingDialog loadingDialog;

	/**
	 * @param asyncTask Instance of the asyncTask is needed in order to be able cancel the process of the asyncTask
	 */
	public LoadingDialogController(AsyncTask asyncTask) {
		super();
		this.asyncTask = asyncTask;
		this.loadingDialog = new LoadingDialog(this);
	}

	/**
	 * Method cancels the process of asyncTask and disposes Loading Dialog.
	 */
	public void cancelProcessAction(){
		this.asyncTask.cancel(CANCEL_IF_RUNNING);
		this.loadingDialog.dispose();
	}

	/**
	 * @param visible Sets visibility of LoadingDialog
	 */
	public void showDialog(boolean visible) {
		this.loadingDialog.setVisible(visible);
	}

	/**
	 * Disposes the Loading Dialog
	 */
	public void disposeDialog() {
		this.loadingDialog.dispose();
	}
}
