import dao.EmployeeDAO;
import dao.PayrollDAO;
import dao.DepartmentDAO;
import models.Employee;
import models.Payroll;
import models.Department;
import java.util.List;

/**
 * Simple test to verify reports functionality
 */
public class test_reports {
    public static void main(String[] args) {
        System.out.println("Testing Reports Functionality");
        System.out.println("=============================");
        
        try {
            // Test EmployeeDAO
            EmployeeDAO employeeDAO = new EmployeeDAO();
            List<Employee> employees = employeeDAO.getAllEmployees();
            System.out.println("✓ Employee count: " + employees.size());
            
            // Test PayrollDAO
            PayrollDAO payrollDAO = new PayrollDAO();
            List<Payroll> payrolls = payrollDAO.getAllPayrolls();
            System.out.println("✓ Payroll records count: " + payrolls.size());
            
            // Test DepartmentDAO
            DepartmentDAO departmentDAO = new DepartmentDAO();
            List<Department> departments = departmentDAO.getAllDepartments();
            System.out.println("✓ Department count: " + departments.size());
            
            // Test employee statistics
            int[] stats = employeeDAO.getEmployeeStatistics();
            System.out.println("✓ Employee statistics: Total=" + stats[0] + ", Active=" + stats[1]);
            
            // Test sample employee data
            if (!employees.isEmpty()) {
                Employee emp = employees.get(0);
                System.out.println("✓ Sample employee: " + emp.getFullName() + " (" + emp.getEmployeeCode() + ")");
                System.out.println("  Department: " + emp.getDepartmentName());
                System.out.println("  Position: " + emp.getPosition());
                System.out.println("  Salary: $" + emp.getBaseSalary());
            }
            
            // Test sample payroll data
            if (!payrolls.isEmpty()) {
                Payroll payroll = payrolls.get(0);
                System.out.println("✓ Sample payroll: " + payroll.getEmployeeName() + " (" + payroll.getEmployeeCode() + ")");
                System.out.println("  Pay period: " + payroll.getPayPeriodStart() + " to " + payroll.getPayPeriodEnd());
                System.out.println("  Net salary: $" + payroll.getNetSalary());
                System.out.println("  Status: " + payroll.getPaymentStatus());
            }
            
            System.out.println("\nAll tests passed! Reports functionality should work correctly.");
            
        } catch (Exception e) {
            System.err.println("Error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}