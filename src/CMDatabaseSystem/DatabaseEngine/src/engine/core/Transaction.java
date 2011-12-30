package engine.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class represents a database transaction. It has multiple operations that
 * can be called in any order. These operations are never applied unless
 * commit() is called. If an error occurs, rollback should be called in order to
 * avoid deadlocks.
 * 
 * 
 * @author Grupo 1
 */
public final class Transaction {
	private Connection databaseConnection;

	/**
	 * Initializes a basic transaction with serializable isolation level.
	 * Serializable ensures that data remains consistent at any given moment.
	 * 
	 * @throws SQLException
	 *             This is thrown if MySQLConfiguration.init was never called
	 */
	public Transaction() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		if (MySQLConfiguration.getUsername() != null)
			try {
				databaseConnection = DriverManager.getConnection(
						MySQLConfiguration.getUrl(), MySQLConfiguration
								.getUsername(), MySQLConfiguration
								.getPassword());
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		else
			try {
				databaseConnection = DriverManager
						.getConnection(MySQLConfiguration.getUrl());
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(-1);
			}

		try {
			databaseConnection.setAutoCommit(false);
			databaseConnection
					.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * This method executes the given select query on the database to which this
	 * object has a connection.
	 * 
	 * @param selectQuery
	 *            A string representing a MySQL select query.
	 * @return A result set containing the results of executing the given query.
	 * @throws SQLException
	 *             If for some reason the query fails.
	 */
	public ResultSet select(String selectQuery) throws SQLException {
		Statement st = databaseConnection.createStatement();
		return st.executeQuery(selectQuery);
	}

	/**
	 * This method executes an update query on the database to which this object
	 * has a connection.
	 * 
	 * @param updateQuery
	 *            A string representing a MySQL update query.
	 * @throws SQLException
	 *             If for some reason the query fails.
	 */
	public void update(String updateQuery) throws SQLException {
		Statement st = databaseConnection.createStatement();
		st.executeUpdate(updateQuery);
	}

	/**
	 * This method executes an insert query on the database to which this object
	 * has a connection.
	 * 
	 * @param insertQuery
	 *            A string representing a MySQL insert query.
	 * @throws SQLException
	 *             If for some reason the query fails.
	 */
	public void insert(String insertQuery) throws SQLException {
		Statement st = databaseConnection.createStatement();
		st.executeUpdate(insertQuery);
	}

	/**
	 * This method executes an insert query on the database to which this object
	 * has a connection.
	 * 
	 * @param insertQuery
	 *            A string representing a MySQL insert query.
	 * @throws SQLException
	 *             If for some reason the query fails.
	 */
	public int insertAndReturnLastInsertedID(String insertQuery)
			throws SQLException {
		Statement st = databaseConnection.createStatement();
		st.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = st.getGeneratedKeys();

		int id = -1;
		if (rs.next()) {
			id = rs.getInt(1);
		} else {
			throw new SQLException("Unable to retrieve last inserted id.");
		}
		rs.close();
		rs = null;
		return id;
	}

	/**
	 * This method executes a delete query on the database to which this object
	 * has a connection.
	 * 
	 * @param insertQuery
	 *            A string representing a MySQL delete query.
	 * @throws SQLException
	 *             If for some reason the query fails.
	 */
	public void delete(String deleteQuery) throws SQLException {
		Statement st = databaseConnection.createStatement();
		st.executeUpdate(deleteQuery);
	}

	/**
	 * This method executes a procedure query on the database to which this
	 * object has a connection.
	 * 
	 * @param procedureQuery
	 *            A string representing a MySQL procedure query.
	 * @throws SQLException
	 *             If for some reason the query fails.
	 */
	public void call(String procedureQuery) throws SQLException {
		CallableStatement cStmt = databaseConnection.prepareCall("{call "
				+ procedureQuery + "}");
		cStmt.execute();
	}

	/**
	 * Commits all the work done in this connection. This may fail if the
	 * connection is already closed. The connection will be closed if the commit
	 * is done.
	 */
	public void commit() {
		try {
			databaseConnection.commit();
			close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Rollbacks all the work done in this connection. This may fail if the
	 * connection is already closed. The connection will be closed if the
	 * rollback is done.
	 */
	public void rollback() {
		try {
			databaseConnection.rollback();
			close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the database connection.
	 * 
	 * @throws SQLException
	 *             If the connection is already closed
	 */
	private void close() throws SQLException {
		databaseConnection.close();
	}
}
