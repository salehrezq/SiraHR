package gui.salary;

import crud.UpdateICRPListener;
import crud.UpdateListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

/**
 *
 * @author Saleh
 */
public class SalaryCancel
        implements
        DisplayableListener,
        EditableListener,
        UpdateListener,
        UpdateICRPListener {

    private JButton btnCancel;
    private List<CancelListener> cancelledListeners;
    private boolean booleditMode, boolDisplayable;

    public SalaryCancel() {

        cancelledListeners = new ArrayList<>();
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new SubmitSalary());

    }

    public JButton getButtonCancel() {
        return btnCancel;
    }

    @Override
    public void displayable() {
        boolDisplayable = true;
        btnCancel.setEnabled(false);
    }

    @Override
    public void unDisplayable() {
        boolDisplayable = false;
        btnCancel.setEnabled(true);
    }

    @Override
    public void editable() {
        booleditMode = true;
        btnCancel.setEnabled(true);
    }

    public void addCancelListener(CancelListener canceller) {
        this.cancelledListeners.add(canceller);
    }

    private void notifyCancelled() {
        this.cancelledListeners.forEach((canceller) -> {
            canceller.cancelled();
        });
    }

    @Override
    public void updated() {
        if (booleditMode) {
            btnCancel.setEnabled(false);
        }
    }

    @Override
    public void updatedICRP() {
        if (booleditMode) {
            btnCancel.setEnabled(false);
        }
    }

    class SubmitSalary implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            notifyCancelled();
            if (booleditMode && boolDisplayable) {
                btnCancel.setEnabled(false);
                booleditMode = false;
            }
        }
    }
}
