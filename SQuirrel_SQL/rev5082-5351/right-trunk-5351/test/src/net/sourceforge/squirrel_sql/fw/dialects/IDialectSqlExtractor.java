
package net.sourceforge.squirrel_sql.fw.dialects;

interface IDialectSqlExtractor {
	boolean supportsOperation(HibernateDialect dialect);
	String[] getSql (HibernateDialect dialect);
}