import adapter.DataManager;
import exception.DataManagerException;
import gui.LoginForm;
import model.SpatialObject;
import model.SpatialObjectType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by mlyko on 06.10.2015.
 */
public class Main {
	public static void main(String[] args) {

		/** TEST */
		try {
			ArrayList<SpatialObjectType> types;
			SpatialObjectType type;
			ArrayList<SpatialObject> objects;
			DataManager.getInstance().connectDatabase("XMLYNA06", "04h3xlr6");
			types = DataManager.getInstance().getAllSpatialObjectTypes();
			type = DataManager.getInstance().getSpatialObjectType(10L);
			objects = DataManager.getInstance().getAllSpatialObjects();
			DataManager.getInstance().updateSpatialObject(objects.get(0));
			DataManager.getInstance().disconnectDatabase();
		} catch (DataManagerException ex) {
			System.out.println("ERROR: " + ex.getMessage());
		}
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
