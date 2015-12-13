package cz.vutbr.fit.pdb.ateam.model.animal;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import oracle.ord.im.OrdImage;

import java.util.Date;

/**
 * Created by Tomas Hanus on 12/12/2015.
 */
public class AnimalModel extends BaseModel {
	private String species;
	private Long location;
	private Date dateFrom;
	private Float weight;
	private Date dateTo;
	private long shiftID;

	private OrdImage image;

	private byte[] imageByteArray;

	public AnimalModel(long id, String name, String species, Long location, Float weight, Date dateFrom, Date dateTo) {
		super(id, name);
		this.species = species;
		this.location = location;
		this.weight = weight;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public AnimalModel(long id, String name, String species, Long location, Float weight, Date dateFrom, Date dateTo, long shiftID) {
		super(id, name);
		this.species = species;
		this.location = location;
		this.weight = weight;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.shiftID = shiftID;
	}

	public AnimalModel(String name, String species) {
		this(0, name, species, Long.valueOf(1), Float.valueOf(0), null, null);
	}

	public AnimalModel(Long id, String name, String species) {
		this(id, name, species, Long.valueOf(0), Float.valueOf(0), null, null);
	}

	public Long getLocation() {
		return location;
	}


	public void setLocation(long location) {
		this.location = location;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public long getShiftID() {
		return shiftID;
	}

	public void setShiftID(long shiftID) {
		this.shiftID = shiftID;
	}

	@Override
	public String getTableName() {
		return "Animals";
	}

	public String getTemporalTableName() {
		return "Animals_Shift";
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public OrdImage getImage() {
		return image;
	}

	public void setImage(OrdImage image) {
		this.image = image;
	}

	public byte[] getImageByteArray() {
		return imageByteArray;
	}

	public void setImageByteArray(byte[] imageByteArray) {
		this.imageByteArray = imageByteArray;
	}
}
