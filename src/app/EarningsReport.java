package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import appOutput.AppData;

public class EarningsReport {
	
	@SuppressWarnings("unused")
	private AppData appData;
	
	public EarningsReport (AppData appData) {
		this.appData = appData;
	}
	
	public List<Object[]> getEarningsData() {
		String query = "SELECT u.userId, u.firstName, s.date, s.totalDuration, tr.cashTip, tr.cardTip " +
						"FROM user u " + 
						"JOIN shift s ON u.userId = s.userId " +
						"JOIN tip_record tr ON s.shiftId = tr.shiftId ";
		List<Object[]> data = new ArrayList<>();
		
	try (Connection connection = AppData.getConnection();
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery()) {
		
		//PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) 
		//writer.print("UserID,Name,date,shiftduration,CashTip,CardTip\n");
		
		while (rs.next()) {
            Object[] row = {
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getDate("date"),
                rs.getBigDecimal("totalDuration"),
                rs.getBigDecimal("cashTip"),
                rs.getBigDecimal("cardTip")
            };
            data.add(row);	
		/* while (rs.next()) {
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
          */   
	}
		 //System.out.println("Data successfully written to file: " + fileName);
		
	}catch (SQLException ex) {
        System.out.println("Database error while generating report.");
        ex.printStackTrace();
    } /*catch (IOException ex) {
        System.out.println("Something went wrong when writing the file.");
        ex.printStackTrace();}
        */
	return data;
	}
}

		
	
