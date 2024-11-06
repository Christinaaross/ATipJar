package app;

import appOutput.AppData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TipAnalytics {

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
                     
        try (Connection conn = appData.getConnection();
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

    // Method for best days to work have to fix later
    public String getBestDaysToWork() {
        return "Friday, Saturday";
    }
}
