package cz.vutbr.fit.pdb.ateam.model.multimedia;

import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import oracle.ord.im.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Jakub on 02.12.2015.
 */
public class ImageModel extends BaseModel {

	private OrdImage image;
	private String imagePath;

	public ImageModel(long id, String name) {
		super(id, name);
	}

	public void setImage(OrdImage image) {
		this.image = image;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public OrdImage getImage() {
		return this.image;
	}

	@Override
	public String getTableName() {
		return "test_images";
	}
}
