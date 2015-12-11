package cz.vutbr.fit.pdb.ateam;

import cz.vutbr.fit.pdb.ateam.gui.LoginForm;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.swing.*;

/**
 * Main class for JAVA application.
 * First GUI initialization is through this class -> loads login dialog.
 *
 * @author Tomas Mlynaric
 */
public class Main {
	public static void main(String[] args) {
		/** GUI */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
					e.printStackTrace();
				}

				LoginForm loginForm = new LoginForm();
				loginForm.setVisible(true);

				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						Utils.whenApplicationClosing();
					}
				});
			}
		});
	}
}
