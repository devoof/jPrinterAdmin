package getPrinterData;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jprinteradmin.Database;
import jprinteradmin.Utility;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeUtils;
import org.snmp4j.util.TreeEvent;

public class Snmpwalk {

    private static String targetAddr;
    private String oidStr;
    private String commStr;
    private int snmpVersion;
    private String portNum;

    Snmpwalk() throws IOException {
        // Set default value.
        targetAddr = null;
        oidStr = null;
        commStr = "public";
        snmpVersion = SnmpConstants.version2c;
        portNum = "161";

    }

    private List<String[]> doSnmpwalk() {
        ArrayList<String[]> snmpValues = new ArrayList<>();
        try {

            Address targetAddress = GenericAddress.parse("udp:" + targetAddr + "/" + portNum);
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();
            // setting up target
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(commStr));
            target.setAddress(targetAddress);
            target.setRetries(3);
            target.setTimeout(snmpTimeout);
            target.setVersion(snmpVersion);
            OID oid = null;
            try {
                oid = new OID(oidStr);
            } catch (RuntimeException ex) {
                System.out.println("OID is not specified correctly.");
                System.exit(1);
            }
            TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> events = treeUtils.getSubtree(target, oid);
            if (events == null || events.size() == 0) {
                System.out.println("No result returned.");
                //System.exit(1);
            }
            // Get snmpwalk result.
            for (TreeEvent event : events) {
                if (event != null) {
                    if (event.isError()) {
                        System.err.println("oid [" + oid + "] " + event.getErrorMessage());
                    }
                    VariableBinding[] varBindings = event.getVariableBindings();
                    if (varBindings == null || varBindings.length == 0) {
                        System.out.println("No result returned.");
                    }
                    try {
                        for (VariableBinding varBinding : varBindings) {
                            snmpValues.add(new String[]{varBinding.getOid().toString(), varBinding.getVariable().getSyntaxString(), varBinding.getVariable().toString()});
                            System.out.println("-> "
                                    + varBinding.getOid()
                                    + " : "
                                    + varBinding.getVariable().getSyntaxString()
                                    + " : "
                                    + varBinding.getVariable());
                        }
                    } catch (Exception ex) {
                    }


                }
                snmp.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Snmpwalk.class.getName()).log(Level.SEVERE, null, ex);
        }
        return snmpValues;
    }

    private void checkAndSetArgs() {



        commStr = snmpCommunity;
        if (snmpVersion1 == 1) {
            snmpVersion = SnmpConstants.version1;
        } else {
            snmpVersion = SnmpConstants.version2c;
        }
        portNum = "161";

        oidStr = "1.3.6.1";
        commStr = snmpCommunity;

    }

// Delegate main function to Snmpwalk.
    public static void main(String[] args) {
        try {
            Snmpwalk snmpwalk = new Snmpwalk();
            Utility.setIniValues();
            Database.setConn();
            setSettings("");
            snmpwalk.checkAndSetArgs();
            snmpwalk.doSnmpwalk();
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public static List<String[]> getSnmpWalk(String target) {
        List<String[]> snmpValues = new ArrayList<>();

        try {


            Snmpwalk snmpwalk = new Snmpwalk();
            setSettings(target);
            snmpwalk.checkAndSetArgs();
            snmpValues = snmpwalk.doSnmpwalk();


        } catch (IOException ex) {
            Logger.getLogger(Snmpwalk.class.getName()).log(Level.SEVERE, null, ex);
        }
        return snmpValues;
    }

    public static void setSettings(String target) {
        try {
            Statement st = Database.conn.createStatement();
            String queryRequestSettings = "SELECT printer_timeout, snmp_community, snmp_version, snmp_timeout, html_timeout FROM SETTINGS_REQUEST";
            ResultSet rs = st.executeQuery(queryRequestSettings);
            while (rs.next()) {
                requestWait = rs.getInt(1);
                snmpCommunity = rs.getString(2);
                targetAddr = target;
                snmpVersion1 = rs.getInt(3);
                snmpTimeout = rs.getInt(4);
                htmlTimeout = rs.getInt(5);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetPrinterValues.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static int requestWait;
    public static String snmpCommunity;
    public static int snmpVersion1;
    public static int snmpTimeout;
    public static int htmlTimeout;
}