import gui.LoginFrame;
import oracle.jdbc.pool.OracleDataSource;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;

/**
 * Created by mlyko on 06.10.2015.
 */
public class Main {
	public static void main(String[] args) {
		/**
		 * GUI
		 */

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				LoginFrame loginFrame = new LoginFrame();
				loginFrame.setVisible(true);
			}
		});

		/**
		 * THE END OF GUI
		 */


		LocalTime currentTime = LocalTime.now();
		System.out.println("---------");
		System.out.println("The current local time is: " + currentTime);

		// 2. cast cviceni
		try {
			// create a OracleDataSource instance
			OracleDataSource ods = new OracleDataSource();
			ods.setURL("jdbc:oracle:thin:@gort.fit.vutbr.cz:1521:dbgort");
			/**
			 * *
			 * To set System properties, run the Java VM with the following at
			 * its command line: ... -Dlogin=LOGIN_TO_ORACLE_DB
			 * -Dpassword=PASSWORD_TO_ORACLE_DB ... or set the project
			 * properties (in NetBeans: File / Project Properties / Run / VM
			 * Options)
			 */
			ods.setUser("XMLYNA06");
			ods.setPassword("04h3xlr6");
			/**
			 *
			 */
			// connect to the database
			Connection conn = ods.getConnection();
			try {
				// create a Statement
				Statement stmt = conn.createStatement();
				try {
					// select something from the system's dual table
					ResultSet rset = stmt.executeQuery(
							"select 1+2 as col1, 3-4 as col2 from dual");
					try {
						// iterate through the result and print the values
						while (rset.next()) {
							System.out.println("col1: '" + rset.getString(1)
									+ "'\tcol2: '" + rset.getString(2) + "'");
						}
					} finally {
						rset.close(); // close the ResultSet
					}
				} finally {
					stmt.close(); // close the Statement
				}
			} finally {
				conn.close(); // close the connection
			}
		} catch (SQLException sqlEx) {
			System.err.println("SQLException: " + sqlEx.getMessage());
		}
	}
}
