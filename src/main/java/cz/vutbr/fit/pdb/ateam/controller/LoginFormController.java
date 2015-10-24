package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.LoginForm;
import cz.vutbr.fit.pdb.ateam.gui.MainFrame;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;

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
					return true;
				} catch (DataManagerException ex) {
					Logger.createLog(Logger.DEBUG_LOG, ex.getMessage());
					return false;
				}
			}

			@Override
			protected void whenDone(boolean success) {
				if (success){
					new MainFrame().setVisible(true);
					Logger.createLog(Logger.DEBUG_LOG, "Database connected successfully.");
					form.dispose();
				} else {
					JOptionPane.showMessageDialog(form,
							"Invalid username or password!",
							"Login failed",
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
		Utils.closeApplication();
	}
}
