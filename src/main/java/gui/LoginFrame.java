package gui;

import controller.LoginFrame.ButtonLogin;
import controller.LoginFrame.ButtonQuit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
	private JLabel lbPassword;
	private JPanel headerPanel;
	private JLabel lbHeader;
	private JPanel loginFormPanel;
	private JLabel lbUsername;
	private JButton btnLogin;
	private JButton btnQuit;
	private JPanel btnPanel;
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
		/* Header label */
		headerPanel = new JPanel();
		lbHeader = new JLabel("Log In");
		lbHeader.setFont(new Font(lbHeader.getFont().getFontName(), Font.BOLD, HEADER_LABEL_FONT_SIZE));
		headerPanel.add(lbHeader);

		/* Panel for the user name and password */
		loginFormPanel = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();
		cs.fill = GridBagConstraints.HORIZONTAL;

		/* User name label */
		lbUsername = new JLabel("Username: ");
		lbUsername.setFont(new Font(lbUsername.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		loginFormPanel.add(lbUsername, cs);

		/* User name input */
		tfUsername = new JTextField(20);
		setComponentFixSize(tfUsername, 200, 25);
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 2;
		loginFormPanel.add(tfUsername, cs);

		/* Password label */
		lbPassword = new JLabel("Password: ");
		lbPassword.setFont(new Font(lbPassword.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		loginFormPanel.add(lbPassword, cs);

		/* Password input */
		pfPassword = new JPasswordField(20);
		setComponentFixSize(pfPassword, 200, 25);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 2;
		loginFormPanel.add(pfPassword, cs);

		/* Login button */
		btnLogin = new JButton("Login");
		btnLogin.setFont(new Font(btnLogin.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		setComponentFixSize(btnLogin, 100, 30);
		btnLogin.addActionListener(new ButtonLogin(this));

		/* Quit Button */
		btnQuit = new JButton("Quit");
		btnQuit.setFont(new Font(btnQuit.getFont().getFontName(), Font.BOLD, DESCR_LABEL_FONT_SIZE));
		setComponentFixSize(btnQuit, 70, 30);
		btnQuit.addActionListener(new ButtonQuit(this));

		/* Panel for the buttons */
		btnPanel = new JPanel();
		btnPanel.add(btnLogin);
		btnPanel.add(btnQuit);

		/* Panel for the whole frame */
		rootPanel = new JPanel(new BorderLayout());
		rootPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
		rootPanel.add(headerPanel, BorderLayout.NORTH);
		rootPanel.add(loginFormPanel, BorderLayout.CENTER);
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

	public JTextField getTfUsername() {
		return tfUsername;
	}
	public void setTfUsername(JTextField tfUserName) {
		this.tfUsername = tfUserName;
	}

	public JPasswordField getPfPassword() {
		return pfPassword;
	}

	public void setPfPassword(JPasswordField pfPassword) {
		this.pfPassword = pfPassword;
	}

	public JLabel getLbPassword() {
		return lbPassword;
	}

	public void setLbPassword(JLabel lbPassword) {
		this.lbPassword = lbPassword;
	}

	public JPanel getHeaderPanel() {
		return headerPanel;
	}

	public void setHeaderPanel(JPanel headerPanel) {
		this.headerPanel = headerPanel;
	}

	public JLabel getLbHeader() {
		return lbHeader;
	}

	public void setLbHeader(JLabel lbHeader) {
		this.lbHeader = lbHeader;
	}

	public JPanel getLoginFormPanel() {
		return loginFormPanel;
	}

	public void setLoginFormPanel(JPanel loginFormPanel) {
		this.loginFormPanel = loginFormPanel;
	}

	public JLabel getLbUsername() {
		return lbUsername;
	}

	public void setLbUsername(JLabel lbUsername) {
		this.lbUsername = lbUsername;
	}

	public JButton getBtnLogin() {
		return btnLogin;
	}

	public void setBtnLogin(JButton btnLogin) {
		this.btnLogin = btnLogin;
	}

	public JButton getBtnQuit() {
		return btnQuit;
	}

	public void setBtnQuit(JButton btnQuit) {
		this.btnQuit = btnQuit;
	}

	public JPanel getBtnPanel() {
		return btnPanel;
	}

	public void setBtnPanel(JPanel btnPanel) {
		this.btnPanel = btnPanel;
	}

	public JPanel getRootPanel() {
		return rootPanel;
	}

	public void setRootPanel(JPanel rootPanel) {
		this.rootPanel = rootPanel;
	}
}
