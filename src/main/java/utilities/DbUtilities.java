package utilities;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.Litmus.Response.MySingleton;

public class DbUtilities {
	
	public static Connection getCon() throws Exception {
		Connection con = null;

		try {
			FileInputStream fis=null;
			
			MySingleton ms = MySingleton.getInstance(fis);
			Class.forName(ms.getDriver());
			con = DriverManager.getConnection(ms.getJdbcURL(),ms.getUsername(),ms.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Error configurations in SQL, PLEASE CHECK............");
			//e.printStackTrace();
		System.exit(0);
		}
		return con;
	}

}
