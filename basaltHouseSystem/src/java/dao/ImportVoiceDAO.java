/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        return 0;
    }

    public boolean inseartImportInvoices(ImportInvoice v, ImportDetail detail) {
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
            detail.setImportId(importId);

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

            if (!"Pending".equalsIgnoreCase(v.getStatus())
                    && detail.getReceivedQuantity() != null
                    && detail.getReceivedQuantity().compareTo(BigDecimal.ZERO) > 0) {
                updateStockAfterImport(v, detail);
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

    public ImportInvoicesDetail getImportInvoicesDetailById(int id) {

        try {
            String sql = """
                         SELECT  top 1 i.ImportId,
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
                           AND i.ImportId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
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
                return detail;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;

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

            st.executeUpdate();
            return true;

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
                          WHERE ImportId = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setObject(1, d.getIngredientId());
            st.setObject(2, d.getOrderedQuantity());
            st.setObject(3, d.getReceivedQuantity());
            st.setObject(4, d.getUnitPrice());
            st.setObject(5, d.getDiscrepancyNote());
            st.setObject(6, d.getNote());
            st.setObject(7, d.getImportId());
            st.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;

    }
}
