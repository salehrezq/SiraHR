package datalink;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Performance;

/**
 *
 * @author Saleh
 */
public class CRUDPerformance {

    private static Connection conn;

    public static boolean create(Performance performance) {

        int insert = 0;

        try {
            String sql = "INSERT INTO performance ("
                    + "`employee_id`, `date_time`, `type_id`,"
                    + " `state`, `amount`, `title`, `description` )"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?)";
            conn = Connect.getConnection();
            PreparedStatement p = conn.prepareStatement(sql);

            p.setInt(1, performance.getEmployeeId());
            p.setObject(2, performance.getDateTime());
            p.setInt(3, performance.getTypeId());
            p.setBoolean(4, performance.getState());
            p.setBigDecimal(5, performance.getAmount());
            p.setString(6, performance.getTitle());
            p.setString(7, performance.getDescription());
            insert = p.executeUpdate();
            conn.commit();

        } catch (SQLException ex) {
            Logger.getLogger(CRUDPerformance.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return insert > 0;
    }

    public static List getPerformanceRecordByEmployeeByMonth(int employeeID, YearMonth ym) {

        List<Performance> performanceList = new ArrayList<>();

        try {
            LocalDate firstOfThisMonth = ym.atDay(1);
            LocalDate firstOfNextMonth = ym.plusMonths(1).atDay(1);

            String sql = "SELECT * FROM `performance`"
                    + " WHERE `employee_id` = ? AND `date_time` >= ? AND `date_time` < ?"
                    + " ORDER BY `date_time` ASC";

            conn = Connect.getConnection();
            PreparedStatement p = conn.prepareStatement(sql);

            p.setInt(1, employeeID);
            p.setObject(2, firstOfThisMonth);
            p.setObject(3, firstOfNextMonth);

            ResultSet result = p.executeQuery();

            while (result.next()) {

                Performance performance = new Performance();
                performance.setId(result.getInt("id"));
                performance.setEmployeeId(result.getInt("employee_id"));
                performance.setState(result.getBoolean("state"));
                performance.setTypeId(result.getInt("type_id"));
                performance.setAmount(result.getBigDecimal("amount"));
                performance.setTitle(result.getString("title"));
                performance.setDescription(result.getString("description"));
                performance.setDateTime(result.getObject("date_time", LocalDateTime.class));
                performanceList.add(performance);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDPerformance.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return performanceList;
    }

    public static BigDecimal getPerformanceRecordByEmployeeByMonthAggregated(int employeeID, YearMonth ym) {

        BigDecimal performanceGain = null;

        try {
            LocalDate firstOfThisMonth = ym.atDay(1);
            LocalDate firstOfNextMonth = ym.plusMonths(1).atDay(1);

            String sql = "SELECT SUM(case when state = 1 then amount ELSE - amount END) "
                    + "AS `performance_gain` "
                    + "FROM `performance` "
                    + "WHERE `employee_id` = ? "
                    + "AND state IN (1, 0) "
                    + "AND `date_time` >= ? "
                    + "AND `date_time` < ?";

            conn = Connect.getConnection();
            PreparedStatement p = conn.prepareStatement(sql);

            p.setInt(1, employeeID);
            p.setObject(2, firstOfThisMonth);
            p.setObject(3, firstOfNextMonth);

            ResultSet result = p.executeQuery();
            boolean isRecordAvailable = result.isBeforeFirst();

            if (isRecordAvailable) {
                result.next();
                performanceGain = result.getBigDecimal("performance_gain");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDPerformance.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return (performanceGain == null) ? BigDecimal.ZERO : performanceGain;
    }

    public static Performance getById(int id) {

        Performance performance = null;
        try {
            String sql = "SELECT * FROM `performance` WHERE id = ? LIMIT 1";
            conn = Connect.getConnection();
            PreparedStatement p = conn.prepareStatement(sql);
            p.setInt(1, id);
            ResultSet result = p.executeQuery();

            while (result.next()) {
                performance = new Performance();
                performance.setId(id);
                performance.setDateTime(result.getObject("date_time", LocalDateTime.class));
                performance.setState(result.getBoolean("state"));
                performance.setTypeId(result.getInt("type_id"));
                performance.setAmount(result.getBigDecimal("amount"));
                performance.setTitle(result.getString("title"));
                performance.setDescription(result.getString("description"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CRUDPerformance.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return performance;
    }

    public static boolean update(Performance performance) {

        int update = 0;

        try {
            String sql = "UPDATE `performance` "
                    + "SET "
                    + "`date_time`= ?, "
                    + "`state` = ?, "
                    + "`type_id` = ?, "
                    + "`amount` = ?, "
                    + "`title` = ?, "
                    + "`description` = ? "
                    + "WHERE id = ?";
            conn = Connect.getConnection();
            PreparedStatement p = conn.prepareStatement(sql);

            p.setObject(1, performance.getDateTime());
            p.setBoolean(2, performance.getState());
            p.setInt(3, performance.getTypeId());
            p.setBigDecimal(4, performance.getAmount());
            p.setString(5, performance.getTitle());
            p.setString(6, performance.getDescription());
            p.setInt(7, performance.getId());

            update = p.executeUpdate();
            conn.commit();

        } catch (SQLException ex) {
            Logger.getLogger(CRUDPerformance.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return update > 0;
    }

    /**
     * Update the {@code locked } attribute of the relation to {@code true }
     * if salary is paid or to {@code false } if salary is deleted. This method
     * uses the passed connection to execute update on it to benefit from atomic
     * commit for data consistency sake.
     *
     * @param conn
     * @param employeeId
     * @param yearMonthSubject
     * @throws SQLException
     */
    private static void switchRelatedPerformanceLock(Connection conn, int employeeId, LocalDate yearMonthSubject, boolean locked) throws SQLException {
        String sqlLockSwitchPerformance = "UPDATE `performance` SET `locked` = " + locked + " "
                + "WHERE `employee_id` = ? AND `date_time` >= ? AND `date_time` < ?";

        LocalDate fotm = H.getYearMonth(yearMonthSubject).atDay(1);
        LocalDate fonm = H.getYearMonth(yearMonthSubject).plusMonths(1).atDay(1);

        LocalDateTime firstOfThisMonth = LocalDateTime.of(fotm, LocalTime.of(0, 0, 0));
        LocalDateTime firstOfNextMonth = LocalDateTime.of(fonm, LocalTime.of(0, 0, 0));

        PreparedStatement lockSwitchPerformance = conn.prepareStatement(sqlLockSwitchPerformance);
        lockSwitchPerformance.setInt(1, employeeId);
        lockSwitchPerformance.setObject(2, firstOfThisMonth);
        lockSwitchPerformance.setObject(3, firstOfNextMonth);
        lockSwitchPerformance.executeUpdate();
    }

    protected static void lockRelatedPerformance(Connection conn, int employeeId, LocalDate yearMonthSubject) throws SQLException {
        switchRelatedPerformanceLock(conn, employeeId, yearMonthSubject, true);
    }

    protected static void unlockRelatedPerformance(Connection conn, int employeeId, LocalDate yearMonthSubject) throws SQLException {
        switchRelatedPerformanceLock(conn, employeeId, yearMonthSubject, false);
    }

    public static boolean delete(Integer id) {

        int delete = 0;

        try {
            String sql = "DELETE FROM `performance` WHERE `id` = ? LIMIT 1";
            conn = Connect.getConnection();
            PreparedStatement p = conn.prepareStatement(sql);

            p.setInt(1, id);

            delete = p.executeUpdate();
            conn.commit();

        } catch (SQLException ex) {
            Logger.getLogger(CRUDPerformance.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Connect.cleanUp();
        }
        return delete > 0;
    }
}
