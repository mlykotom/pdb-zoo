package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tomas Hanus
 */
public class ContentPanelObserverSubject {
	private static ContentPanelObserverSubject instance = new ContentPanelObserverSubject();
	private List<ObjectSelectionChangedListener> objectSelectionChangedListeners = new ArrayList<ObjectSelectionChangedListener>();

	public void notifyAllObjectSelectionChangedListeners(){
		for (ObjectSelectionChangedListener listener :  this.objectSelectionChangedListeners){
			listener.notifyObjectSelectionChanged();
		}
	}

	/**
	 * @return
	 */
	public synchronized static ContentPanelObserverSubject getInstance(){
		return instance;
	}

	/**
	 * @param listener
	 */
	public void subscribeForSelectionChange(ObjectSelectionChangedListener listener){
		this.objectSelectionChangedListeners.add(listener);
	}

	public interface ObjectSelectionChangedListener{
		void notifyObjectSelectionChanged(SpatialObjectModel spatialObjectModel);
	}
}

