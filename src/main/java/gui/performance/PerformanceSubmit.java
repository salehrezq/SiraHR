package gui.performance;

import crud.CreateListener;
import crud.UpdateListener;
import gui.EmployeeSelectedListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import model.Employee;
import model.Performance;

/**
 *
 * @author Saleh
 */
public class PerformanceSubmit
        implements
        Subject,
        EmployeeSelectedListener,
        DisplayableListener,
        EditableListener,
        CancelListener,
        RowSelectedListener,
        RowDeselectedListener {

    private Operation operation;
    private JButton btnSubmit;
    private PerformanceInput performanceInput;
    private StringBuilder stringBuilder;
    private Employee employee;
    private List<CreateListener> createListeners;
    private List<UpdateListener> updateListeners;
    private boolean boolPerformanceDisplayMode, boolEditMode;
    private Integer performanceId;

    public PerformanceSubmit() {

        createListeners = new ArrayList<>();
        updateListeners = new ArrayList<>();

        btnSubmit = new JButton("Submit");
        btnSubmit.setEnabled(false);
        btnSubmit.addActionListener(new SubmitPerformance());
        stringBuilder = new StringBuilder(145);

        operation = new CreateOperation();
        operation.switchOperationFor(this);
    }

    public void setPerformanceInput(PerformanceInput performanceInput) {
        this.performanceInput = performanceInput;
    }

    public void addPerformanceUpdatedListener(UpdateListener updateListener) {
        this.updateListeners.add(updateListener);
    }

    private void notifyUpdated() {
        this.updateListeners.forEach((updateListener) -> {
            updateListener.updated();
        });
    }

    public void addPerformanceCreatedListener(CreateListener createListener) {
        this.createListeners.add(createListener);
    }

    private void notifyCreated() {
        this.createListeners.forEach((createListener) -> {
            createListener.created();
        });
    }

    public JButton getSubmitButton() {
        return btnSubmit;
    }

    private String prepareInputsFailMessage(List<String> failMessages) {

        this.stringBuilder.setLength(0);

        failMessages.stream().forEach(msg -> {
            this.stringBuilder.append(msg).append("\n");
        });

        return this.stringBuilder.toString();
    }

    private LocalDateTime getDateTimeCombined() {

        LocalDate date = performanceInput.getDate();
        String time = performanceInput.getTime().toLowerCase();

        // Formate time from "01:15 pm" to "13:15"
        DateTimeFormatter parseStringTime = DateTimeFormatter.ofPattern("hh:mm a", Locale.UK);
        LocalTime timeParsedFromString_12_to_24 = LocalTime.parse(time, parseStringTime);

        LocalDateTime dateTime = LocalDateTime.of(date, timeParsedFromString_12_to_24);
        return dateTime;
    }

    @Override
    public void displayable() {
        boolPerformanceDisplayMode = true;
        btnSubmit.setEnabled(false);
    }

    @Override
    public void unDisplayable() {
        boolPerformanceDisplayMode = false;
        if (employee != null) {
            operation = new CreateOperation();
            operation.switchOperationFor(this);
            btnSubmit.setEnabled(true);
        }
    }

    @Override
    public void editable() {
        boolEditMode = true;
        operation = new UpdateOperation();
        operation.switchOperationFor(this);
        btnSubmit.setEnabled(true);
    }

    @Override
    public void cancelled() {
        if (boolPerformanceDisplayMode) {
            boolEditMode = false;
            btnSubmit.setEnabled(false);
        }
    }

    @Override
    public JButton getOperationButton() {
        return this.btnSubmit;
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public Operation getOperation() {
        return this.operation;
    }

    @Override
    public void rowSelectedWithRecordId(int id) {
        performanceId = id;
    }

    @Override
    public void rowDeselection() {
        performanceId = null;
    }

    private class ValidateWithMessages {

        private List<String> messages;
        private List<Boolean> booleans;

        public ValidateWithMessages(List<Boolean> booleans, List<String> messages) {
            this.booleans = booleans;
            this.messages = messages;
        }
    }

    private ValidateWithMessages validatePerformanceInputs() {

        List<String> messages = new ArrayList<>();
        List<Boolean> booleans = new ArrayList<>();

        if (performanceInput.getBoolTfTimeFilled()) {
            booleans.add(true);
        } else {
            booleans.add(false);
            messages.add("Time: input either incorrect or empty");
        }
        if (performanceInput.getBoolDateFilled()) {
            booleans.add(true);
        } else {
            booleans.add(false);
            messages.add("Date: empty input");
        }
        if (performanceInput.getBoolComboState()) {
            booleans.add(true);
        } else {
            booleans.add(false);
            messages.add("State: not selected");
        }
        if (performanceInput.getBoolComboType()) {
            booleans.add(true);
        } else {
            booleans.add(false);
            messages.add("Type: not selected");
        }
        if (performanceInput.getBoolTfAmountFilled()) {
            booleans.add(true);
        } else {
            booleans.add(false);
            messages.add("Amount: input either incorrect or empty");
        }

        String title = performanceInput.getTitle();
        boolean isTitleBlanck = title.isBlank();
        int titleLength = title.length();

        if (isTitleBlanck) {
            booleans.add(false);
            messages.add("Title: empty input");
        } else if (!isTitleBlanck && titleLength < 10) {
            booleans.add(false);
            messages.add("Title: minimum 10 charachters required");
        } else if (!isTitleBlanck && titleLength >= 10) {
            booleans.add(true);
        }

        return new ValidateWithMessages(booleans, messages);
    }

    @Override
    public void employeeSelected(Employee employee) {
        if (!boolPerformanceDisplayMode) {
            btnSubmit.setEnabled(true);
        }
        this.employee = employee;
    }

    @Override
    public void employeeDeselected() {
        btnSubmit.setEnabled(false);
        this.employee = null;
    }

    class SubmitPerformance implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            ValidateWithMessages validateWithMessages = validatePerformanceInputs();

            // Check if List of boolean values are all true or one value at least is false
            // Important note: If booleans.size() is zero;
            // the result of allMatch() is always true.
            // So you have to check the size first, or depending on context needs.
            boolean areAllInputsFilled = validateWithMessages.booleans.stream().allMatch(Boolean::booleanValue);

            if (areAllInputsFilled) {

                Performance performance = new Performance();
                performance.setEmployeeId(employee.getId());
                performance.setDateTime(getDateTimeCombined());
                performance.setTypeId(performanceInput.getPerformanceType().getId());
                performance.setState(performanceInput.getStateOfPerformance());
                performance.setAmount(performanceInput.getAmount());
                performance.setTitle(performanceInput.getTitle());
                performance.setDescription(performanceInput.getDescription());

                if (boolEditMode) {
                    performance.setId(performanceId);
                }
                // Create|Update
                boolean submitted = operation.post(performance);

                if (submitted) {
                    notifyCreated();

                    String informMessage = null;

                    if (getOperation() instanceof CreateOperation) {
                        notifyCreated();
                        informMessage = "Performance created successfully.";
                    } else if (getOperation() instanceof UpdateOperation) {
                        boolEditMode = false;
                        notifyUpdated();
                        btnSubmit.setEnabled(false);
                        informMessage = "Performance updated successfully.";
                    }
                    JOptionPane.showConfirmDialog(null,
                            informMessage,
                            "Info", JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                JOptionPane.showConfirmDialog(null,
                        prepareInputsFailMessage(validateWithMessages.messages), "Incorrect inputs or empty fields",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
