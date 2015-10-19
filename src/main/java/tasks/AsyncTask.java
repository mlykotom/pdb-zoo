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
public abstract class AsyncTask extends SwingWorker<Void, String>{
	private LoadingDialogController loadingDialogController;

	public AsyncTask() {
		this.loadingDialogController = new LoadingDialogController(this); //TODO Decide whether to add the frame to anchor created Loading dialog to
		this.execute();
		loadingDialogController.showDialog(true);
	}

	@Override
	protected void done() {
		super.done();
		this.loadingDialogController.disposeDialog();
	}


}
