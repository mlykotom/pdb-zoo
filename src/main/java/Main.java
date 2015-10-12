import gui.LoginFrame;
import utils.Logger;
import utils.Utils;

import javax.swing.*;

/**
 * Created by mlyko on 06.10.2015.
 */
public class Main {
	public static void main(String[] args) {
		/** GUI */
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				LoginFrame loginFrame = new LoginFrame();
				loginFrame.setVisible(true);
			}
		});

		/** Closing connection with database at the end of the application */
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				Utils.closeApplication();
				Logger.createLog(Logger.DEBUG_LOG, "Closing application");
			}
		}, "Shutdown-thread"));
	}
}
