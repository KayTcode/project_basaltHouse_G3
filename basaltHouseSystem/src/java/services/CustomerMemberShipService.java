/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.CustomerMembershipDAO;
import java.util.HashMap;
import java.util.List;
import model.CustomerRanking;
import model.MembershipRank;

/**
 *
 * @author admin
 */
public class CustomerMemberShipService {

    private static final CustomerMembershipDAO dao = new CustomerMembershipDAO();

    public HashMap<String, Object> getCustomeRankingById(int id) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            CustomerRanking c = dao.getCustomeRankingById(id);
            if (c == null) {
                s.put("error", "Không tìm thấy người dùng");
            } else {
            }
            s.put("success", c);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    }

    public HashMap<String, Object> getRankName() {
        HashMap<String, Object> s = new HashMap<>();
        try {
            List<MembershipRank> c = dao.getRankName();
            if (c == null) {
                s.put("error", "Không tìm thấy người dùng");
            } else {
            }
            s.put("success", c);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    }
}
