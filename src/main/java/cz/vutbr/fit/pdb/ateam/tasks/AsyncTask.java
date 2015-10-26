package cz.vutbr.fit.pdb.ateam.tasks;

import cz.vutbr.fit.pdb.ateam.controller.LoadingDialogController;

import javax.swing.*;

/**
 * This is abstract class representing an asynchronous task that requires
 * a longer execution time.
 * <p>
 * To use this Async task you have to override doInBackground() void method.
 * Content of this method is executed on a separate thread. While executing
 * doInBackground method a dialog with cancel btn is shown.
 *
 * // TODO make adapter on this class so that cannot be called execute()
 * @author Tomas Hanus
 */
public abstract class AsyncTask extends SwingWorker<Boolean, String>{
	private LoadingDialogController loadingDialogController;

	/**
	 * Allowed ways of creating async task
	 */
	private enum ProgressType{
		TYPE_DIALOG,
		TYPE_PROGRESSBAR,
	}

	private ProgressType type;
	private JProgressBar progressBar;
	private int errorCode;

	/**
	 * Creates async task with dialog type
	 */
	public AsyncTask() {
		this.type = ProgressType.TYPE_DIALOG;
	}

	/**
	 * Creates async task with progressbar type
	 * @param progressPanel progress bar in GUI
	 */
	public AsyncTask(JProgressBar progressPanel){
		this.type = ProgressType.TYPE_PROGRESSBAR;
		this.progressBar = progressPanel;
	}

	/**
	 * It's called after the process of doItBackground method is finished.
	 * It also closes the Loading dialog.
	 */
	@Override
	protected void done() {
		super.done();
		switch (this.type){
			case TYPE_DIALOG:
				this.loadingDialogController.disposeDialog();
				break;
			case TYPE_PROGRESSBAR:
				this.progressBar.setVisible(false);
				break;
		}

		whenDone(isComplete());
	}

	/**
	 * It should be used when you want to find out if your async task was successful.
	 *
	 * @return Returns True if process of asyncTask was completed, False if not.
	 */
	private boolean isComplete(){
		try {
			return get();
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * Correctly handles start of async task based on type
	 */
	public void start(){
		this.execute();
		switch (this.type){
			case TYPE_DIALOG:
				this.loadingDialogController = new LoadingDialogController(this);
				loadingDialogController.showDialog(true);
				break;

			case TYPE_PROGRESSBAR:
				this.progressBar.setVisible(true);
				break;
		}
	}

	/**
	 * Correctly handles when async task ends
	 * @param success checks what doInBackground returns
	 */
	abstract protected void whenDone(boolean success);

	protected void setErrorCode(int errorCode){ this.errorCode = errorCode; }

	protected int getErrorCode(){ return this.errorCode; }
}
