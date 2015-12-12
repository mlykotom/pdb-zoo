package cz.vutbr.fit.pdb.ateam.utils;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class includes helpful static methods used in the whole application.
 *
 * @author Jakub Tutko
 */
public class Utils {
	private static final String FOREVER_DATE = "01-Jan-2500";
	/**
	 * Disconnects database and closes application.
	 */
	public static void whenApplicationClosing() {
		try{
			DataManager.getInstance().disconnectDatabase();
			Logger.createLog(Logger.DEBUG_LOG, "Closing application with System.exit(0).");
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, "Cannot logout DB! Closing application anyway!");
		}
	}

	/**
	 * Method set fixed size for the given JComponent.
	 *
	 * @param component JComponent to work with
	 * @param width final width of the component
	 * @param height final height of the component
	 */
	public static void setComponentFixSize( JComponent component ,int width, int height) {
		component.setMinimumSize(new Dimension(width, height));
		component.setMaximumSize(new Dimension(width, height));
		component.setPreferredSize(new Dimension(width, height));
	}

	/**
	 * Static method for changing whole content of the JPanel
	 *
	 * @param panel content will be changed in this panel
	 * @param content content, which will replace old content
	 */
	public static void changePanelContent(JPanel panel, JComponent content) {
		panel.removeAll();
		panel.add(content);
		panel.revalidate();
		panel.repaint();
	}

	public static int getMin(int num, int num2){
		if(num < num2){
			return  num;
		}

		return num2;
	}

	public static int getMax(int num, int num2){
		if(num > num2){
			return num;
		}

		return num2;
	}

	/**
	 * If you want use date only as Year, Mont, Day this function removes any extra time units (hours, minutes, ... )
	 * @param date
	 * @return
	 */
	public static Date removeTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getForeverDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
		Date foreverDate = new Date();
		try {
			foreverDate = formatter.parse(FOREVER_DATE);

		} catch (ParseException e) {
			Logger.createLog(Logger.ERROR_LOG, "getForeverDate: ParseException");
		}
		return foreverDate;
	}

	/**
	 * Changes table's button background and foreground according to button's selection.
	 *
	 * @param isSelected flag if button is selected
	 * @param button table's button
	 * @param table button
	 */
	public static void setButtonForegroundAndBackground(boolean isSelected, JButton button, JTable table) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
	}
}
