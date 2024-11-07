package app;
import appOutput.AppData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//
public class TipAnalytics {

    @SuppressWarnings("unused")
	private AppData appData;

    public TipAnalytics(AppData appData) {
        this.appData = appData;
    }

    public double calculateAverageEarningsPerHour() {
        double totalTips = 0.0;
        double totalHours = 0.0;

        // SQL query to calculate total tips and total hours
        String sql = "SELECT SUM(tr.cashTip + tr.cardTip) AS total_tips, SUM(s.totalDuration) AS total_hours " +
                     "FROM shift s " +
                     "JOIN tip_record tr ON s.shiftId = tr.shiftId";
                     
        try (Connection conn = AppData.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                totalTips = rs.getDouble("total_tips");
                totalHours = rs.getDouble("total_hours");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalHours > 0 ? totalTips / totalHours : 0.0;
    }

    public String getBestDaysToWork() {
    	StringBuilder bestDays = new StringBuilder();
    	
    	String sql = "SELECT DAYNAME(s.date) AS DayOfWeek, SUM(tr.cashTip + tr.cardTip) AS TotalTips\r\n"
    			+ "        FROM shift s\r\n"
    			+ "        JOIN tip_record tr ON s.shiftId = tr.shiftId\r\n"
    			+ "        GROUP BY DayOfWeek\r\n"
    			+ "        ORDER BY TotalTips DESC\r\n"
    			+ "        LIMIT 2; -- Get top 2 days";
    	
    	 try (Connection conn = AppData.getConnection();
    	         Statement stmt = conn.createStatement();
    	         ResultSet rs = stmt.executeQuery(sql)) {

    	        while (rs.next()) {
    	            String day = rs.getString("DayOfWeek");
    	            bestDays.append(day).append(", ");
    	        }

    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
    	 
    	    if (bestDays.length() > 0) {
    	        bestDays.setLength(bestDays.length() - 2);
    	    }
    	    
        return bestDays.toString();
    }
}
