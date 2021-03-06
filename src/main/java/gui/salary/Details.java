package gui.salary;

/**
 *
 * @author Saleh
 */
import gui.EmployeeSelectedListener;
import gui.TwoColumnsLabelsAndFields;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import model.Employee;

/**
 *
 * @author Saleh
 */
public class Details implements EmployeeSelectedListener {

    private final JPanel container, panelSalaryComponents;
    private JComponent[] componentsRefrence;
    private JTextField tfMonthelySalary, tfSalaryAdvances, tfAttendanceDeductions, tfPerformanceGain;
    private final String tfInitialValue = "0";

    public Details() {

        container = new JPanel(new GridBagLayout());
        GridBagConstraints c;

        JComponent[] components = {
            tfMonthelySalary = new JTextField(10),
            tfSalaryAdvances = new JTextField(10),
            tfAttendanceDeductions = new JTextField(10),
            tfPerformanceGain = new JTextField(10)};

        componentsRefrence = components;

        String[] labels = {
            "Agreed Salary",
            "Salary Advances deducted",
            "Attendance deductions",
            "Performance gain",};

        for (JComponent component : componentsRefrence) {
            JTextField tf = (JTextField) component;
            tf.setEditable(false);
            tf.setText(tfInitialValue);
        }

        panelSalaryComponents = (JPanel) TwoColumnsLabelsAndFields.getTwoColumnLayout(labels, components);
        c = new GridBagConstraints();
        c.gridy = 0;
        c.insets = new Insets(20, 0, 0, 0);
        c.anchor = GridBagConstraints.PAGE_START;
        container.add(panelSalaryComponents, c);
    }

    public JPanel getContainer() {
        return container;
    }

    @Override
    public void employeeSelected(Employee employee) {
        tfMonthelySalary.setText(employee.getSalary().toPlainString());
        tfSalaryAdvances.setText(tfInitialValue);
        tfAttendanceDeductions.setText(tfInitialValue);
        tfPerformanceGain.setText(tfInitialValue);
    }

    @Override
    public void employeeDeselected() {
        for (JComponent component : componentsRefrence) {
            ((JTextField) component).setText(tfInitialValue);
        }
    }

    public void setTfSalaryAdvances(String salaryAdvances) {
        this.tfSalaryAdvances.setText(salaryAdvances);
    }

    public void setTfAttendanceDeductions(String deductionsValue) {
        this.tfAttendanceDeductions.setText(deductionsValue);
    }

    public void setTfPerformanceGain(String performanceGain) {
        this.tfPerformanceGain.setText(performanceGain);
    }

}
