package services;

import dao.DiscountCodeDAO;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.CustomerDiscountCode;
import model.DiscountCode;

public class DiscountCodeService {

    private static final DiscountCodeDAO dao = new DiscountCodeDAO();
    private static final int EXPIRING_DAYS = 7;
    private static final int ACTIVE_CUSTOMER_VOUCHER_STATUS = 1;

    public HashMap<String, Object> getDiscountCode() {
        HashMap<String, Object> s = new HashMap<>();
        try {
            List<DiscountCode> list = dao.getDiscountCode();
            if (list == null) {
                s.put("error", "Danh sach DiscountCode loi");
            } else {
                s.put("success", list);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            s.put("error", "Danh sach DiscountCode loi");
        }
        return s;
    }

    public HashMap<String, Object> getVoucherById(int id) {
        HashMap<String, Object> s = new HashMap<>();
        try {
            List<CustomerDiscountCode> list = dao.getVoucherById(id);
            if (list == null) {
                s.put("error", "Danh sach DiscountCode loi");
            } else {
                s.put("success", list);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            s.put("error", "Danh sach DiscountCode loi");
        }
        return s;
    }

    public HashMap<String, Object> statusVoucherById(Integer id) {
        HashMap<String, Object> s = new HashMap<>();
        HashMap<Integer, String> voucherStatus = new HashMap<>();
        HashMap<Integer, String> voucherStatusText = new HashMap<>();
        HashMap<Integer, String> voucherStatusClass = new HashMap<>();
        HashMap<Integer, String> publicVoucherStatus = new HashMap<>();
        HashMap<Integer, String> publicVoucherStatusText = new HashMap<>();
        HashMap<Integer, String> publicVoucherStatusClass = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        try {
            List<DiscountCode> publicVouchers = dao.getDiscountCode();
            for (DiscountCode item : publicVouchers) {
                putVoucherStatus(publicVoucherStatus, publicVoucherStatusText, publicVoucherStatusClass,
                        item.getDiscountId(), item.getEndDate(), now);
            }

            s.put("publicVouchers", publicVouchers);
            s.put("publicVoucherStatus", publicVoucherStatus);
            s.put("publicVoucherStatusText", publicVoucherStatusText);
            s.put("publicVoucherStatusClass", publicVoucherStatusClass);

            if (id != null) {
                List<CustomerDiscountCode> list = dao.getVoucherById(id);
                for (CustomerDiscountCode item : list) {
                    putVoucherStatus(voucherStatus, voucherStatusText, voucherStatusClass,
                            item.getCustomerDiscountId(), item.getEndDate(), now);
                }

                s.put("listP", list);
                s.put("voucherStatus", voucherStatus);
                s.put("voucherStatusText", voucherStatusText);
                s.put("voucherStatusClass", voucherStatusClass);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            s.put("error", "Danh sach voucher loi");
        }

        return s;
    }

    public HashMap<String, Object> applyVoucherCode(String code, Integer accountId) {
        HashMap<String, Object> result = new HashMap<>();
        String voucherCode = code != null ? code.trim() : "";

        if (voucherCode.isEmpty()) {
            result.put("error", "Vui long nhap dung voucher");
            return result;
        }

        try {
            DiscountCode publicVoucher = findPublicVoucher(voucherCode);
            if (publicVoucher != null) {
                result.put("voucher", publicVoucher);
                result.put("success", "them ma giam gia thanh cong");
                return result;
            }

            if (accountId != null) {
                DiscountCode customerVoucher = findCustomerVoucher(voucherCode, accountId);
                if (customerVoucher != null) {
                    result.put("voucher", customerVoucher);
                    result.put("success", "them ma giam gia thanh cong");
                    return result;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        result.put("error", "ma code khong ton tai hoac da het thoi gian su dung");
        return result;
    }

    private DiscountCode findPublicVoucher(String code) {
        List<DiscountCode> publicVouchers = dao.getDiscountCode();
        for (DiscountCode item : publicVouchers) {
            if (code.equalsIgnoreCase(item.getCode()) && isVoucherUsable(item.getStartDate(), item.getEndDate())) {
                return item;
            }
        }

        return null;
    }

    private DiscountCode findCustomerVoucher(String code, int accountId) {
        CustomerDiscountCode item = dao.getCustomerVoucherByCode(accountId, code);
        if (item == null || item.isIsUsed() || !isVoucherUsable(item.getStartDate(), item.getEndDate())) {
            return null;
        }

        if (item.getStatus() != ACTIVE_CUSTOMER_VOUCHER_STATUS
                && !dao.updateCustomerVoucherStatus(item.getCustomerDiscountId(), ACTIVE_CUSTOMER_VOUCHER_STATUS)) {
            return null;
        }

        item.setStatus(ACTIVE_CUSTOMER_VOUCHER_STATUS);
        return toDiscountCode(item);
    }

    private DiscountCode toDiscountCode(CustomerDiscountCode item) {
        DiscountCode voucher = new DiscountCode();
        voucher.setDiscountId(item.getDiscountId());
        voucher.setCode(item.getCode());
        voucher.setDiscountPercent(item.getDiscountPercent());
        voucher.setDiscountAmount(item.getDiscountAmount());
        voucher.setStartDate(item.getStartDate());
        voucher.setEndDate(item.getEndDate());
        voucher.setDescription(item.getDescription());
        return voucher;
    }

    private boolean isVoucherUsable(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        boolean started = startDate == null || !startDate.isAfter(now);
        boolean notExpired = endDate == null || !endDate.isBefore(now);
        return started && notExpired;
    }

    private void putVoucherStatus(Map<Integer, String> statusMap,
            Map<Integer, String> statusTextMap,
            Map<Integer, String> statusClassMap,
            int key,
            LocalDateTime endDate,
            LocalDateTime now) {
        String status = "available";
        String statusText = "Kh\u1ea3 d\u1ee5ng";
        String statusClass = "";

        if (endDate != null) {
            long daysLeft = ChronoUnit.DAYS.between(now.toLocalDate(), endDate.toLocalDate());

            if (daysLeft < 0) {
                status = "expired";
                statusText = "H\u1ebft h\u1ea1n";
                statusClass = "voucher-status--expired";
            } else if (daysLeft <= EXPIRING_DAYS) {
                status = "expiring";
                statusText = "S\u1eafp h\u1ebft h\u1ea1n";
                statusClass = "voucher-status--warning";
            }
        }

        statusMap.put(key, status);
        statusTextMap.put(key, statusText);
        statusClassMap.put(key, statusClass);
    }
}
