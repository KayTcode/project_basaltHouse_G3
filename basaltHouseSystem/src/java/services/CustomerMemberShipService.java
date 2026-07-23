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
                s.put("error", "ID không hợp lệ");
            } else if (m.getRankName() == null || m.getRankName().trim().isEmpty()) {
                s.put("error", "Tên hạng không hợp lệ");
            } else if (m.getMinTotalSpent() == null || m.getMinTotalSpent().signum() < 0) {
                s.put("error", "Mốc chi tiêu không hợp lệ");
            } else if (m.getDiscountValue() < 0 || m.getDiscountValue() > 100) {
                s.put("error", "Giá trị giảm giá không hợp lệ");
            } else if (m.isIsDeleted() && !dao.hasOtherActiveRank(m.getRankId())) {
                s.put("error", "Phải có ít nhất một hạng thành viên đang hoạt động");
            } else if (dao.updateRanking(m)) {
                s.put("success", "Cập nhật thành công");
            } else {
                s.put("error", "Không thể cập nhật hạng thành viên");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            s.put("error", "Không thể cập nhật hạng thành viên");
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
    public HashMap<String, Object> searchCustomer(String key, int rankId) {
        HashMap<String, Object> s = new HashMap<>();
        List<CustomerMembership> list = new ArrayList<>();
        try {
            String searchKey = key == null ? "" : key.trim();
            list = dao.searchByName(searchKey, rankId);
            s.put("success", list);
        } catch (Exception e) {
            s.put("error", "Khong the tai danh sach hoi vien");
            System.err.println(e.getMessage());
        }
        return s;
    }
}
