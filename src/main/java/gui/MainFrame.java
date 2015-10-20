package gui;

import controller.MenuBarController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Frame for the whole application.
 * <p>
 * All windows of the application are opened in this frame
 * as a JPanel. This frame contains only navigation menu.
 *
 * @author Jakub Tutko
 */
public class MainFrame extends JFrame {
	private static final int WINDOW_DEFAULT_WIDTH = 1024;
	private static final int WINDOW_DEFAULT_HEIGHT = 768;

	private MenuBarController menuBarController;

	JTabbedPane tabbedPane;

	/**
	 * Constructor creates instance of the MenuBarController for
	 * events occurred in MenuBar and initializes frame.
	 */
	public MainFrame() {
		this.menuBarController = new MenuBarController(this);
		initUI();
	}

	/**
	 * Method initializes MainFrame. New frame contains only navigation
	 * menu at the top of the frame.
	 */
	public void initUI() {
		// ------ window
		setTitle("ZOO");
		setSize(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// ------ menu
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(createLogoutButton());

		// ------ content
		JPanel activePage = switchContent(new ContentPanel());
	}


	/**
	 * Switch content of some panel
	 * @param panelToShow
	 */
	public JPanel switchContent(JPanel panelToShow){
		Container contentPane = getContentPane();
		contentPane.removeAll();
		contentPane.add(panelToShow);
		// TODO is it necessary?
		//contentPane.revalidate();
		//contentPane.repaint();

		return panelToShow;
	}


	/**
	 * Factory for login button
	 * @return
	 */
	private Component createLogoutButton() {
		JButton logoutButton = new JButton("Logout");
		logoutButton.setIcon(new ImageIcon("logoutMenu-icon.png"));
		logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuBarController.logoutMenuAction();
			}
		});

		return logoutButton;
	}


}
