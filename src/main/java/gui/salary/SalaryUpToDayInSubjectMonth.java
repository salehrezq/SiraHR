package gui.salary;

import gui.EmployeeSelectedListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import model.Employee;
import gui.MenuItemSalaryUpToDateListener;

/**
 *
 * @author Saleh
 */
public class SalaryUpToDayInSubjectMonth
        implements
        SubjectDateChangeListener,
        MenuItemSalaryUpToDateListener,
        EmployeeSelectedListener {

    private JPanel container;
    private JCheckBox cbSalaryUpToDayInSubjectMonth;
    private JSpinner spinnerMonthDays;
    private SpinnerModel spnModel;
    private SalaryInput salaryInput;
    private int dayOfMonth;
    private LocalDate dateSelected;
    private Mode mode;
    private ArrayList<SalaryUpToDateSpinnerCheckedListener> salaryUpToDateSpinnerCheckedListeners;

    public enum Mode {
        END_OF_MONTH, BEFORE_END_OF_MONTH;
    }

    public SalaryUpToDayInSubjectMonth() {

        mode = Mode.END_OF_MONTH;

        container = new JPanel();
        cbSalaryUpToDayInSubjectMonth = new JCheckBox("Salary up to selected day of subject month");
        cbSalaryUpToDayInSubjectMonth.addActionListener(new SalaryUpToDayInSubjectMonthModeHandler());
        container.add(cbSalaryUpToDayInSubjectMonth);

        spinnerMonthDays = new JSpinner();
        spinnerMonthDays.addChangeListener(new DayOfSubjectMonthSelectedSpinnerHandler());
        container.add(spinnerMonthDays);

        salaryUpToDateSpinnerCheckedListeners = new ArrayList<>();

        enableControls(false);
    }

    public JPanel getContainer() {
        return this.container;
    }

    public void setSalaryInput(SalaryInput salaryInput) {
        this.salaryInput = salaryInput;
    }

    public void setSpinnerMonthDayslModel(LocalDate subjectYearMonth) {
        LocalDate lastDay = subjectYearMonth.with(TemporalAdjusters.lastDayOfMonth()); //2015-11-30
        int oneDayBeforeLastDayOfMonth = lastDay.getDayOfMonth() - 1;
        dayOfMonth = oneDayBeforeLastDayOfMonth;
        spnModel = new SpinnerNumberModel(oneDayBeforeLastDayOfMonth, 1, oneDayBeforeLastDayOfMonth, 1);
        spinnerMonthDays.setModel(spnModel);
    }

    private void setSpinnerMonthDayslModel(YearMonth yearMonth) {
        LocalDate dateOfSpinner = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), 1);
        setSpinnerMonthDayslModel(dateOfSpinner);
    }

    @Override
    public void yearOrMonthChanged(YearMonth yearMonth) {
        setSpinnerMonthDayslModel(yearMonth);
    }

    @Override
    public void yearAndMonthNotChanged(YearMonth yearMonth) {
        setSpinnerMonthDayslModel(yearMonth);
    }

    protected int getDayOfMonth() {
        return this.dayOfMonth;
    }

    protected Mode getMode() {
        return this.mode;
    }

    private void enableControls(boolean enable) {
        cbSalaryUpToDayInSubjectMonth.setEnabled(enable);
        spinnerMonthDays.setEnabled(enable);
    }

    @Override
    public void salaryUpToDateAbility(boolean enable) {
        enableControls(enable);
        if (!enable) {
            mode = Mode.END_OF_MONTH;
            cbSalaryUpToDayInSubjectMonth.setSelected(false);
        }
    }

    @Override
    public void employeeSelected(Employee employee) {
        mode = Mode.END_OF_MONTH;
    }

    @Override
    public void employeeDeselected() {
        mode = Mode.END_OF_MONTH;
    }

    public void addSalaryUpToDateSpinnerCheckedListener(SalaryUpToDateSpinnerCheckedListener sutdscl) {
        this.salaryUpToDateSpinnerCheckedListeners.add(sutdscl);
    }

    private void notifySpinnerChecked(boolean checked) {
        this.salaryUpToDateSpinnerCheckedListeners.forEach((sutdscl) -> {
            sutdscl.spinnerChecked(checked);
        });
    }

    private class DayOfSubjectMonthSelectedSpinnerHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JSpinner spinner = (JSpinner) e.getSource();
            dayOfMonth = (int) spinner.getValue();
        }
    }

    private class SalaryUpToDayInSubjectMonthModeHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            notifySpinnerChecked(checkBox.isSelected());
            if (checkBox.isSelected()) {
                mode = Mode.BEFORE_END_OF_MONTH;
            } else {
                mode = Mode.END_OF_MONTH;
            }
        }
    }
}
