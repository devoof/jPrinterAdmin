/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jprinteradmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JTable;
import org.ini4j.Ini;
import org.ini4j.Wini;

/**
 *
 * @author stefan
 */
public class Utility {

    public static String defaultDirectory() {
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN")) {
            return System.getenv("APPDATA");
        } else if (OS.contains("MAC")) {
            return System.getProperty("user.home") + "/Library/Application "
                    + "Support";
        } else if (OS.contains("NUX")) {
            return System.getProperty("user.home");
        } else {
            return System.getProperty("user.dir");
        }
    }

    public static List<String[]> jTable2StringList(JTable table) {
        List<String[]> retValue;
        retValue = new ArrayList();
        for (int i = 0; i < table.getRowCount(); i++) {
            String[] retValueSingle = new String[table.getColumnCount()];
            for (int ii = 0; ii < table.getColumnCount(); ii++) {
                retValueSingle[ii] = table.getValueAt(i, ii).toString();
            }
            retValue.add(retValueSingle);
        }
        return retValue;
    }

    public static void setUser() {
        user = System.getProperty("user.name");
    }

    public static void setIniValues() {
        checkDir(defaultDirectory() + System.getProperty("file.separator") + ".jprinteradmin");
        checkFile(defaultDirectory() + System.getProperty("file.separator") + ".jprinteradmin" + System.getProperty("file.separator") + "jprinteradmin.ini");
        String filename = defaultDirectory() + System.getProperty("file.separator") + ".jprinteradmin" + System.getProperty("file.separator") + "jprinteradmin.ini";
        iniLF = setIniValue(filename, "APEARANCE", "LOOKANDFEEL", iniLF);
        iniDatabaseType = setIniValue(filename, "DATABASE", "TYPE", iniDatabaseType);
        iniDatabaseName = setIniValue(filename, "DATABASE", "NAME", iniDatabaseName);
        iniDatabaseUser = setIniValue(filename, "DATABASE", "USER", iniDatabaseName);
        iniDatabasePassword = setIniValue(filename, "DATABASE", "PASSWORD", iniDatabasePassword);
        iniDatabaseHost = setIniValue(filename, "DATABASE", "HOST", iniDatabaseHost);
        iniDatabasePort = setIniValue(filename, "DATABASE", "PORT", iniDatabasePort);
        iniDatabaseInstance = setIniValue(filename, "DATABASE", "INSTANCE", iniDatabasePort);

    }

    private static String setIniValue(String filename, String section, String key, String defaultValue) {
        try {
            Wini prefs = new Wini(new File(filename));
            Ini.Section sect = prefs.get(section);
            if (sect == null) {
                prefs.add(section);
                sect = prefs.get(section);
            }
            if (sect.containsKey(key)) {
                return sect.get(key);
            } else {
                sect.put(key, defaultValue);
            }

            prefs.store();
        } catch (IOException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return defaultValue;
    }

    public static void writeIniValuesToFile() {
        try {
            String filename = defaultDirectory() + System.getProperty("file.separator") + ".jprinteradmin" + System.getProperty("file.separator") + "jprinteradmin.ini";
            Wini prefs = new Wini(new File(filename));
            Ini.Section apearance = prefs.get("APEARANCE");
            apearance.put("LOOKANDFEEL", iniLF);
            Ini.Section db = prefs.get("DATABASE");
            db.put("NAME", iniDatabaseName);
            db.put("TYPE", iniDatabaseType);
            db.put("USER", iniDatabaseUser);
            db.put("PASSWORD", iniDatabasePassword);
            db.put("HOST", iniDatabaseHost);
            db.put("PORT", iniDatabasePort);
            db.put("INSTANCE", iniDatabaseInstance);
            prefs.store();
        } catch (IOException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean checkDir(String dirName) {
        File stats = new File(dirName);
        if (stats.exists()) {
            return true;
        } else {
            if (stats.mkdir()) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static boolean checkFile(String dirName) {
        File stats = new File(dirName);
        if (stats.exists()) {
            return true;
        } else {
            try {
                if (stats.createNewFile()) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException ex) {
                Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public static int checkDB() {
        //returns 0 if not set, 1 if not working,2 if version is too high,3 if version is too low, 4 if everything is fine
        switch (Utility.iniDatabaseType) {
            case "local":
                Utility.dbQuotes = "\"";
                Utility.dbNow = "datetime('now', 'localtime')";
                break;
            case "mysql":
                Utility.dbQuotes = "`";
                Utility.dbNow = "now()";
                break;
            case "mssql":
                Utility.dbQuotes = "\"";
                Utility.dbNow = "getdate()";
        }
        if (iniDatabaseType.equals("")) {
            return 0;
        } else {
            try {
                Statement stat = Database.conn.createStatement();
                ResultSet rs = stat.executeQuery("SELECT " + Utility.dbQuotes + "value" + Utility.dbQuotes + " FROM " + Utility.dbQuotes + "BASE" + Utility.dbQuotes + " WHERE " + Utility.dbQuotes + "name" + Utility.dbQuotes + "='dbversion'");
                rs.next();
                if (rs.getString(1).equals(dbVersion)) {
                    return 4;
                } else if (Double.parseDouble(rs.getString(1)) > Double.parseDouble(dbVersion)) {
                    return 2;
                } else if (Double.parseDouble(rs.getString(1)) < Double.parseDouble(dbVersion)) {
                    return 3;
                }

            } catch (SQLException | NumberFormatException | NullPointerException ex) {
                return 1;
                //Logger.getLogger(utility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        ip = ip.trim();
        if ((ip.length() < 6) & (ip.length() > 15)) {
            return false;
        }

        try {
            Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }

    public static String intToIp(Long i) {
        return ((i >> 24) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "."
                + ((i >> 8) & 0xFF) + "."
                + (i & 0xFF);
    }

    public static Boolean ping(String ip, int timeOut) {

        try {

            InetAddress host = InetAddress.getByName(ip);

            return host.isReachable(timeOut);
        } catch (Exception ex) {
            return false;
        }

    }

    public static Long ipToInt(String addr) {
        String[] addrArray = addr.split("\\.");
        long num = 0;
        for (int i = 0; i < addrArray.length; i++) {
            int power = 3 - i;
            num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
        }
        return num;
    }

    public static int getDefaultRequestMethodId() {
        int id = -1;
        try {
            Statement stat = Database.conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT id FROM REQUEST_METHOD WHERE " + Utility.dbQuotes + "defaultMethod" + Utility.dbQuotes + "=1");
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    public static String implode(ArrayList elements, String connector) {
        String retValue = null;
        // clauseElements is a ArrayList with Strings in it; connector is to conncet the elements
        if (elements.size() > 0) {
            retValue = "";
            int size = elements.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    retValue += connector;
                }
                retValue += elements.get(i).toString();
            }
        }
        return retValue;
    }
    
    public static void copyFile( String inFile, String outFile) throws IOException {
        
        File in = new File(inFile);
        File out = new File(outFile);
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(in).getChannel();
            outChannel = new FileOutputStream(out).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }         
    
    public static String escapeSqlString (String string){
        if (string != null){
            switch (Utility.iniDatabaseType){
                case "mysql":
                    string = string.replace("'", "\\'");
                    string = string.replace("\\", "\\\\");
                    break;
                case "local":    
                    string = string.replace("'", "\\'");                                     
                    break;
                case "mssql":
                    string = string.replace("'", "''");                                     
                    break;
                    
                    
            }
            return string; 
        }
        return "NULL";
    }
    
    public static String escapeHTML(String string) {
        string = string.replace("ä", "&auml;");
        string = string.replace("ü", "&uuml;");
        string = string.replace("ö", "&öuml;");
        string = string.replace("Ä", "&Auml;");
        string = string.replace("Ü", "&Uuml;");
        string = string.replace("Ö", "&Ouml;");
        string = string.replace("ß", "&szlig;");
        string = string.replace("<", "&gt;");
        string = string.replace(">", "&lt;");
        string = string.replace(" ", "&nbsp;");
        
        return string;
    }
    
    public static String escapeSqlString4export (String string){
        if (string != null){
            string = string.replace("'", "<StringDelimiter>");
            return string;
        }
        return "NULL";
    }    
    
    public static String escapeSqlString4import (String string){
        if (string != null){
            switch (Utility.iniDatabaseType){
                case "mysql":
                    string = string.replace("<StringDelimiter>", "\\'");                                     
                    break;
                case "local":    
                    string = string.replace("<StringDelimiter>", "\\'");                                     
                    break;
                case "mssql":
                    string = string.replace("<StringDelimiter>", "''");                                     
                    break;          
            }
            return string; 
        }
        return "NULL";
    }

    
    public static String iniLF = "Nimbus";
    public static String iniDatabaseType = "";
    public static String iniDatabaseName = "";
    public static String iniDatabaseUser = "";
    public static String iniDatabasePassword = "";
    public static String iniDatabaseHost = "";
    public static String iniDatabasePort = "";
    public static String iniDatabaseInstance ="";
    public static String dbVersion = "0.5";
    public static String appVersion = "0.5";
    public static String user;
    public static String dbQuotes;
    public static String dbNow;

}
/**
 *
 * @author stefan
 */
