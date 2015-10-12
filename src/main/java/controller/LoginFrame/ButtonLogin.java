package controller.LoginFrame;

import adapter.DataManager;
import adapter.DataManagerException;
import gui.LoginFrame;
import gui.MenuFrame;
import logger.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionListener for the login button in the LoginFrame.
 * <p>
 * Button tries to connect database with the given User name and Password.
 * When connection is successful, LoginFrame is disposed and MenuFrame is
 * opened. Otherwise application stays on the LoginFrame and error message
 * is displayed.
 *
 * @author Jakub Tutko
 */
public class ButtonLogin implements ActionListener {

	private LoginFrame frame;

	/**
	 * Constructor saves instance of the LoginFrame. All changes will be
	 * applicable on this frame.
	 *
	 * @param frame instance of the LoginFrame to work with
	 */
	public ButtonLogin(LoginFrame frame) {
		this.frame = frame;
	}

	/**
	 * Action tries connect database, with User name and Password from the
	 * frame resources. On success, LoginFrame is disposed and MenuFrame is
	 * set to be visible. On failure, error dialog is displayed.
	 *
	 * @param e event, which causes this action
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		DataManager dataManager = DataManager.getInstance();

		try {
			dataManager.connectDatabase(frame.getTfUsername().getText(), String.valueOf(frame.getPfPassword().getPassword()));

			frame.dispose();
			new MenuFrame().setVisible(true);

		} catch (DataManagerException ex) {
			Logger.createLog(Logger.ERROR_LOG, ex.getMessage());

			JOptionPane.showMessageDialog(frame,
					"Invalid username or password!",
					"Login failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
