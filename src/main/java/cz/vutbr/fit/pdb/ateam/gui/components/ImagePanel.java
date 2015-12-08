package cz.vutbr.fit.pdb.ateam.gui.components;

import cz.vutbr.fit.pdb.ateam.utils.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jakub on 02.12.2015.
 */
public class ImagePanel extends JPanel {
	private BufferedImage originalImage = null;
	private JPanel parentPanel = null;

	public ImagePanel(InputStream inputStream, JPanel parentPanel) {
		try {
			this.parentPanel = parentPanel;
			this.originalImage = ImageIO.read(inputStream);
		} catch (IOException e) {
			Logger.createLog(Logger.ERROR_LOG, "IOException: " + e.getMessage());
		}
	}

	public ImagePanel(BufferedImage bufferedImage, JPanel parentPanel) {
		this.originalImage = bufferedImage;
		this.parentPanel = parentPanel;
	}

	public ImagePanel(JPanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	public void setParentPanel(JPanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	public ImagePanel copy() {
		return new ImagePanel(originalImage, parentPanel);
	}

	public byte[] getByteArray() {
		try {
			if(originalImage == null) return null;

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "png", outputStream);

			return outputStream.toByteArray();
		} catch (IOException e) {
			Logger.createLog(Logger.ERROR_LOG, "IOException : " + e.getMessage());
			return null;
		}
	}

	public void changeImage(File file) {
		changeImageFromObject(file);
	}

	public void changeImage(InputStream inputStream) {
		changeImageFromObject(inputStream);
	}

	private void changeImageFromObject(Object object) {
		try {
			if(object instanceof File) {
				originalImage = ImageIO.read((File) object);
			} else if(object instanceof InputStream) {
				originalImage = ImageIO.read((InputStream) object);
			}

			revalidate();
			repaint();
		} catch (IOException e) {
			Logger.createLog(Logger.ERROR_LOG, "IOException: " + e.getMessage());
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if(originalImage == null) return;

		super.paintComponent(g);

		Image scaledImage;
		if(parentPanel != null)
			scaledImage = originalImage.getScaledInstance(parentPanel.getWidth(), parentPanel.getHeight(), Image.SCALE_SMOOTH);
		else
			scaledImage = originalImage;

		g.drawImage(scaledImage, 0, 0, null);
	}
}
