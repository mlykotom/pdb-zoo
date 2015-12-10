package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.gui.LoginForm;
import cz.vutbr.fit.pdb.ateam.gui.MainFrame;

/**
 * Class controls all events occurred in MainFrame's MenuBar.
 *
 * @author Jakub Tutko
 */
public class MenuBarController extends Controller {
	private MainFrame mainFrame;

	/**
	 * Constructor saves instance of the MainFrame class.
	 *
	 * @param mainFrame instance of the MainFrame
	 */
	public MenuBarController(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
	}

	/**
	 * LogoutMenu in MenuBar logout user and opens LoginForm.
	 */
	public void logoutMenuAction() {
		dataManager.disconnectDatabase();
		mainFrame.dispose();
		new LoginForm().setVisible(true);
	}
}
