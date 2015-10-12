package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by Jakub on 10.10.2015.
 */
public class MenuFrame extends JFrame {

	// TODO

	public MenuFrame() {
		initUI();
	}

	private void initUI() {

		/* creating button */
		JButton quitButton = new JButton("Quit");
		quitButton.setToolTipText("Quit application");

		/* action on button click */
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		createLayout(quitButton);
		createMenuBar();

		/* window title */
		setTitle("ZOO IS");

		/* size of the window */
		setSize(300, 200);

		/* window will be at the center of the screen */
		setLocationRelativeTo(null);

		/* operation for X button in the upper right corner */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void createLayout(JComponent... arg) {

		JPanel pane = (JPanel) getContentPane();
		GroupLayout gl = new GroupLayout(pane);
		pane.setLayout(gl);

		/* sets gaps between elements */
		gl.setAutoCreateContainerGaps(true);

		gl.setHorizontalGroup(gl.createParallelGroup()
				.addComponent(arg[0])
		);

		gl.setVerticalGroup(gl.createSequentialGroup()
				.addComponent(arg[0])
		);

		pack();
	}

	private void createMenuBar() {

		JMenuBar menubar = new JMenuBar();
		ImageIcon icon = new ImageIcon("exit.png");

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		JMenuItem eMenuItem = new JMenuItem("Exit", icon);
		eMenuItem.setMnemonic(KeyEvent.VK_E);
		eMenuItem.setToolTipText("Exit application");
		eMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		JMenuItem eMenuItem1 = new JMenuItem("Open");
		JMenuItem eMenuItem2 = new JMenuItem("Save");

		file.add(eMenuItem1);
		file.add(eMenuItem2);
		file.addSeparator();
		file.add(eMenuItem);
		menubar.add(file);

		setJMenuBar(menubar);
	}

}
