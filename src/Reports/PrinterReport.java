/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reports;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jprinteradmin.Database;
import jprinteradmin.Utility;
import java.util.UUID;
import javax.swing.table.TableModel;
import org.jopendocument.dom.spreadsheet.SheetTableModel.MutableTableModel;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 *
 * @author stefan
 */
public class PrinterReport {

    public final int outputAsCSV = 0;
    public final int outputAsODS = 1;
    public final int outputAsHTML = 2;
    public final int file = 0;
    public final int email = 1;
    public String name;
    public String heading;
    public int exportTo; // 0 - file ; 1 - email
    public int id;
    public int linking; // 0 and; 1 or
    public List<String[]> outputFields = new ArrayList();
    public List<String[]> counterValues = new ArrayList();
    public List<String[]> conditions = new ArrayList();
    public int order; //field id for ordering
    public int outputAs;  //0 csv ; 1 ods; 2 html
    public String destination; // filename or email-address
    public Boolean subTotal;
    public Boolean subAvg;
    public List<String[]> reportAsList = new ArrayList();
    public List<String> reportHeadings = new ArrayList();
    public List<String> reportKinds = new ArrayList();
    public List<Integer> reportLinesSubTotal = new ArrayList();
    public List<Integer> reportLinesTotal = new ArrayList();

