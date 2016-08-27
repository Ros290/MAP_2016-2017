package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TableData {

	private DbAccess db;
		
	public TableData(DbAccess db) {
		this.db=db;
	}

	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException{
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		TableSchema tSchema=new TableSchema(db,table);
		
		String query="select ";
		
		for(int i=0;i<tSchema.getNumberOfAttributes();i++){
			Column c=tSchema.getColumn(i);
			if(i>0)
				query+=",";
			query += c.getColumnName();
		}
		
		if(tSchema.getNumberOfAttributes()==0)
			throw new SQLException();
		query += (" FROM "+table);
		
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty=true;
		while (rs.next()) {
			empty=false;
			Example currentTuple=new Example();
			for(int i=0;i<tSchema.getNumberOfAttributes();i++)
				if(tSchema.getColumn(i).isNumber())
					currentTuple.add(rs.getDouble(i+1));
				else
					currentTuple.add(rs.getString(i+1));
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if(empty) throw new EmptySetException();
		
		return transSet;
	}
	
	
	/*
	 * Formula ed esegue una interrogazione SQL, per estrarre i valori distinti ordinati di column 
	 * e popolare un insieme, opportunamente scelto, da restituire
	 */
	public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException{
		
		Set<Object> transSet = new TreeSet<Object>();
	
		Statement statement = DbAccess.getConnection().createStatement();
		
		String query="SELECT " + column.getColumnName() + " FROM " + table + " ORDER BY " + column.getColumnName();
		
		ResultSet rs = statement.executeQuery(query);
		int index = 1;
		while (rs.next()) {
			transSet.add(rs.getObject(column.getColumnName()));
			index ++;
		}
		rs.close();
		statement.close();
				
		return transSet;
	}
}
