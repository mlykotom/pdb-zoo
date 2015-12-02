package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.utils.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jakub on 02.12.2015.
 */
public class ImagePanel extends JPanel {
	private BufferedImage image;

	public ImagePanel(File file) {
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			Logger.createLog(Logger.ERROR_LOG, "IOException: " + e.getMessage());
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
	}
}
