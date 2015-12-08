package cz.vutbr.fit.pdb.ateam.gui;

import cz.vutbr.fit.pdb.ateam.controller.MenuBarController;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * cz.vutbr.fit.pdb.ateam.Main Frame for the whole application.
 * <p/>
 * All windows of the application are opened in this frame
 * as a JPanel. This frame contains only navigation menu.
 *
 * @author Jakub Tutko
 */
public class MainFrame extends JFrame {
	private static final int WINDOW_DEFAULT_WIDTH = 1024;
	private static final int WINDOW_DEFAULT_HEIGHT = 768;

	private MenuBarController menuBarController;
	public ContentPanel contentPanel;

	/**
	 * Constructor creates instance of the MenuBarController for
	 * events occurred in MenuBar and initializes frame.
	 */
	public MainFrame() {
		this.menuBarController = new MenuBarController(this);
		contentPanel = new ContentPanel();
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
		setJMenuBar(menuBar);

		// ------ content
		switchContent(contentPanel);
		pack();
	}


	/**
	 * Switch content of content panel
	 *
	 * @param panelToShow instanciated component to show
	 */
	public JPanel switchContent(JPanel panelToShow) {
		Container contentPane = getContentPane();
		contentPane.removeAll();
		contentPane.add(panelToShow);
		//contentPane.add(new MultimediaTest()); // TODO: show multimedia
		// TODO is it necessary? usually yes, but here I don't know
		//contentPane.revalidate();
		//contentPane.repaint();

		return panelToShow;
	}


	/**
	 * Factory for logout button
	 *
	 * @return created logout button
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
		Utils.setComponentFixSize(logoutButton, 100, 30);

		return logoutButton;
	}


}
