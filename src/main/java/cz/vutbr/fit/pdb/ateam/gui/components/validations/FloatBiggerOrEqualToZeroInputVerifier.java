package cz.vutbr.fit.pdb.ateam.gui.components.validations;

import javax.swing.*;

/**
 * This class should be used if you need to validate Bigger than or equal to zero Float input on a JTextField component.
 *
 * @Author Tomas Hanus on 12/13/2015.
 */
public class FloatBiggerOrEqualToZeroInputVerifier extends InputVerifier {
	@Override
	public boolean verify(JComponent input) {
		String text = ((JTextField) input).getText();
		try {
			Float number = Float.valueOf(text);
			if (number < 0) {
				((JTextField) input).setText(String.valueOf(0.0));
				JOptionPane.showMessageDialog(null,
						"Error: Please enter number bigger than or equal to 0", "Error Massage",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (NumberFormatException e) {
			((JTextField) input).setText(String.valueOf(0.0));
			JOptionPane.showMessageDialog(null,
					"Error: Input must be a floating point number", "Error Massage",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
