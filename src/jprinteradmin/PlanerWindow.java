/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jprinteradmin;

import Reports.PrinterReport;
import getPrinterData.GetPrinterValues;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stefan
 */
public class PlanerWindow {

    public void start() {
        Database.lock isLock = Database.getLock("SCHEDULE", 0); //NOI18N
        if (isLock.isLocked) {
            try {
                String startDate = null;
                Statement st = Database.conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT " + Utility.dbNow); //NOI18N
                while (rs.next()) {
                    startDate = rs.getString(1);
                }
                String result = java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATASET IS LOCKED. USER:") + isLock.user + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString(". DATE: ") + isLock.datetime + "."; //NOI18N
                String updateQuery = "INSERT INTO SCHEDULE_LOG (id, startDate, finishedDate, schedule_id, job_id, " + Utility.dbQuotes + "result" + Utility.dbQuotes + ") VALUES ('" + Database.getNextId("SCHEDULE_LOG") + "', '" + startDate + "', '" + startDate + "', -1, NULL, '" + result + "')";             //NOI18N
                st.execute(updateQuery);
            } catch (SQLException ex) {
                Logger.getLogger(PlanerWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Database.setLock("SCHEDULE", 0); //NOI18N
            List<Integer[]> validJobs = getValidJobs();
            runJobs(validJobs);
            Database.delLock("SCHEDULE", 0); //NOI18N
            try {
                Database.conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(PlanerWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void runJobs(List<Integer[]> validJobs) {
        for (int i = 0; i < validJobs.size(); i++) {
            try {
                Statement st = Database.conn.createStatement();
                Statement st2 = Database.conn.createStatement();
                String queryGetJob = "SELECT id, " + Utility.dbQuotes + "type" + Utility.dbQuotes + ", x_id, " + Utility.dbQuotes + "name" + Utility.dbQuotes + " FROM JOBS WHERE id = " + validJobs.get(i)[1]; //NOI18N
                ResultSet rs = st.executeQuery(queryGetJob);
                String startDate = null;
                String finishedDate = null;
                while (rs.next()) {
                    int type = rs.getInt(2);
                    switch (type) {
                        case 0:
                            //ReadPrinterData
                            GetPrinterValues gpv = new GetPrinterValues();
                            try {
                                ResultSet rs2 = st2.executeQuery("SELECT " + Utility.dbNow); //NOI18N
                                while (rs2.next()) {
                                    startDate = rs2.getString(1);
                                }
                                gpv.main(false);
                                String result = gpv.result;
                                System.out.println(result);
                                rs2 = st2.executeQuery("SELECT " + Utility.dbNow); //NOI18N
                                while (rs2.next()) {
                                    finishedDate = rs2.getString(1);
                                }
                                String updateQuery = "INSERT INTO SCHEDULE_LOG (id, startDate, finishedDate, schedule_id, job_id, " + Utility.dbQuotes + "result" + Utility.dbQuotes + ") VALUES ('" + Database.getNextId("SCHEDULE_LOG") + "', '" + startDate + "', '" + finishedDate + "', '" + validJobs.get(i)[0] + "', '" + validJobs.get(i)[1] + "', '" + result + "')"; //NOI18N
                                st2.execute(updateQuery);


                            } catch (IOException ex) {
                                Logger.getLogger(PlanerWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        case 1:
                            
                            ResultSet rs2 = st2.executeQuery("SELECT " + Utility.dbNow); //NOI18N
                            while (rs2.next()) {
                                startDate = rs2.getString(1);
                            }                            
                            PrinterReport pr = new PrinterReport();
                            pr.loadReport(rs.getString(4));
                            pr.createReportAsList();
                            String result = pr.executeReport().toString();
                            rs2 = st2.executeQuery("SELECT " + Utility.dbNow); //NOI18N
                            while (rs2.next()) {
                                finishedDate = rs2.getString(1);
                            }   
                            if ( pr.message.length() > 0 ) {
                                result = result + ": " + pr.message;
                            }
                            String updateQuery = "INSERT INTO SCHEDULE_LOG (id, startDate, finishedDate, schedule_id, job_id, " + Utility.dbQuotes + "result" + Utility.dbQuotes + ") VALUES ('" + Database.getNextId("SCHEDULE_LOG") + "', '" + startDate + "', '" + finishedDate + "', '" + validJobs.get(i)[0] + "', '" + validJobs.get(i)[1] + "', '" + result + "')"; //NOI18N
                            st2.execute(updateQuery);                            
                            break;
                        case 2:                           
                            ResultSet rs3 = st2.executeQuery("SELECT " + Utility.dbNow); //NOI18N
                            while (rs3.next()) {
                                startDate = rs3.getString(1);
                            }                            
                            Dataexchange.ExportJob ej = new Dataexchange.ExportJob();
                            ej.load(rs.getInt(3));
                            result = ej.executeBackup(false).toString();
                            if ( ej.message.length() > 0 ) {
                                result = result + ": " + ej.message;
                            }                            
                            rs3 = st2.executeQuery("SELECT " + Utility.dbNow); //NOI18N
                            while (rs3.next()) {
                                finishedDate = rs3.getString(1);
                            }                            
                            String updateQuery2 = "INSERT INTO SCHEDULE_LOG (id, startDate, finishedDate, schedule_id, job_id, " + Utility.dbQuotes + "result" + Utility.dbQuotes + ") VALUES ('" + Database.getNextId("SCHEDULE_LOG") + "', '" + startDate + "', '" + finishedDate + "', '" + validJobs.get(i)[0] + "', '" + validJobs.get(i)[1] + "', '" + result + "')"; //NOI18N
                            st2.execute(updateQuery2);                            
                            break;                            
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(PlanerWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //NOI18N
        Date date = new Date();
        return dateFormat.format(date);
    }

    private List<Integer[]> getValidJobs() {
        //returns all job_ids in order that have to be done today and not have been already done
        List<Integer[]> returnValue = new ArrayList<>();
        try {
            Statement st = Database.conn.createStatement();
            String stGetAllSchedules = "select " + Utility.dbQuotes + "type" + Utility.dbQuotes + ", values1,values2,SCHEDULE_JOBS.job_id,SCHEDULE_JOBS.schedule_id,startDate from "
                    + "SCHEDULE " + //NOI18N
                    "INNER JOIN SCHEDULE_JOBS ON SCHEDULE_JOBS.schedule_id=SCHEDULE.id " + //NOI18N
                    "LEFT JOIN SCHEDULE_LOG ON SCHEDULE_LOG.schedule_id=SCHEDULE_JOBS.schedule_id AND startDate >= '" + this.getDate() + "'" + //NOI18N
                    "WHERE startDate IS NULL " + //NOI18N
                    "ORDER BY SCHEDULE_JOBS.schedule_id, " + Utility.dbQuotes + "ORDER" + Utility.dbQuotes; //NOI18N //NOI18N //NOI18N
            ResultSet rs = st.executeQuery(stGetAllSchedules);
            Calendar c = Calendar.getInstance();
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            while (rs.next()) {
                String type = rs.getString(1);
                String[] values1 = rs.getString(2).split(","); //NOI18N
                String[] values2 = rs.getString(3).split(","); //NOI18N
                int jobId = rs.getInt(4);
                int scheduleId = rs.getInt(5);
                switch (type) {
                    case "0": //NOI18N
                        // weekly schedule
                        if (Arrays.asList(values1).contains(String.valueOf(dayOfWeek))) {
                            returnValue.add(new Integer[]{scheduleId, jobId});
                        }
                        break;
                    case "1": //NOI18N
                        // monthly schedule
                        if (Arrays.asList(values1).contains(String.valueOf(dayOfMonth))) {
                            returnValue.add(new Integer[]{scheduleId, jobId});
                        }
                        break;
                    case "2": //NOI18N
                        // yearly schedule
                        if (Arrays.asList(values2).contains(String.valueOf(dayOfMonth)) && Arrays.asList(values1).contains(String.valueOf(month))) {
                            returnValue.add(new Integer[]{scheduleId, jobId});
                        }
                        break;
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanerWindow.class.getName()).log(Level.SEVERE, null, ex);

        }

        return returnValue;

    }
}
