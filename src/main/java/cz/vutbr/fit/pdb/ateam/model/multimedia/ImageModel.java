package cz.vutbr.fit.pdb.ateam.model.multimedia;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import oracle.ord.im.OrdImage;

/**
 * Created by Jakub on 02.12.2015.
 */
public class ImageModel extends BaseModel {

	private OrdImage image;
	private byte[] imageByteArray;

	public ImageModel(long id, String name) {
		super(id, name);
	}

	public byte[] getImageByteArray() {
		return imageByteArray;
	}

	public void setImageByteArray(byte[] imageByteArray) {
		this.imageByteArray = imageByteArray;
	}

	public void setImage(OrdImage image) {
		this.image = image;
	}

	public OrdImage getImage() {
		return this.image;
	}

	public ImageModel copy() {
		ImageModel newModel = new ImageModel(this.getId(), this.getName());
		newModel.setImage(this.getImage());

		return newModel;
	}

	@Override
	public String getTableName() {
		return "test_images";
	}
}
