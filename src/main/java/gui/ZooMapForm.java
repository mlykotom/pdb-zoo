package gui;

import com.intellij.uiDesigner.core.GridLayoutManager;
import controller.ZooMapFormController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jakub on 14.10.2015.
 */
public class ZooMapForm extends JPanel {
	private final ZooMapFormController controller;

	public ZooMapForm() {
		this.controller = new ZooMapFormController(this);
		initUI();
	}

	public void initUI() {

	}

}
