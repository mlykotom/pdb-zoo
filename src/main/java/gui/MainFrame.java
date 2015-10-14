package gui;

import controller.MenuBarController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Main Frame for the whole application.
 * <p>
 * All windows of the application are opened in this frame
 * as a JPanel. This frame contains only navigation menu.
 *
 * @author Jakub Tutko
 */
public class MainFrame extends JFrame {
	private MenuBarController menuBarController;

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

		JButton homeMenu = new JButton("Home");
		homeMenu.setIcon(new ImageIcon("homeMenu-icon.png"));
		homeMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuBarController.homeMenuAction();
			}
		});

		JButton zooMapMenu = new JButton("Zoo map");
		//zooMapMenu.setIcon(new ImageIcon("homeMenu-icon.png"));
		zooMapMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuBarController.zooMapMenuAction();
			}
		});

		JButton logoutMenu = new JButton("Logout");
		logoutMenu.setIcon(new ImageIcon("logoutMenu-icon.png"));
		logoutMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menuBarController.logoutMenuAction();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(homeMenu);
		menuBar.add(zooMapMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(logoutMenu);
		setJMenuBar(menuBar);

		setTitle("ZOO");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
