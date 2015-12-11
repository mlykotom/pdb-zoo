package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.LoginForm;
import cz.vutbr.fit.pdb.ateam.gui.MainFrame;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import javax.swing.*;

/**
 * Class controls all events, which occurs in LoginForm.
 *
 * @author Jakub Tutko
 */
public class LoginFormController extends Controller {

	private LoginForm form;

	/**
	 * Constructor saves LoginForm instance. Controller will
	 * use this instance in all events handler.
	 *
	 * @param loginForm instance of the LoginForm class
	 */
	public LoginFormController(LoginForm loginForm) {
		super();
		this.form = loginForm;
	}

	/**
	 * Button tries to connect database with the given User name and Password.
	 * When connection is successful, LoginForm is disposed and MainFrame is
	 * opened. Otherwise application stays on the LoginForm and error message
	 * is displayed.
	 */
	public void loginButtonAction() {
		AsyncTask connectToDB = new AsyncTask() {
			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					dataManager.connectDatabase(form.getUserNameTextField().getText(), String.valueOf(form.getPasswordPasswordField().getPassword()));
					reloadAllData();
					return true;
				} catch (DataManagerException ex) {
					this.setErrorCode(ex.getErrorCode());
					Logger.createLog(Logger.DEBUG_LOG, ex.getMessage());
					return false;
				}
			}

			@Override
			protected void onDone(boolean success) {
				if (success){
					form.dispose();
					new MainFrame().setVisible(true);
					Logger.createLog(Logger.DEBUG_LOG, "Database connected successfully.");
				} else {
					String errorTitle;
					String errorMessage;

					switch(this.getErrorCode()){
						case DataManagerException.ERROR_CODE_INVALID_LOGIN:
							errorTitle = "Login failed";
							errorMessage = "Invalid username or password!";
							break;

						case DataManagerException.ERROR_CODE_MAX_SESSION:
							errorTitle = "Max user per session";
							errorMessage = "Maximum users on database per session reached!";
							break;

						default:
							errorTitle = "Unknown error";
							errorMessage = "Error type " + this.getErrorCode() + "happened";
							break;
					}

					JOptionPane.showMessageDialog(form,
							errorMessage,
							errorTitle,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		connectToDB.start();
	}

	/**
	 * Button closes the application.
	 */
	public void quitButtonAction() {
		System.exit(0);
	}
}
