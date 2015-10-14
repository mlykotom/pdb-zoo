package gui;

import controller.ZooMapFormController;

import javax.swing.*;

/**
 * Created by Jakub on 14.10.2015.
 */
public class ZooMapForm extends JPanel {
	private final ZooMapFormController controller;

	public ZooMapForm() {
		this.controller = new ZooMapFormController(this);
		initUI();
	}

	private void initUI() {

	}
}
