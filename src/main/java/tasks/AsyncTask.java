package tasks;

import gui.dialog.LoadingDialog;

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
	private LoadingDialog loadingDialog;

	public AsyncTask() {
		this.loadingDialog = new LoadingDialog(); //TODO Decide whether to add the frame to anchor created Loading dialog to
		this.execute();
		loadingDialog.setVisible(true);
	}

	@Override
	protected void done() {
		super.done();
		this.loadingDialog.dispose();
	}

	//TODO async thread cancellation
}
