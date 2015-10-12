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
 * Created by Jakub on 12.10.2015.
 */
public class ButtonLogin implements ActionListener {

	private LoginFrame frame;

	public ButtonLogin(LoginFrame frame) {
		this.frame = frame;
	}

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
