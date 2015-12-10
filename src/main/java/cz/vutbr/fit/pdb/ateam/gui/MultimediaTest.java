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
import java.util.ArrayList;
import java.util.Objects;

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
	private JButton mirrorButton;
	private JTextField idTextField;
	private JButton loadButton;
	private JButton compareButton;
	private JFileChooser fileChooser;

	private ImageModel model;

	public MultimediaTest() {
		add(rootPanel);

		fileChooser = new JFileChooser();

		Utils.setComponentFixSize(imagePanelParent, IMAGE_WIDTH, IMAGE_HEIGHT);

		imagePanel = new ImagePanel(imagePanelParent);
		Utils.changePanelContent(imagePanelParent, imagePanel);

		// BUTTONS -----------------------------------------------------------------

		openFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(rootPanel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					changeImagePanelContent(file);
				}
			}
		});

		mirrorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (model == null) {
						// TODO save model first ERROR MESSAGE
						return;
					}

					DataManager.getInstance().mirrorImage(model);
					changeImagePanelContent(model);
				} catch (DataManagerException e1) {
					e1.printStackTrace();
				}
			}
		});

		saveImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					byte[] bytes = imagePanel.getByteArray();
					if (bytes == null) {
						// TODO open something first ERROR MESSAGE
						return;
					}

					if (model == null) {
						model = new ImageModel(0, "<<new>>");
					}

					model.setImageByteArray(bytes);
					model.setIsChanged(true);
					DataManager.getInstance().saveModel(model);
				} catch (DataManagerException e1) {
					Logger.createLog(Logger.ERROR_LOG, e1.getMessage());
				}
			}
		});

		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (idTextField.getText() == null) return;

					Long id = Long.parseLong(idTextField.getText());
					model = DataManager.getInstance().getImage(id);
					changeImagePanelContent(model);
				} catch (DataManagerException e1) {
					Logger.createLog(Logger.ERROR_LOG, e1.getMessage());
				}
			}
		});

		compareButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (model == null) {
						// TODO open image and save or load model first ERROR MESSAGE
						return;
					}

					if (model.getId() == 0L) {
						// TODO model needs to be saved first ERROR MESSAGE
						return;
					}

					ArrayList<ImageModel> models;
					ImagePanel image1 = null;
					ImagePanel image2 = null;
					ImagePanel image3 = null;

					models = DataManager.getInstance().getThreeSimilarImages(model);

					if (models.size() > 0) {
						image1 = new ImagePanel(models.get(0).getImage().getDataInStream(), null);
						image2 = new ImagePanel(models.get(1).getImage().getDataInStream(), null);
						image3 = new ImagePanel(models.get(2).getImage().getDataInStream(), null);
					}

					CompareImagesDialog dialog = new CompareImagesDialog(rootPanel, imagePanel.copy(), image1, image2, image3);
					dialog.setVisible(true);

				} catch (DataManagerException e1) {
					Logger.createLog(Logger.ERROR_LOG, e1.getMessage());
				} catch (SQLException e1) {
					Logger.createLog(Logger.ERROR_LOG, "SQLException: " + e1.getMessage());
				}
			}
		});
	}

	private void changeImagePanelContent(Object obj) {
		try {
			if (obj instanceof ImageModel)
				imagePanel.changeImage(((ImageModel) obj).getImage().getDataInStream());
			else if (obj instanceof File)
				imagePanel.changeImage((File) obj);
		} catch (SQLException e) {
			Logger.createLog(Logger.ERROR_LOG, "SQLException: " + e.getMessage());
		}
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
		mirrorButton = new JButton();
		mirrorButton.setText("mirror and save");
		rootPanel.add(mirrorButton, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
