/**
 * 
 */
package com.bitBusy.goingg.database.setup;

import com.bitBusy.goingg.database.tables.TableUserAccount;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author SumaHarsha
 *
 */
public class DBManager extends SQLiteOpenHelper{
	
	/**Flag to indicate if db is open*/
	private static boolean isOpen;

	/**updated for rel 1.9*/	
	private static final int DBVERSION = 6;
	
	//private static final String ALTERTABLEADDCOLNUMGOING = "ALTER TABLE TableSavedReminders ADD COLUMN " + TableSavedReminders.NUMGOING.name() + 
		//	" INTEGER";
//	private static final String ALTERTABLEADDCOLNUMNOPE = "ALTER TABLE TableSavedReminders ADD COLUMN " + TableSavedReminders.NUMNOPE.name() + 
			//" INTEGER";
	//private static final String ALTERTABLEADDCOLNUMMAYBE= "ALTER TABLE TableSavedReminders ADD COLUMN " + TableSavedReminders.NUMMAYBE.name() + 
			//" INTEGER";
	
	/** Name of the database*/
	private static final String DBNAME = "weventData";
	
	/** String holding create table command*/
	private static final String CREATETABLECOMMAND = "CREATE TABLE ";
	
		/** Reference to instance of this class */
	private static DBManager myDBManagerInstance;
	
	/** Reference to the writeable database instance*/
	private static SQLiteDatabase myWritableDb;

	


	//TODO
	//Add foreign key constraint:
	//http://www.codeproject.com/Articles/119293/Using-SQLite-Database-with-Android
	

	/* Constructor - (makes it a Singleton class
	 *  calls the constructor of super class
	 * @param the current context (View)
	 */
	private DBManager(Context the_context)
	{
		super(the_context, DBNAME, null, DBVERSION);
	}

	 /**
     * Get default instance of the class to keep it a singleton
     * @param the_context the application context
     * @return instance of this class
     */
    public static DBManager getInstance(Context the_context) {
        if (myDBManagerInstance == null) {
        	myDBManagerInstance = new DBManager(the_context);
        }
        return myDBManagerInstance;
    }
	@Override
	/** First method called when this class is referenced
	 * Method creates the database tables
	 * @param the_db the object that refers to the database
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	public void onCreate(SQLiteDatabase the_db) {
			
		//Get array of Classes (enums) that each refer to one table of the DB
		Class<?>[] tables = getTableClasses();
		//Create each table
		for (int i = 0; i < tables.length; i++)
		{
			createTable(the_db, tables[i]);
		}
	}
	
	 /**
     * Returns a writable database instance in order not to open and close many
     * SQLiteDatabase objects simultaneously
     *
     * @return a writable instance to SQLiteDatabase
     */
    public SQLiteDatabase getMyWritableDatabase() {
    	
        if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
            myWritableDb = this.getWritableDatabase();
        }
         return myWritableDb;
    }
 
    @Override
    public void close() {
        super.close();
        if (myWritableDb != null) {
            myWritableDb.close();
            myWritableDb = null;
        }
    } 
	/**
	 * Method to get all the table Classes of the database
	 * @return an array of Class<?> of tables
	 */
	private Class<?>[] getTableClasses()
	{
		Class<?>[] classArr = new Class<?>[TablesEnum.values().length];
		int i = 0;
		for (TablesEnum value: TablesEnum.values())
		{
			classArr[i] = value.getClassRef();
			i++;
		}
		return classArr;

	}
	/*
	 * Method to create a table in the database 
	 * @param the_db the database (object), in which the table is to be created
	 * @param the_table the Class representing a table (enum)
	 */
	private void createTable(SQLiteDatabase the_db, Class<?> the_table)
	{
		if (the_db != null && the_table != null && the_table.isEnum())
		{
			//Get enum constants - they will be of type TableDefinition
			TableDefinition[] constants =  (TableDefinition[]) the_table.getEnumConstants();
			//StringBuffer over String since it is mutable
			StringBuffer createTable = new StringBuffer(CREATETABLECOMMAND);
			
			//Building the SQL statement to create table
			createTable.append(the_table.getSimpleName());
			createTable.append("(");
			
			//Append all the columns of this table and their types
			for (int i = 0; i< constants.length; i++)
			{
				createTable.append(constants[i].getName());
				createTable.append(constants[i].getType());
				if (i < constants.length-1)
				{
					createTable.append(",");
				}

			}
			createTable.append(");");
			the_db.execSQL(createTable.toString());
		}
	}
	
	
	@Override
	/*
	 * Method executed on upgrade of the database
	 * Deletes tables if already exist, and re-creates them
	 * @param the_db the database(object), in which tables are dropped and re-created
	 * @param oldVersion of the database
	 * @param newVersion of the database
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	public void onUpgrade(SQLiteDatabase the_db, int oldVersion, int newVersion) {

		/*Class<?>[] tables = getTableClasses();
		//drop and create each table
		for (int i = 0; i < tables.length; i++)
		{
			dropTable(the_db, tables[i]);
			createTable(the_db, tables[i]);
		}*/
		if (newVersion > oldVersion)
		{
			try
			{
		/*	the_db.execSQL(ALTERTABLEADDCOLNUMGOING);
			the_db.execSQL(ALTERTABLEADDCOLNUMNOPE);
			the_db.execSQL(ALTERTABLEADDCOLNUMMAYBE);
			createTable(the_db, TableMarkedEvents.class);*/
			createTable(the_db, TableUserAccount.class);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Method to drop a table
	 * @param the_db the database(object), in which the table is to be dropped
	 * @param the_table which is to be dropped
	 */
/*	private void dropTable(SQLiteDatabase the_db, Class<?> the_table)
	{
		if (the_db != null && the_table != null)
		{
			the_db.execSQL(DROPTABLECOMMAND + the_table.getSimpleName());
		}
	}*/

	/** Method to get the isOpen flag value
	 * @return boolean flag value
	 */
	public boolean isOpen()
	{
		return isOpen;
	}
	
	/** Method to set isOpen
	 * @param value to set it to
	 */
	public void setIsOpen(boolean the_value)
	{
		isOpen = the_value;
	}

}
