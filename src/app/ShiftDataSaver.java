package app;

import appOutput.AppData;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class ShiftDataSaver {

    private AppData appData;

    public ShiftDataSaver(AppData appData) {
        this.appData = appData;
    }

    public boolean saveShiftData(LocalDate selectedDate, BigDecimal shiftHours, BigDecimal cashTips, BigDecimal cardTips) {
        boolean isSuccessful = false;

        try (Connection connection = appData.getConnection()) {
        	 connection.setAutoCommit(false);
        	 
        	// inserts eveything successfully
            String insertShiftQuery = "INSERT INTO shift (date, totalDuration, userId) VALUES (?, ?, ?)";
           
            try (PreparedStatement shiftStmt = connection.prepareStatement(insertShiftQuery, Statement.RETURN_GENERATED_KEYS)) {
               
            	shiftStmt.setDate(1, java.sql.Date.valueOf(selectedDate));
                shiftStmt.setBigDecimal(2, shiftHours);
                shiftStmt.setInt(3, 1);  // Placeholder for user ADD IN LATER

                int rowsAffected = shiftStmt.executeUpdate();
                if (rowsAffected > 0) {
                	
                    // getting the shiftId
                    ResultSet generatedKeys = shiftStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int shiftId = generatedKeys.getInt(1);

                        // Step 2: Insert data into the `tip_record` table using the shiftId
                        String insertTipQuery = "INSERT INTO tip_record (shiftId, cashTip, cardTip) VALUES (?, ?, ?)";
                        try (PreparedStatement tipStmt = connection.prepareStatement(insertTipQuery)) {
                            tipStmt.setInt(1, shiftId);
                            tipStmt.setBigDecimal(2, cashTips);
                            tipStmt.setBigDecimal(3, cardTips);
                            tipStmt.executeUpdate();
                        }
                    }
                    connection.commit();
                    isSuccessful = true; // Data saved successfully
                }
            } catch (SQLException ex) {
            	connection.rollback(); // IF ERROR OCCURS
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return isSuccessful;
    }
}

