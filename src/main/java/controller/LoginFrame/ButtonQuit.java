package controller.LoginFrame;

import gui.LoginFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Jakub on 12.10.2015.
 */
public class ButtonQuit implements ActionListener {

	private LoginFrame frame;

	public ButtonQuit(LoginFrame frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
