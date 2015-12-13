package cz.vutbr.fit.pdb.ateam.model.animal;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;

/**
 * @Author Tomas Hanus on 12/12/2015.
 */
public class AnimalsShiftModel extends BaseModel {
	private static final String TABLE_NAME = "Animals_Shift";

	public AnimalsShiftModel(long id, String name) {
		super(id, name);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
