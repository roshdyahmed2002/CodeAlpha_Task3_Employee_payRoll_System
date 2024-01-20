import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PayrollUI {
    private JFrame frame;
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private DbConnect dbConnect ;
/*
    private DbConnect dbConnect = new DbConnect();
*/

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PayrollUI payrollUI = new PayrollUI();
            payrollUI.createAndShowUI();
        });
    }

    public void createAndShowUI() {
        frame = new JFrame("Employee Data");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create UI components
        JButton calculateSalaryButton = new JButton("Calculate Salary");
        JButton addEmployeeButton = new JButton("Add Employee");

        calculateSalaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAndShowSalary();
            }
        });

        addEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddEmployeeDialog();
            }
        });

        // Create the employee table
        String[] columnNames = {"ID", "Name", "Hourly Rate", "Hours Worked", "Calculate", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == getColumnIndex("Delete");
            }
        };
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        employeeTable.setFillsViewportHeight(true);

        // Add components to the frame
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(calculateSalaryButton);
        panel.add(addEmployeeButton);
        panel.add(scrollPane);
        frame.add(panel);

        // Add a listener for table cell clicks (for the "Delete" button)
        employeeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int column = employeeTable.columnAtPoint(evt.getPoint());
                int row = employeeTable.rowAtPoint(evt.getPoint());
                if (column == getColumnIndex("Delete")) {
                    deleteEmployee(row);
                }
            }
        });

        frame.setVisible(true);


         dbConnect = new DbConnect();
         dbConnect.createTables();

        // Add sample data to the table
        addSampleData();
    }

    private void calculateAndShowSalary() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            // Retrieve data from the selected row
            int empId = (int) employeeTable.getValueAt(selectedRow, 0);
            String empName = (String) employeeTable.getValueAt(selectedRow, 1);
            int hourlyRate = (int) employeeTable.getValueAt(selectedRow, 2);
            int hoursWorked = (int) employeeTable.getValueAt(selectedRow, 3);

            // Calculate salary (simple calculation for demonstration)
            int salary = hourlyRate * hoursWorked;

            // Display salary in a JOptionPane
            String salaryMessage = "Employee ID: " + empId + "\n" +
                    "Employee Name: " + empName + "\n" +
                    "Calculated Salary: $" + salary;
            JOptionPane.showMessageDialog(frame, salaryMessage, "Calculated Salary", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select an employee to calculate salary.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSampleData() {
        // Add some sample data to the table
        addEmployeeToTable(1, "John Doe", 20, 40);
        addEmployeeToTable(2, "Jane Smith", 18, 35);
        addEmployeeToTable(3, "Roshdy", 10, 10);
        addEmployeeToTable(4, "Bob Johnson", 25, 30);

        // Uncomment the code below if you want to load data from the database

        try {
            ArrayList<Employee> employees = dbConnect.readEmployeeData(dbConnect.getConnect());

            for (Employee employee : employees) {
                addEmployeeToTable(employee.getEmp_id(), employee.getName(), employee.getHourlyRate(), employee.getHoursWorked());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void addEmployeeToTable(int empId, String empName, int hourlyRate, int hoursWorked) {
        // Add a new row to the table
        Object[] rowData = {empId, empName, hourlyRate, hoursWorked, "Calculate", "Delete"};
        tableModel.addRow(rowData);
    }

    private void deleteEmployee(int row) {
        int empId = (int) employeeTable.getValueAt(row, getColumnIndex("ID"));
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this employee?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Perform deletion from the database
            try {
                dbConnect.deleteEmployee(dbConnect.getConnect(), empId);
                System.out.println("deletedd donee");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Remove the row from the table
            tableModel.removeRow(row);
        }
    }

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            if (tableModel.getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private void showAddEmployeeDialog() {
        JDialog addEmployeeDialog = new JDialog(frame, "Add Employee", true);
        addEmployeeDialog.setSize(300, 200);
        addEmployeeDialog.setLayout(new GridLayout(4, 2));

        JTextField nameField = new JTextField();
        JTextField hourlyRateField = new JTextField();
        JTextField hoursWorkedField = new JTextField();

        addEmployeeDialog.add(new JLabel("Name:"));
        addEmployeeDialog.add(nameField);
        addEmployeeDialog.add(new JLabel("Hourly Rate:"));
        addEmployeeDialog.add(hourlyRateField);
        addEmployeeDialog.add(new JLabel("Hours Worked:"));
        addEmployeeDialog.add(hoursWorkedField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get values from text fields
                String name = nameField.getText();
                int hourlyRate = Integer.parseInt(hourlyRateField.getText());
                int hoursWorked = Integer.parseInt(hoursWorkedField.getText());

                // Insert new employee into the table
                try {
                    dbConnect.insertEmployee(dbConnect.getConnect(), name, hourlyRate, hoursWorked);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                // Add new employee to the table on UI
                addEmployeeToTable(tableModel.getRowCount() + 1, name, hourlyRate, hoursWorked);

                // Close the dialog
                addEmployeeDialog.dispose();
            }
        });

        addEmployeeDialog.add(addButton);

        addEmployeeDialog.setVisible(true);
    }
}
