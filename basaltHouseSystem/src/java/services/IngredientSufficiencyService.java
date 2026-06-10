/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import java.util.List;
import model.ProcessOrderResult;

/**
 *
 * @author admin
 */
public class IngredientSufficiencyService {
    public ProcessOrderResult validate(int orderId) {
        IngredientCheckService checkService = new IngredientCheckService();
        ProcessOrderResult result = new ProcessOrderResult();

       
        List<String> checkErrors = checkService.check(orderId);
        if (!checkErrors.isEmpty()) {
            for (String err : checkErrors) {
                result.addError(err);
            }
 
            return result;
        }    
        return result; 
    } 
}
