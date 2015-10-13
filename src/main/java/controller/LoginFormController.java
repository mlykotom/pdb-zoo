package controller;

import exception.DataManagerException;
import gui.LoginForm;
import gui.MainFrame;
import utils.Logger;
import utils.Utils;

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
		try {
			dataManager.connectDatabase(form.getUserNameTextField().getText(), String.valueOf(form.getPasswordPasswordField().getPassword()));

			form.dispose();
			new MainFrame().setVisible(true);

			Logger.createLog(Logger.DEBUG_LOG, "Database connected successfully.");

		} catch (DataManagerException ex) {
			Logger.createLog(Logger.DEBUG_LOG, ex.getMessage());

			JOptionPane.showMessageDialog(form,
					"Invalid username or password!",
					"Login failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Button closes the application.
	 */
	public void quitButtonAction() {
		Utils.closeApplication();
	}
}
