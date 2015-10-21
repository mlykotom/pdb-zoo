package controller;

import gui.map.ZooMapPanel;

/**
 * Class controls all events occurred in ZooMapForm.
 *
 * @author Jakub Tutko
 */
public class ZooMapPanelController extends Controller{
	private ZooMapPanel form;

	/**
	 * Constructor saves instance of the ZooMapForm as local
	 * variable. This form is than used for all changes made
	 * in ZooMapForm.
	 *
	 * @param zooMapPanel instance of the ZooMapForm
	 */
	public ZooMapPanelController(ZooMapPanel zooMapPanel) {
		super();
		this.form = zooMapPanel;
	}
}
