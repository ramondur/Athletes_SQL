package AssignmentDB2DuranRamon;

import java.sql.*;
import java.io.*;

/*It is assumed that people will only enter existing event numbers.*/

public class AssignmentDB2DuranRamon {
	/**
	 * @author Ramon Duran with studentID = r0693934
	 */
	static
	{
		try
		{
			/* Type 4 Driver */
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Could not load MySql driver.");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	public static void main (String args[])
	{
		// Variables
		String uname = null;
		String psswrd = null;
		String nameevent = null;
		int eventnr= 0;
		int nbparticipants = 0;
		/* Location of the database */
		String host = "jdbc:mysql://localhost/athletedb";
		/* Reading log-in data (username and password) */
		try
		{
			BufferedReader br1 = new BufferedReader (new InputStreamReader(System.in));
			System.out.print("Enter your username on MySql: ");
			uname = br1.readLine();
			BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter your password on MySql: ");
			psswrd = br2.readLine();
		}
		catch (IOException e)
		{
			System.out.print("Failed to get uname/passwd");
			System.out.println(":" + e.getMessage());
			System.exit(1);
		}
		/* Example of querying a database */
		try
		{
			/* Connect to MySql database */
			Connection conn = DriverManager.getConnection(host, uname, psswrd);
			System.out.println("Connection established...");
			System.out.println();
			try
			{
				BufferedReader br3= new BufferedReader(new InputStreamReader(System.in));
				String line=null;
				System.out.println();
				System.out.print("Please enter the event number (0 to close): ");
				/* Create statement */
				Statement stmt = conn.createStatement();
				/* Read input from command line */
				while ((line = br3.readLine()) != null && !line.equals("0")) 
				{
					eventnr = Integer.parseInt(line);
					/* Queries */
					String queryInfoEvent = "SELECT Name, Date, Location FROM event WHERE eventID = "+eventnr+";";
					String queryAthPerfo = "SELECT Fname, Lname, Performance_in_minutes\n" + 
							"FROM athlete\n" + 
							"INNER JOIN participation_ind\n" + 
							"USING (athleteID)\n" + 
							"WHERE eventID = "+eventnr+
							" ORDER BY Performance_in_minutes;";
					String queryNumberParti ="SELECT COUNT(*)\n" + 
							"FROM athlete\n" + 
							"INNER JOIN participation_ind\n" + 
							"USING (athleteID)\n" + 
							"WHERE eventID = "+eventnr+";";
					String queryWinnerWomen = "SELECT Fname, Lname\n" + 
							"FROM athlete\n" + 
							"INNER JOIN participation_ind\n" + 
							"USING (athleteID)\n" + 
							"WHERE eventID = "+eventnr+" AND Performance_in_minutes = \n" + 
							"(SELECT MIN(Performance_in_minutes) AS fastest\n" + 
							"	 FROM athlete\n" + 
							"	 INNER JOIN participation_ind\n" + 
							"	 USING (athleteID)\n" + 
							"	 WHERE eventID = "+eventnr+" AND sex = 'F'\n" + 
							"	 GROUP BY sex\n" + 
							"	 HAVING MIN(Performance_in_minutes));";
					String queryWinnerMen = "SELECT Fname, Lname\n" + 
							"FROM athlete\n" + 
							"INNER JOIN participation_ind\n" + 
							"USING (athleteID)\n" + 
							"WHERE eventID = "+eventnr+" AND Performance_in_minutes = \n" + 
							"(SELECT MIN(Performance_in_minutes) AS fastest\n" + 
							"	 FROM athlete\n" + 
							"	 INNER JOIN participation_ind\n" + 
							"	 USING (athleteID)\n" + 
							"	 WHERE eventID = "+eventnr+" AND sex = 'M'\n" + 
							"	 GROUP BY sex\n" + 
							"	 HAVING MIN(Performance_in_minutes));";
					/* Execute the queries and print out the results*/	
					ResultSet numberParti = stmt.executeQuery(queryNumberParti);
					numberParti.next();
					nbparticipants = Integer.parseInt(numberParti.getString(1));
					numberParti.close();
					ResultSet infoEvent = stmt.executeQuery(queryInfoEvent);
					infoEvent.next();
					nameevent = infoEvent.getString(1);
					System.out.println("The running event "+nameevent+ " is organized on "+infoEvent.getString(2)+" at "+infoEvent.getString(3));
					String repeatedStar = new String(new char[60]).replace('\0', '*');
					System.out.println(repeatedStar);
					System.out.println(nameevent+" has "+nbparticipants+" participants.");
					System.out.println();
					System.out.println("First Name // Last Name // Performance");
					System.out.println("------------------------------");
					infoEvent.close();
					ResultSet athPerfo =  stmt.executeQuery(queryAthPerfo);
					while (athPerfo.next())
					{
						System.out.print(athPerfo.getString(1));
						System.out.print((" // "));
						System.out.print(athPerfo.getString(2));
						System.out.print((" // "));
						if (athPerfo.getString(3) != null)
						{
							System.out.print(athPerfo.getString(3)+" minutes");
						}
						else
						{
							System.out.print("***");
						}
						System.out.println();
					}
					System.out.println();
					athPerfo.close();
					ResultSet winnerMen = stmt.executeQuery(queryWinnerMen);
					if (!winnerMen.next())
					{
						System.out.print("The winner of "+nameevent+" is ***");
					}
					else
					{
						System.out.print("The winner of "+nameevent+" is "+winnerMen.getString(1)+" "+winnerMen.getString(2));
					}
					winnerMen.close();
					ResultSet winnerWomen = stmt.executeQuery(queryWinnerWomen);
					if (!winnerWomen.next())
					{
						System.out.println(" (men) and *** (women).");
					}
					else
					{
						System.out.println(" (men) and "+winnerWomen.getString(1)+" "+winnerWomen.getString(2)+" (women).");
					}
					winnerWomen.close();

					System.out.println();
					System.out.print("Please enter the event number (0 to close): ");
				}
				stmt.close();
			}
			catch (IOException e)
			{
				System.exit(1);
			}
			System.out.print("END OF SESSION");
			conn.close();
			System.exit(1);
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: ");
			System.err.println(e.getMessage());
		}
	}
}
