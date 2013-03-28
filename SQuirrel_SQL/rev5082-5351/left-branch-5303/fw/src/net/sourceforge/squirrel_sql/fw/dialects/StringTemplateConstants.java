
package net.sourceforge.squirrel_sql.fw.dialects;


public interface StringTemplateConstants
{

	
	String ST_ADD_UNIQUE_CONSTRAINT_STYLE_ONE = 
		"ALTER TABLE $tableName$ " +
		"ADD $constraint$ $constraintName$ UNIQUE $index$ $indexName$ $indexType$ ($columnName;  separator=\",\"$)";

	String ST_ADD_UNIQUE_CONSTRAINT_STYLE_TWO =
		"ALTER TABLE $tableName$ " +
		"ADD CONSTRAINT $constraintName$ UNIQUE ($columnName;  separator=\",\"$)";
	
	String ST_ADD_AUTO_INCREMENT_STYLE_ONE = 
		"ALTER TABLE $tableName$ MODIFY $columnName$ BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY";
	
	String ST_ADD_AUTO_INCREMENT_STYLE_TWO = 
		"ALTER TABLE $tableName$ ALTER COLUMN $columnName$ IDENTITY";
	
	String ST_ADD_FOREIGN_KEY_CONSTRAINT_STYLE_ONE = 
		"ALTER TABLE $childTableName$ " +
		"ADD $constraint$ $constraintName$ FOREIGN KEY ( $childColumn; separator=\",\"$ ) " +
		"REFERENCES $parentTableName$ ( $parentColumn; separator=\",\"$ )";

	String ST_ALTER_COLUMN_NAME_STYLE_ONE = 
		"ALTER TABLE $tableName$ RENAME COLUMN $oldColumnName$ to $newColumnName$";

	String ST_ALTER_COLUMN_SET_DEFAULT_STYLE_ONE = 
		"ALTER TABLE $tableName$ " +
		"ALTER $columnName$ SET DEFAULT $defaultValue$";
		
	String ST_ALTER_COLUMN_SET_DATA_TYPE_STYLE_ONE = 
		"ALTER TABLE $tableName$ " +
		"ALTER $columnName$ SET DATA TYPE $dataType$";
	
	String ST_ALTER_COLUMN_DROP_DEFAULT_STYLE_ONE = 
		"ALTER TABLE $tableName$ " +
		"ALTER $columnName$ DROP DEFAULT";		
	
	String ST_ALTER_COLUMN_NULL_STYLE_ONE = 
		"ALTER TABLE $tableName$ ALTER COLUMN $columnName$ SET $nullable$";
	
	String ST_ALTER_SEQUENCE_STYLE_ONE = 
		"ALTER SEQUENCE $sequenceName$ " +
		"$restartWith$ $startValue$ " +
		"$incrementBy$ $incrementValue$ ";
		
	String ST_ALTER_SEQUENCE_STYLE_TWO = 
		"ALTER SEQUENCE $sequenceName$ $startWith$ $increment$ $minimum$ $maximum$ $cache$ $cycle$";
	
	String ST_CREATE_INDEX_STYLE_ONE = 
		"CREATE $accessMethod$ INDEX $indexName$ $indexType$ " +
		"ON $tableName$ ( $columnName; separator=\",\"$ )";
	
	String ST_CREATE_INDEX_STYLE_TWO =
		"CREATE $unique$ $storageOption$ INDEX $indexName$ " +
		"ON $tableName$ ( $columnName; separator=\",\"$ )";

	String ST_CREATE_INDEX_STYLE_THREE =
		"CREATE $unique$ INDEX $indexName$ " +
		"ON $tableName$ ( $columnName; separator=\",\"$ )";
	
	String ST_CREATE_SEQUENCE_STYLE_ONE = 
		"CREATE SEQUENCE $sequenceName$ START WITH $startValue$ " +
		"INCREMENT BY $incrementValue$ $cache$ $cacheValue$";	

	String ST_CREATE_SEQUENCE_STYLE_TWO = 
		"CREATE SEQUENCE $sequenceName$ $startWith$ " +
		"$increment$ $minimum$ $maximum$ $cache$ $cycle$";

	String ST_CREATE_SEQUENCE_STYLE_THREE = 
		"CREATE SEQUENCE $sequenceName$  " +
		"$increment$ $minimum$ $maximum$ $startWith$ $cache$ $cycle$";
	
	
	String ST_CREATE_VIEW_STYLE_ONE =
		"CREATE VIEW $viewName$ " +
		"AS $selectStatement$ $with$ $checkOptionType$ $checkOption$";
	
	String ST_CREATE_VIEW_STYLE_TWO = 
		"CREATE VIEW $viewName$ " +
		"AS $selectStatement$ $withCheckOption$";
	
	String ST_DROP_COLUMN_STYLE_ONE = 
		"ALTER TABLE $tableName$ DROP $columnName$ $cascade$";

	String ST_DROP_COLUMN_STYLE_TWO = 
		"ALTER TABLE $tableName$ DROP COLUMN $columnName$ $cascade$";
	
	String ST_DROP_CONSTRAINT_STYLE_ONE = 
		"ALTER TABLE $tableName$ DROP CONSTRAINT $constraintName$";
	
	String ST_DROP_INDEX_STYLE_ONE = 
		"DROP INDEX $indexName$ ON $tableName$";
	
	String ST_DROP_INDEX_STYLE_TWO =
		"DROP INDEX $tableName$.$indexName$";
	
	String ST_DROP_INDEX_STYLE_THREE = 
		"DROP INDEX $indexName$";
	
	String ST_DROP_SEQUENCE_STYLE_ONE = 
		"DROP SEQUENCE $sequenceName$ $cascade$";
	
