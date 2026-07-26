package dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class CustomerMembershipWriteIntegrationTest {

    private CustomerMembershipWriteIntegrationTest() {
    }

    public static void main(String[] args) throws Exception {
        DBContext context = new DBContext();
        int accountId = 0;
        int customerId = 0;
        try {
            accountId = createAccount(context);
            customerId = createCustomer(context, accountId);

            BigDecimal amount = new BigDecimal("125000");
            new CustomerMembershipDAO().addTotalSpent(customerId, amount);
            verifyMembership(context, customerId, amount);
        } finally {
            cleanup(context, customerId, accountId);
            if (context.connection != null) {
                context.connection.close();
            }
        }
    }

    private static int createAccount(DBContext context) throws Exception {
        String sql = """
                     INSERT INTO Accounts
                            (RoleId, Email, PasswordHash, IsEmailVerified,
                             IsActive, CreatedAt, IsDeleted,
                             FailedAttempts, IsLocked)
                     OUTPUT INSERTED.AccountId
                     VALUES
                            (2, ?, 'TEST_ONLY', 1,
                             1, GETDATE(), 0,
                             0, 0)
                     """;
        try (PreparedStatement statement =
                context.connection.prepareStatement(sql)) {
            statement.setString(
                    1, "codex.membership." + System.nanoTime() + "@test.local");
            try (ResultSet result = statement.executeQuery()) {
                require(result.next(), "test account was not created");
                return result.getInt("AccountId");
            }
        }
    }

    private static int createCustomer(DBContext context, int accountId)
            throws Exception {
        String sql = """
                     INSERT INTO Customers
                            (AccountId, FullName, Phone, AvatarUrl,
                             CreatedAt, IsDeleted)
                     OUTPUT INSERTED.CustomerId
                     VALUES (?, 'Codex Membership Test', NULL, NULL,
                             GETDATE(), 0)
                     """;
        try (PreparedStatement statement =
                context.connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            try (ResultSet result = statement.executeQuery()) {
                require(result.next(), "test customer was not created");
                return result.getInt("CustomerId");
            }
        }
    }

    private static void verifyMembership(
            DBContext context, int customerId, BigDecimal amount)
            throws Exception {
        String sql = """
                     SELECT membership.TotalSpent,
                            membership.RankId,
                            (
                                SELECT TOP 1 RankId
                                FROM MembershipRanks
                                WHERE IsDeleted = 0
                                  AND MinTotalSpent <= ?
                                ORDER BY MinTotalSpent DESC, RankId DESC
                            ) AS ExpectedRankId
                     FROM CustomerMemberships membership
                     WHERE membership.CustomerId = ?
                     """;
        try (PreparedStatement statement =
                context.connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, amount);
            statement.setInt(2, customerId);
            try (ResultSet result = statement.executeQuery()) {
                require(result.next(), "membership was not created");
                require(
                        amount.compareTo(result.getBigDecimal("TotalSpent")) == 0,
                        "membership total spent is incorrect");
                require(
                        result.getInt("RankId")
                                == result.getInt("ExpectedRankId"),
                        "membership rank is incorrect");
            }
        }
    }

    private static void cleanup(
            DBContext context, int customerId, int accountId) throws Exception {
        if (context.connection == null) {
            return;
        }
        try (Statement statement = context.connection.createStatement()) {
            statement.execute("""
                              SET ANSI_NULLS ON;
                              SET QUOTED_IDENTIFIER ON;
                              SET ANSI_PADDING ON;
                              SET ANSI_WARNINGS ON;
                              SET CONCAT_NULL_YIELDS_NULL ON;
                              SET ARITHABORT ON;
                              SET NUMERIC_ROUNDABORT OFF;
                              """);
        }
        if (customerId > 0) {
            try (PreparedStatement statement = context.connection.prepareStatement(
                    "DELETE FROM CustomerMemberships WHERE CustomerId = ?")) {
                statement.setInt(1, customerId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = context.connection.prepareStatement(
                    "DELETE FROM Customers WHERE CustomerId = ?")) {
                statement.setInt(1, customerId);
                statement.executeUpdate();
            }
        }
        if (accountId > 0) {
            try (PreparedStatement statement = context.connection.prepareStatement(
                    "DELETE FROM Accounts WHERE AccountId = ?")) {
                statement.setInt(1, accountId);
                statement.executeUpdate();
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
