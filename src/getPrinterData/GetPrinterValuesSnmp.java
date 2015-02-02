/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package getPrinterData;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jprinteradmin.Database;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 *
 * @author stefan
 */
public class GetPrinterValuesSnmp {

    public static String hexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    private static int countLetter(String str, char letter) {
        str = str.toLowerCase();
        letter = Character.toLowerCase(letter);
        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            char currentLetter = str.charAt(i);
            if (currentLetter == letter) {
                count++;
            }
        }

        return count;
    }
    public static Snmp snmp;

    public static void start() throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        // Do not forget this line!
        transport.listen();
    }

    private CommunityTarget getTarget() {
        String address = null;
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(GetPrinterValues.snmpCommunity));

        target.setAddress(targetAddress);
        target.setRetries(3);
        target.setTimeout(GetPrinterValues.snmpTimeout);
        if (GetPrinterValues.snmpVersion == 1) {
            target.setVersion(SnmpConstants.version1);
        } else {
            target.setVersion(SnmpConstants.version2c);
        }

        return target;
    }

    public ResponseEvent get(OID oids[]) throws IOException {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);

        ResponseEvent event = snmp.send(pdu, getTarget(), null);
        if (event != null) {
            return event;
        }

        throw new RuntimeException("GET timed out");
    }

    public String getAsString(OID oid) throws IOException {
        ResponseEvent event = get(new OID[]{oid});
        return event.getResponse().get(0).getVariable().toString();
    }

    public static String snmpGet(String host, String strOID) {
        GetPrinterValues.setSettings();
        String strResponse = "";
        ResponseEvent response;
        try {
            OctetString community1 = new OctetString(GetPrinterValues.snmpCommunity);
            host = host + "/" + "161";
            Address tHost = new UdpAddress(host);
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(community1);
            if (GetPrinterValues.snmpVersion == 1) {
                comtarget.setVersion(SnmpConstants.version1);
            } else {
                comtarget.setVersion(SnmpConstants.version2c);
            }
            comtarget.setAddress(tHost);
            comtarget.setRetries(3);
            comtarget.setTimeout(GetPrinterValues.snmpTimeout);
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(strOID)));
            pdu.setType(PDU.GET);
            snmp = new Snmp(transport);
            response = snmp.get(pdu, comtarget);
            if (response != null) {
                if (response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")) {
                    PDU pduresponse = response.getResponse();
                    strResponse = pduresponse.getVariableBindings().firstElement().toValueString();

                }
            } else {
                System.out.println("Looks like a TimeOut occured ");
            }
            snmp.close();
        } catch (Exception e) {
        }
        //System.out.println("Response="+strResponse);
        return strResponse;
    }

    public static String getSnmpValue(int requestMethodValuesSnmpId, String ip) {
        String value = "";
        String oid = "";
        int hexStringConversion = 0;
        String query = "select oid, hexStringConversion from REQUEST_METHOD_VALUES_SNMP WHERE id=" + requestMethodValuesSnmpId;


        try {
            Statement stat = Database.conn.createStatement();
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                oid = rs.getString(1);
                hexStringConversion = rs.getInt(2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetPrinterValuesSnmp.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (hexStringConversion == 1 && countLetter(snmpGet(ip, oid), ':') > 2) {
            return hexToString(snmpGet(ip, oid).replace(":", ""));
        }

        return snmpGet(ip, oid);
        /*
         SimpleSnmpClient client = new SimpleSnmpClient("udp:"+ip+"/161");
         try {
         value = client.getAsString(new OID(oid));
         } catch (IOException ex) {
         Logger.getLogger(getPrinterValuesSnmp.class.getName()).log(Level.SEVERE, null, ex);
         }
         String a="";
         return value;
         */
    }
}