	String ST_DROP_VIEW_STYLE_ONE = 
		"DROP VIEW $viewName$";

	String ST_DROP_TABLE_STYLE_ONE = 
		"DROP TABLE $tableName$ $cascade$";

	String ST_DROP_MATERIALIZED_VIEW_STYLE_ONE = 
		"DROP MATERIALIZED VIEW $tableName$ $cascade$";
	
	String ST_MODIFY_TABLE_TO_RECONSTRUCT = 
		"MODIFY $tableName$ TO RECONSTRUCT";
	
	String ST_RENAME_OBJECT_STYLE_ONE = 
		"ALTER TABLE $oldObjectName$ RENAME TO $newObjectName$";

	String ST_RENAME_TABLE_STYLE_ONE = 
		"RENAME TABLE $oldObjectName$ TO $newObjectName$";
	
	String ST_RENAME_VIEW_STYLE_ONE = 
		"RENAME VIEW $oldObjectName$ TO $newObjectName$";
	
	String ST_SP_RENAME_STYLE_ONE = 
		"sp_rename $oldObjectName$, $newObjectName$";

	String ST_UPDATE_STYLE_ONE =
		"UPDATE $destTableName$ " +
		"SET $columnName$ = $columnValue$ " +
		"where $whereColumnName$ = $whereValue$";		
	
	
	String ST_UPDATE_CORRELATED_QUERY_STYLE_ONE = 
		"UPDATE $destTableName$ dest SET $columnName$ = " +
		"(SELECT src.$columnName$ " +
		 "FROM $sourceTableName$ src " +
		 "where src.$whereColumnName$ = dest.$whereValue$)";		

	
	String ST_UPDATE_CORRELATED_QUERY_STYLE_TWO = 
		"UPDATE $destTableName$ SET $columnName$ = " +
		"(SELECT $columnName$ " +
		 "FROM $sourceTableName$ " +
		 "where $sourceTableName$.$whereColumnName$ = $destTableName$.$whereValue$)";		
	
	
	
	String ST_ACCESS_METHOD_KEY = "accessMethod";

	String ST_CACHE_KEY = "cache";
	
	String ST_CASCADE_KEY = "cascade";
	
	String ST_CYCLE_KEY = "cycle";
	
	String ST_CACHE_VALUE_KEY = "cacheValue";
	
	String ST_CATALOG_NAME_KEY = "catalogName";
	
	String ST_CHECK_OPTION_KEY = "checkOption";
	
	String ST_CHECK_OPTION_TYPE = "checkOptionType";
	
	String ST_CHILD_COLUMN_KEY = "childColumn";
	
	String ST_CHILD_TABLE_KEY = "childTableName";
	
	String ST_COLUMN_NAME_KEY = "columnName";

	String ST_COLUMN_VALUE_KEY = "columnValue";	
	
	String ST_CONSTRAINT_KEY = "constraint";
	
	String ST_CONSTRAINT_NAME_KEY = "constraintName";

	String ST_DATA_TYPE_KEY = "dataType";
	
	String ST_DEFAULT_VALUE_KEY = "defaultValue";
	
	String ST_DEST_TABLE_NAME_KEY = "destTableName";
	
	String ST_GENERATOR_NAME_KEY = "generatorName";
	
	String ST_INCREMENT_KEY = "increment";
	
	String ST_INCREMENT_VALUE_KEY = "incrementValue";	
	
	String ST_INCREMENT_BY_KEY = "incrementBy";
	
	String ST_INDEX_COLUMNS_KEY = "indexColumns";

	String ST_INDEX_COLUMN_NAME_KEY = "indexColumnName";	
	
	String ST_INDEX_KEY = "index";
	
	String ST_INDEX_NAME_KEY = "indexName";
	
	String ST_INDEX_TYPE_KEY = "indexType";
	
	String ST_MAXIMUM_KEY = "maximum";
	
	String ST_MINIMUM_KEY = "minimum";
	
	String ST_NEW_COLUMN_NAME_KEY = "newColumnName";
	
	String ST_NEW_OBJECT_NAME_KEY = "newObjectName";
	
	String ST_NULLABLE_KEY = "nullable";
	
	String ST_OLD_COLUMN_NAME_KEY = "oldColumnName";
	
	String ST_OLD_OBJECT_NAME_KEY = "oldObjectName";
	
	String ST_PARENT_COLUMN_KEY = "parentColumn";
	
	String ST_PARENT_TABLE_KEY = "parentTableName";
	
	String ST_RESTART_WITH_KEY = "restartWith";
	
	String ST_SCHEMA_NAME_KEY = "schemaName";	
	
	String ST_SEQUENCE_NAME_KEY = "sequenceName";
	
	String ST_SELECT_STATEMENT_KEY = "selectStatement";
	
	String ST_SOURCE_TABLE_NAME_KEY = "sourceTableName";
	
	String ST_START_VALUE_KEY = "startValue";
	
	String ST_START_WITH_KEY = "startWith";
	
	String ST_STORAGE_OPTION_KEY = "storageOption";
	
	String ST_TABLE_NAME_KEY = "tableName";
	
	String ST_TRIGGER_NAME_KEY = "triggerName";
	
	String ST_UNIQUE_KEY = "unique";
	
	String ST_VALUE_KEY = "value";
	
	String ST_VIEW_NAME_KEY = "viewName";
	
	String ST_WHERE_COLUMN_NAME_KEY = "whereColumnName";
	
	String ST_WHERE_VALUE_KEY = "whereValue";
	
	String ST_WITH_CHECK_OPTION_KEY = "withcheckOption"; 
}