    public int loadReport(String name) {
        //returns -1 if an error occured; 0 if no report was found with the fiven name; 1if if th report was loaded succesfully
        int result = -1;
        try {
            int repId = -1;
            Statement stat = Database.conn.createStatement();
            String stmGetRepId = "SELECT id FROM REPORT WHERE " + Utility.dbQuotes + "name" + Utility.dbQuotes + " = '" + Utility.escapeSqlString(name) + "'";
            ResultSet resGetRepId = stat.executeQuery(stmGetRepId);
            while (resGetRepId.next()) {
                repId = resGetRepId.getInt(1);
            }
            if (repId != -1) {
                /*stat.execute("DELETE FROM REPORT WHERE id =" + repId);
                 stat.execute("DELETE FROM REPORT_OUTPUT_FIELDS WHERE id =" + repId);
                 stat.execute("DELETE FROM REPORT_COUNTERVALUES WHERE id =" + repId);
                 stat.execute("DELETE FROM REPORT_CONDITIONS WHERE id =" + repId);*/
                this.outputFields.clear();
                this.counterValues.clear();
                this.conditions.clear();
                this.id = repId;
                String stmReport = "SELECT " + Utility.dbQuotes + "name" + Utility.dbQuotes + ", heading, sort_id, linking, " + Utility.dbQuotes + "destination" + Utility.dbQuotes + ", " + Utility.dbQuotes + "outputAs" + Utility.dbQuotes + ", subtotal, " + Utility.dbQuotes + "exportTo" + Utility.dbQuotes + " from REPORT where id=" + repId;
                ResultSet rs = stat.executeQuery(stmReport);
                while (rs.next()) {
                    this.name = rs.getString(1);
                    this.heading = rs.getString(2);
                    this.order = rs.getInt(3);
                    this.linking = rs.getInt(4);
                    this.outputAs = rs.getInt(6);
                    this.destination = rs.getString(5);
                    this.subTotal = rs.getInt(7) == 1;
                    this.exportTo = rs.getInt(8);

                }
                String stmOutputfields = "SELECT " + Utility.dbQuotes + "field_id" + Utility.dbQuotes + ", field_id, label FROM REPORT_OUTPUTFIELDS WHERE report_id = " + repId + " ORDER BY " + Utility.dbQuotes + "order" + Utility.dbQuotes;
                rs = stat.executeQuery(stmOutputfields);
                
                while (rs.next()) {
                    this.outputFields.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
                }        
                String stmCounterValues = "SELECT " + Utility.dbQuotes + "field_id" + Utility.dbQuotes + ", " + Utility.dbQuotes + "period" + Utility.dbQuotes + ", output, " + Utility.dbQuotes + "sum" + Utility.dbQuotes + ", " + Utility.dbQuotes + "avg" + Utility.dbQuotes + ", " + Utility.dbQuotes + "label" + Utility.dbQuotes + " FROM REPORT_COUNTERVALUES WHERE report_id = " + repId + " ORDER BY " + Utility.dbQuotes + "order" + Utility.dbQuotes;
                rs = stat.executeQuery(stmCounterValues);
                
                while (rs.next()) {
                    this.counterValues.add(new String[]{rs.getString(1), "", rs.getString(2), rs.getString(3),rs.getString(4), rs.getString(5), rs.getString(6)});
                }   
                String stmConditions = "SELECT " + Utility.dbQuotes + "order" + Utility.dbQuotes + ", field_id, operator, " + Utility.dbQuotes + "value" + Utility.dbQuotes + " FROM REPORT_CONDITIONS WHERE report_id = " + repId + " ORDER BY " + Utility.dbQuotes + "order" + Utility.dbQuotes;
                rs = stat.executeQuery(stmConditions);
                while (rs.next()) {
                    this.conditions.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3),rs.getString(4)});
                }            
                result = 1;
            } else {
                result = 0;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int saveReport() {
        //returns 1 if new report saved; 2 if existing report was altered
        int result;
        int reportId;
        int resDelRep = this.deleteReport(name);
        if (resDelRep > -1) {
            result = 2;
            reportId = resDelRep;
        } else {
            result = 1;
            reportId = Database.getNextId("REPORT");
        }
        int subTotalInt = 0;
        if (subTotal == true) {
            subTotalInt = 1;
        }
        try {
            Statement stat = Database.conn.createStatement();
            String stmReport = "INSERT INTO REPORT (id, " + Utility.dbQuotes + "name" + Utility.dbQuotes + ", heading, sort_id, linking, " + Utility.dbQuotes + "outputAs" + Utility.dbQuotes + ", " + Utility.dbQuotes + "destination" + Utility.dbQuotes + ", subtotal, " + Utility.dbQuotes + "exportTo" + Utility.dbQuotes + ") "
                    + "VALUES (" + reportId + ", '" + Utility.escapeSqlString(name) + "', '" + Utility.escapeSqlString(heading) + "', " + order + ", " + linking + ", " + outputAs + " , '" + Utility.escapeSqlString(destination) + "', " + subTotalInt + ", " + this.exportTo + " )";
            stat.execute(stmReport);
            for (int i = 0; i < outputFields.size(); i++) {
                String stmOutputfields = "INSERT INTO REPORT_OUTPUTFIELDS (report_id, " + Utility.dbQuotes + "order" + Utility.dbQuotes + ", field_id, label) "
                        + "VALUES (" + reportId + ", " + i + ", " + outputFields.get(i)[0] + ", '" + Utility.escapeSqlString(outputFields.get(i)[2]) + "')";
                stat.execute(stmOutputfields);
            }
            for (int i = 0; i < counterValues.size(); i++) {
                String stmCounterValues = "INSERT INTO REPORT_COUNTERVALUES (report_id, " + Utility.dbQuotes + "order" + Utility.dbQuotes + ", field_id, period, label, " + Utility.dbQuotes + "sum" + Utility.dbQuotes + ", " + Utility.dbQuotes + "avg" + Utility.dbQuotes + ", " + Utility.dbQuotes + "output" + Utility.dbQuotes + ") "
                        + "VALUES (" + reportId + ", " + i + ", " + counterValues.get(i)[0] + ", " + counterValues.get(i)[2].substring(0, 1) + ", '" + Utility.escapeSqlString(counterValues.get(i)[6]) + "', " + counterValues.get(i)[4] + ", " + counterValues.get(i)[5] + ", " + counterValues.get(i)[3].substring(0,1) + ")";
                stat.execute(stmCounterValues);
            }
            for (int i = 0; i < conditions.size(); i++) {
                String stmConditions = "INSERT INTO REPORT_CONDITIONS (report_id, " + Utility.dbQuotes + "order" + Utility.dbQuotes + ", field_id, operator, " + Utility.dbQuotes + "value" + Utility.dbQuotes + ") "
                        + "VALUES (" + reportId + ", " + i + ", " + conditions.get(i)[0] + ",  " + conditions.get(i)[2].substring(0, 1) + " ,'" + Utility.escapeSqlString(conditions.get(i)[3]) + "')";
                stat.execute(stmConditions);
            }
            if (jobs_id == -1){
                jobs_id = Database.getNextId("JOBS");
            }
            String stmJobs = "INSERT INTO " + Utility.dbQuotes + "JOBS" + Utility.dbQuotes + " (id, " + Utility.dbQuotes + "type" + Utility.dbQuotes + ", x_id, " + Utility.dbQuotes + "name" + Utility.dbQuotes + ") " +
                " VALUES (" + jobs_id + ", 1, " + reportId + ", '" + Utility.escapeSqlString(this.name) + "')";
            stat.execute(stmJobs);
        } catch (SQLException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("hallo");
        return result;
    }

    int jobs_id = -1;
    
    public int deleteReport(String name) {
        // returns the id of the deleted report; -1 if there is nothing to delete
        int result = -1;
        try {
            int repId = -1;
            Statement stat = Database.conn.createStatement();
            String stmGetRepId = "SELECT id FROM REPORT WHERE " + Utility.dbQuotes + "name" + Utility.dbQuotes + " = '" + Utility.escapeSqlString(name) + "'";
            ResultSet resGetRepId = stat.executeQuery(stmGetRepId);
            while (resGetRepId.next()) {
                repId = resGetRepId.getInt(1);
            }
            if (repId != -1) {
                stat.execute("DELETE FROM REPORT WHERE id =" + repId);
                stat.execute("DELETE FROM REPORT_OUTPUTFIELDS WHERE report_id =" + repId);
                stat.execute("DELETE FROM REPORT_COUNTERVALUES WHERE report_id =" + repId);
                stat.execute("DELETE FROM REPORT_CONDITIONS WHERE report_id =" + repId);
                ResultSet rs2 = stat.executeQuery("SELECT " + Utility.dbQuotes + "id" + Utility.dbQuotes + " FROM " + Utility.dbQuotes + "JOBS" + Utility.dbQuotes + " WHERE " + Utility.dbQuotes + "type" + Utility.dbQuotes + " = 1 AND x_id =" + repId);
                while (rs2.next()){
                    jobs_id = rs2.getInt(1);
                }
                stat.execute("DELETE FROM " + Utility.dbQuotes + "JOBS" + Utility.dbQuotes + " WHERE " + Utility.dbQuotes + "type" + Utility.dbQuotes + " = 1 AND x_id =" + repId);
                result = repId;
            } else {
                result = -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public void createReportAsList(){
        try {
            
            Statement st = Database.conn.createStatement();
            String mainQuery = "SELECT DISTINCT PRINTER.id, firstSeen, lastSeen, b.request_value FROM PRINTER"
                        + " INNER JOIN PRINTER_VALUES ON PRINTER_VALUES.printer_id = PRINTER.id"
                        + " INNER JOIN REQUEST_VALUES ON REQUEST_VALUES.id = PRINTER_VALUES.request_value_id " //NOI18N
                        + " LEFT JOIN PRINTER_VALUES as b on (b.printer_id = PRINTER.id and b.request_value_id=" + this.order + ")";            
            if (this.conditions.size() > 0) {
                mainQuery += " WHERE ";
                String linkingS = "OR";
                if (linking == 0){
                    linkingS = "AND";
                }
                
                for (int i = 0 ; i < this.conditions.size() ; i++){
                    if (i > 0) {
                        mainQuery += linkingS;
                    }
                    switch (this.conditions.get(i)[2].substring(0, 1)){
                        case "0": //equal
                            mainQuery += "(PRINTER_VALUES.request_value = '" + Utility.escapeSqlString(this.conditions.get(i)[3]) + "'";
                            break;
                        case "1": //not equal
                            mainQuery += "(PRINTER_VALUES.request_value <> '" + Utility.escapeSqlString(this.conditions.get(i)[3]) + "'";
                            break;                            
                        case "2": //greate than
                            mainQuery += "(PRINTER_VALUES.request_value >= '" + Utility.escapeSqlString(this.conditions.get(i)[3]) + "'";
                            break;
                        case "3": //lower than
                            mainQuery += "(PRINTER_VALUES.request_value <= '" + Utility.escapeSqlString(this.conditions.get(i)[3]) + "'";
                            break;
                        case "4": //starts with
                            mainQuery += "(PRINTER_VALUES.request_value like '" + Utility.escapeSqlString(this.conditions.get(i)[3]) + "%'";
                            break;                            
                        case "5":  //ends with
                            mainQuery += "(PRINTER_VALUES.request_value like '%" + Utility.escapeSqlString(this.conditions.get(i)[3]) + "'";
                            break;                            
                    }
                    mainQuery += " AND " + Utility.dbQuotes + "PRINTER_VALUES" + Utility.dbQuotes + "." + Utility.dbQuotes + "request_value_id" + Utility.dbQuotes + " = " + this.conditions.get(i)[0] + ")"; //NOI18N
                }
                
            }
            mainQuery += " ORDER BY b.request_value";
            ResultSet rs = st.executeQuery(mainQuery); //NOI18N
            ArrayList<String> list2 = new ArrayList<>();
            int i2 = 0;
            Boolean[] alreadySet = new Boolean[this.counterValues.size()];
            Double[] subTotals = new Double[this.counterValues.size()];
            Double[] totals  = new Double[this.counterValues.size()];
            Integer[] totalsCount = new Integer[this.counterValues.size()];
            String orderValue = null;
            Boolean firstRunThrough = false;
            while (rs.next()) {
                if (firstRunThrough && (orderValue == null ? rs.getString(4) != null : !orderValue.equals(rs.getString(4))) && this.subTotal){
                    list2.add(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("SUM") + " " + orderValue);
                    for (int i=1 ; i < this.outputFields.size() ; i++){
                        list2.add("");
                    }
                    for (int i=0 ; i < this.counterValues.size() ; i++){
                        if (subTotals[i] == null){
                            list2.add("0");
                        } else {
                            list2.add(String.valueOf(Double.valueOf(Math.round(subTotals[i] * 100) / 100.0)));
                        }
                        
                        subTotals[i] = 0.0;
                    }
                    int size = list2.size();
                    String[] ins = new String[size];
                    for (int i = 0; i < size; i++) {
                        ins[i] = list2.get(i);
                    }
                    this.reportAsList.add(ins);     
                    this.reportLinesSubTotal.add(i2);
                    i2++;
                    list2.clear();

                }
                orderValue = rs.getString(4);
                firstRunThrough = true;
                for (int i = 0 ; i < this.outputFields.size() ; i++){
                    String query = "SELECT DISTINCT REQUEST_VALUES.name, request_value, REQUEST_METHOD_VALUES.kind,"
                            + " REQUEST_METHOD_VALUES.request_method_values_x_id, REQUEST_VALUES.type FROM "
                            + "REQUEST_VALUES LEFT JOIN "
                            + "PRINTER_VALUES ON ((PRINTER_VALUES.request_value_id = REQUEST_VALUES.id OR PRINTER_VALUES.request_value_id IS NULL) AND PRINTER_VALUES.printer_id=" + rs.getInt(1) + ")"
                            + "LEFT JOIN  REQUEST_METHOD_VALUES ON REQUEST_METHOD_VALUES.request_values_id = REQUEST_VALUES.id AND REQUEST_METHOD_VALUES.kind='internal'"
                            + " WHERE PRINTER_VALUES.request_value_id = " + this.outputFields.get(i)[0] ;

                    Statement st2 = Database.conn.createStatement();
                    ResultSet rs2 = st2.executeQuery(query);

                    while (rs2.next()) {
                        if (i2 == 0) {
                            if (this.outputFields.get(i)[2].length() > 0){
                                this.reportHeadings.add(this.outputFields.get(i)[2]);
                            } else {
                                this.reportHeadings.add(rs2.getString(1));
                            }
                            
                            reportKinds.add(rs2.getString(5));
                        }
                        if ("internal".equals(rs2.getString(3)) && rs2.getInt(4) == 2) {
                            list2.add(rs.getString(2));
                        } else if ("internal".equals(rs2.getString(3)) && rs2.getInt(4) == 3) {
                            list2.add(rs.getString(3));
                        } else if (rs2.getString(2) != null) {
                            list2.add(rs2.getString(2));
                        } else {
                            list2.add("");
                        }
                    }
                }
                int iTotals = 0;
                for (int i = 0 ; i < this.counterValues.size() ; i++){
                    Calendar cal1 = Calendar.getInstance(); 
                    Calendar cal2 = Calendar.getInstance();
                    int year;
                    switch(counterValues.get(i)[2].substring(0,1)){
                        case "0":
                            cal2.add(Calendar.MONTH, -1);
                            break;
                        case "1":
                            cal2.add(Calendar.MONTH, -3);
                            break;
                        case "2":
                            year = cal1.get(Calendar.YEAR);
                            cal2.set(Calendar.YEAR, year);
                            cal2.set(Calendar.MONTH, 0);
                            cal2.set(Calendar.DAY_OF_MONTH, 0);   
                            cal1.set(Calendar.YEAR, year);
                            cal1.set(Calendar.MONTH, 11);
                            cal1.set(Calendar.DAY_OF_MONTH, 31);                             
                            break;                            
                        case "3":
                            cal1.add(Calendar.YEAR, -1);
                            cal1.add(Calendar.MONTH, -11);
                            break;
                        case "4":
                            cal1.add(Calendar.YEAR, -1);
                            cal1.add(Calendar.MONTH, -9);
                            break; 
                        case "5":
                            cal2.add(Calendar.YEAR, -1);
                            year = cal2.get(Calendar.YEAR);
                            cal2.set(Calendar.YEAR, year);
                            cal2.set(Calendar.DAY_OF_YEAR, 1);
  
                            cal1.set(Calendar.YEAR, year);
                            cal1.set(Calendar.MONTH, 11);
                            cal1.set(Calendar.DAY_OF_MONTH, 31);                                
                            break;                                                        
                    }
                    SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");

                    String date1 = dateF.format(cal1.getTime());
                    String date2 = dateF.format(cal2.getTime());
                    String query1 = "SELECT DISTINCT REQUEST_VALUES.name, requestvalue, REQUEST_METHOD_VALUES.kind,"
                            + " REQUEST_METHOD_VALUES.request_method_values_x_id, REQUEST_VALUES.type, " + Utility.dbQuotes + "time" + Utility.dbQuotes + " FROM "
                            + "REQUEST_VALUES LEFT JOIN "
                            + "PRINTER_COUNTER_VALUES ON ((PRINTER_COUNTER_VALUES.request_value_id = REQUEST_VALUES.id OR PRINTER_COUNTER_VALUES.request_value_id IS NULL) AND PRINTER_COUNTER_VALUES.printer_id=" + rs.getInt(1) + ")"
                            + "LEFT JOIN  REQUEST_METHOD_VALUES ON REQUEST_METHOD_VALUES.request_values_id = REQUEST_VALUES.id AND REQUEST_METHOD_VALUES.kind='internal'"
                            + " WHERE PRINTER_COUNTER_VALUES.request_value_id = " + this.counterValues.get(i)[0] 
                            + " AND PRINTER_COUNTER_VALUES.time = (SELECT MIN(" + Utility.dbQuotes + "time" + Utility.dbQuotes + ") FROM PRINTER_COUNTER_VALUES as b WHERE b.request_value_id=" + this.counterValues.get(i)[0] +" AND b.printer_id=" + rs.getInt(1)
                            + " AND b.time >= '" + date2 + "')";
                    String query2 = "SELECT DISTINCT REQUEST_VALUES.name, requestvalue, REQUEST_METHOD_VALUES.kind,"
                            + " REQUEST_METHOD_VALUES.request_method_values_x_id, REQUEST_VALUES.type, " + Utility.dbQuotes + "time" + Utility.dbQuotes + " FROM "
                            + "REQUEST_VALUES LEFT JOIN "
                            + "PRINTER_COUNTER_VALUES ON ((PRINTER_COUNTER_VALUES.request_value_id = REQUEST_VALUES.id OR PRINTER_COUNTER_VALUES.request_value_id IS NULL) AND PRINTER_COUNTER_VALUES.printer_id=" + rs.getInt(1) + ")"
                            + "LEFT JOIN  REQUEST_METHOD_VALUES ON REQUEST_METHOD_VALUES.request_values_id = REQUEST_VALUES.id AND REQUEST_METHOD_VALUES.kind='internal'"
                            + " WHERE PRINTER_COUNTER_VALUES.request_value_id = " + this.counterValues.get(i)[0] 
                            + " AND PRINTER_COUNTER_VALUES.time = (SELECT MAX(" + Utility.dbQuotes + "time" + Utility.dbQuotes + ") FROM PRINTER_COUNTER_VALUES as b WHERE b.request_value_id=" + this.counterValues.get(i)[0] +" AND b.printer_id=" + rs.getInt(1)
                            + " AND b.time <= '" + date1 + "')";                    
                    Statement st2 = Database.conn.createStatement();
                    ResultSet rs2 = st2.executeQuery(query1);

                    Integer firstValue = null;
                    Integer lastValue = null;
                    String firstDate = null;
                    String lastDate = null;
                    
                    while (rs2.next()) {
                        if (alreadySet[i] == null) {
                            if (this.counterValues.get(i)[6].length() > 0){
                                this.reportHeadings.add(this.counterValues.get(i)[6]);
                            } else {
                                if("0".equals(this.counterValues.get(i)[3].substring(0,1))){
                                    this.reportHeadings.add(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("SUM") + " " + rs2.getString(1));
                                } else {
                                    this.reportHeadings.add(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("AVERAGE") + " " + rs2.getString(1));
                                }
                            }
                            this.reportKinds.add("Integer");
                            alreadySet[i] = true;
                        } 
                        
                        firstValue = rs2.getInt(2);
                        firstDate = rs2.getString(6);                        
                    } 
                    ResultSet rs3 = st2.executeQuery(query2);
                    while (rs3.next()) {
                        lastValue = rs3.getInt(2);
                        lastDate = rs3.getString(6);
                    }  
                    
                    switch (this.counterValues.get(i)[3].substring(0,1)) {
                        
                        case "0":
                            if (lastValue != null && firstValue != null){
                                list2.add(String.valueOf(lastValue - firstValue));
                               if (subTotals[iTotals] == null){
                                    subTotals[iTotals] = Double.valueOf(lastValue - firstValue);
                                } else {
                                    subTotals[iTotals] +=Double.valueOf(lastValue - firstValue);
                                }
                                if (totals[iTotals] == null){
                                    totals[iTotals] = Double.valueOf(lastValue - firstValue);
                                } else {
                                    totals[iTotals] += Double.valueOf(lastValue - firstValue);
                                }    
                                if (totalsCount[iTotals] == null){
                                    totalsCount[iTotals] = 1;
                                } else {
                                    totalsCount[iTotals]++;
                                }
                            } else {
                                list2.add("");
                                if (totalsCount[iTotals] == null){
                                    totalsCount[iTotals] = 1;
                                } else {
                                    totalsCount[iTotals]++;
                                }
                            }
                            break;
                        case "1":
                            java.util.Date date = null;
                            java.util.Date preDate = null;
                            try {
                                if (lastDate != null && firstDate != null){
                                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastDate);
                                    preDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(firstDate);
                                }
                            } catch (ParseException ex) {
                                //Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
                                System.out.println(ex.getStackTrace());
                            }

                            Long between = null;
                            Double betweenInDays = null;
                            Double averagePerDay = null;
                            Double averagePerDayRounded = null;
                            int betweenCounter = 0;   
                            if (firstValue != null && lastValue != null) {
                                between = (preDate.getTime() - date.getTime());
                                betweenCounter = firstValue - lastValue;    
                            }
                            if (between != null) {
                                betweenInDays = Double.valueOf(between) / 86400000;
                                averagePerDay = betweenCounter / betweenInDays;
                                averagePerDayRounded = Double.valueOf(Math.round(averagePerDay * 100) / 100.0);
                                list2.add(String.valueOf(averagePerDayRounded));
                                if (subTotals[iTotals] == null){
                                    subTotals[iTotals] = averagePerDayRounded;
                                } else {
                                    subTotals[iTotals] += averagePerDayRounded;
                                }
                                if (totals[iTotals] == null){
                                    totals[iTotals] = averagePerDayRounded;
                                } else {
                                    totals[iTotals] += averagePerDayRounded;
                                }
                                if (totalsCount[iTotals] == null){
                                    totalsCount[iTotals] = 1;
                                } else {
                                    totalsCount[iTotals]++;
                                }
                            } else {
                                list2.add("");
                                if (totalsCount[iTotals] == null){
                                    totalsCount[iTotals] = 1;
                                } else {
                                    totalsCount[iTotals]++;
                                }
                            }                           
                            break;
                    }
                    iTotals++;
                }    
             
                int size = list2.size();
                String[] ins = new String[size];
                for (int i = 0; i < size; i++) {
                    ins[i] = list2.get(i);
                }
                this.reportAsList.add(ins);                    
                list2.clear();

                i2++;
            }
            if (firstRunThrough && this.subTotal){
               list2.add(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("SUM") + " " + orderValue);
               for (int i=1 ; i < this.outputFields.size() ; i++){
                   list2.add("");
               }
               for (int i=0 ; i < this.counterValues.size() ; i++){
                   list2.add(String.valueOf(Double.valueOf(Math.round(subTotals[i] * 100) / 100.0)));
                   subTotals[i] = 0.0;
               }
               int size = list2.size();
               String[] ins = new String[size];
               for (int i = 0; i < size; i++) {
                   ins[i] = list2.get(i);
               }
               this.reportAsList.add(ins);    
               this.reportLinesSubTotal.add(i2);
               list2.clear();
               
            }  
            list2.add(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("AVERAGE"));
            for (int i=1 ; i < this.outputFields.size() ; i++){
                list2.add("");
            }        
            Boolean endAvgExists = false;
            for ( int i = 0 ; i < this.counterValues.size() ; i++ ){
                int endAvg = 0;
                if ("1".equals(this.counterValues.get(i)[5])){
                    endAvg = 1;
                }
                if (endAvg == 1){
                   endAvgExists = true;
                   list2.add(String.valueOf(Double.valueOf(Math.round(totals[i] / totalsCount[i] * 100) / 100.0)));
                } else {
                   list2.add("");
                }
            } 
            if (endAvgExists) {
                i2++;
                int size = list2.size();
                String[] ins = new String[size];
                for (int i = 0; i < size; i++) {
                    ins[i] = list2.get(i);
                }             
                this.reportLinesTotal.add(i2);
                this.reportAsList.add(ins);
            }
            list2.clear();
            list2.add(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("SUM"));
            for (int i=1 ; i < this.outputFields.size() ; i++){
                list2.add("");
            }        
            Boolean endSumExists = false;
            for ( int i = 0 ; i < this.counterValues.size() ; i++ ){
                int endSum = 0;
                if ("1".equals(this.counterValues.get(i)[4])){
                    endSum = 1;
                }
                if (endSum == 1){
                   endSumExists = true;
                   list2.add(String.valueOf(Double.valueOf(Math.round(totals[i]  * 100) / 100.0)));
                } else {
                   list2.add("");
                }
            } 
            if (endSumExists) {
                i2++;
                int size = list2.size();
                String[] ins = new String[size];
                for (int i = 0; i < size; i++) {
                    ins[i] = list2.get(i);
                }     
                this.reportLinesTotal.add(i2);
                this.reportAsList.add(ins);
            }         
        } catch (SQLException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }        
    }

    public Boolean executeReport(){
        System.out.println("tempdir: " + System.getProperty("java.io.tmpdir"));
        String filename = null;
        String ext = null;
        Boolean returnValue = true;

        switch (this.outputAs){
            case outputAsCSV:
                filename = getReportAsCSV();
                ext = ".csv";
                break;   
            case outputAsHTML:
                filename = getReportAsHTML();
                ext = ".html";
                break;  
            case outputAsODS:
                filename = getReportAsODS();
                ext = ".ods";
                break;
                
        }
        if (filename == null){
            return false;
        }
        switch (this.exportTo){
            case file:
                try {
                    Utility.copyFile(filename, this.destination);
                    File file1 = new File(filename);
                    file1.delete();
                    
                } catch (IOException ex) {
                    Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
                    returnValue = false;
                }
                
                break;
            case email:
                returnValue = this.sendMail(filename, this.name + ext);
                File file1 = new File(filename);
                file1.delete();                
                break;
        }
        return returnValue;      
        
    }
    
    public String getReportAsCSV(){
        //returns tempFilename
        String filename = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + ".csv";
        File file = new File(filename);
        try {
            FileWriter writer = new FileWriter(file, true);
            for ( int i = 0 ; i < this.reportHeadings.size() ; i++ ){
                writer.write("\"" + this.reportHeadings.get(i) + "\"");
                if (i < ( this.reportHeadings.size() -1 )) {
                    writer.write(",");
                }
            }
            writer.write(System.lineSeparator());
            for ( int i = 0 ; i < this.reportAsList.size() ; i++ ){
                for ( int ii = 0 ; ii < this.reportAsList.get(i).length ; ii++ ) {
                    if (this.reportKinds.get(ii).equals("String")){
                        writer.write("\"" + this.reportAsList.get(i)[ii] + "\"");
                    } else {
                        writer.write(this.reportAsList.get(i)[ii]);
                    }
                    if (ii < ( this.reportAsList.get(i).length -1 )) {
                        writer.write(",");
                    }                    
                }
                if (i < ( this.reportAsList.size() -1 )) {
                    writer.write(System.lineSeparator());
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return filename;
    }
    
    public String getReportAsHTML(){
        //returns tempFilename
        String filename = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + ".html";
        File file = new File(filename);
        try {
            try (FileWriter writer = new FileWriter(file, true)) {
                Calendar cal  = Calendar.getInstance();
                Date     time = cal.getTime();
                writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html14/loose.dtd\">"+ System.lineSeparator());

                writer.write("<HTML>" + System.lineSeparator() + "<HEAD>" + System.lineSeparator());
                writer.write("<META HTTP-EQUIV=\"content-type\" CONTENT=\"text/html; charset=utf-8\">"+ System.lineSeparator());
                writer.write("</HEAD>" + System.lineSeparator() + "<BODY>" + System.lineSeparator());
                writer.write("<H1>" + this.heading + "</H1><br><P ALIGN=\"RIGHT\" FONT-SIZE=\"SMALL\">" + this.name + " " + time.toLocaleString() + "</p>"+ System.lineSeparator());
                writer.write("<TABLE BORDER=\"0\" CELLSPACING=\"2\">" + System.lineSeparator() + "<TR>" + System.lineSeparator());
                for ( int i = 0 ; i < this.reportHeadings.size() ; i++ ){
                    writer.write("<TH BGCOLOR=\"#FFFFCC\">" + this.reportHeadings.get(i) + "</TH>" + System.lineSeparator());
                }
                writer.write("</TR>" + System.lineSeparator());
                for ( int i = 0 ; i < this.reportAsList.size() ; i++ ){
                    writer.write("<TR>" + System.lineSeparator());
                    for ( int ii = 0 ; ii < this.reportAsList.get(i).length ; ii++ ) {
                        if (this.reportKinds.get(ii).equals("String")){
                            if (this.reportLinesSubTotal.contains(i)){
                                writer.write("<TD BGCOLOR=\"#C0C0C0\"><STRONG>" + this.reportAsList.get(i)[ii] + "</STRONG></TD>" + System.lineSeparator());
                            } else if (this.reportLinesTotal.contains(i)){
                                writer.write("<TD BGCOLOR=\"#00FF00\"><STRONG>" + this.reportAsList.get(i)[ii] + "</STRONG></TD>" + System.lineSeparator());
                            } else {
                                writer.write("<TD BGCOLOR=\"#C0C0C0\">" + this.reportAsList.get(i)[ii] + "</TD>" + System.lineSeparator());
                            }
                        } else {
                           if (this.reportLinesSubTotal.contains(i)){
                                writer.write("<TD BGCOLOR=\"#C0C0C0\" ALIGN=\"RIGHT\"><STRONG>" + this.reportAsList.get(i)[ii] + "</STRONG></TD>" + System.lineSeparator());
                            } else if (this.reportLinesTotal.contains(i)){
                                writer.write("<TD BGCOLOR=\"#00FF00\" ALIGN=\"RIGHT\"><STRONG>" + this.reportAsList.get(i)[ii] + "</STRONG></TD>" + System.lineSeparator());
                            } else {
                                writer.write("<TD BGCOLOR=\"#C0C0C0\" ALIGN=\"RIGHT\">" + this.reportAsList.get(i)[ii] + "</TD>" + System.lineSeparator());
                            }                            
                            
                        }

                        
                  
                    }
                    writer.write("</TR>"+ System.lineSeparator());                    
                }
                writer.write("</TABLE>" + System.lineSeparator() + "</BODY>"+ System.lineSeparator() + "</HTML>");
                writer.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return filename;
    }  
    public String getReportAsODS(){
        //returns tempFilename
        String filename = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + ".ods";
        int size = this.reportHeadings.size();
        final Class[] types = new Class[size];
        String[] ins2 = new String[size];
        final boolean[] canEdit = new boolean[size];
        for (int i = 0; i < size; i++) {
            ins2[i] = this.reportHeadings.get(i);
            canEdit[i] = false;
            if (this.reportKinds.get(i).equals("Integer")) {
                types[i] = java.lang.Number.class;
            } else {
                types[i] = java.lang.String.class;
            }
        }


        Object[][] obj = new Object[this.reportAsList.size() + 4][size];
        for (int ii = 0; ii < this.reportAsList.size(); ii++) {
            for ( int i3 = 0 ; i3 < this.reportAsList.get(ii).length ; i3++ ) {
                if ( this.reportKinds.get(i3).equals("String")) {
                    obj[ii][i3] = this.reportAsList.get(ii)[i3];
                } else if (this.reportAsList.get(ii)[i3].length() > 0 ) {
                    obj[ii][i3] = Double.valueOf(this.reportAsList.get(ii)[i3]);
                } else {
                    obj[ii][i3] = "";
                }
            }
            
        }
        Calendar cal  = Calendar.getInstance();
        Date     time = cal.getTime();        
        obj[this.reportAsList.size() +1 ][0] = this.heading;
        obj[this.reportAsList.size() +2 ][0] = this.name + " " + time.toLocaleString();
        TableModel model = new javax.swing.table.DefaultTableModel(
                obj,
                ins2) {
            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        final File file1  = new File(filename);
        try {
            SpreadSheet ods = SpreadSheet.createEmpty(model);
            //ColumnStyle cs = new ColumnStyle();
            //ods.getTableModel("").getCellAt(1, 1).setBackgroundColor(Color.yellow);
            MutableTableModel mtm = ods.getFirstSheet().getMutableTableModel(0, 0);
            
            for ( int  i = 0 ; i < this.reportHeadings.size() ; i++ ) {
                mtm.getCellAt(0, i).setBackgroundColor(Color.LIGHT_GRAY);
            }
            for ( int i = 0 ; i < this.reportLinesSubTotal.size() ; i++ ) {
                for ( int  ii = 0 ; ii < this.reportHeadings.size() ; ii++ ) {
                    mtm.getCellAt(this.reportLinesSubTotal.get(i) + 1, ii).setBackgroundColor(Color.yellow);
                }                
            }
            for ( int i = 0 ; i < this.reportLinesTotal.size() ; i++ ) {
                for ( int  ii = 0 ; ii < this.reportHeadings.size() ; ii++ ) {
                    mtm.getCellAt(this.reportLinesTotal.get(i) + 1, ii).setBackgroundColor(Color.green);
                }                
            }            
            ods.saveAs(file1);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filename;
    }
    
    private Boolean sendMail ( String filename, String emailFilename ) {

        Properties props = System.getProperties();
        String user = null;
        String password = null;
        String host = null;
        String port = null;
        int smtpAuth = 0;
        int starttls = 0;
        String fromAddress = null;
        try {

            String statement = "select smtp_server, smtp_auth, smtp_username, smtp_password, smtp_starttls, fromAddress from SETTINGS_EMAIL";
            Statement stat = Database.conn.createStatement();        
            ResultSet rs = stat.executeQuery(statement);  
            while (rs.next()) {
                props.put("mail.smtp.host", rs.getString(1).split(":")[0]);
                props.put("mail.smtp.port", rs.getString(1).split(":")[1]);
                props.put("mail.transport.protocol", "smtp");
                fromAddress = rs.getString(6);
                if (rs.getInt(2) == 1) {
                    user=rs.getString(3);
                    password=rs.getString(4);
                    host=rs.getString(1).split(":")[0];
                    port=rs.getString(1).split(":")[1];
                    smtpAuth = rs.getInt(2);
                    starttls = rs.getInt(5);
                    
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.tls", "true");
                    props.put("mail.smtp.user", rs.getString(3));
                    props.put("mail.smtp.password", rs.getString(4));
                    if ( rs.getInt(5) == 1 ) {
                        props.put("mail.smtp.starttls.enable", "true");
                    }
                }
                
            }     
        } catch (SQLException ex) {
            Logger.getLogger(PrinterReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        try 
        {

         
            Session session = Session.getInstance(props, null);
            // create a message
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAddress));
            InternetAddress[] address = {new InternetAddress(this.destination)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(this.heading);

            // create and fill the first message part
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText("Report from jPrinterAdmin.");

            // create the second message part
            MimeBodyPart mbp2 = new MimeBodyPart();

                  // attach the file to the message
            FileDataSource fds = new FileDataSource(filename);
            mbp2.setDataHandler(new DataHandler(fds));
            mbp2.setFileName(emailFilename);

            // create the Multipart and add its parts to it
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);

            // add the Multipart to the message
            msg.setContent(mp);

            // set the Date: header
            msg.setSentDate(new Date());

            // send the message
            if (smtpAuth == 1){
                Transport transport = session.getTransport("smtps");
                transport.connect(host, Integer.valueOf(port), user, password);
                transport.sendMessage(msg, msg.getAllRecipients());
                transport.close(); 
            } else {
                Transport transport = session.getTransport("smtp");
                transport.connect();
                transport.sendMessage(msg, msg.getAllRecipients());
                
                transport.close();                 
            }


        } 
        catch (MessagingException mex) 
        {
            mex.printStackTrace();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null) {
            ex.printStackTrace();
                return false;
            }
        }        
        return true;
    }
    
}
