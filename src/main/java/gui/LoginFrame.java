package gui;

import controller.DataManager;
import controller.DataManagerException;
import logger.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Frame for Log In form.
 * <p>
 * This frame is shown at the start of the application. Frame
 * includes form, which is used to connect with the database
 * server. User can set there his UserName and Password. If connection
 * ends successfully, application disposes this frame and opens
 * MenuFrame, otherwise user can not move out of this frame
 * without closing the application.
 *
 * @author Jakub Tutko
 */
public class LoginFrame extends JFrame {

	public static final int DESCR_LABEL_FONT_SIZE = 14;
	public static final int HEADER_LABEL_FONT_SIZE = 26;

	private JTextField tfUsername;
	private JPasswordField pfPassword;

	/**
	 * Constructor calls initUI() method
	 */
	public LoginFrame() {
		initUI();
	}

	/**
	 * Method initializes LoginFrame content
	 */
	private void initUI() {
		/* Header label */
		JPanel headerPanel = new JPanel();
		JLabel lbHeader = new JLabel("Log In");
		lbHeader.setFont(new Font(lbHeader.getFont().getFontName(), Font.BOLD, HEADER_LABEL_FONT_SIZE));
		headerPanel.add(lbHeader);

		/* Panel for the user name and password */
		JPanel formPanel = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();
		cs.fill = GridBagConstraints.HORIZONTAL;

		/* User name label */
		JLabel lbUsername = new JLabel("Username: ");
		lbUsername.setFont(new Font(lbUsername.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		formPanel.add(lbUsername, cs);

		/* User name input */
		tfUsername = new JTextField(20);
		setComponentFixSize(tfUsername, 200, 25);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		formPanel.add(tfUsername, cs);

		/* Password label */
		JLabel lbPassword = new JLabel("Password: ");
		lbPassword.setFont(new Font(lbPassword.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		formPanel.add(lbPassword, cs);

		/* Password input */
		pfPassword = new JPasswordField(20);
		setComponentFixSize(pfPassword, 200, 25);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		formPanel.add(pfPassword, cs);

		/* Login button */
		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(new Font(btnLogin.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		setComponentFixSize(btnLogin, 100, 30);
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				DataManager dataManager = DataManager.getInstance();

				try {
					dataManager.connectDatabase(tfUsername.getText(), String.valueOf(pfPassword.getPassword()));

					dispose();
					new MenuFrame().setVisible(true);

				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());

					JOptionPane.showMessageDialog(LoginFrame.this,
							"Invalid username or password!",
							"Login failed",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		/* Quit Button */
		JButton btnQuit = new JButton("Quit");
		btnQuit.setFont(new Font(btnQuit.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		setComponentFixSize(btnQuit, 70, 30);
		btnQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		/* Panel for the buttons */
		JPanel btnPanel = new JPanel();
		btnPanel.add(btnLogin);
		btnPanel.add(btnQuit);

		/* Panel for the whole frame */
		JPanel rootPanel = new JPanel(new BorderLayout());
		rootPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
		rootPanel.add(headerPanel, BorderLayout.NORTH);
		rootPanel.add(formPanel, BorderLayout.CENTER);
		rootPanel.add(btnPanel, BorderLayout.PAGE_END);

		/* Setting root panel */
		getContentPane().add(rootPanel);

		pack();
		setTitle("Zoo IS");
		setResizable(false);
		setLocationRelativeTo(null);
	}

	/**
	 * Method set fixed size for the given JComponent.
	 *
	 * @param component JComponent to work with
	 * @param width final width of the component
	 * @param height final height of the component
	 */
	public static void setComponentFixSize( JComponent component ,int width, int height) {
		component.setMinimumSize(new Dimension(width, height));
		component.setMaximumSize(new Dimension(width, height));
		component.setPreferredSize(new Dimension(width, height));
	}
}
