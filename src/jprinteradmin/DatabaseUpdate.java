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
                    case "0.5":
                        statements.add("CREATE TABLE CONS_PRODUCTS (product_code VARCHAR(10) NOT NULL, product_description VARCHAR(60), supplier_code VARCHAR(10), supplier_product_code VARCHAR(30), purchase_price DOUBLE, sales_price DOUBLE, range DOUBLE, PRIMARY KEY (product_code))");
                        statements.add("CREATE TABLE CONS_SUPPLIER (supplier_code VARCHAR(10), address1 VARCHAR(30),address2 VARCHAR(30),street VARCHAR(30),zip VARCHAR(8),city VARCHAR(30),country VARCHAR(30),PRIMARY KEY (supplier_code))");
                        statements.add("CREATE TABLE CONS_PRODUCTS_DEVICETYPES(product_code VARCHAR(10), device_type_id INT,PRIMARY KEY (product_code, device_type_id))");
                        statements.add("CREATE TABLE CONS_WAREHOUSES(id INT, description VARCHAR(30), address1 VARCHAR(30),address2 VARCHAR(30),street VARCHAR(30),zip VARCHAR(8),city VARCHAR(30),country VARCHAR(30),PRIMARY KEY (id))");
                        dbversionDB = "0.6";
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
