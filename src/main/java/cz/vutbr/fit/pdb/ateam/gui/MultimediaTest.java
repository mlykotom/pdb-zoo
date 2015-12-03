package cz.vutbr.fit.pdb.ateam.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.components.ImagePanel;
import cz.vutbr.fit.pdb.ateam.gui.dialog.CompareImagesDialog;
import cz.vutbr.fit.pdb.ateam.model.multimedia.ImageModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

/**
 * Created by Jakub on 02.12.2015.
 */
public class MultimediaTest extends JPanel {
	private static final int IMAGE_WIDTH = 200;
	private static final int IMAGE_HEIGHT = 200;

	private JPanel rootPanel;
	private JButton saveImageButton;
	private JButton openFileButton;
	private JPanel imagePanelParent;
	private ImagePanel imagePanel;
	private JButton rotateButton;
	private JTextField idTextField;
	private JButton loadButton;
	private JButton compareButton;
	private JFileChooser fileChooser;

	private ImageModel model;

	public MultimediaTest() {
		add(rootPanel);

		fileChooser = new JFileChooser();

		Utils.setComponentFixSize(imagePanelParent, IMAGE_WIDTH, IMAGE_HEIGHT);

		openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(rootPanel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					System.out.println("File Selected: " + file.getName() + "!");

					if (model == null) model = new ImageModel(0, file.getName());

					imagePanel = new ImagePanel(file, imagePanelParent);
					Utils.changePanelContent(imagePanelParent, imagePanel);

				} else {
					System.out.println("Cancel button pressed!");
				}
			}
		});

		rotateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (imagePanel == null) return;

				imagePanel.rotateImage();
			}
		});

		saveImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (imagePanel == null) return;

				model.setImageByteArray(imagePanel.getByteArray());
				model.setIsChanged(true);

				try {
					DataManager.getInstance().saveModel(model);
				} catch (DataManagerException e1) {
					Logger.createLog(Logger.ERROR_LOG, e1.getMessage());
				}

			}
		});

		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(idTextField.getText() == null) return;

				Long id = Long.parseLong(idTextField.getText());

				try {
					model = DataManager.getInstance().getImage(id);
				} catch (DataManagerException e1) {
					Logger.createLog(Logger.ERROR_LOG, e1.getMessage());
				}

				try {
					imagePanel = new ImagePanel(model.getImage().getDataInStream(), imagePanelParent);
					Utils.changePanelContent(imagePanelParent, imagePanel);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		compareButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CompareImagesDialog dialog = new CompareImagesDialog();
				dialog.pack();
				dialog.setVisible(true);
			}
		});
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		rootPanel = new JPanel();
		rootPanel.setLayout(new GridLayoutManager(5, 3, new Insets(15, 15, 15, 15), -1, -1));
		saveImageButton = new JButton();
		saveImageButton.setText("save");
		rootPanel.add(saveImageButton, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		openFileButton = new JButton();
		openFileButton.setText("open");
		rootPanel.add(openFileButton, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		imagePanelParent = new JPanel();
		imagePanelParent.setLayout(new CardLayout(0, 0));
		imagePanelParent.setBackground(new Color(-1114881));
		imagePanelParent.setForeground(new Color(-1));
		rootPanel.add(imagePanelParent, new GridConstraints(0, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		imagePanelParent.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-16777216)));
		rotateButton = new JButton();
		rotateButton.setText("rotate");
		rootPanel.add(rotateButton, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		idTextField = new JTextField();
		rootPanel.add(idTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		loadButton = new JButton();
		loadButton.setText("load");
		rootPanel.add(loadButton, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		compareButton = new JButton();
		compareButton.setText("compare");
		rootPanel.add(compareButton, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
