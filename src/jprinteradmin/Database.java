/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jprinteradmin;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stefan
 */
public class Database {

    public static void setConn() {
        try {
            Class.forName(getJDBCclass());
            switch (Utility.iniDatabaseType) {
                case "local":
                    conn = DriverManager.getConnection(getConnString());
                    break;
                case "mysql":
                    conn = DriverManager.getConnection(getConnString(), Utility.iniDatabaseUser, Utility.iniDatabasePassword);
                    break;
                case "mssql":
                    conn = DriverManager.getConnection(getConnString(), Utility.iniDatabaseUser, Utility.iniDatabasePassword);
                    Statement stat = conn.createStatement();
                    stat.execute("SET DATEFORMAT 'ymd'");
                    stat.close();
                    
                    break;
            }
        } catch (ClassNotFoundException | SQLException ex) {
            //Logger.getLogger(createDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void lock(){ 
        switch (Utility.iniDatabaseType) {
            case "mysql":
                try {
                    ArrayList<String> tables = new ArrayList();
                    DatabaseMetaData metaData = Database.conn.getMetaData();
                    String[] types = {"TABLE"};
                    ResultSet rsTables = metaData.getTables(null, null, null, types);
                    while (rsTables.next()) {
                        tables.add(rsTables.getString(3) + " WRITE");
                    }        
                    String statement = "LOCK TABLES " + Utility.implode(tables, ",");
                    Statement stat = Database.conn.createStatement();
                    stat.execute(statement);
                    System.out.println("Database locked");
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Lock database failed");
                }
                break;
            case "mssql":
                try {
                    String statement = "ALTER DATABASE " + Utility.iniDatabaseName + " SET SINGLE_USER";
                    Statement stat;            
                    stat = Database.conn.createStatement();
                    stat.execute(statement); 
                    System.out.println("Database locked");
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Lock database failed");
                }      
        }

    }
    
    public static void unlock(){ 
        switch (Utility.iniDatabaseType) {
            case "mysql":
                try {       
                    String statement = "UNLOCK TABLES";
                    Statement stat = Database.conn.createStatement();
                    stat.execute(statement);
                    System.out.println("Database unlocked");                    
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("unlock database failed");
                }
                break;
            case "mssql":
                try {
                    String statement = "ALTER DATABASE " + Utility.iniDatabaseName + " SET MULTI_USER";
                    Statement stat;            
                    stat = Database.conn.createStatement();
                    stat.execute(statement); 
                    System.out.println("Database unlocked");
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("unlock database failed");
                } 
        }

    }    
    
    private static String getJDBCclass() {
        String class1 = null;
        switch (Utility.iniDatabaseType) {
            case "local":
                class1 = "org.sqlite.JDBC";
                break;
            case "mysql":
                class1 = "com.mysql.jdbc.Driver";
                break;
            case "mssql":
                class1 = "net.sourceforge.jtds.jdbc.Driver";
                break;
        }
        return class1;
    }

    private static String getConnString() {
        String connString = null;
        switch (Utility.iniDatabaseType) {
            case "local":
                connString = "jdbc:sqlite:" + jprinteradmin.Utility.defaultDirectory() + File.separator + ".jprinteradmin" + File.separator + "jprinteradmin.db";
                break;
            case "mysql":
                connString = ("jdbc:mysql://" + Utility.iniDatabaseHost + ":" + Utility.iniDatabasePort + "/" + Utility.iniDatabaseName + "?zeroDateTimeBehavior=convertToNull");
                break;
            case "mssql":
                String instance = "";

                if (Utility.iniDatabaseInstance.length() > 0 ) {
                    instance = ";instance=" + Utility.iniDatabaseInstance;
                }                
                connString = "jdbc:jtds:sqlserver://10.0.1.16:" + Utility.iniDatabasePort + "/" + Utility.iniDatabaseName + instance;
                break;

        }
        return connString;
    }
    public static int databaseState;

  public static Connection conn;

    public static void setLock(String table, int id) {
        System.out.println("setLock:" + System.getProperty("user.name") + " - " + table + " - " + id);
        try {
            try (Statement stat = conn.createStatement()) {
                System.out.println("INSERT INTO DBLOCKS (" + Utility.dbQuotes + "user" + Utility.dbQuotes + ", " + Utility.dbQuotes + "table" + Utility.dbQuotes + ", id, " + Utility.dbQuotes + "time" + Utility.dbQuotes + ") VALUES ('" + Utility.user + "', '" + table + "', '" + id + "', " + Utility.dbNow + ")");
                stat.executeUpdate("INSERT INTO DBLOCKS (" + Utility.dbQuotes + "user" + Utility.dbQuotes + ", " + Utility.dbQuotes + "table" + Utility.dbQuotes + ", id, " + Utility.dbQuotes + "time" + Utility.dbQuotes + ") VALUES ('" + Utility.user + "', '" + table + "', '" + id + "', " + Utility.dbNow + ")");

            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static lock getLock(String table, int id) {
        lock rlock = new lock();
        rlock.isLocked = false;
        try {
            try (Statement stat = conn.createStatement()) {
                String query = "SELECT " + Utility.dbQuotes + "user" + Utility.dbQuotes + "," + Utility.dbQuotes + "time" + Utility.dbQuotes + " from DBLOCKS WHERE id=" + id + " AND " + Utility.dbQuotes + "table" + Utility.dbQuotes + "='" + table + "'";
                ResultSet rs = stat.executeQuery(query);
                if (rs.next()) {
                    rlock.user = rs.getString(1);
                    rlock.datetime = rs.getString(2);
                    rlock.isLocked = true;
                    rlock.table = table;
                    rlock.id = id;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rlock;
    }
    
    public static int countLocks(){
        //returns true if locks present, false if there are noelocks and null if an exception has occured
        int locks = 0;
        try {
            try (Statement stat = conn.createStatement()) {
                String query = "SELECT COUNT(*) FROM DBLOCKS";
                ResultSet rs = stat.executeQuery(query);
                if (rs.next()) {                    
                   locks = rs.getInt(1);                    
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            locks = 0;
        }
        return locks;        
    }

    public static void delLock(String table, int id) {
        System.out.println("delLock:" + System.getProperty("user.name") + " - " + table + " - " + id);
        try {
            try (Statement stat = conn.createStatement()) {
                String query = "DELETE FROM DBLOCKS WHERE " + Utility.dbQuotes + "user" + Utility.dbQuotes + "='" + System.getProperty("user.name") + "' AND " + Utility.dbQuotes + "table" + Utility.dbQuotes + "='" + table + "' AND id='" + id + "'";
                System.out.println(stat.executeUpdate(query) + " deleted");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static int getNextId(String table) {
        int id = 0;
        try {
            try (Statement stat = conn.createStatement()) {
                String query = "SELECT MAX(id) FROM " + table + "";
                try (ResultSet rs = stat.executeQuery(query)) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ++id;
    }

    public static class lock {

        public String datetime;
        public String user;
        public String table;
        public int id;
        public Boolean isLocked;

        public lock() {
        }

        private lock(String datetime, String user, Boolean locked) {
        }
    }
}
