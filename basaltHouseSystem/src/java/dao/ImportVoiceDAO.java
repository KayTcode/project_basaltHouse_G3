/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.ImportDetail;
import model.ImportInvoice;
import model.ImportInvoicesDetail;

/**
 *
 * @author admin
 */
public class ImportVoiceDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<HashMap<String, Object>> getIngredientStockRowsBySupplier(int supplierId) {
        return queryIngredientStockRows(null, supplierId);
    }

    public List<HashMap<String, Object>> getIngredientStockRows(String key) {
        return queryIngredientStockRows(key, null);
    }

    private List<HashMap<String, Object>> queryIngredientStockRows(
            String key, Integer supplierId) {
        List<HashMap<String, Object>> rows = new ArrayList<>();
        String normalizedKey = normalizeSearchKey(key);
        String searchPattern = "%";
        if (normalizedKey != null) {
            searchPattern = "%" + normalizedKey + "%";
        }
        int selectedSupplierId = 0;
        if (supplierId != null) {
            selectedSupplierId = supplierId;
        }

        String sql = """
                     SELECT i.IngredientId,
                            i.IngredientName,
                            i.Unit,
                            i.StockQuantity,
                            i.MinStockQuantity,
                            i.SupplierId,
                            COALESCE(s.SupplierName, N'Chưa có NCC') AS SupplierName
                     FROM Ingredients i
                     LEFT JOIN Suppliers s
                            ON s.SupplierId = i.SupplierId
                           AND s.IsDeleted = 0
                     WHERE i.IsDeleted = 0
                       AND i.IsActive = 1
                       AND i.IngredientName LIKE ?
                       AND (? = 0 OR i.SupplierId = ?)
                     ORDER BY i.IngredientName ASC
                     """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, searchPattern);
            statement.setInt(2, selectedSupplierId);
            statement.setInt(3, selectedSupplierId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(mapIngredientStockRow(result));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return rows;
    }

    private HashMap<String, Object> mapIngredientStockRow(ResultSet result)
            throws SQLException {
        HashMap<String, Object> row = new HashMap<>();
        row.put("ingredientId", result.getInt("IngredientId"));
        row.put("ingredientName", result.getString("IngredientName"));
        row.put("unit", result.getString("Unit"));
        row.put("stockQuantity", result.getBigDecimal("StockQuantity"));
        row.put("minStockQuantity", result.getBigDecimal("MinStockQuantity"));
        Object supplierId = result.getObject("SupplierId");
        if (supplierId == null) {
            row.put("supplierId", "");
        } else {
            row.put("supplierId", supplierId);
        }
        row.put("supplierName", result.getString("SupplierName"));
        return row;
    }

    public List<HashMap<String, Object>> getSupplierOptions() {
        List<HashMap<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT SupplierId, COALESCE(SupplierName, N'Chua co NCC') AS SupplierName
                         FROM Suppliers
                         WHERE IsDeleted = 0
                         ORDER BY SupplierName ASC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("SupplierId"));
                row.put("name", rs.getString("SupplierName"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public Integer getStaffIdByAccountId(int accountId) {
        try {
            String sql = """
                         SELECT StaffId
                         FROM Staffs
                         WHERE AccountId = ?
                           AND IsDeleted = 0
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, accountId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("StaffId");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean insertImportInvoice(ImportInvoice v, List<ImportDetail> details) {
        if (details == null || details.isEmpty()) {
            return false;
        }
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            String sql = """
                     INSERT INTO [dbo].[ImportInvoices]
                                    ([ImportCode]
                                    ,[SupplierId]
                                    ,[CreatedByStaffId]
                                    ,[ConfirmedByStaffId]
                                    ,[Status]
                                    ,[OrderedDate]
                                    ,[ExpectedDate]
                                    ,[ReceivedDate]
                                    ,[SupplierInvoiceCode]
                                    ,[TotalOrderedAmount]
                                    ,[TotalReceivedAmount]
                                    ,[Note]
                                    ,[RejectReason]
                                    ,[IsDeleted])
                     OUTPUT INSERTED.ImportId
                              VALUES
                                    (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, v.getImportCode());
            st.setObject(2, v.getSupplierId());
            st.setObject(3, v.getCreatedByStaffId());
            st.setObject(4, v.getConfirmedByStaffId());
            st.setObject(5, v.getStatus());
            st.setObject(6, v.getOrderedDate());
            st.setObject(7, v.getExpectedDate());
            st.setObject(8, v.getReceivedDate());
            st.setObject(9, v.getSupplierInvoiceCode());
            st.setObject(10, v.getTotalOrderedAmount());
            st.setObject(11, v.getTotalReceivedAmount());
            st.setObject(12, v.getNote());
            st.setObject(13, v.getRejectReason());
            st.setObject(14, v.isIsDeleted());
            rs = st.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Khong lay duoc ma phieu nhap vua tao.");
            }
            int importId = rs.getInt("ImportId");

            String detailSql = """
                               INSERT INTO [dbo].[ImportDetails]
                                           ([ImportId]
                                           ,[IngredientId]
                                           ,[OrderedQuantity]
                                           ,[ReceivedQuantity]
                                           ,[UnitPrice]
                                           ,[DiscrepancyNote]
                                           ,[Note]
                                           ,[IsDeleted])
                                     VALUES
                                           (?,?,?,?,?,?,?,?)
                               """;
            for (ImportDetail detail : details) {
                if (!ingredientBelongsToSupplier(detail.getIngredientId(), v.getSupplierId())) {
                    throw new SQLException("Nguyên liệu không thuộc nhà cung cấp đã chọn.");
                }

                detail.setImportId(importId);
                st = connection.prepareStatement(detailSql);
                st.setObject(1, detail.getImportId());
                st.setObject(2, detail.getIngredientId());
                st.setObject(3, detail.getOrderedQuantity());
                st.setObject(4, detail.getReceivedQuantity());
                st.setObject(5, detail.getUnitPrice());
                st.setObject(6, detail.getDiscrepancyNote());
                st.setObject(7, detail.getNote());
                st.setObject(8, detail.isIsDeleted());
                st.executeUpdate();

            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception rollbackError) {
                System.err.println(rollbackError.getMessage());
            }
            System.err.println(e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(oldAutoCommit);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private boolean ingredientBelongsToSupplier(int ingredientId, int supplierId) throws SQLException {
        String sql = """
                     SELECT 1
                     FROM Ingredients
                     WHERE IngredientId = ?
                       AND SupplierId = ?
                       AND IsDeleted = 0
                       AND IsActive = 1
                     """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ingredientId);
            statement.setInt(2, supplierId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public HashMap<Integer, BigDecimal> getReceivedQuantityByIngredient(LocalDate auditDate) {
        HashMap<Integer, BigDecimal> imported = new HashMap<>();
        if (auditDate == null) {
            return imported;
        }

        String sql = """
                     SELECT IngredientId,
                            SUM(QuantityChanged) AS ImportedQuantity
                     FROM IngredientStockLogs
                     WHERE IsDeleted = 0
                       AND RefType = 'ImportInvoice'
                       AND ChangeType IN ('Import', 'ImportEdit')
                       AND CreatedAt >= ?
                       AND CreatedAt < ?
                     GROUP BY IngredientId
                     """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(auditDate.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(auditDate.plusDays(1).atStartOfDay()));
            try (ResultSet rs2 = ps.executeQuery()) {
                while (rs2.next()) {
                    imported.put(rs2.getInt("IngredientId"), rs2.getBigDecimal("ImportedQuantity"));
                }
            }
        } catch (Exception e) {
            System.err.println("getReceivedQuantityByIngredient Error: " + e.getMessage());
        }
        return imported;
    }

    public List<ImportInvoicesDetail> getImportInvoicesDetail(String key) {
        return queryImportInvoiceDetails(key, null);
    }

    public List<ImportInvoicesDetail> getImportInvoiceDetailsById(int importId) {
        return queryImportInvoiceDetails(null, importId);
    }

    private List<ImportInvoicesDetail> queryImportInvoiceDetails(
            String key, Integer importId) {
        List<ImportInvoicesDetail> details = new ArrayList<>();
        String normalizedKey = normalizeSearchKey(key);
        String searchPattern = "%";
        if (normalizedKey != null) {
            searchPattern = "%" + normalizedKey + "%";
        }
        int selectedImportId = 0;
        if (importId != null) {
            selectedImportId = importId;
        }

        String sql = """
                     SELECT i.ImportId,
                            id.ImportDetailId,
                            i.OrderedDate,
                            i.ExpectedDate,
                            i.ReceivedDate,
                            i.ImportCode,
                            i.SupplierInvoiceCode,
                            i.SupplierId,
                            COALESCE(su.SupplierName, N'Chưa có NCC') AS SupplierName,
                            id.IngredientId,
                            ingre.IngredientName,
                            ingre.Unit,
                            id.OrderedQuantity,
                            id.ReceivedQuantity,
                            ingre.StockQuantity,
                            id.UnitPrice,
                            i.TotalOrderedAmount,
                            i.TotalReceivedAmount,
                            i.Status,
                            s.FullName,
                            i.Note AS InvoiceNote,
                            i.RejectReason,
                            id.DiscrepancyNote,
                            id.Note AS DetailNote
                     FROM ImportInvoices i
                     JOIN ImportDetails id
                       ON i.ImportId = id.ImportId
                      AND id.IsDeleted = 0
                     JOIN Staffs s ON i.CreatedByStaffId = s.StaffId
                     JOIN Ingredients ingre ON id.IngredientId = ingre.IngredientId
                     LEFT JOIN Suppliers su ON i.SupplierId = su.SupplierId
                     WHERE i.IsDeleted = 0
                       AND (? = 0 OR i.ImportId = ?)
                       AND EXISTS (
                           SELECT 1
                           FROM ImportDetails searchedDetail
                           JOIN Ingredients searchedIngredient
                             ON searchedIngredient.IngredientId = searchedDetail.IngredientId
                           WHERE searchedDetail.ImportId = i.ImportId
                             AND searchedDetail.IsDeleted = 0
                             AND searchedIngredient.IngredientName LIKE ?
                       )
                     ORDER BY i.OrderedDate DESC,
                              i.ImportId DESC,
                              id.ImportDetailId ASC
                     """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, selectedImportId);
            statement.setInt(2, selectedImportId);
            statement.setString(3, searchPattern);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    details.add(mapImportInvoiceDetail(result));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return details;
    }

    private ImportInvoicesDetail mapImportInvoiceDetail(ResultSet result)
            throws SQLException {
        ImportInvoicesDetail detail = new ImportInvoicesDetail();
        detail.setImportId(result.getInt("ImportId"));
        detail.setImportDetailId(result.getInt("ImportDetailId"));
        detail.setOrderedDate(getLocalDateTime(result, "OrderedDate"));
        detail.setExpectedDate(getLocalDateTime(result, "ExpectedDate"));
        detail.setReceivedDate(getLocalDateTime(result, "ReceivedDate"));
        detail.setImportCode(result.getString("ImportCode"));
        detail.setSupplierInvoiceCode(result.getString("SupplierInvoiceCode"));
        detail.setSupplierId(result.getInt("SupplierId"));
        detail.setSupplierName(result.getString("SupplierName"));
        detail.setIngredientId(result.getInt("IngredientId"));
        detail.setIngredientName(result.getString("IngredientName"));
        detail.setUnit(result.getString("Unit"));
        detail.setOrderedQuantity(result.getBigDecimal("OrderedQuantity"));
        detail.setReceivedQuantity(result.getBigDecimal("ReceivedQuantity"));
        detail.setStockQuantity(result.getBigDecimal("StockQuantity"));
        detail.setUnitPrice(result.getBigDecimal("UnitPrice"));
        detail.setTotalOrderedAmount(result.getBigDecimal("TotalOrderedAmount"));
        detail.setTotalReceivedAmount(result.getBigDecimal("TotalReceivedAmount"));
        detail.setStatus(result.getString("Status"));
        detail.setStaffName(result.getString("FullName"));
        detail.setInvoiceNote(result.getString("InvoiceNote"));
        detail.setRejectReason(result.getString("RejectReason"));
        detail.setDiscrepancyNote(result.getString("DiscrepancyNote"));
        detail.setDetailNote(result.getString("DetailNote"));
        return detail;
    }

    private LocalDateTime getLocalDateTime(ResultSet result, String column)
            throws SQLException {
        Timestamp value = result.getTimestamp(column);
        if (value == null) {
            return null;
        }
        return value.toLocalDateTime();
    }

    public boolean updateImportInvoice(
            int importId,
            String status,
            String note,
            String rejectReason,
            int actingStaffId,
            List<ImportDetail> details) {
        if (connection == null || details == null || details.isEmpty()) {
            return false;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            List<ImportInvoicesDetail> oldDetails = getImportInvoiceDetailsById(importId);
            validateSubmittedDetails(importId, oldDetails, details);

            HashMap<Integer, BigDecimal> stockChanges =
                    calculateStockChanges(oldDetails, status);
            updateInvoiceHeader(
                    importId, status, note, rejectReason, actingStaffId);
            for (ImportDetail detail : details) {
                updateInvoiceDetail(detail);
            }

            for (java.util.Map.Entry<Integer, BigDecimal> entry : stockChanges.entrySet()) {
                if (entry.getValue().compareTo(BigDecimal.ZERO) != 0) {
                    adjustStockAfterImportEdit(
                            importId, entry.getKey(), entry.getValue(), actingStaffId);
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly();
            System.err.println(e.getMessage());
            return false;
        } finally {
            restoreAutoCommit(oldAutoCommit);
        }
    }

    private void validateSubmittedDetails(
            int importId,
            List<ImportInvoicesDetail> oldDetails,
            List<ImportDetail> submittedDetails) throws SQLException {
        if (oldDetails.isEmpty() || oldDetails.size() != submittedDetails.size()) {
            throw new SQLException("Chi tiết phiếu nhập không hợp lệ.");
        }

        HashMap<Integer, ImportInvoicesDetail> oldById = new HashMap<>();
        for (ImportInvoicesDetail oldDetail : oldDetails) {
            oldById.put(oldDetail.getImportDetailId(), oldDetail);
        }

        for (ImportDetail submittedDetail : submittedDetails) {
            if (submittedDetail.getImportId() != importId
                    || oldById.remove(submittedDetail.getImportDetailId()) == null) {
                throw new SQLException("Chi tiết phiếu nhập không hợp lệ.");
            }
        }
        if (!oldById.isEmpty()) {
            throw new SQLException("Chi tiết phiếu nhập không hợp lệ.");
        }
    }

    private HashMap<Integer, BigDecimal> calculateStockChanges(
            List<ImportInvoicesDetail> oldDetails, String newStatus) {
        HashMap<Integer, BigDecimal> changes = new HashMap<>();
        String oldStatus = oldDetails.get(0).getStatus();
        for (ImportInvoicesDetail detail : oldDetails) {
            BigDecimal stockDelta = calculateStockDelta(
                    oldStatus, newStatus, detail.getReceivedQuantity());
            if (stockDelta.compareTo(BigDecimal.ZERO) != 0) {
                mergeStockChange(
                        changes,
                        detail.getIngredientId(),
                        stockDelta);
            }
        }
        return changes;
    }

    static BigDecimal calculateStockDelta(
            String oldStatus, String newStatus, BigDecimal receivedQuantity) {
        BigDecimal delta = BigDecimal.ZERO;
        BigDecimal safeQuantity = safe(receivedQuantity);
        if (isStockAppliedStatus(oldStatus)) {
            delta = delta.subtract(safeQuantity);
        }
        if (isStockAppliedStatus(newStatus)) {
            delta = delta.add(safeQuantity);
        }
        return delta;
    }

    private void updateInvoiceHeader(
            int importId,
            String status,
            String note,
            String rejectReason,
            int actingStaffId) throws SQLException {
        String sql = """
                     UPDATE ImportInvoices
                     SET Status = ?,
                         ConfirmedByStaffId = ?,
                         Note = ?,
                         RejectReason = ?
                     WHERE ImportId = ?
                       AND IsDeleted = 0
                     """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            if (isStockAppliedStatus(status)) {
                statement.setInt(2, actingStaffId);
            } else {
                statement.setNull(2, Types.INTEGER);
            }
            statement.setString(3, note);
            statement.setString(4, rejectReason);
            statement.setInt(5, importId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Không cập nhật được phiếu nhập.");
            }
        }
    }

    private void updateInvoiceDetail(ImportDetail detail) throws SQLException {
        String sql = """
                     UPDATE ImportDetails
                     SET DiscrepancyNote = ?,
                         Note = ?
                     WHERE ImportDetailId = ?
                       AND ImportId = ?
                       AND IsDeleted = 0
                     """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, detail.getDiscrepancyNote());
            statement.setString(2, detail.getNote());
            statement.setInt(3, detail.getImportDetailId());
            statement.setInt(4, detail.getImportId());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Không cập nhật được chi tiết phiếu nhập.");
            }
        }
    }

    private void rollbackQuietly() {
        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            System.err.println(rollbackError.getMessage());
        }
    }

    private void restoreAutoCommit(boolean oldAutoCommit) {
        try {
            connection.setAutoCommit(oldAutoCommit);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static boolean isStockAppliedStatus(String status) {
        return "Confirmed".equalsIgnoreCase(status);
    }

    private String normalizeSearchKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return null;
        }
        return key.trim();
    }

    private static BigDecimal safe(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value;
    }

    private void mergeStockChange(
            HashMap<Integer, BigDecimal> changes, int ingredientId, BigDecimal quantity) {
        changes.put(ingredientId,
                changes.getOrDefault(ingredientId, BigDecimal.ZERO).add(quantity));
    }

    private void adjustStockAfterImportEdit(
            int importId, int ingredientId, BigDecimal quantityChanged, int staffId)
            throws SQLException {
        BigDecimal quantityBefore;
        BigDecimal quantityAfter;
        String updateSql = """
                           UPDATE Ingredients
                           SET StockQuantity = StockQuantity + ?
                           OUTPUT deleted.StockQuantity AS QuantityBefore,
                                  inserted.StockQuantity AS QuantityAfter
                           WHERE IngredientId = ?
                             AND StockQuantity + ? >= 0
                           """;
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setBigDecimal(1, quantityChanged);
            statement.setInt(2, ingredientId);
            statement.setBigDecimal(3, quantityChanged);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException(
                            "Không thể cập nhật phiếu vì tồn kho nguyên liệu sẽ bị âm.");
                }
                quantityBefore = result.getBigDecimal("QuantityBefore");
                quantityAfter = result.getBigDecimal("QuantityAfter");
            }
        }

        String logSql = """
                        INSERT INTO IngredientStockLogs
                                   (IngredientId, ChangeType, QuantityBefore,
                                    QuantityChanged, QuantityAfter, RefType,
                                    RefId, StaffId, IsDeleted)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
                        """;
        try (PreparedStatement statement = connection.prepareStatement(logSql)) {
            statement.setInt(1, ingredientId);
            statement.setString(2, "ImportEdit");
            statement.setBigDecimal(3, quantityBefore);
            statement.setBigDecimal(4, quantityChanged);
            statement.setBigDecimal(5, quantityAfter);
            statement.setString(6, "ImportInvoice");
            statement.setInt(7, importId);
            statement.setInt(8, staffId);
            statement.executeUpdate();
        }
    }
}
