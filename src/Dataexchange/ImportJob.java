/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dataexchange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jprinteradmin.Database;
import jprinteradmin.Utility;

/**
 *
 * @author stefan
 */


public final class ImportJob implements Runnable {
    
    public String fileName;



    ImportJob() {
        this.fileName = jprinteradmin.JPrinterAdmin.mw.im.jFileChooser1.getSelectedFile().getAbsolutePath();
        jprinteradmin.JPrinterAdmin.mw.im.setEnabled(false);
        executeImport(true);
        jprinteradmin.JPrinterAdmin.mw.im.setEnabled(true);
    }
    
    public Boolean executeImport(Boolean gui) {
        Boolean versionsequal = true;
        try {
           Statement stat = Database.conn.createStatement();
           File file = new File(this.fileName);
           BufferedReader in = new BufferedReader(
		   new InputStreamReader(
                      new FileInputStream(file), "UTF8"));
           String zeile;
           int i = 0;
           
           while ((zeile = in.readLine()) != null) {
               i++;
               String statement =  Utility.escapeSqlString4import (zeile.replace("<delimiter>", Utility.dbQuotes));
               if (i == 1) {
                   String dbversion = zeile.split(":")[1];
                   if (gui) {
                       if (!Utility.dbVersion.equals(dbversion)){
                           versionsequal = false;
                           jprinteradmin.JPrinterAdmin.mw.im.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATABASE-VERSIONS ARE NOT EQUAL"));
                       }
                      jprinteradmin.JPrinterAdmin.mw.im.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATABASE VERSION OF FILE") + ":" + dbversion);
                   }
               } else {
                   if (versionsequal){
                       stat.execute(statement);
                       if (gui) {
                           jprinteradmin.JPrinterAdmin.mw.im.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATASET") + ": " + i);
                       }
                        
                   }
               }
                 
            }           
            if (gui && versionsequal) {
                jprinteradmin.JPrinterAdmin.mw.im.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("SUCCESSFULLY IMPORTED"));
            }           

        } catch (IOException ex) {
            Logger.getLogger(ImportJob.class.getName()).log(Level.SEVERE, null, ex);
            if (gui) {
                jprinteradmin.JPrinterAdmin.mw.im.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("AN ERROR OCCURRED") + " " + ex.getLocalizedMessage());
            }             
        } catch (SQLException ex) {
            Logger.getLogger(ImportJob.class.getName()).log(Level.SEVERE, null, ex);
        }      
        return true;
    }

    @Override
    public void run() {
        this.fileName = jprinteradmin.JPrinterAdmin.mw.im.jFileChooser1.getSelectedFile().getAbsolutePath();
        jprinteradmin.JPrinterAdmin.mw.im.setEnabled(false);
        executeImport(true);
        jprinteradmin.JPrinterAdmin.mw.im.setEnabled(true);
    }    

    private void main(boolean gui) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
