package gui;

import controller.LoginFrameController;

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
 * MainFrame, otherwise user can not move out of this frame
 * without closing the application.
 *
 * @author Jakub Tutko
 */
public class LoginFrame extends JFrame {

	public static final int DESCR_LABEL_FONT_SIZE = 14;
	public static final int HEADER_LABEL_FONT_SIZE = 26;

	private JTextField userNameTextField;
	private JPasswordField passwordPasswordField;
	private JLabel passwordLabel;
	private JPanel headerPanel;
	private JLabel headerLabel;
	private JPanel loginFormPanel;
	private JLabel userNameLabel;
	private JButton loginButton;
	private JButton quitButton;
	private JPanel buttonsPanel;
	private JPanel rootPanel;

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
		final LoginFrameController controller = new LoginFrameController(this);

		/* Header label */
		headerPanel = new JPanel();
		headerLabel = new JLabel("Log In");
		headerLabel.setFont(new Font(headerLabel.getFont().getFontName(), Font.BOLD, HEADER_LABEL_FONT_SIZE));
		headerPanel.add(headerLabel);

		/* Panel for the user name and password */
		loginFormPanel = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();
		cs.fill = GridBagConstraints.HORIZONTAL;

		/* User name label */
		userNameLabel = new JLabel("Username: ");
		userNameLabel.setFont(new Font(userNameLabel.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		loginFormPanel.add(userNameLabel, cs);

		/* User name input */
		userNameTextField = new JTextField(20);
		userNameTextField.setText("XMLYNA06"); // TODO: remove later
		setComponentFixSize(userNameTextField, 200, 25);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		loginFormPanel.add(userNameTextField, cs);

		/* Password label */
		passwordLabel = new JLabel("Password: ");
		passwordLabel.setFont(new Font(passwordLabel.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		loginFormPanel.add(passwordLabel, cs);

		/* Password input */
		passwordPasswordField = new JPasswordField(20);
		passwordPasswordField.setText("04h3xlr6"); // TODO: remove later
		setComponentFixSize(passwordPasswordField, 200, 25);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		loginFormPanel.add(passwordPasswordField, cs);

		/* Login button */
		loginButton = new JButton("Login");
		loginButton.setFont(new Font(loginButton.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		setComponentFixSize(loginButton, 100, 30);
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.loginButtonAction();
			}
		});

		/* Quit Button */
		quitButton = new JButton("Quit");
		quitButton.setFont(new Font(quitButton.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		setComponentFixSize(quitButton, 70, 30);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.quitButtonAction();
			}
		});

		/* Panel for the buttons */
		buttonsPanel = new JPanel();
		buttonsPanel.add(loginButton);
		buttonsPanel.add(quitButton);

		/* Panel for the whole frame */
		rootPanel = new JPanel(new BorderLayout());
		rootPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
		rootPanel.add(headerPanel, BorderLayout.NORTH);
		rootPanel.add(loginFormPanel, BorderLayout.CENTER);
		rootPanel.add(buttonsPanel, BorderLayout.PAGE_END);

		/* Setting root panel */
		setContentPane(rootPanel);

		pack();
		setTitle("Zoo IS");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

	public JTextField getUserNameTextField() {
		return userNameTextField;
	}
	public void setUserNameTextField(JTextField tfUserName) {
		this.userNameTextField = tfUserName;
	}

	public JPasswordField getPasswordPasswordField() {
		return passwordPasswordField;
	}

	public void setPasswordPasswordField(JPasswordField passwordPasswordField) {
		this.passwordPasswordField = passwordPasswordField;
	}

	public JLabel getPasswordLabel() {
		return passwordLabel;
	}

	public void setPasswordLabel(JLabel passwordLabel) {
		this.passwordLabel = passwordLabel;
	}

	public JPanel getHeaderPanel() {
		return headerPanel;
	}

	public void setHeaderPanel(JPanel headerPanel) {
		this.headerPanel = headerPanel;
	}

	public JLabel getHeaderLabel() {
		return headerLabel;
	}

	public void setHeaderLabel(JLabel headerLabel) {
		this.headerLabel = headerLabel;
	}

	public JPanel getLoginFormPanel() {
		return loginFormPanel;
	}

	public void setLoginFormPanel(JPanel loginFormPanel) {
		this.loginFormPanel = loginFormPanel;
	}

	public JLabel getUserNameLabel() {
		return userNameLabel;
	}

	public void setUserNameLabel(JLabel userNameLabel) {
		this.userNameLabel = userNameLabel;
	}

	public JButton getLoginButton() {
		return loginButton;
	}

	public void setLoginButton(JButton loginButton) {
		this.loginButton = loginButton;
	}

	public JButton getQuitButton() {
		return quitButton;
	}

	public void setQuitButton(JButton quitButton) {
		this.quitButton = quitButton;
	}

	public JPanel getButtonsPanel() {
		return buttonsPanel;
	}

	public void setButtonsPanel(JPanel buttonsPanel) {
		this.buttonsPanel = buttonsPanel;
	}

	public JPanel getRootPanel() {
		return rootPanel;
	}

	public void setRootPanel(JPanel rootPanel) {
		this.rootPanel = rootPanel;
	}
}
