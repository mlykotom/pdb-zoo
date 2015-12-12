package cz.vutbr.fit.pdb.ateam.observer;

import cz.vutbr.fit.pdb.ateam.controller.MenuBarController;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import java.awt.event.HierarchyBoundsAdapter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Observer for app state changed (saved,loading,..)
 *
 * @author Tomas Mlynaric
 */
public class AppStateChangedObservable{
	private static AppStateChangedObservable instance = new AppStateChangedObservable();
	private MenuBarController mainFrameController;
	private Timer timer = new Timer();

	public synchronized static AppStateChangedObservable getInstance() {
		return instance;
	}

	public void notifyStateChanged(){
		notifyStateChanged("", false);
	}

	public void notifyStateChanged(String state){
		notifyStateChanged(state, false);
	}

	synchronized public void notifyStateChanged(String state, boolean hideAutomatically) {
		mainFrameController.appStateChangedListener(state);
		if(hideAutomatically){
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					notifyStateChanged();
				}
			}, 5 * 1000);
		}
	}

	public void subscribe(MenuBarController menuBarController) {
		this.mainFrameController = menuBarController;
	}
}
