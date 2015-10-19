package controller;

import gui.dialog.LoadingDialog;
import tasks.AsyncTask;

/**
 * @Author Tomas Hanus
 */
public class LoadingDialogController extends Controller {
	private static final boolean CANCEL_IF_RUNNING = true;

	private AsyncTask asyncTask;
	private LoadingDialog loadingDialog;

	public LoadingDialogController(AsyncTask asyncTask) {
		super();
		this.asyncTask = asyncTask;
		this.loadingDialog = new LoadingDialog(this);
	}

	public void cancelProcessAction(){
		this.asyncTask.cancel(CANCEL_IF_RUNNING);
		this.loadingDialog.dispose();
	}

	public void showDialog(boolean visible) {
		this.loadingDialog.setVisible(visible);
	}

	public void disposeDialog() {
		this.loadingDialog.dispose();
	}
}
