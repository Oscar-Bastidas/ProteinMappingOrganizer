package promaporg;

import java.sql.*;
import java.util.Arrays;

// This custom class calculates the population standard deviation of the surviving interactions in all snapshots

public class MetricStdDevs {
    private String dbURL;
    private int start;
    private int end;
    private String database;

    public MetricStdDevs(String user, String password, int start, int end, String database) {
        this.dbURL = "jdbc:mariadb://127.0.0.1:3306/" + database +
                "?user=" + user +
                "&password=" + password +
                "&allowMultiQueries=true&useSSL=false";
        this.start = start;
        this.end = end;
        this.database = database;
    }

    // Helper method to calculate standard deviation
    private double stddev(double[] arr) {
        double mean = Arrays.stream(arr).average().orElse(0.0);
        double variance = Arrays.stream(arr)
                                .map(x -> (x - mean) * (x - mean))
                                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    public void calculateStdDevs() {
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            conn.setAutoCommit(false);
            System.out.println("\n\n\nConnected.\n\n\n");

            // 1. Ensure stddev columns exist
            try (Statement alterStmt = conn.createStatement()) {
                alterStmt.executeUpdate(
                        "ALTER TABLE master_results " +
                        "ADD COLUMN IF NOT EXISTS metric1STDDEV DOUBLE, " +
                        "ADD COLUMN IF NOT EXISTS metric2STDDEV DOUBLE, " +
                        "ADD COLUMN IF NOT EXISTS metric3STDDEV DOUBLE"
                );
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate column name")) {
                    e.printStackTrace();
                }
            }

            // 2. Get all records from master_results (including id!)
            String retrieveInteraction =
                    "SELECT id, A_resname, A_resnum, A_resatom, A_atomnum, " +
                    "B_resname, B_resnum, B_resatom, B_atomnum " +
                    "FROM master_results";
            try (
                PreparedStatement stmtRetrieval = conn.prepareStatement(retrieveInteraction);
                ResultSet rs1 = stmtRetrieval.executeQuery()
            ) {
                while (rs1.next()) {
                    int id = rs1.getInt("id");
                    String aResname = rs1.getString("A_resname");
                    String aResnum = rs1.getString("A_resnum");
                    String aResatom = rs1.getString("A_resatom");
                    String aAtomnum = rs1.getString("A_atomnum");
                    String bResname = rs1.getString("B_resname");
                    String bResnum = rs1.getString("B_resnum");
                    String bResatom = rs1.getString("B_resatom");
                    String bAtomnum = rs1.getString("B_atomnum");

                    double[] metric1COMPONENT = new double[end - start + 1];
                    double[] metric2COMPONENT = new double[end - start + 1];
                    double[] metric3COMPONENT = new double[end - start + 1];

                    // 3. For each map table, retrieve metric1, metric2, metric3
                    //for (int j = start - 1; j < end; j++) {
		    //ORIGINAL
		    for (int j = 0; j < (end - start + 1); j++) {
                        String mapTable = "map" + Integer.toString(j + start);
			//ORIGINAL
			//String mapTable = "map" + (j + 1);
                        String retrieveMetrics =
                                "SELECT metric1, metric2, metric3 FROM " + mapTable +
                                " WHERE A_resname = ? AND A_resnum = ? AND A_resatom = ? AND A_atomnum = ? " +
                                "AND B_resname = ? AND B_resnum = ? AND B_resatom = ? AND B_atomnum = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(retrieveMetrics)) {
                            stmt.setString(1, aResname);
                            stmt.setString(2, aResnum);
                            stmt.setString(3, aResatom);
                            stmt.setString(4, aAtomnum);
                            stmt.setString(5, bResname);
                            stmt.setString(6, bResnum);
                            stmt.setString(7, bResatom);
                            stmt.setString(8, bAtomnum);

                            try (ResultSet rsMetrics = stmt.executeQuery()) {
                                if (rsMetrics.next()) {
                                    metric1COMPONENT[j] = rsMetrics.getDouble("metric1");
                                    metric2COMPONENT[j] = rsMetrics.getDouble("metric2");
                                    metric3COMPONENT[j] = rsMetrics.getDouble("metric3");
                                } else {
                                    metric1COMPONENT[j] = 0.0;
                                    metric2COMPONENT[j] = 0.0;
                                    metric3COMPONENT[j] = 0.0;
                                }
                            }
                        }
                    }

                    // 4. Calculate standard deviations
                    double metric1STDDEV = stddev(metric1COMPONENT);
                    double metric2STDDEV = stddev(metric2COMPONENT);
                    double metric3STDDEV = stddev(metric3COMPONENT);

                    // 5. Update master_results with stddevs
                    String updateSql = "UPDATE master_results SET metric1STDDEV = ?, metric2STDDEV = ?, metric3STDDEV = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, metric1STDDEV);
                        updateStmt.setDouble(2, metric2STDDEV);
                        updateStmt.setDouble(3, metric3STDDEV);
                        updateStmt.setInt(4, id);
                        updateStmt.executeUpdate();
                    }
                }
                // Commit after all updates
                conn.commit();
            }
        } catch (SQLException e) {
            System.out.println("\n\n\nDatabase operation failed.\n\n\n");
            e.printStackTrace();
        }
    }
}

