package controller.LoginFrame;

import gui.LoginFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionListener for the quit button in LoginFrame.
 * <p>
 * Button closes the application.
 *
 * @author Jakub Tutko
 */
public class ButtonQuit implements ActionListener {

	private LoginFrame frame;

	/**
	 * Constructor saves instance of the frame, in which action
	 * will be applicable.
	 *
	 * @param frame instance of the LoginFrame class
	 */
	public ButtonQuit(LoginFrame frame) {
		this.frame = frame;
	}

	/**
	 * Action closes the application with exit code 0.
	 *
	 * @param e event, which causes this action
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
