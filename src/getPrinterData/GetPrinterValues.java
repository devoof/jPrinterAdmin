package getPrinterData;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jprinteradmin.Database;
import jprinteradmin.Utility;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stefan
 */
public class GetPrinterValues implements Runnable {

    public static String[] getRequestMethodValuesXid(int requestMethod, String requestValue) {
        String[] ret = new String[2];
        String query = "SELECT REQUEST_METHOD_VALUES.kind, REQUEST_METHOD_VALUES.request_method_values_x_id FROM REQUEST_METHOD_VALUES" + //NOI18N
                " INNER JOIN REQUEST_METHOD ON REQUEST_METHOD.id=REQUEST_METHOD_VALUES.request_method_id " + //NOI18N
                " INNER JOIN REQUEST_VALUES ON REQUEST_VALUES.id=REQUEST_METHOD_VALUES.request_values_id " + //NOI18N
                " WHERE REQUEST_METHOD.id='" + requestMethod + "' AND REQUEST_VALUES.name='" + Utility.escapeSqlString(requestValue) + "'"; //NOI18N
        System.out.println(query);
        try {
            Statement st = Database.conn.createStatement();
            try (ResultSet rs = st.executeQuery(query)) {
                while (rs.next()) {
                    ret[0] = rs.getString(1);
                    ret[1] = rs.getString(2);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetPrinterValues.class.getName()).log(Level.SEVERE, null, ex);

        }
        return ret;
    }

    public String getMacAddress(String ipAddress) {
        String macAddress = "";
        InetAddress ip;
        try {
            ip = InetAddress.getByName(ipAddress);
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            macAddress = (sb.toString());
        } catch (Exception e) {
        }
        return macAddress;
    }

    public void main(Boolean gui) throws IOException, SQLException {
        Database.lock isLock = Database.getLock("READ-PRINTER-DATA", 0);
        if (isLock.isLocked) {
            if (gui) {
                jprinteradmin.JPrinterAdmin.mw.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATASET IS LOCKED. USER:") + isLock.user + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString(". DATE: ") + isLock.datetime + ".");
            } else {
                this.result = java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATASET IS LOCKED. USER:") + isLock.user + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString(". DATE: ") + isLock.datetime + ".";
            }
        } else {
            Database.setLock("READ-PRINTER-DATA", 0);
            setSettings();
            String query_ip = "SELECT startip,endip,customer,id FROM IP_AREAS"; //NOI18N
            int defaultRequestMethodId = Utility.getDefaultRequestMethodId();
            String[] requestMethodValuesXid = getRequestMethodValuesXid(defaultRequestMethodId, "devicetype"); //NOI18N
            Statement stat;
            ResultSet rs;
            int countmatch = 0;
            int countscan = 0;
            int ipareacount = 0;

            List deviceTypes = new ArrayList();
            List deviceTypeIds = new ArrayList();
            List deviceTypeRequestMethodId = new ArrayList();
            Statement stat1 = Database.conn.createStatement();
            ResultSet rs1 = stat1.executeQuery("SELECT id,name,request_method_id FROM DEVICE_TYPES");
            while (rs1.next()) {
                deviceTypes.add(rs1.getString(2));
                deviceTypeIds.add(rs1.getInt(1));
                deviceTypeRequestMethodId.add(rs1.getInt(3));
            }
            stat = Database.conn.createStatement();
            rs = stat.executeQuery(query_ip);
            while (rs.next()) {
                ipareacount++;
                String startip = rs.getString(1);
                String endip = rs.getString(2);
                String customer = rs.getString(3);
                String ip = null;
                for (Long i = Utility.ipToInt(startip); i <= Utility.ipToInt(endip); i++) {
                    if (Utility.ping(Utility.intToIp(i), requestWait)) {
                        ip = Utility.intToIp(i);

                        String sysDescr = ""; //NOI18N
                        try {
                            int rmv = Integer.parseInt(requestMethodValuesXid[1]);
                            switch (requestMethodValuesXid[0]) {
                                case "snmp": //NOI18N                                
                                    sysDescr = GetPrinterValuesSnmp.getSnmpValue(rmv, ip);
                                    break;
                                case "html":
                                    sysDescr = GetPrinterValuesHTML.getHTMLValueByDbSettings(rmv, ip);
                                    break;
                                case "internal":
                                    switch (rmv) {
                                        case 1:
                                            sysDescr = ip;
                                            break;
                                        case 4:
                                            sysDescr = getMacAddress(ip);
                                            break;

                                    }
                            }

                        } catch (Exception ex) {
                        }
                        int devicetypeid;
                        int request_method_id = 0;
                        Statement stdev = Database.conn.createStatement();
                        if (!deviceTypes.contains(sysDescr) && sysDescr.length() > 0) {
                            devicetypeid = Database.getNextId("DEVICE_TYPES");

                            String query = "INSERT INTO DEVICE_TYPES(id," + Utility.dbQuotes + "name" + Utility.dbQuotes + ",request_method_id) VALUES("
                                    + devicetypeid + ",'" + Utility.escapeSqlString(sysDescr) + "'," + defaultRequestMethodId + ")";
                            stdev.executeUpdate(query);
                            deviceTypes.add(sysDescr);
                            deviceTypeIds.add(devicetypeid);
                            deviceTypeRequestMethodId.add(defaultRequestMethodId);
                            request_method_id = defaultRequestMethodId;
                        } else if (sysDescr.length() > 0) {
                            devicetypeid = (int) deviceTypeIds.get(deviceTypes.indexOf(sysDescr));
                            request_method_id = (int) deviceTypeRequestMethodId.get(deviceTypes.indexOf(sysDescr));

                        }
                        if (sysDescr.length() > 0) {
                            countmatch++;
                            //System.out.println(ip+" "+sysDescr+": "+devicetypeid);
                            String queryValues = "SELECT id,counter FROM REQUEST_VALUES ORDER BY identification DESC";
                            Statement statValue = Database.conn.createStatement();
                            Statement statValue2 = Database.conn.createStatement();
                            Statement statValue3 = Database.conn.createStatement();
                            ResultSet rsValues = statValue2.executeQuery(queryValues);
                            int i1 = 0;
                            int printerId = -1;
                            String kind = "";
                            int xid = -1;
                            while (rsValues.next()) {
                                int requestValueId = rsValues.getInt(1);
                                int counter = rsValues.getInt(2);
                                String queryMethod = "SELECT kind,request_method_values_x_id from REQUEST_METHOD_VALUES"
                                        + " INNER JOIN REQUEST_METHOD on REQUEST_METHOD.id=REQUEST_METHOD_VALUES."
                                        + "request_method_id WHERE (REQUEST_METHOD.id=" + request_method_id + " OR "
                                        + "REQUEST_METHOD.id= " + defaultRequestMethodId + ") AND REQUEST_METHOD_VALUES.request_values_id=" + rsValues.getInt(1) + " ORDER BY " + Utility.dbQuotes + "REQUEST_METHOD" + Utility.dbQuotes + "." + Utility.dbQuotes + "defaultMethod" + Utility.dbQuotes + " DESC";
                                //System.out.println(queryMethod);
                                ResultSet rsMethod = statValue.executeQuery(queryMethod);
                                while (rsMethod.next()) {
                                    kind = rsMethod.getString(1);
                                    xid = rsMethod.getInt(2);
                                }
                                //System.out.println("kind: "+kind+" xid:"+xid);
                                String value = "";
                                switch (kind) {
                                    case "snmp":
                                        value = GetPrinterValuesSnmp.getSnmpValue(xid, ip);
                                        break;
                                    case "html":
                                        value = GetPrinterValuesHTML.getHTMLValueByDbSettings(xid, ip);
                                        break;
                                    case "internal":
                                        switch (xid) {
                                            case 1:
                                                value = ip;
                                                break;
                                            case 2:

                                                break;
                                            case 3:
                                                break;
                                            case 4:

                                                value = getMacAddress(ip);
                                                break;
                                            case 5:
                                                value = customer;
                                                break;

                                        }
                                }

                                if (i1++ == 0) {
                                    //IdentifictionPart
                                    System.out.println("SELECT " + Utility.dbQuotes + "id" + Utility.dbQuotes + " FROM PRINTER WHERE identificationString='" + Utility.escapeSqlString(value) + "'");
                                    ResultSet rsPrinter = statValue3.executeQuery("SELECT " + Utility.dbQuotes + "id" + Utility.dbQuotes + " FROM PRINTER WHERE identificationString='" + Utility.escapeSqlString(value) + "'");
                                    while (rsPrinter.next()) {
                                        printerId = rsPrinter.getInt(1);

                                    }
                                    if (printerId == -1) {
                                        printerId = Database.getNextId("PRINTER");
                                        String queryInsert = "INSERT INTO PRINTER (id,identificationString, firstSeen, lastSeen) VALUES(" + printerId + ", '" + Utility.escapeSqlString(value) + "', " + Utility.dbNow + ", " + Utility.dbNow + ")";
                                        statValue.executeUpdate(queryInsert);
                                    } else {
                                        String queryUpdate = "UPDATE PRINTER SET lastSeen = " + Utility.dbNow + " WHERE identificationString = '" + Utility.escapeSqlString(value) + "'";
                                        statValue.executeUpdate(queryUpdate);
                                    }


                                    //System.out.println("printerid: "+printerId);
                                }
                                String queryValue = "INSERT INTO PRINTER_VALUES (printer_id,request_value_id,request_value) VALUES(" + printerId + "," + requestValueId + ",'" + Utility.escapeSqlString(value) + "')";
                                if (!"userdefined".equals(kind)) {
                                    statValue.executeUpdate("DELETE FROM PRINTER_VALUES WHERE printer_id=" + printerId + " AND request_value_id=" + rsValues.getInt(1));
                                    statValue.executeUpdate(queryValue);
                                }
                                if (counter == 1) {

                                    String queryValueCounter = "INSERT INTO PRINTER_COUNTER_VALUES (printer_id,request_value_id, " + Utility.dbQuotes + "time" + Utility.dbQuotes + ", requestvalue) VALUES(" + printerId + "," + rsValues.getInt(1) + ", " + Utility.dbNow + ",'0" + Utility.escapeSqlString(value) + "')";

                                    try {
                                        statValue.executeUpdate(queryValueCounter);
                                    } catch (Exception e) {
                                    }
                                }

                            }
                        }
                    }



                    if (gui) {
                        jprinteradmin.JPrinterAdmin.mw.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("IP-AREA") + ":" + ipareacount + "; " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("IPS SCANNED") + ": " + ++countscan + "; " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("PRINTER FOUND") + ": " + countmatch + " (" + Utility.intToIp(i) + ")");
                        //jprinteradmin.JPrinterAdmin.mw.repaint();
                    } else {
                        ++countscan;
                    }
                }
            }

            if (gui) {


                //jprinteradmin.JPrinterAdmin.mw.repaint();
                try {
                    jprinteradmin.JPrinterAdmin.mw.getDBValues();
                    jprinteradmin.JPrinterAdmin.mw.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("IP-AREA") + ":" + ipareacount + "; " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("IPS SCANNED") + ": " + ++countscan + "; " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("PRINTER FOUND") + ": " + countmatch);
                } catch (Exception e) {
                }
            } else {
                result = java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("IP-AREA") + ":" + ipareacount + "; " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("IPS SCANNED") + ": " + ++countscan + "; " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("PRINTER FOUND") + ": " + countmatch;
            }
            Database.delLock("READ-PRINTER-DATA", 0);
        }
    }
    public String result;

    public static void setSettings() {
        try {
            Statement st = Database.conn.createStatement();
            String queryRequestSettings = "SELECT printer_timeout, snmp_community, snmp_version, snmp_timeout, html_timeout FROM SETTINGS_REQUEST";
            ResultSet rs = st.executeQuery(queryRequestSettings);
            while (rs.next()) {
                requestWait = rs.getInt(1);
                snmpCommunity = rs.getString(2);

                snmpVersion = rs.getInt(3);
                snmpTimeout = rs.getInt(4);
                htmlTimeout = rs.getInt(5);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetPrinterValues.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static int requestWait;
    public static String snmpCommunity;
    public static int snmpVersion;
    public static int snmpTimeout;
    public static int htmlTimeout;

    @Override
    public void run() {
        jprinteradmin.JPrinterAdmin.mw.readPrData.setEnabled(false);
        try {
            main(true);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(GetPrinterValues.class.getName()).log(Level.SEVERE, null, ex);
        }
        jprinteradmin.JPrinterAdmin.mw.readPrData.setEnabled(true);
    }
}
