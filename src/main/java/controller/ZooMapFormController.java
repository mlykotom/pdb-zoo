package controller;

import gui.ZooMapForm;

/**
 * Class controls all events occurred in ZooMapForm.
 *
 * @author Jakub Tutko
 */
public class ZooMapFormController extends Controller {
	private ZooMapForm form;

	/**
	 * Constructor saves instance of the ZooMapForm as local
	 * variable. This form is than used for all changes made
	 * in ZooMapForm.
	 *
	 * @param zooMapForm instance of the ZooMapForm
	 */
	public ZooMapFormController(ZooMapForm zooMapForm) {
		super();
		this.form = zooMapForm;
	}
}
