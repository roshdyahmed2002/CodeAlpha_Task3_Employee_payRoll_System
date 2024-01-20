import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnect {

    private static String DB_NAME = "EmployeePayRollSystem";
    private static String USERNAME = "postgres";
    private static String PASSWORD = "-1234-";
    private static Connection connection;

    public DbConnect(){
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + DB_NAME, USERNAME, PASSWORD);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public static Connection getConnect() {
    /*    try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + DB_NAME, USERNAME, PASSWORD);
            System.out.println("HERE");
        } catch (SQLException ex) {
            Logger.getLogger(DbConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
*/
        return connection;
    }

    public void createTables() {
        Statement statement;
        try {
            statement = connection.createStatement();

            // Creating the Employee table with hourlyRate and hoursWorked columns
            String createEmployeeTable = "CREATE TABLE Employee (" +
                    "employee_id SERIAL PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "hourly_rate INT NOT NULL," +
                    "hours_worked INT NOT NULL" +
                    ");";

            statement.execute(createEmployeeTable);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void insertEmployee(Connection connection, String name, int hourlyRate, int hoursWorked) throws Exception {
        String sql = "INSERT INTO Employee (name, hourly_rate, hours_worked) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, hourlyRate);
            statement.setInt(3, hoursWorked);

            statement.executeUpdate();
        }
    }

    public static ArrayList<Employee> readEmployeeData(Connection connection) throws Exception {
        String sql = "SELECT * FROM Employee";
        ArrayList<Employee> employees = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int employeeId = resultSet.getInt("employee_id");
                String name = resultSet.getString("name");
                int hourlyRate = resultSet.getInt("hourly_rate");
                int hoursWorked = resultSet.getInt("hours_worked");

                employees.add(new Employee(employeeId, name, hourlyRate, hoursWorked));
            }
        }
        return employees;
    }

    public static void deleteEmployee(Connection connection, int employeeId) throws Exception {
        String sql = "DELETE FROM Employee WHERE employee_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            statement.executeUpdate();
        }
    }
}
