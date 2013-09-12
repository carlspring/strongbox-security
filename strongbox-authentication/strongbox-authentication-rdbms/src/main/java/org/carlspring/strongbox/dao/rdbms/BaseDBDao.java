package org.carlspring.strongbox.dao.rdbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author mtodorov
 */
public interface BaseDBDao
{

    String getTableName();

    Connection getConnection()
            throws SQLException;

    long count()
            throws SQLException;

    long count(String whereClause)
            throws SQLException;

    void closeConnection(Connection connection)
            throws SQLException;

    void closeStatement(Statement statement)
            throws SQLException;

    void closeResultSet(ResultSet resultSet)
            throws SQLException;

    void deleteById(String fieldIdName, long fieldIdValue)
            throws SQLException;

    void deleteByWhereClause(String whereClause)
            throws SQLException;

    String getJdbcURL();

    String getJdbcURLShutdown();

    String getDriver();

    String getDatasourceName();

}