package app;
import appOutput.AppData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
//
public class TipAnalytics {

    @SuppressWarnings("unused")
	private AppData appData;

    public TipAnalytics(AppData appData) {
        this.appData = appData;
    }
    
    public double calculateAvgPH(List<String> bestDays) {
        double totalTips = 0.0;
        double totalHours = 0.0;
        
        if (bestDays == null || bestDays.isEmpty()) {
            return 0.0; 
        }
        
        String placeholders = String.join(", ", bestDays.stream().map(day -> "?").toArray(String[]::new));
        String sql = 
        			"SELECT SUM(tr.cashTip + tr.cardTip) AS total_tips, SUM(s.totalDuration) AS total_hours " +
                     "FROM shift s " +
                     "JOIN tip_record tr ON s.shiftId = tr.shiftId " +
                     "WHERE DAYNAME(s.date) IN (" + placeholders + ")" ;
                     
        
        try (Connection conn = AppData.getConnection();
        	PreparedStatement pre = conn.prepareStatement(sql.toString())){

        	for (int i = 0; i < bestDays.size(); i++) {
                pre.setString(i + 1, bestDays.get(i).toUpperCase());
            }
        	
        	try (ResultSet rs = pre.executeQuery()) {
                if (rs.next()) {
                    totalTips = rs.getDouble("total_tips");
                    totalHours = rs.getDouble("total_hours");  }
            }
        } catch (SQLException e) {
        	System.err.println("Error calculating average tips: " + e.getMessage());
            e.printStackTrace(); 
            }
        return totalHours > 0 ? totalTips / totalHours : 0.0;
    }

    public List<String> getBestDaysList() {
    
    	List<String> bestDays = new ArrayList<>();
    	String sql = "SELECT DAYNAME(s.date) AS DayOfWeek, SUM(tr.cashTip + tr.cardTip) AS TotalTips "
    			+ "        FROM shift s "
    			+ "        JOIN tip_record tr ON s.shiftId = tr.shiftId "
    			+ "        GROUP BY DayOfWeek "
    			+ "        ORDER BY TotalTips DESC "
    			+ "        LIMIT 2; ";
    	
    	 try (Connection conn = AppData.getConnection();
                 PreparedStatement pre = conn.prepareStatement(sql);
                 ResultSet rs = pre.executeQuery()) {

                while (rs.next()) {
                    bestDays.add(rs.getString("DayOfWeek")); }
            } catch (SQLException e) {
                e.printStackTrace();}
            return bestDays;
        }
    }
