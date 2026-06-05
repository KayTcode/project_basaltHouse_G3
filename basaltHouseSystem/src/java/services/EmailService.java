/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import utils.ConfigLoader;

/**
 *
 * @author KayT
 */
public class EmailService {

    private static final String STMP_USERNAME = ConfigLoader.get("email.username");
    private static final String STMP_PASSWORD = ConfigLoader.get("email.password");
}
