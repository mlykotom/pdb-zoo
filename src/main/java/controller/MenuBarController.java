package controller;

import gui.LoginForm;
import gui.MainFrame;
import gui.map.ZooMapPanel;

import javax.swing.*;

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
	 * HomeMenu in MenuBar opens home screen of the application.
	 */
	public void homeMenuAction() {
		// TODO: change to show menuJPanel
	}

	/**
	 * LogoutMenu in MenuBar logout user and opens LoginForm.
	 */
	public void logoutMenuAction() {
		dataManager.disconnectDatabase();
		mainFrame.dispose();
		new LoginForm().setVisible(true);
	}

	/**
	 * ZooMapMenu in MenuBar opens JPanel with zooMapForm in MainFrame.
	 */
	public void zooMapMenuAction() {
		JPanel contentPane = (JPanel) mainFrame.getContentPane();

		contentPane.removeAll();
		contentPane.add(new ZooMapPanel());
		contentPane.revalidate();
		contentPane.repaint();
	}
}
