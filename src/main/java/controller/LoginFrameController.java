package controller;

import exception.DataManagerException;
import gui.LoginFrame;
import gui.MainFrame;
import utils.Logger;
import utils.Utils;

import javax.swing.*;

/**
 * Class controls all events, which occurs in LoginFrame.
 *
 * @author Jakub Tutko
 */
public class LoginFrameController extends Controller {

	private LoginFrame frame;

	/**
	 * Constructor saves LoginFrame instance. Controller will
	 * use this instance in all events handler.
	 *
	 * @param loginFrame instance of the LoginFrame class
	 */
	public LoginFrameController(LoginFrame loginFrame) {
		super();
		this.frame = loginFrame;
	}

	/**
	 * Button tries to connect database with the given User name and Password.
	 * When connection is successful, LoginFrame is disposed and MainFrame is
	 * opened. Otherwise application stays on the LoginFrame and error message
	 * is displayed.
	 */
	public void loginButtonAction() {
		try {
			dataManager.connectDatabase(frame.getUserNameTextField().getText(), String.valueOf(frame.getPasswordPasswordField().getPassword()));

			frame.dispose();
			new MainFrame().setVisible(true);

			Logger.createLog(Logger.DEBUG_LOG, "Database connected successfully");

		} catch (DataManagerException ex) {
			Logger.createLog(Logger.DEBUG_LOG, ex.getMessage());

			JOptionPane.showMessageDialog(frame,
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
