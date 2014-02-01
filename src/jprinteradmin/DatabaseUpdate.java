/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jprinteradmin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stefan
 */
public class DatabaseUpdate {
    String dbversionApp = Utility.dbVersion;
    String dbversionDB = null;

    Statement stat;

    public DatabaseUpdate() {
        try {
            this.stat = Database.conn.createStatement();
            String statementGetDBv = "SELECT " + Utility.dbQuotes + "value" + Utility.dbQuotes + " from " + Utility.dbQuotes + "BASE" + Utility.dbQuotes + " WHERE " + Utility.dbQuotes + "name" + Utility.dbQuotes + " = 'dbversion'";
            ResultSet rs = stat.executeQuery(statementGetDBv);
            while (rs.next()) {
                dbversionDB = rs.getString(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUpdate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void updateDatabase(Boolean gui){
        try {
            this.stat = Database.conn.createStatement();
            while (!dbversionApp.equals(dbversionDB)){
                List<String> statements = new ArrayList();
                switch (dbversionDB) {
                    case "0.2":
                        statements.add("UPDATE " + Utility.dbQuotes + "BASE" + Utility.dbQuotes + " SET " + Utility.dbQuotes + "value" + Utility.dbQuotes + " = '0.5' WHERE " + Utility.dbQuotes + "name" + Utility.dbQuotes + " = 'dbversion'");
                        statements.add("UPDATE " + Utility.dbQuotes + "BASE" + Utility.dbQuotes + " SET " + Utility.dbQuotes + "value" + Utility.dbQuotes + " = '0.5' WHERE " + Utility.dbQuotes + "name" + Utility.dbQuotes + " = 'appversion'");
                        statements.add("CREATE TABLE EXPORT_JOBS (id int NOT NULL, " + Utility.dbQuotes + "name" + Utility.dbQuotes + " VARCHAR(40), folder VARCHAR(50), keepLast int null, PRIMARY KEY (id))");
                        statements.add("CREATE TABLE EXPORT_JOB_FILES (id int NOT NULL, export_job_id int, file_url VARCHAR(70), file_date DATETIME, PRIMARY KEY (id))");
                        dbversionDB = "0.5";
                        break;
                }
                for (int i = 0 ; i < statements.size() ; i++ ) {
                    Boolean result = stat.execute(statements.get(i));
                    if (gui){
                        jprinteradmin.MainWindow.duw.jTextAreaResult.setText(jprinteradmin.MainWindow.duw.jTextAreaResult.getText() + statements.get(i) + ": " + result.toString() + "\n");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUpdate.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }


    
}
