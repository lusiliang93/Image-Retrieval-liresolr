package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestJDBC
{

	public static void main(String[] args)
	{
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;

		try
		{
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/solr?user=solr&password=solr");

			statement = conn.createStatement();

			rs = statement.executeQuery("select * from tb_user");

			while (rs.next())
			{
				System.out.println(rs.getString("id"));
				System.out.println(rs.getString("name"));
			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != rs)
					rs.close();
				if (null != statement)
					statement.close();
				if (null != conn)
					conn.close();
			}
			catch (SQLException e2)
			{
				e2.printStackTrace();
			}

		}

	}
}
