/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.attendance;

/**
 *
 * @author Saleh
 */
public interface SubmitAttendanceListener {

    public void attendanceSubmitSucceeded();

    public void attendanceNoChange();

    public void attendanceSubmitFailed();
}
