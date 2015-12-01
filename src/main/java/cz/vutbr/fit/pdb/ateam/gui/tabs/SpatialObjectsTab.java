package cz.vutbr.fit.pdb.ateam.gui.tabs;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cz.vutbr.fit.pdb.ateam.controller.SpatialObjectTabController;
import cz.vutbr.fit.pdb.ateam.gui.ContentPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Tomas Mlynaric on 22.10.2015.
 */
public class SpatialObjectsTab extends JPanel {
	private final ContentPanel mainPanel;
	private JPanel rootPanel;
	private SpatialObjectTabController spatialObjectTabController;

	public SpatialObjectsTab(ContentPanel mainPanel) {
		spatialObjectTabController = new SpatialObjectTabController(this);
		this.mainPanel = mainPanel;
		add(rootPanel);
	}

	public ContentPanel getMainPanel() {
		return mainPanel;
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
		rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return rootPanel;
	}
}
