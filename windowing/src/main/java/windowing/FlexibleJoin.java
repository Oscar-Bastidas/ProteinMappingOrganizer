package windowing;

import java.util.ArrayList;
import java.util.List;

public class FlexibleJoin {
    // Instance fields
    private int windowSize;
    private int startIncrementor;
    private int endIncrementor;
    private int currentResultsTableIndex;
    private final int joinLimit = 61; // MariaDB's documented join limit

    // Constructor
    public FlexibleJoin(int windowSize, int startIncrementor, int endIncrementor, int currentResultsTableIndex) {
        this.windowSize = windowSize;
        this.startIncrementor = startIncrementor;
        this.endIncrementor = endIncrementor;
	this.currentResultsTableIndex = currentResultsTableIndex;
    }

    // Main method to build the SQL for joining all tables
    public String join() {
        List<String> currentTables = new ArrayList<>();
        for (int i = startIncrementor; i <= endIncrementor; i++) {
            currentTables.add("map" + i);
        }

        int round = 1;
        List<String> nextTables;
        StringBuilder allSQL = new StringBuilder();

        while (currentTables.size() > 1) {
            nextTables = new ArrayList<>();
            for (int i = 0; i < currentTables.size(); i += joinLimit) {
                List<String> batch = currentTables.subList(i, Math.min(i + joinLimit, currentTables.size()));
                String resultTable = (currentTables.size() <= joinLimit)
                        ? "20_master_windows_results" + String.valueOf(currentResultsTableIndex)
                        : "20theResults" + round + "_" + (i / joinLimit + 1);
                allSQL.append(buildJoinSQL(batch, resultTable)).append("\n");
                nextTables.add(resultTable);
            }
            currentTables = nextTables;
            round++;
        }

        // Optional: Clean up intermediate tables (except master_results)
        if (round > 2) {
            for (int r = 1; r < round; r++) {
                int batches = (int) Math.ceil((double) windowSize / Math.pow(joinLimit, r));
                for (int b = 1; b <= batches; b++) {
                    String tempTable = "20theResults" + r + "_" + b;
                    allSQL.append("DROP TABLE IF EXISTS ").append(tempTable).append(";").append("\n");
                }
            }
        }

        return allSQL.toString();
    }

    // Utility method to build a JOIN SQL statement for a batch of tables
    private String buildJoinSQL(List<String> tableNames, String resultTableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(resultTableName).append(" AS SELECT ");
        sb.append(tableNames.get(0)).append(".A_resname, ")
          .append(tableNames.get(0)).append(".A_resnum, ")
          .append(tableNames.get(0)).append(".A_resatom, ")
          .append(tableNames.get(0)).append(".A_atomnum, ")
          .append(tableNames.get(0)).append(".B_resname, ")
          .append(tableNames.get(0)).append(".B_resnum, ")
          .append(tableNames.get(0)).append(".B_resatom, ")
          .append(tableNames.get(0)).append(".B_atomnum ");
        sb.append("FROM ").append(tableNames.get(0));
        for (int i = 1; i < tableNames.size(); i++) {
            sb.append(" JOIN ").append(tableNames.get(i)).append(" ON ");
            sb.append(tableNames.get(i-1)).append(".A_resname = ").append(tableNames.get(i)).append(".A_resname AND ")
              .append(tableNames.get(i-1)).append(".A_resnum = ").append(tableNames.get(i)).append(".A_resnum AND ")
              .append(tableNames.get(i-1)).append(".A_resatom = ").append(tableNames.get(i)).append(".A_resatom AND ")
              .append(tableNames.get(i-1)).append(".A_atomnum = ").append(tableNames.get(i)).append(".A_atomnum AND ")
              .append(tableNames.get(i-1)).append(".B_resname = ").append(tableNames.get(i)).append(".B_resname AND ")
              .append(tableNames.get(i-1)).append(".B_resnum = ").append(tableNames.get(i)).append(".B_resnum AND ")
              .append(tableNames.get(i-1)).append(".B_resatom = ").append(tableNames.get(i)).append(".B_resatom AND ")
              .append(tableNames.get(i-1)).append(".B_atomnum = ").append(tableNames.get(i)).append(".B_atomnum ");
        }
        sb.append(";");
        return sb.toString();
    }
}

