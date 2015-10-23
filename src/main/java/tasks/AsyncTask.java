package tasks;

import controller.LoadingDialogController;

import javax.swing.*;

/**
 * This is abstract class representing an asynchronous task that requires
 * a longer execution time.
 * <p>
 * To use this Async task you have to override doInBackground() void method.
 * Content of this method is executed on a separate thread. While executing
 * doInBackground method a dialog with cancel btn is shown.
 *
 * @author Tomas Hanus
 */
public abstract class AsyncTask extends SwingWorker<Boolean, String>{
	private LoadingDialogController loadingDialogController;

	public AsyncTask() {
		this.loadingDialogController = new LoadingDialogController(this); //TODO Decide whether to add the frame to anchor created Loading dialog to
		this.execute();
		loadingDialogController.showDialog(true);
	}

	/**
	 * It's called after the process of doItBackground method is finished.
	 * It also closes the Loading dialog.
	 */
	@Override
	protected void done() {
		super.done();
		this.loadingDialogController.disposeDialog();
	}

	/**
	 * It should be used when you want to find out if your async task was successful.
	 *
	 * @return Returns True if process of asyncTask was completed, False if not.
	 */
	public Boolean isComplete(){
		try {
			return get();
		} catch (Exception e) {
			return false;
		}
	}


}
