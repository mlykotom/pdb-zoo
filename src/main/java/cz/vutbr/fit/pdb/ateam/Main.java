package cz.vutbr.fit.pdb.ateam;

import cz.vutbr.fit.pdb.ateam.gui.LoginForm;

import javax.swing.*;

/**
 * Created by mlyko on 06.10.2015.
 */
public class Main {
	public static void main(String[] args) {

		/** TEST */
		/*try {
			ArrayList<SpatialObjectTypeModel> types;
			SpatialObjectTypeModel type;
			ArrayList<SpatialObjectModel> spatialObjectModels;
			DataManager.getInstance().connectDatabase("XMLYNA06", "04h3xlr6");
			types = DataManager.getInstance().getAllSpatialObjectTypes();
			type = DataManager.getInstance().getSpatialObjectType(10L);
			spatialObjectModels = DataManager.getInstance().getAllSpatialObjects();
			DataManager.getInstance().updateSpatial(spatialObjectModels.get(0));
			DataManager.getInstance().disconnectDatabase();
		} catch (DataManagerException ex) {
			System.out.println("ERROR: " + ex.getMessage());
		}
		*/
		/** ******/

		/** GUI */
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
					e.printStackTrace();
				}

				LoginForm loginForm = new LoginForm();
				loginForm.setVisible(true);
			}
		});
	}
}
