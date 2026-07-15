/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.CustomerMembershipDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.CustomerMembership;
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
                s.put("success", c);
            }

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
                s.put("success", c);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return s;
    }



    public HashMap<String, Object> chekUpdateRanking(MembershipRank m) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            if (m.getRankId() <= 0) {
                s.put("error", "ID khong hop le");
            }else{
                dao.updateRanking(m);
              s.put("success", "Cap nhat thanh cong");
            }
        } catch (Exception e) {
        }
        return s;

    }

    public HashMap<String, Object> checkInseartRanking(MembershipRank m) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            if (m.getRankName() == null || m.getRankName().trim().isEmpty()) {
                s.put("error", "Ten hang khong hop le");
            } else if (m.getMinTotalSpent() == null || m.getMinTotalSpent().signum() < 0) {
                s.put("error", "Moc chi tieu khong hop le");
            } else if (m.getDiscountValue() < 0 || m.getDiscountValue() > 100) {
                s.put("error", "Gia tri giam gia khong hop le");
            } else {
                int rankId = dao.inseartRanking(m);
                m.setRankId(rankId);
                s.put("rankId", rankId);
                s.put("success", "Them thanh cong");
            }
        } catch (Exception e) {
            System.err.println("Membership rank insert failed: " + e.getMessage());
            s.put("error", "Them that bai");
        }
        return s;

    }
        public HashMap<String, Object> searchCustomer(String key,int rankId ,String status){
        HashMap<String, Object> s = new HashMap<>();
        List<CustomerMembership>list  = new ArrayList<>();
            try {
                String searchKey = key == null ? "" : key.trim();
                String memberStatus = status == null ? "" : status.trim();
                list = dao.searchByName(searchKey, rankId, memberStatus);
                s.put("success", list);
            } catch (Exception e) {
                s.put("error", "Khong the tai danh sach hoi vien");
                System.err.println(e.getMessage());
            }
        return s;
        }
        
       public HashMap<String, Object> updateLockId(int id){
        HashMap<String, Object> s = new HashMap<>();
           try {
               if(id<=0){
                   s.put("error", "Id khong hop le");
               }else if (dao.updateLocked(id)) {
                   s.put("success", "Cap nhat trang thai membership thanh cong");
               } else {
                   s.put("error", "Khach hang chua co membership");
               }
           } catch (Exception e) {
               s.put("error", "Khong the cap nhat trang thai membership");
               System.err.println(e.getMessage());
           }
        return s;
       
       }
}
