package app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import org.eclipse.wb.swing.FocusTraversalOnArray;

import appOutput.AppData;

import java.awt.Component;
import javax.swing.ListSelectionModel;
import java.awt.Color;

public class EarningsReportUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEarningsReport;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    EventQueue.invokeLater(() -> {
	        try {
	            AppData appData = new AppData(); // AppData implemented
	            EarningsReport earningsReport = new EarningsReport(appData);
	            EarningsReportUI frame = new EarningsReportUI(earningsReport);
	            frame.setVisible(false); // false so screen doesnt populate
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}

	/**
	 * Create the frame.
	 */
	public EarningsReportUI(EarningsReport earningsReport) {
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setBounds(200, 200, 650, 600);
	    contentPane = new JPanel();
	    contentPane.setToolTipText("Earnings Report");
	    contentPane.setBorder(null);

	    setContentPane(contentPane);
	    contentPane.setLayout(null);
	    
	    txtEarningsReport = new JTextField();
	    txtEarningsReport.setForeground(new Color(240, 248, 255));
	    txtEarningsReport.setBackground(new Color(128, 0, 0));
	    txtEarningsReport.setFont(new Font("Times New Roman", Font.BOLD, 12));
	    txtEarningsReport.setHorizontalAlignment(SwingConstants.CENTER);
	    txtEarningsReport.setText("Earnings Report");
	    txtEarningsReport.setBounds(25, 11, 572, 20);
	    contentPane.add(txtEarningsReport);
	    txtEarningsReport.setColumns(10);
	    
	    table = new JTable();
	    table.setBackground(Color.WHITE);
	    table.setCellSelectionEnabled(true);
	    table.setColumnSelectionAllowed(true);
	    table.setRowSelectionAllowed(true);
	    table.setBorder(null);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    table.setShowVerticalLines(false);
	    table.setFillsViewportHeight(true);
	    table.setFont(new Font("Arial", Font.PLAIN, 11));
	    table.setBounds(25, 32, 572, 471);
	    contentPane.add(table);
	    
	    JScrollBar scrollBar = new JScrollBar();
	    scrollBar.setBounds(580, 32, 17, 472);
	    contentPane.add(scrollBar);
	    contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{txtEarningsReport, table, scrollBar}));
	    
	    // Populate table with earnings data
	    populateTableWithEarnings(earningsReport);
	}

	private void populateTableWithEarnings(EarningsReport earningsReport) {
	    // Define column names for the table
	    String[] columnNames = {"tipID", "Name", "Date", "Shift Duration", "Cash Tip", "Card Tip"};

	    // Create a DefaultTableModel with the column names
	    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

	    // Data from EarningsReport
	    List<Object[]> data = earningsReport.getEarningsData();

	    // Add each row of data to the table model
	    for (Object[] row : data) {
	        tableModel.addRow(row);
	    }

	    // Set the model to the JTable
	    table.setModel(tableModel);
	
	}
	// earnings report 
	public List<Object[]> getEarningsData() {
	    String query = "SELECT tr.tipID, u.firstName, s.date, s.totalDuration, tr.cashTip, tr.cardTip " +
	                   "FROM user u " +
	                   "JOIN shift s ON u.userId = s.userId " +
	                   "JOIN tip_record tr ON s.shiftId = tr.shiftId";
	    List<Object[]> data = new ArrayList<>();
	    
	    try (Connection connection = AppData.getConnection();
	         PreparedStatement stmt = connection.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {
	
	        while (rs.next()) {
	            Object[] row = {
	                rs.getInt("tipID"),
	                rs.getString("firstName"),
	                rs.getDate("date"),
	                rs.getBigDecimal("totalDuration"),
	                rs.getBigDecimal("cashTip"),
	                rs.getBigDecimal("cardTip")
	            };
	            data.add(row);
	        }
	    } catch (SQLException ex) {
	        System.err.println("Database error while fetching data: " + ex.getMessage());
	        ex.printStackTrace();
	    }
	    
	    return data;
	}

}
