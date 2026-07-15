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
import java.time.LocalDate;
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

    public List<HashMap<String, Object>> getIngredientStockRows() {
        List<HashMap<String, Object>> list = new ArrayList<>();
        try {
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
                         ORDER BY i.IngredientName ASC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("ingredientId", rs.getInt("IngredientId"));
                row.put("ingredientName", rs.getString("IngredientName"));
                row.put("unit", rs.getString("Unit"));
                row.put("stockQuantity", rs.getBigDecimal("StockQuantity"));
                row.put("minStockQuantity", rs.getBigDecimal("MinStockQuantity"));
                Object supplierId = rs.getObject("SupplierId");
                row.put("supplierId", supplierId == null ? "" : supplierId);
                row.put("supplierName", rs.getString("SupplierName"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public List<HashMap<String, Object>> getIngredientStockRowsBySupplier(int supplierId) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT i.IngredientId,
                                i.IngredientName,
                                i.Unit,
                                i.StockQuantity,
                                i.MinStockQuantity,
                                i.SupplierId,
                                COALESCE(s.SupplierName, N'Chưa có NCC') AS SupplierName
                         FROM Ingredients i
                         INNER JOIN Suppliers s
                                 ON s.SupplierId = i.SupplierId
                                AND s.IsDeleted = 0
                         WHERE i.IsDeleted = 0
                           AND i.IsActive = 1
                           AND i.SupplierId = ?
                         ORDER BY i.IngredientName ASC
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, supplierId);
            rs = st.executeQuery();
            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("ingredientId", rs.getInt("IngredientId"));
                row.put("ingredientName", rs.getString("IngredientName"));
                row.put("unit", rs.getString("Unit"));
                row.put("stockQuantity", rs.getBigDecimal("StockQuantity"));
                row.put("minStockQuantity", rs.getBigDecimal("MinStockQuantity"));
                row.put("supplierId", rs.getInt("SupplierId"));
                row.put("supplierName", rs.getString("SupplierName"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

     public List<HashMap<String, Object>> getIngredientStockRows(String key) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        try {
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
                                                    AND i.IsActive = 1 and i.IngredientName like ?
                                                  ORDER BY i.IngredientName ASC
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, "%"+key+"%");
            rs = st.executeQuery();
            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("ingredientId", rs.getInt("IngredientId"));
                row.put("ingredientName", rs.getString("IngredientName"));
                row.put("unit", rs.getString("Unit"));
                row.put("stockQuantity", rs.getBigDecimal("StockQuantity"));
                row.put("minStockQuantity", rs.getBigDecimal("MinStockQuantity"));
                Object supplierId = rs.getObject("SupplierId");
                row.put("supplierId", supplierId == null ? "" : supplierId);
                row.put("supplierName", rs.getString("SupplierName"));
                list.add(row);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;
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

    public boolean inseartImportInvoices(ImportInvoice v, List<ImportDetail> details) {
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

                if ("Confirmed".equalsIgnoreCase(v.getStatus())
                        && detail.getReceivedQuantity() != null
                        && detail.getReceivedQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    updateStockAfterImport(v, detail);
                }
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

    private void updateStockAfterImport(ImportInvoice invoice, ImportDetail detail) throws SQLException {
        BigDecimal quantityBefore;
        BigDecimal quantityAfter;

        String updateStockSql = """
                                UPDATE Ingredients
                                SET StockQuantity = StockQuantity + ?
                                OUTPUT deleted.StockQuantity AS QuantityBefore,
                                       inserted.StockQuantity AS QuantityAfter
                                WHERE IngredientId = ?
                                """;
        st = connection.prepareStatement(updateStockSql);
        st.setObject(1, detail.getReceivedQuantity());
        st.setObject(2, detail.getIngredientId());
        rs = st.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Không cập nhật được tồn kho nguyên liệu.");
        }

        quantityBefore = rs.getBigDecimal("QuantityBefore");
        quantityAfter = rs.getBigDecimal("QuantityAfter");

        String logSql = """
                        INSERT INTO [dbo].[IngredientStockLogs]
                                    ([IngredientId]
                                    ,[ChangeType]
                                    ,[QuantityBefore]
                                    ,[QuantityChanged]
                                    ,[QuantityAfter]
                                    ,[RefType]
                                    ,[RefId]
                                    ,[StaffId]
                                    ,[IsDeleted])
                              VALUES
                                    (?,?,?,?,?,?,?,?,0)
                        """;
        st = connection.prepareStatement(logSql);
        st.setObject(1, detail.getIngredientId());
        st.setObject(2, "Import");
        st.setObject(3, quantityBefore);
        st.setObject(4, detail.getReceivedQuantity());
        st.setObject(5, quantityAfter);
        st.setObject(6, "ImportInvoice");
        st.setObject(7, detail.getImportId());
        st.setObject(8, invoice.getConfirmedByStaffId() != null
                ? invoice.getConfirmedByStaffId()
                : invoice.getCreatedByStaffId());
        st.executeUpdate();
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

    public List<ImportInvoicesDetail> getImportInvoicesDetail() {
        List<ImportInvoicesDetail> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT i.ImportId,
                                id.ImportDetailId,
                                i.OrderedDate,
                                i.ExpectedDate,
                                i.ReceivedDate,
                                i.ImportCode,
                                i.SupplierInvoiceCode,
                                i.SupplierId,
                                COALESCE(su.SupplierName, N'Chua co NCC') AS SupplierName,
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
                         JOIN ImportDetails id ON i.ImportId = id.ImportId AND id.IsDeleted = 0
                         JOIN Staffs s ON i.CreatedByStaffId = s.StaffId
                         JOIN Ingredients ingre ON id.IngredientId = ingre.IngredientId
                         LEFT JOIN Suppliers su ON i.SupplierId = su.SupplierId
                         WHERE i.IsDeleted = 0
                         ORDER BY i.OrderedDate DESC, i.ImportId DESC
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                 ImportInvoicesDetail detail = new ImportInvoicesDetail();
                detail.setImportId(rs.getInt("ImportId"));
                detail.setImportDetailId(rs.getInt("ImportDetailId"));
                detail.setOrderedDate(rs.getTimestamp("OrderedDate") == null ? null : rs.getTimestamp("OrderedDate").toLocalDateTime());
                detail.setExpectedDate(rs.getTimestamp("ExpectedDate") == null ? null : rs.getTimestamp("ExpectedDate").toLocalDateTime());
                detail.setReceivedDate(rs.getTimestamp("ReceivedDate") == null ? null : rs.getTimestamp("ReceivedDate").toLocalDateTime());
                detail.setImportCode(rs.getString("ImportCode"));
                detail.setSupplierInvoiceCode(rs.getString("SupplierInvoiceCode"));
                detail.setSupplierId(rs.getInt("SupplierId"));
                detail.setSupplierName(rs.getString("SupplierName"));
                detail.setIngredientId(rs.getInt("IngredientId"));
                detail.setIngredientName(rs.getString("IngredientName"));
                detail.setUnit(rs.getString("Unit"));
                detail.setOrderedQuantity(rs.getBigDecimal("OrderedQuantity"));
                detail.setReceivedQuantity(rs.getBigDecimal("ReceivedQuantity"));
                detail.setStockQuantity(rs.getBigDecimal("StockQuantity"));
                detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                detail.setTotalOrderedAmount(rs.getBigDecimal("TotalOrderedAmount"));
                detail.setTotalReceivedAmount(rs.getBigDecimal("TotalReceivedAmount"));
                detail.setStatus(rs.getString("Status"));
                detail.setStaffName(rs.getString("FullName"));
                detail.setInvoiceNote(rs.getString("InvoiceNote"));
                detail.setRejectReason(rs.getString("RejectReason"));
                detail.setDiscrepancyNote(rs.getString("DiscrepancyNote"));
                detail.setDetailNote(rs.getString("DetailNote"));
                
                list.add(detail);

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;

    }
  public List<ImportInvoicesDetail> getImportInvoicesDetail(String key) {
        List<ImportInvoicesDetail> list = new ArrayList<>();
        try {
            String sql = """
                         SELECT i.ImportId,
                                                         id.ImportDetailId,
                                                         i.OrderedDate,
                                                         i.ExpectedDate,
                                                         i.ReceivedDate,
                                                         i.ImportCode,
                                                         i.SupplierInvoiceCode,
                                                         i.SupplierId,
                                                         COALESCE(su.SupplierName, N'Chua co NCC') AS SupplierName,
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
                                                  JOIN ImportDetails id ON i.ImportId = id.ImportId AND id.IsDeleted = 0
                                                  JOIN Staffs s ON i.CreatedByStaffId = s.StaffId
                                                  JOIN Ingredients ingre ON id.IngredientId = ingre.IngredientId
                                                  LEFT JOIN Suppliers su ON i.SupplierId = su.SupplierId
                                                  WHERE i.IsDeleted = 0 and  ingre.IngredientName like ?
                                                  ORDER BY i.OrderedDate DESC, i.ImportId DESC
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, "%"+key+"%");
            rs = st.executeQuery();
            while (rs.next()) {
                 ImportInvoicesDetail detail = new ImportInvoicesDetail();
                detail.setImportId(rs.getInt("ImportId"));
                detail.setImportDetailId(rs.getInt("ImportDetailId"));
                detail.setOrderedDate(rs.getTimestamp("OrderedDate") == null ? null : rs.getTimestamp("OrderedDate").toLocalDateTime());
                detail.setExpectedDate(rs.getTimestamp("ExpectedDate") == null ? null : rs.getTimestamp("ExpectedDate").toLocalDateTime());
                detail.setReceivedDate(rs.getTimestamp("ReceivedDate") == null ? null : rs.getTimestamp("ReceivedDate").toLocalDateTime());
                detail.setImportCode(rs.getString("ImportCode"));
                detail.setSupplierInvoiceCode(rs.getString("SupplierInvoiceCode"));
                detail.setSupplierId(rs.getInt("SupplierId"));
                detail.setSupplierName(rs.getString("SupplierName"));
                detail.setIngredientId(rs.getInt("IngredientId"));
                detail.setIngredientName(rs.getString("IngredientName"));
                detail.setUnit(rs.getString("Unit"));
                detail.setOrderedQuantity(rs.getBigDecimal("OrderedQuantity"));
                detail.setReceivedQuantity(rs.getBigDecimal("ReceivedQuantity"));
                detail.setStockQuantity(rs.getBigDecimal("StockQuantity"));
                detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                detail.setTotalOrderedAmount(rs.getBigDecimal("TotalOrderedAmount"));
                detail.setTotalReceivedAmount(rs.getBigDecimal("TotalReceivedAmount"));
                detail.setStatus(rs.getString("Status"));
                detail.setStaffName(rs.getString("FullName"));
                detail.setInvoiceNote(rs.getString("InvoiceNote"));
                detail.setRejectReason(rs.getString("RejectReason"));
                detail.setDiscrepancyNote(rs.getString("DiscrepancyNote"));
                detail.setDetailNote(rs.getString("DetailNote"));
                
                list.add(detail);

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return list;

    }
    public List<ImportInvoicesDetail> getImportInvoiceDetailsById(int importId) {
        List<ImportInvoicesDetail> details = new ArrayList<>();
        try {
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
                           AND i.ImportId = ?
                         ORDER BY id.ImportDetailId ASC
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, importId);
            rs = st.executeQuery();
            while (rs.next()) {
                ImportInvoicesDetail detail = new ImportInvoicesDetail();
                detail.setImportId(rs.getInt("ImportId"));
                detail.setImportDetailId(rs.getInt("ImportDetailId"));
                detail.setOrderedDate(rs.getTimestamp("OrderedDate") == null
                        ? null : rs.getTimestamp("OrderedDate").toLocalDateTime());
                detail.setExpectedDate(rs.getTimestamp("ExpectedDate") == null
                        ? null : rs.getTimestamp("ExpectedDate").toLocalDateTime());
                detail.setReceivedDate(rs.getTimestamp("ReceivedDate") == null
                        ? null : rs.getTimestamp("ReceivedDate").toLocalDateTime());
                detail.setImportCode(rs.getString("ImportCode"));
                detail.setSupplierInvoiceCode(rs.getString("SupplierInvoiceCode"));
                detail.setSupplierId(rs.getInt("SupplierId"));
                detail.setSupplierName(rs.getString("SupplierName"));
                detail.setIngredientId(rs.getInt("IngredientId"));
                detail.setIngredientName(rs.getString("IngredientName"));
                detail.setUnit(rs.getString("Unit"));
                detail.setOrderedQuantity(rs.getBigDecimal("OrderedQuantity"));
                detail.setReceivedQuantity(rs.getBigDecimal("ReceivedQuantity"));
                detail.setStockQuantity(rs.getBigDecimal("StockQuantity"));
                detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                detail.setTotalOrderedAmount(rs.getBigDecimal("TotalOrderedAmount"));
                detail.setTotalReceivedAmount(rs.getBigDecimal("TotalReceivedAmount"));
                detail.setStatus(rs.getString("Status"));
                detail.setStaffName(rs.getString("FullName"));
                detail.setInvoiceNote(rs.getString("InvoiceNote"));
                detail.setRejectReason(rs.getString("RejectReason"));
                detail.setDiscrepancyNote(rs.getString("DiscrepancyNote"));
                detail.setDetailNote(rs.getString("DetailNote"));
                details.add(detail);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return details;
    }


    public boolean updateImportVoice(ImportInvoice s) {
        try {
            String sql = """
                          UPDATE [dbo].[ImportInvoices]
                             SET [ImportCode] = ?
                                ,[SupplierId] = ?
                                ,[Status] = ?
                                ,[OrderedDate] = ?
                                ,[ExpectedDate] = ?
                                ,[ReceivedDate] = ?
                                ,[SupplierInvoiceCode] = ?
                                ,[TotalOrderedAmount] = ?
                                ,[TotalReceivedAmount] = ?
                                ,[Note] = ?
                                ,[RejectReason] = ?
                           WHERE ImportId = ?
                          """;
            st = connection.prepareStatement(sql);
            st.setObject(1, s.getImportCode());
            st.setObject(2, s.getSupplierId());
            st.setObject(3, s.getStatus());
            st.setObject(4, s.getOrderedDate());
            st.setObject(5, s.getExpectedDate());
            st.setObject(6, s.getReceivedDate());
            st.setObject(7, s.getSupplierInvoiceCode());
            st.setObject(8, s.getTotalOrderedAmount());
            st.setObject(9, s.getTotalReceivedAmount());
            st.setObject(10, s.getNote());
            st.setObject(11, s.getRejectReason());
            st.setObject(12, s.getImportId());

            return st.executeUpdate() == 1;

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean updateImportVoiceDetail(ImportDetail d) {
        try {
            String sql = """
                         UPDATE [dbo].[ImportDetails]
                            SET 
                               [IngredientId] = ?
                               ,[OrderedQuantity] = ?
                               ,[ReceivedQuantity] = ?
                               ,[UnitPrice] = ?
                               ,[DiscrepancyNote] = ?
                               ,[Note] = ?
                          WHERE ImportDetailId = ?
                            AND ImportId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, d.getIngredientId());
            st.setObject(2, d.getOrderedQuantity());
            st.setObject(3, d.getReceivedQuantity());
            st.setObject(4, d.getUnitPrice());
            st.setObject(5, d.getDiscrepancyNote());
            st.setObject(6, d.getNote());
            st.setObject(7, d.getImportDetailId());
            st.setObject(8, d.getImportId());
            return st.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;

    }

    public boolean updateImportVoice(ImportInvoice invoice, List<ImportDetail> details) {
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            List<ImportInvoicesDetail> oldDetails =
                    getImportInvoiceDetailsById(invoice.getImportId());
            if (oldDetails.isEmpty() || oldDetails.size() != details.size()) {
                connection.rollback();
                return false;
            }

            HashMap<Integer, BigDecimal> stockChanges = new HashMap<>();
            if (isStockAppliedStatus(oldDetails.get(0).getStatus())) {
                for (ImportInvoicesDetail oldDetail : oldDetails) {
                    mergeStockChange(stockChanges, oldDetail.getIngredientId(),
                            safe(oldDetail.getReceivedQuantity()).negate());
                }
            }
            if (isStockAppliedStatus(invoice.getStatus())) {
                for (ImportDetail detail : details) {
                    mergeStockChange(stockChanges, detail.getIngredientId(),
                            safe(detail.getReceivedQuantity()));
                }
            }

            if (!updateImportVoice(invoice)) {
                connection.rollback();
                return false;
            }

            for (ImportDetail detail : details) {
                if (!ingredientBelongsToSupplier(detail.getIngredientId(), invoice.getSupplierId())
                        || !updateImportVoiceDetail(detail)) {
                    connection.rollback();
                    return false;
                }
            }

            int staffId = getImportStaffId(invoice.getImportId());
            for (java.util.Map.Entry<Integer, BigDecimal> entry : stockChanges.entrySet()) {
                if (entry.getValue().compareTo(BigDecimal.ZERO) != 0) {
                    adjustStockAfterImportEdit(
                            invoice.getImportId(), entry.getKey(), entry.getValue(), staffId);
                }
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

    private boolean isStockAppliedStatus(String status) {
        return "Confirmed".equalsIgnoreCase(status);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void mergeStockChange(
            HashMap<Integer, BigDecimal> changes, int ingredientId, BigDecimal quantity) {
        changes.put(ingredientId,
                changes.getOrDefault(ingredientId, BigDecimal.ZERO).add(quantity));
    }

    private int getImportStaffId(int importId) throws SQLException {
        String sql = """
                     SELECT COALESCE(ConfirmedByStaffId, CreatedByStaffId) AS StaffId
                     FROM ImportInvoices
                     WHERE ImportId = ?
                     """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, importId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("Không tìm thấy nhân viên của phiếu nhập.");
                }
                return result.getInt("StaffId");
            }
        }
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
