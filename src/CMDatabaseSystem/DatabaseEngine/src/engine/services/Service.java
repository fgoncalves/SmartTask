package engine.services;

import java.sql.SQLException;

import engine.core.Transaction;

/**
 * 
 * This class represents a service. A service is a set of operations applied to
 * the database inside a transaction. Such operations are committed in the end
 * of the service, or rolled back in case an SQLException is thrown by the
 * service's subclass.<br/>
 * 
 * When the Service object is created a protected attribute transaction of type
 * Transaction is created. This should be used by the subclass to execute
 * operations to the database.
 * 
 * @author Grupo 1
 * 
 * @param <T>
 *            Any type of object.
 */
public abstract class Service<T> {
	protected Transaction transaction;

	public Service() {
		try {
			transaction = new Transaction();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * All implemented services should override this method. If a database
	 * access is needed, a protected attribute transaction is made available
	 * through this class.
	 * 
	 * @return An instance of the type T.
	 * @throws SQLException
	 *             If this method executes any query to the database it may
	 *             throw an SQLException. This should be thrown if an error
	 *             occurs and a roll back is needed.
	 */
	protected abstract T action() throws SQLException;

	/**
	 * This method executes and commits the transaction. If an error occurs then
	 * the transaction is rolled back.
	 * 
	 * @return The value returned by the Service's subclass action method.
	 */
	public T execute() {
		T value = null;
		try {
			value = action();
			transaction.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			transaction.rollback();
		}
		return value;
	}
}
