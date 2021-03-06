/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.attendance;

import datalink.CRUDAttendance;
import gui.EmployeeSelectedListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import model.AbsentOrLateEntity;
import model.AttendanceDeduction;
import model.Employee;

/**
 *
 * @author Saleh
 */
public class MonthAttendanceDeductions implements EmployeeSelectedListener {

    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scrollTable;
    private JPanel panelGather;
    private JPanel panelControlls;
    private JPanel panelTable;
    private JButton btnRequestData;
    GridBagConstraints c;
    private List records;
    private int employeeId;
    private YearMonth yearAndMonth;
    private JFormattedTextField tfYear;
    private JComboBox monthsList;
    private final String[] monthsNums;
    private Color lateColor;

    public MonthAttendanceDeductions() {
        super();

        LocalDate today = LocalDate.now();
        yearAndMonth = YearMonth.of(today.getYear(), today.getMonthValue());

        panelControlls = new JPanel();

        btnRequestData = new JButton("Request");
        btnRequestData.addActionListener(new ActionGetData());

        monthsNums = new String[]{"Jan [1]", "Feb [2]", "Mar [3]", "Apr [4]", "May [5]",
            "Jun [6]", "Jul [7]", "Aug [8]", "Sep [9]", "Oct [10]", "Nov [11]", "Dec [12]"};

        tfYear = new JFormattedTextField(getMaskFormatter());
        tfYear.setPreferredSize(new Dimension(40, 20));

        monthsList = new JComboBox<>(monthsNums);
        monthsList.setSelectedIndex(yearAndMonth.getMonthValue() - 1);

        panelControlls.add(btnRequestData);
        panelControlls.add(tfYear);
        panelControlls.add(monthsList);

        model = new DefaultTableModel(new String[]{"Day Name", "Day Num", "Type", "Deduction", "Desc EN", "Desc AR"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable cells editing.
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(5);
        table.getColumnModel().getColumn(2).setPreferredWidth(15);
        table.getColumnModel().getColumn(3).setPreferredWidth(30);
        table.getColumnModel().getColumn(4).setPreferredWidth(280);
        table.getColumnModel().getColumn(5).setPreferredWidth(280);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getColumnModel().getColumn(5).setCellRenderer(getRightAlignmentRenderer(5));

        lateColor = new Color(255, 255, 219);
        for (int i = 0; i < 6; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(getRowsColorRenderer(i));
        }

        scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panelTable = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        panelTable.add(scrollTable, c);

        panelGather = new JPanel(new GridBagLayout());
        panelGather.setBorder(BorderFactory.createLineBorder(Color.red));
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        panelGather.add(panelControlls, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        panelGather.add(panelTable, c);
        panelGather.setVisible(true);

    }

    private DefaultTableCellRenderer getRightAlignmentRenderer(int targetColumn) {

        DefaultTableCellRenderer renderer;
        renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                Component tableCellRendererComponent
                        = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                int align = DefaultTableCellRenderer.LEFT;
                if (column == targetColumn) {
                    align = DefaultTableCellRenderer.RIGHT;
                }
                ((DefaultTableCellRenderer) tableCellRendererComponent).setHorizontalAlignment(align);
                return tableCellRendererComponent;
            }
        };

        return renderer;
    }

    private DefaultTableCellRenderer getRowsColorRenderer(int targetColumn) {

        DefaultTableCellRenderer renderer;
        renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                Component tableCellRendererComponent
                        = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String type = (String) table.getModel().getValueAt(row, 2);

                if (type.contains("Late")) {
                    setBackground(lateColor);
                    setForeground(table.getForeground());
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                return tableCellRendererComponent;
            }
        };
        return renderer;
    }

    public JPanel getPanelTable() {
        return this.panelGather;
    }

    public void setSelectedEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setYearAndMonth(YearMonth ym) {
        this.yearAndMonth = ym;
    }

    public List<AbsentOrLateEntity> getTata() {

        if (employeeId < 1) {
            return null;
        }

        int year = Integer.valueOf(tfYear.getText());
        int month = this.monthsList.getSelectedIndex() + 1;
        YearMonth ym = YearMonth.of(year, month);
        setYearAndMonth(ym);

        records = CRUDAttendance.getAbsenceAndLatesRecordByEmployeeByMonth(employeeId, ym);

        return records;
    }

    @Override
    public void employeeSelected(Employee employee) {
        // cleare the model when new employee node selected.
        model.setRowCount(0);
        employeeId = employee.getId();
    }

    @Override
    public void employeeDeselected() {
        // cleare the model when no employee node selected.
        model.setRowCount(0);
        employeeId = -1;
    }

    private class ActionGetData implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            // Clear the model every time, to append fresh results
            // and not accumulate on previous results
            model.setRowCount(0);

            List<AbsentOrLateEntity> listOfAbsentAndLateDays = getTata();

            if (listOfAbsentAndLateDays == null) {
                JOptionPane.showConfirmDialog(panelGather,
                        "Select Employee", "",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<AttendanceDeduction> attendanceDeductionsList
                    = AttendanceDeductionsCalculator
                            .getAbsentsAndLatesDeductions(listOfAbsentAndLateDays, yearAndMonth);

            Object[] modelRow = new Object[6];
            int size = attendanceDeductionsList.size();
            for (int i = 0; i < size; i++) {
                AttendanceDeduction attendanceDeduction = attendanceDeductionsList.get(i);
                LocalDate date = attendanceDeduction.geDate();
                modelRow[0] = date.getDayOfWeek().toString();
                modelRow[1] = date.getDayOfMonth();
                modelRow[2] = getValueBasedOnType(attendanceDeduction);
                modelRow[3] = attendanceDeduction.getDeduction();
                modelRow[4] = attendanceDeduction.getDescriptionEN();
                modelRow[5] = attendanceDeduction.getDescriptionAR();
                model.addRow(modelRow);
            }
        }

        private String getValueBasedOnType(AttendanceDeduction attendanceDeduction) {

            String typeValue = null;
            AttendanceDeduction.Type type = attendanceDeduction.getType();

            if (type == AttendanceDeduction.Type.ABSENT) {
                typeValue = "Absent";
            } else if (type == AttendanceDeduction.Type.LATE) {
                typeValue = "Late (" + attendanceDeduction.getMinutesLate() + ")";
            }
            return typeValue;
        }
    }

    /**
     * This method returns MaskFormatter that enforces 4 digits The # character
     * represent digit, and four of them (####) means the allowed number of
     * digits.
     *
     * The current year is used as a place holder.
     *
     * @return MaskFormatter
     */
    private MaskFormatter getMaskFormatter() {
        MaskFormatter mask = null;
        try {
            mask = new MaskFormatter("####");
            mask.setPlaceholder(String.valueOf(yearAndMonth.getYear()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mask;
    }
}
