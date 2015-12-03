package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jakub on 02.12.2015.
 */
public class ImagePanel extends JPanel {
	private BufferedImage originalImage;
	private JPanel parentPanel;

	public ImagePanel(InputStream inputStream, JPanel parentPanel) {
		try {
			originalImage = ImageIO.read(inputStream);
			this.parentPanel = parentPanel;
		} catch (IOException e) {
			Logger.createLog(Logger.ERROR_LOG, "IOException: " + e.getMessage());
		}
	}

	public ImagePanel(File file, JPanel parentPanel) {
		try {
			originalImage = ImageIO.read(file);
			this.parentPanel = parentPanel;
		} catch (IOException e) {
			Logger.createLog(Logger.ERROR_LOG, "IOException: " + e.getMessage());
		}
	}

	public byte[] getByteArray() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(originalImage, "png", outputStream);
			return outputStream.toByteArray();
		} catch (IOException e) {
			Logger.createLog(Logger.ERROR_LOG, "IOException : " + e.getMessage());
			return null;
		}
	}

	public void rotateImage() {
		AffineTransform transform = new AffineTransform();
		transform.rotate(Math.toRadians(90), originalImage.getWidth()/2, originalImage.getHeight()/2);
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		originalImage = op.filter(originalImage, null);

		Utils.changePanelContent(parentPanel, this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image scaledImage = originalImage.getScaledInstance(parentPanel.getWidth(), parentPanel.getHeight(), Image.SCALE_SMOOTH);
		g.drawImage(scaledImage, 0, 0, null); // see javadoc for more info on the parameters
	}
}
