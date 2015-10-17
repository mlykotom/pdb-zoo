package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import controller.LoginFormController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Form for Log In form.
 * <p/>
 * This form is shown at the start of the application. Form
 * includes form, which is used to connect with the database
 * server. User can set there his UserName and Password. If connection
 * ends successfully, application disposes this form and opens
 * MainFrame, otherwise user can not move out of this form
 * without closing the application.
 *
 * @author Jakub Tutko
 */
public class LoginForm extends JFrame {
	private final LoginFormController controller;

	private JPanel rootPanel;
	private JTextField userNameTextField;
	private JPasswordField passwordPasswordField;
	private JButton loginButton;
	private JButton quitButton;

	/**
	 * Constructor creates controller and initialize form content.
	 */
	public LoginForm() {
		controller = new LoginFormController(this);

		initUI();
	}

	/**
	 * Initialization of the LoginForm content.
	 */
	public void initUI() {
		userNameTextField.setText("XMLYNA06"); // TODO: remove later
		passwordPasswordField.setText("04h3xlr6"); // TODO: remove later

		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.loginButtonAction();
			}
		});
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.quitButtonAction();
			}
		});

		setContentPane(rootPanel);

		pack();
		setTitle("ZOO");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public JTextField getUserNameTextField() {
		return userNameTextField;
	}

	public JPasswordField getPasswordPasswordField() {
		return passwordPasswordField;
	}

}
