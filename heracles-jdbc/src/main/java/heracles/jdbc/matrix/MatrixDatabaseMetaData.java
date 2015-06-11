package heracles.jdbc.matrix;

import heracles.jdbc.matrix.exception.MatrixException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class MatrixDatabaseMetaData implements DatabaseMetaData {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatrixDatabaseMetaData.class);

	private DatabaseMetaData databaseMetaData;

	public MatrixDatabaseMetaData(DataSource dataSource) {
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection(dataSource);
			this.databaseMetaData = conn.getMetaData();
		} catch (SQLException e) {
			LOGGER.error("MatrixDatabaseMetaData : init MatrixDatabaseMetaData error : " + e.getMessage());
			throw new MatrixException(e);
		} finally {

		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return databaseMetaData.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return databaseMetaData.isWrapperFor(iface);
	}

	@Override
	public boolean allProceduresAreCallable() throws SQLException {
		return false;
	}

	@Override
	public boolean allTablesAreSelectable() throws SQLException {
		return databaseMetaData.allTablesAreSelectable();
	}

	@Override
	public String getURL() throws SQLException {
		return databaseMetaData.getURL();
	}

	@Override
	public String getUserName() throws SQLException {
		return databaseMetaData.getUserName();
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return databaseMetaData.isReadOnly();
	}

	@Override
	public boolean nullsAreSortedHigh() throws SQLException {
		return databaseMetaData.nullsAreSortedHigh();
	}

	@Override
	public boolean nullsAreSortedLow() throws SQLException {
		return databaseMetaData.nullsAreSortedLow();
	}

	@Override
	public boolean nullsAreSortedAtStart() throws SQLException {
		return databaseMetaData.nullsAreSortedAtStart();
	}

	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return databaseMetaData.nullsAreSortedAtEnd();
	}

	@Override
	public String getDatabaseProductName() throws SQLException {
		return databaseMetaData.getDatabaseProductName();
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return databaseMetaData.getDatabaseProductVersion();
	}

	@Override
	public String getDriverName() throws SQLException {
		return databaseMetaData.getDriverName();
	}

	@Override
	public String getDriverVersion() throws SQLException {
		return databaseMetaData.getDriverVersion();
	}

	@Override
	public int getDriverMajorVersion() {
		return databaseMetaData.getDriverMajorVersion();
	}

	@Override
	public int getDriverMinorVersion() {
		return databaseMetaData.getDriverMinorVersion();
	}

	@Override
	public boolean usesLocalFiles() throws SQLException {
		return databaseMetaData.usesLocalFiles();
	}

	@Override
	public boolean usesLocalFilePerTable() throws SQLException {
		return databaseMetaData.usesLocalFilePerTable();
	}

	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return databaseMetaData.supportsMixedCaseIdentifiers();
	}

	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return databaseMetaData.storesUpperCaseIdentifiers();
	}

	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return databaseMetaData.storesLowerCaseIdentifiers();
	}

	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return databaseMetaData.storesMixedCaseIdentifiers();
	}

	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return databaseMetaData.supportsMixedCaseQuotedIdentifiers();
	}

	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return databaseMetaData.storesUpperCaseQuotedIdentifiers();
	}

	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return databaseMetaData.storesLowerCaseQuotedIdentifiers();
	}

	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return databaseMetaData.storesMixedCaseQuotedIdentifiers();
	}

	@Override
	public String getIdentifierQuoteString() throws SQLException {
		return databaseMetaData.getIdentifierQuoteString();
	}

	@Override
	public String getSQLKeywords() throws SQLException {
		return databaseMetaData.getSQLKeywords();
	}

	@Override
	public String getNumericFunctions() throws SQLException {
		return databaseMetaData.getNumericFunctions();
	}

	@Override
	public String getStringFunctions() throws SQLException {
		return databaseMetaData.getStringFunctions();
	}

	@Override
	public String getSystemFunctions() throws SQLException {
		return databaseMetaData.getSystemFunctions();
	}

	@Override
	public String getTimeDateFunctions() throws SQLException {
		return databaseMetaData.getTimeDateFunctions();
	}

	@Override
	public String getSearchStringEscape() throws SQLException {
		return databaseMetaData.getSearchStringEscape();
	}

	@Override
	public String getExtraNameCharacters() throws SQLException {
		return databaseMetaData.getExtraNameCharacters();
	}

	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return databaseMetaData.supportsAlterTableWithAddColumn();
	}

	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return databaseMetaData.supportsAlterTableWithDropColumn();
	}

	@Override
	public boolean supportsColumnAliasing() throws SQLException {
		return databaseMetaData.supportsColumnAliasing();
	}

	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return databaseMetaData.nullPlusNonNullIsNull();
	}

	@Override
	public boolean supportsConvert() throws SQLException {
		return databaseMetaData.supportsConvert();
	}

	@Override
	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		return databaseMetaData.supportsConvert(fromType, toType);
	}

	@Override
	public boolean supportsTableCorrelationNames() throws SQLException {
		return databaseMetaData.supportsTableCorrelationNames();
	}

	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return databaseMetaData.supportsDifferentTableCorrelationNames();
	}

	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOrderByUnrelated() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupBy() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupByUnrelated() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsLikeEscapeClause() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleResultSets() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsNonNullableColumns() throws SQLException {
		return databaseMetaData.supportsNonNullableColumns();
	}

	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return databaseMetaData.supportsMinimumSQLGrammar();
	}

	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return databaseMetaData.supportsCoreSQLGrammar();
	}

	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return databaseMetaData.supportsExtendedSQLGrammar();
	}

	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return databaseMetaData.supportsANSI92EntryLevelSQL();
	}

	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return databaseMetaData.supportsANSI92IntermediateSQL();
	}

	@Override
	public boolean supportsANSI92FullSQL() throws SQLException {
		return databaseMetaData.supportsANSI92FullSQL();
	}

	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return databaseMetaData.supportsIntegrityEnhancementFacility();
	}

	@Override
	public boolean supportsOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsFullOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public String getSchemaTerm() throws SQLException {
		return databaseMetaData.getSchemaTerm();
	}

	@Override
	public String getProcedureTerm() throws SQLException {
		return databaseMetaData.getProcedureTerm();
	}

	@Override
	public String getCatalogTerm() throws SQLException {
		return databaseMetaData.getCatalogTerm();
	}

	@Override
	public boolean isCatalogAtStart() throws SQLException {
		return databaseMetaData.isCatalogAtStart();
	}

	@Override
	public String getCatalogSeparator() throws SQLException {
		return databaseMetaData.getCatalogSeparator();
	}

	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsPositionedDelete() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsPositionedUpdate() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSelectForUpdate() throws SQLException {
		return databaseMetaData.supportsSelectForUpdate();
	}

	@Override
	public boolean supportsStoredProcedures() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInExists() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInIns() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsUnion() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsUnionAll() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public int getMaxBinaryLiteralLength() throws SQLException {
		return databaseMetaData.getMaxBinaryLiteralLength();
	}

	@Override
	public int getMaxCharLiteralLength() throws SQLException {
		return databaseMetaData.getMaxCharLiteralLength();
	}

	@Override
	public int getMaxColumnNameLength() throws SQLException {
		return databaseMetaData.getMaxColumnNameLength();
	}

	@Override
	public int getMaxColumnsInGroupBy() throws SQLException {
		return databaseMetaData.getMaxColumnsInGroupBy();
	}

	@Override
	public int getMaxColumnsInIndex() throws SQLException {
		return databaseMetaData.getMaxColumnsInIndex();
	}

	@Override
	public int getMaxColumnsInOrderBy() throws SQLException {
		return databaseMetaData.getMaxColumnsInOrderBy();
	}

	@Override
	public int getMaxColumnsInSelect() throws SQLException {
		return databaseMetaData.getMaxColumnsInSelect();
	}

	@Override
	public int getMaxColumnsInTable() throws SQLException {
		return databaseMetaData.getMaxColumnsInTable();
	}

	@Override
	public int getMaxConnections() throws SQLException {
		return databaseMetaData.getMaxConnections();
	}

	@Override
	public int getMaxCursorNameLength() throws SQLException {
		return databaseMetaData.getMaxCursorNameLength();
	}

	@Override
	public int getMaxIndexLength() throws SQLException {
		return databaseMetaData.getMaxIndexLength();
	}

	@Override
	public int getMaxSchemaNameLength() throws SQLException {
		return databaseMetaData.getMaxSchemaNameLength();
	}

	@Override
	public int getMaxProcedureNameLength() throws SQLException {
		return databaseMetaData.getMaxProcedureNameLength();
	}

	@Override
	public int getMaxCatalogNameLength() throws SQLException {
		return databaseMetaData.getMaxCatalogNameLength();
	}

	@Override
	public int getMaxRowSize() throws SQLException {
		return databaseMetaData.getMaxRowSize();
	}

	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return false;
	}

	@Override
	public int getMaxStatementLength() throws SQLException {
		return databaseMetaData.getMaxStatementLength();
	}

	@Override
	public int getMaxStatements() throws SQLException {
		return databaseMetaData.getMaxStatements();
	}

	@Override
	public int getMaxTableNameLength() throws SQLException {
		return databaseMetaData.getMaxTableNameLength();
	}

	@Override
	public int getMaxTablesInSelect() throws SQLException {
		return databaseMetaData.getMaxTablesInSelect();
	}

	@Override
	public int getMaxUserNameLength() throws SQLException {
		return databaseMetaData.getMaxUserNameLength();
	}

	@Override
	public int getDefaultTransactionIsolation() throws SQLException {
		return databaseMetaData.getDefaultTransactionIsolation();
	}

	@Override
	public boolean supportsTransactions() throws SQLException {
		return databaseMetaData.supportsTransactions();
	}

	@Override
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return databaseMetaData.supportsTransactionIsolationLevel(level);
	}

	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return databaseMetaData.supportsDataDefinitionAndDataManipulationTransactions();
	}

	@Override
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return databaseMetaData.supportsDataManipulationTransactionsOnly();
	}

	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return databaseMetaData.dataDefinitionCausesTransactionCommit();
	}

	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return databaseMetaData.dataDefinitionIgnoredInTransactions();
	}

	@Override
	public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
			throws SQLException {
		return databaseMetaData.getProcedures(catalog, schemaPattern, procedureNamePattern);
	}

	@Override
	public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern,
			String columnNamePattern) throws SQLException {
		return databaseMetaData.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
	}

	@Override
	public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
			throws SQLException {
		return databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types);
	}

	@Override
	public ResultSet getSchemas() throws SQLException {
		return databaseMetaData.getSchemas();
	}

	@Override
	public ResultSet getCatalogs() throws SQLException {
		return databaseMetaData.getCatalogs();
	}

	@Override
	public ResultSet getTableTypes() throws SQLException {
		return databaseMetaData.getTableTypes();
	}

	@Override
	public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
			throws SQLException {
		return databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
	}

	@Override
	public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
			throws SQLException {
		return databaseMetaData.getColumnPrivileges(catalog, schema, table, columnNamePattern);
	}

	@Override
	public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
			throws SQLException {
		return databaseMetaData.getTablePrivileges(catalog, schemaPattern, tableNamePattern);
	}

	@Override
	public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
			throws SQLException {
		return databaseMetaData.getBestRowIdentifier(catalog, schema, table, scope, nullable);
	}

	@Override
	public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
		return databaseMetaData.getVersionColumns(catalog, schema, table);
	}

	@Override
	public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		return databaseMetaData.getPrimaryKeys(catalog, schema, table);
	}

	@Override
	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		return databaseMetaData.getImportedKeys(catalog, schema, table);
	}

	@Override
	public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
		return databaseMetaData.getExportedKeys(catalog, schema, table);
	}

	@Override
	public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable,
			String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
		return databaseMetaData.getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog,
				foreignSchema, foreignTable);
	}

	@Override
	public ResultSet getTypeInfo() throws SQLException {
		return databaseMetaData.getTypeInfo();
	}

	@Override
	public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
			throws SQLException {
		return databaseMetaData.getIndexInfo(catalog, schema, table, unique, approximate);
	}

	@Override
	public boolean supportsResultSetType(int type) throws SQLException {
		return databaseMetaData.supportsResultSetType(type);
	}

	@Override
	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return databaseMetaData.supportsResultSetConcurrency(type, concurrency);
	}

	@Override
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return databaseMetaData.ownUpdatesAreVisible(type);
	}

	@Override
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return databaseMetaData.ownDeletesAreVisible(type);
	}

	@Override
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return databaseMetaData.ownInsertsAreVisible(type);
	}

	@Override
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return databaseMetaData.othersUpdatesAreVisible(type);
	}

	@Override
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return databaseMetaData.othersDeletesAreVisible(type);
	}

	@Override
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return databaseMetaData.othersInsertsAreVisible(type);
	}

	@Override
	public boolean updatesAreDetected(int type) throws SQLException {
		return databaseMetaData.updatesAreDetected(type);
	}

	@Override
	public boolean deletesAreDetected(int type) throws SQLException {
		return databaseMetaData.deletesAreDetected(type);
	}

	@Override
	public boolean insertsAreDetected(int type) throws SQLException {
		return databaseMetaData.insertsAreDetected(type);
	}

	@Override
	public boolean supportsBatchUpdates() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
			throws SQLException {
		return databaseMetaData.getUDTs(catalog, schemaPattern, typeNamePattern, types);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return databaseMetaData.getConnection();
	}

	@Override
	public boolean supportsSavepoints() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsNamedParameters() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleOpenResults() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGetGeneratedKeys() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		return databaseMetaData.getSuperTypes(catalog, schemaPattern, typeNamePattern);
	}

	@Override
	public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return databaseMetaData.getSuperTables(catalog, schemaPattern, tableNamePattern);
	}

	@Override
	public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern,
			String attributeNamePattern) throws SQLException {
		return databaseMetaData.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
	}

	@Override
	public boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return false;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return databaseMetaData.getResultSetHoldability();
	}

	@Override
	public int getDatabaseMajorVersion() throws SQLException {
		return databaseMetaData.getDatabaseMajorVersion();
	}

	@Override
	public int getDatabaseMinorVersion() throws SQLException {
		return databaseMetaData.getDatabaseMinorVersion();
	}

	@Override
	public int getJDBCMajorVersion() throws SQLException {
		return databaseMetaData.getJDBCMajorVersion();
	}

	@Override
	public int getJDBCMinorVersion() throws SQLException {
		return databaseMetaData.getJDBCMinorVersion();
	}

	@Override
	public int getSQLStateType() throws SQLException {
		return databaseMetaData.getSQLStateType();
	}

	@Override
	public boolean locatorsUpdateCopy() throws SQLException {
		return databaseMetaData.locatorsUpdateCopy();
	}

	@Override
	public boolean supportsStatementPooling() throws SQLException {
		return databaseMetaData.supportsStatementPooling();
	}

	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		return databaseMetaData.getRowIdLifetime();
	}

	@Override
	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
		return databaseMetaData.getSchemas(catalog, schemaPattern);
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return false;
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return databaseMetaData.autoCommitFailureClosesAllResultSets();
	}

	@Override
	public ResultSet getClientInfoProperties() throws SQLException {
		return databaseMetaData.getClientInfoProperties();
	}

	@Override
	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
		return databaseMetaData.getFunctions(catalog, schemaPattern, functionNamePattern);
	}

	@Override
	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern,
			String columnNamePattern) throws SQLException {
		return databaseMetaData.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
	}

	@Override
	public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern,
			String columnNamePattern) throws SQLException {
		return databaseMetaData.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
	}

	@Override
	public boolean generatedKeyAlwaysReturned() throws SQLException {
		return false;
	}

}
