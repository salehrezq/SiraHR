/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import model.Employee;

/**
 *
 * @author Saleh
 */
public interface EmployeeSelectedListener {

    public void employeeSelected(Employee employee);

    public void employeeDeselected();
}
