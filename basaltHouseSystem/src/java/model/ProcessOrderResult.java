/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class ProcessOrderResult {

    private boolean success;
    private List<String> errors;

    public ProcessOrderResult() {
        this.success = true;
        this.errors = new ArrayList<>();

    }

    public void addError(String error) {
        this.success = false;
        this.errors.add(error);
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrors() {
        return errors;
    }

}
