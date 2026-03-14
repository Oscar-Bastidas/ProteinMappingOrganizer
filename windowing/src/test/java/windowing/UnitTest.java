/*package windowing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest 
    extends TestCase
{
        public AppTest( String testName )
    {
        super( testName );
    }

        public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

        public void testApp()
    {
        assertTrue( true );
    }
}*/

/*package windowing;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class IterativeLoaderTest {

    @Test
    void testTableJoins_WritesFullSQLToFile() throws Exception {
        // === Arrange ===
        String user = "root";
        String password = "password";
        String database = "testdb";
        int startIncrementor = 1;
        int endIncrementor = 5;
        int totalQuantity = 100;
        int currentResultsTableIndex = 1;

        // Mock the JDBC objects
        Connection mockConn = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);

        // When DriverManager.getConnection(...) is called, return the mock connection
        mockStatic(DriverManager.class);
        when(DriverManager.getConnection(anyString())).thenReturn(mockConn);
        when(mockConn.createStatement()).thenReturn(mockStatement);

        // Capture the SQL that gets executed
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
	when(mockStatement.execute(anyString())).thenReturn(true);

        //doNothing().when(mockStatement).execute(sqlCaptor.capture());

        // === Act ===
        IterativeLoader loader = new IterativeLoader(
                user, password, database, startIncrementor, endIncrementor,
                totalQuantity, currentResultsTableIndex
        );
        loader.tableJoins(); // this will call FlexibleJoin internally

        // === Assert / Write Output ===
        String executedSQL = String.join("\n", sqlCaptor.getAllValues());

        // Write to file for manual inspection / CLI execution
        try (FileWriter fw = new FileWriter("target/full_query.sql")) {
            fw.write(executedSQL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("✅ SQL written to: target/full_query.sql");
        System.out.println("=== Preview of captured SQL ===");
        System.out.println(executedSQL.substring(0, Math.min(1000, executedSQL.length())) + "...");
    }
}*/

/*
package windowing;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.mockito.Mockito.*;

public class IterativeLoaderTest {

    @Test
    void testTableJoins_WritesFullSQLToFile() throws Exception {
        String user = "root";
        String password = "password";
        String database = "testdb";
        int startIncrementor = 1;
        int endIncrementor = 50;
        int totalQuantity = 200;
        int currentResultsTableIndex = 1;

        // Mock JDBC behavior
        Connection mockConn = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        mockStatic(DriverManager.class);
        when(DriverManager.getConnection(anyString())).thenReturn(mockConn);
        when(mockConn.createStatement()).thenReturn(mockStatement);
        when(mockStatement.execute(anyString())).thenReturn(true);

        // Run the actual logic under test
        IterativeLoader loader = new IterativeLoader(
                user, password, database, startIncrementor, endIncrementor,
                totalQuantity, currentResultsTableIndex
        );
        loader.tableJoins();

        // ✅ Capture all SQL statements that were executed
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockStatement, atLeastOnce()).execute(sqlCaptor.capture());

        // Combine into a single SQL script
        String executedSQL = String.join("\n\n", sqlCaptor.getAllValues());

        // Write it to a file
        try (FileWriter fw = new FileWriter("target/full_query.sql")) {
            fw.write(executedSQL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("✅ SQL written to: target/full_query.sql");
        System.out.println("=== Preview of captured SQL ===");
        System.out.println(executedSQL.substring(0, Math.min(1000, executedSQL.length())) + "...");
    }
}*/



package windowing;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UnitTest {

    @Test
    void testWindowingMain_WritesAllSQLToFile() throws Exception {
        // Simulate args[] to main)
        String[] args = {
                "root",           // user
                "password",       // password
                "6vsb_b_vs_c",         // database
                "1",              // start
                "20",             // end
                "200",            // totalQuantity
                "1"               // resultsTableIndex
        };

        // Mock static DriverManager and related JDBC objects
        Connection mockConn = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);

        mockStatic(DriverManager.class);
        when(DriverManager.getConnection(anyString())).thenReturn(mockConn);
        when(mockConn.createStatement()).thenReturn(mockStatement);
        when(mockStatement.execute(anyString())).thenReturn(true);

        // Capture *every* SQL command executed across all iterations
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        // Run the *actual* program entry point
        windowing.main(args);

        // Verify interactions and capture all SQL
        verify(mockStatement, atLeastOnce()).execute(sqlCaptor.capture());

        // Combine all captured SQL statements into one string
        StringBuilder fullSQL = new StringBuilder();
        for (String sql : sqlCaptor.getAllValues()) {
            fullSQL.append(sql).append("\n\n");
        }

        // Write all SQL to file
        try (FileWriter fw = new FileWriter("target/full_query.sql")) {
            fw.write(fullSQL.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("✅ All SQL queries written to target/full_query.sql");
        System.out.printf("Captured %d total SQL statements%n", sqlCaptor.getAllValues().size());
        if (fullSQL.length() > 1000) {
            System.out.println("=== SQL Preview ===");
            System.out.println(fullSQL.substring(0, 1000) + "...\n[truncated]");
        } else {
            System.out.println(fullSQL);
        }
    }
}


