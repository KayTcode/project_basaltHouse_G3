package services;

import dao.ImportVoiceDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.ImportDetail;
import model.ImportInvoice;
import model.ImportInvoicesDetail;

public class ImportVoiceService {

    private final ImportVoiceDAO dao = new ImportVoiceDAO();

    public HashMap<String, Object> createImportInvoice(
            ImportInvoice invoice, List<ImportDetail> details) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            if (dao.insertImportInvoice(invoice, details)) {
                result.put("success", true);
            } else {
                result.put("error", "Không thể tạo phiếu nhập.");
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }

    public HashMap<String, Object> updateImportInvoice(
            int importId,
            String status,
            String note,
            String rejectReason,
            int actingStaffId,
            List<ImportDetail> details) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            boolean updated = dao.updateImportInvoice(
                    importId, status, note, rejectReason, actingStaffId, details);
            if (updated) {
                result.put("success", true);
            } else {
                result.put("error", "Cập nhật phiếu nhập không thành công.");
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }

    public HashMap<String, Object> getImportInvoiceDetailsById(int importId) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            List<ImportInvoicesDetail> details = dao.getImportInvoiceDetailsById(importId);
            if (details == null || details.isEmpty()) {
                result.put("error", "Không tìm thấy phiếu nhập.");
            } else {
                result.put("success", details);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }

    public HashMap<String, Object> getImportInvoicesDetail(String key) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            List<ImportInvoicesDetail> details = dao.getImportInvoicesDetail(key);
            if (details == null) {
                result.put("error", "Không thể đọc danh sách phiếu nhập.");
            } else {
                result.put("success", groupInvoices(details));
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println(e.getMessage());
        }
        return result;
    }

    private List<ImportInvoicesDetail> groupInvoices(List<ImportInvoicesDetail> details) {
        Map<Integer, ImportInvoicesDetail> grouped = new LinkedHashMap<>();
        for (ImportInvoicesDetail detail : details) {
            ImportInvoicesDetail invoice = grouped.get(detail.getImportId());
            if (invoice == null) {
                detail.setIngredientCount(1);
                grouped.put(detail.getImportId(), detail);
            } else {
                invoice.setIngredientCount(invoice.getIngredientCount() + 1);
                invoice.setIngredientName(
                        invoice.getIngredientName() + ", " + detail.getIngredientName());
                invoice.setOrderedQuantity(
                        add(invoice.getOrderedQuantity(), detail.getOrderedQuantity()));
                invoice.setReceivedQuantity(
                        add(invoice.getReceivedQuantity(), detail.getReceivedQuantity()));
            }
        }
        return new ArrayList<>(grouped.values());
    }

    private BigDecimal add(BigDecimal left, BigDecimal right) {
        BigDecimal safeLeft = BigDecimal.ZERO;
        BigDecimal safeRight = BigDecimal.ZERO;
        if (left != null) {
            safeLeft = left;
        }
        if (right != null) {
            safeRight = right;
        }
        return safeLeft.add(safeRight);
    }
}
