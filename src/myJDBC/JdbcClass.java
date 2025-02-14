package myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/batch_java","root","Pass@123");
			String query = "UPDATE batch_java.practice1 SET Class = 11 WHERE Roll_Num = 112;";
			Statement st = con.createStatement();
			int feedback = st.executeUpdate(query);
			System.out.print(feedback);
			st.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("connection succesfull");
		}
	}

}
