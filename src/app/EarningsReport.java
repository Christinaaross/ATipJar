package app;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import appOutput.AppData;

public class EarningsReport {
	
	@SuppressWarnings("unused")
	private AppData appData;
	
	public EarningsReport (AppData appData) {
		this.appData = appData;
	}
	
	public void writeCSVD(String fileName) {
		String query = "SELECT u.userId, u.firstName, s.date, s.totalDuration, tr.cashTip, tr.cardTip " +
						"FROM user u " + 
						"JOIN shift s ON u.userId = s.userId " +
						"JOIN tip_record tr ON s.shiftId = tr.shiftId ";
						
	try (Connection connection = AppData.getConnection();
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {				
		
		writer.print("UserID,Name,date,shiftduration,CashTip,CardTip\n");
		
		 while (rs.next()) {
             writer.print(rs.getInt("userId"));
             writer.print(",");
             writer.print(rs.getString("firstName"));
             writer.print(",");
             writer.print(rs.getDate("date"));
             writer.print(",");
             writer.print(rs.getBigDecimal("totalDuration"));
             writer.print(",");
             writer.print(rs.getBigDecimal("cashTip"));
             writer.print(",");
             writer.println(rs.getBigDecimal("cardTip"));
             
	}
		 System.out.println("Data successfully written to file: " + fileName);
		
	}catch (SQLException ex) {
        System.out.println("Database error while generating report.");
        ex.printStackTrace();
    } catch (IOException ex) {
        System.out.println("Something went wrong when writing the file.");
        ex.printStackTrace();
	
}
	}
}

		
	
