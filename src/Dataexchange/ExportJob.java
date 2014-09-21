package Dataexchange;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jprinteradmin.Database;
import jprinteradmin.Utility;

/**
 *
 * @author stefan
 */


public class ExportJob implements Runnable {
    
    public String exportName;
    public String folder;
    public int keepExports;
    public int id = -1;
    public String message = "";
    
    public Boolean executeBackup(Boolean gui) {
        if (!gui && Database.countLocks() > 1) {
            this.message = java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("THERE IS AT LEAST ONE LOCK");
            return false;
        } else if (gui && Database.countLocks() > 1) {
            jprinteradmin.JPrinterAdmin.mw.ex.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("AN ERROR OCCURRED") + " " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("THERE IS AT LEAST ONE LOCK"));
        } else {
            try {
                Database.lock();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
                Date date = new Date();
                String sdate = (dateFormat.format(date));            
                DatabaseMetaData metaData = Database.conn.getMetaData();
                String[] types = {"TABLE"};
                ResultSet rsTables = metaData.getTables(null, null, null, types);
                String fileUrl = folder + File.separator + exportName + "_" + sdate + ".jpabackup";
                File file = new File(fileUrl);
                try (FileWriter writer = new FileWriter(file,true)) {
                    writer.write("db-version:" + Utility.dbVersion + System.getProperty("line.separator"));
                    while (rsTables.next()) {
                        
                        ResultSet rsTableMetaData = metaData.getColumns(null, null, rsTables.getString(3), null);
                        ArrayList<String> fieldsWUQ = new ArrayList<>();
                        ArrayList<String> fieldsWQ = new ArrayList<>();
                        ArrayList<String> datatypes = new ArrayList<>();
                        while (rsTableMetaData.next()){
                            fieldsWUQ.add("<delimiter>" + rsTableMetaData.getString(4) + "<delimiter>");
                            fieldsWQ.add(Utility.dbQuotes + rsTableMetaData.getString(4) + Utility.dbQuotes);
                            datatypes.add(rsTableMetaData.getString(6));
                        }
                        Statement stat = Database.conn.createStatement();
                        String statementData = "SELECT " + Utility.implode(fieldsWQ, ",") + " FROM " + Utility.dbQuotes + rsTables.getString(3) + Utility.dbQuotes;
                        String deleteStatement = "DELETE FROM <delimiter>" + rsTables.getString(3).toUpperCase() + "<delimiter>";
                        writer.write(deleteStatement + System.getProperty("line.separator"));
                        ResultSet rsTableData = stat.executeQuery(statementData);
                        int iDatasets=0;
                        while (rsTableData.next()) {
                            ArrayList<String> data = new ArrayList<>();
                            for (int i = 0 ; i < fieldsWUQ.size() ; i++ ) {
                                if (datatypes.get(i).equals("INT") || datatypes.get(i).equals("FLOAT")){
                                    data.add(Utility.escapeSqlString(rsTableData.getString(i + 1)));
                                } else if (rsTableData.getObject(i +1) == null) {
                                    data.add("NULL");
                                } else {
                                    data.add("'" + Utility.escapeSqlString4export(rsTableData.getString(i + 1)) + "'");
                                }
                            }
                            if (gui) {
                                jprinteradmin.JPrinterAdmin.mw.ex.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("EXPORTING TABLE") + " " + rsTables.getString(3) + " " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATASET") + " " + ++iDatasets);
                            }
                            String insStatement = "INSERT INTO <delimiter>" + rsTables.getString(3).toUpperCase() + "<delimiter> (" + Utility.implode(fieldsWUQ, ",") + ") "
                                    + " VALUES (" + Utility.implode(data, ",") + ")";
                            writer.write(insStatement + System.getProperty("line.separator"));
                            
                        }

                    }
                    if (gui) {
                        jprinteradmin.JPrinterAdmin.mw.ex.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("SUCCESSFULLY EXPORTED"));
                    }
                    
                    
                    writer.flush();
                    Statement stat = Database.conn.createStatement();
                    Statement stat2 = Database.conn.createStatement();
                    if ( this.id > -1 ) {
                        
                        String statementQueryOldFiles = "SELECT file_url, id FROM EXPORT_JOB_FILES WHERE export_job_id = " + this.id + " ORDER BY id desc";
                        System.out.println(statementQueryOldFiles);
                        ResultSet rs = stat.executeQuery(statementQueryOldFiles);
                        if ( this.keepExports > -1 ) {
                            int i = 0;
                            
                            while (rs.next()) {
                                i++;                              
                                if (i > this.keepExports ) {
                                    File file2del = new File(rs.getString(1));
                                    if (file2del.exists()) {
                                        file2del.delete();
                                    }
                                    stat2.execute("DELETE FROM EXPORT_JOB_FILES WHERE id = " + rs.getString(2));
                                    System.out.println("File " + rs.getString(1) + " deleted");
                                }    
                            }
                        }
                    
                        DateFormat dateFormatDB = new SimpleDateFormat("yyyy-MM.dd HH:mm:ss");
                        Date dateDB = new Date();
                        String sdateDB = (dateFormatDB.format(dateDB)) + ".0";                         
                        String writeLog = "INSERT INTO EXPORT_JOB_FILES (id, export_job_id, file_url, file_date) "
                                + "VALUES (" + Database.getNextId("EXPORT_JOB_FILES") + ", " + this.id + ", '" + fileUrl  + "', '" + sdateDB + "')";
                        
                        stat.execute(writeLog);
                        
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(ExportWindow.class.getName()).log(Level.SEVERE, null, ex);
                if (gui) {
                    jprinteradmin.JPrinterAdmin.mw.ex.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("AN ERROR OCCURRED") + " " + ex.getLocalizedMessage());
                    System.out.println(Arrays.toString(ex.getStackTrace()));
                } else {
                    message = ex.getLocalizedMessage();
                    System.out.println(Arrays.toString(ex.getStackTrace()));
                }             
            } catch (IOException ex) {
                Logger.getLogger(ExportJob.class.getName()).log(Level.SEVERE, null, ex);
                if (gui) {
                    jprinteradmin.JPrinterAdmin.mw.ex.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("AN ERROR OCCURRED") + " " + ex.getLocalizedMessage());
                    System.out.println(Arrays.toString(ex.getStackTrace()));
                } else {
                    message = ex.getLocalizedMessage();
                    System.out.println(Arrays.toString(ex.getStackTrace()));
                }           
            }   
            Database.unlock();
        }    
        return true;
    }
    
    @Override
    public void run() {
        jprinteradmin.JPrinterAdmin.mw.ex.setEnabled(false);
        this.folder = jprinteradmin.JPrinterAdmin.mw.ex.jTextFieldFolder.getText();
        this.exportName = jprinteradmin.JPrinterAdmin.mw.ex.jTextFieldJobName.getText();
        if ( jprinteradmin.JPrinterAdmin.mw.ex.jTextFieldId.getText().length() > 0 ) {
            this.id = Integer.valueOf(jprinteradmin.JPrinterAdmin.mw.ex.jTextFieldId.getText());
        }
        if ( jprinteradmin.JPrinterAdmin.mw.ex.jComboBox1.getSelectedIndex() == 7 ){
            this.keepExports = -1;
        } else {
            this.keepExports = jprinteradmin.JPrinterAdmin.mw.ex.jComboBox1.getSelectedIndex();
        }
        executeBackup(true);
        jprinteradmin.JPrinterAdmin.mw.ex.setEnabled(true);
    }    
    
    public void save() {
        if ( id == -1) {
            id = Database.getNextId("EXPORT_JOBS");        
            try {
                Statement stat = Database.conn.createStatement();
                String statement = "INSERT INTO EXPORT_JOBS (id, name, folder, keepLast) VALUES ( " + id + ""
                        + ", '" + this.exportName + "','" + this.folder + "'," + this.keepExports + ")";
                int z=1;
                stat.execute(statement);
                statement = "INSERT INTO " + Utility.dbQuotes +"JOBS" + Utility.dbQuotes +" "
                        + "(id, " + Utility.dbQuotes +"type" + Utility.dbQuotes +",x_id,name) "
                        + "VALUES (" + Database.getNextId("JOBS") + ", 2, " + id + ", '" + this.exportName + "')";
                stat.execute(statement);            
            } catch (SQLException ex) {
                Logger.getLogger(ExportJob.class.getName()).log(Level.SEVERE, null, ex);
            }       
        } else {
            try {
                Statement stat = Database.conn.createStatement();
                String statement = "UPDATE EXPORT_JOBS SET " + Utility.dbQuotes + "name" + Utility.dbQuotes + " = '" + this.exportName + "', folder = '" + this.folder + "', keepLast = " + this.keepExports + " WHERE id = " + id;
                stat.execute(statement);
                statement = "UPDATE " + Utility.dbQuotes +"JOBS" + Utility.dbQuotes + " "
                        + "SET " + Utility.dbQuotes +"name" + Utility.dbQuotes +" = '" + this.exportName + "' "
                        + "WHERE " + Utility.dbQuotes + "type" + Utility.dbQuotes + " = 2 AND x_id = " + id ;
                stat.execute(statement);   
                
            } catch (SQLException ex) {
                Logger.getLogger(ExportJob.class.getName()).log(Level.SEVERE, null, ex);
            }              
        }
    }
    
    public void load (int id) {
        try {
            this.id = id;
            Statement stat = Database.conn.createStatement();
            String statement = "SELECT name, folder, keepLast FROM EXPORT_JOBS WHERE id = " + id;
            ResultSet rs = stat.executeQuery(statement);        
            while (rs.next()) {
                this.exportName = rs.getString(1);
                this.folder = rs.getString(2);
                this.keepExports = rs.getInt(3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ExportJob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void delete (int id) {
        try {
            Statement stat = Database.conn.createStatement();
            String statement = "DELETE FROM EXPORT_JOBS WHERE id = " + id;
            stat.execute(statement);
            statement = "DELETE FROM EXPORT_JOB_FILES WHERE export_job_id = " +id;
            stat.execute(statement);
            statement = "DELETE FROM " + Utility.dbQuotes + "JOBS" + Utility.dbQuotes + " WHERE " + Utility.dbQuotes + "type" + Utility.dbQuotes + " = 2 AND x_id = " +id;
            stat.execute(statement);            
        } catch (SQLException ex) {
            Logger.getLogger(ExportJob.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }    
}
