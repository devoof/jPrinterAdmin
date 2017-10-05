/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package getPrinterData;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jprinteradmin.Database;
import jprinteradmin.Utility;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author stefan
 */
public class GetPrinterValuesHTML {

    public static void main(String[] args) {
        String value = getHTMLValueByDbSettings(1, "10.0.15.18");
    }

    public static String getHTMLValueByDbSettings(int requestMethodValuesHtmlId, String ip) {
        String value;
        String cookie_name = "";
        String request_url = "";
        String authentication_url = "";
        String get = "";
        String tag = "";
        int valueNr = 0;
        List<String> listReplaces = new ArrayList<>();
        List<String> listPostData = new ArrayList<>();
        String query1 = "SELECT request_url, authentication_url, cookie_name, " + Utility.dbQuotes + "get" + Utility.dbQuotes + ", " + Utility.dbQuotes + "value" + Utility.dbQuotes + ", tag from REQUEST_METHOD_VALUES_HTML WHERE id=" + requestMethodValuesHtmlId;
        try {
            Statement stat = Database.conn.createStatement();
            ResultSet rs = stat.executeQuery(query1);
            while (rs.next()) {
                request_url = rs.getString(1);
                authentication_url = rs.getString(2);
                cookie_name = rs.getString(3);
                get = rs.getString(4);
                valueNr = rs.getInt(5);
                tag = rs.getString(6);
            }
            String query2 = "SELECT " + Utility.dbQuotes + "variable" + Utility.dbQuotes + ", " + Utility.dbQuotes + "value" + Utility.dbQuotes + " FROM REQUEST_METHOD_VALUES_HTML_POSTDATA WHERE rmvh_id=" + requestMethodValuesHtmlId;
            rs = stat.executeQuery(query2);

            while (rs.next()) {
                listPostData.add(rs.getString(1));
                listPostData.add(rs.getString(2));
            }
            String query3 = "SELECT " + Utility.dbQuotes + "string" + Utility.dbQuotes + " FROM REQUEST_METHOD_VALUES_HTML_STRINGREMOVES WHERE rmvh_id=" + requestMethodValuesHtmlId;
            rs = stat.executeQuery(query3);

            while (rs.next()) {
                listReplaces.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(GetPrinterValuesHTML.class.getName()).log(Level.SEVERE, null, ex);
        }
        value = getHTMLValue(ip, valueNr, cookie_name, request_url, authentication_url, get, tag, listPostData, listReplaces);
        return value;
    }

    public static String getHTMLValue(String ip, int valueNr, String cookie_name, String request_url, String authentication_url, String get, String tag, List<String> listPostData, List<String> listReplaces) {
        String cookie = null;
        String value = "";
        try {
            String postdata[] = listPostData.toArray(new String[listPostData.size()]);
            System.out.println("postdatasize:" + listPostData.size());
            try {
                if (listPostData.size() > 0 && cookie_name.length() > 0) {
                    Connection.Response res = Jsoup.connect(authentication_url.replace("[ip]", ip))
                            .timeout(8000)
                            .data(postdata)
                            .method(Method.POST)
                            .execute();
                    cookie = res.cookie(cookie_name);
                } else if (cookie_name.length() > 0) {
                    Connection.Response res = Jsoup.connect(authentication_url.replace("[ip]", ip))
                            .timeout(8000)
                            .method(Method.POST)
                            .execute();
                    cookie = res.cookie(cookie_name);
                }

                System.out.println("cookie:" + cookie);

            } catch (Exception ex) {
                Logger.getLogger(GetPrinterValuesHTML.class.getName()).log(Level.SEVERE, null, ex);
            }

            String url = request_url.replace("[ip]", ip);

            Document document;
            if (cookie != null) {
                document = Jsoup.connect(url).timeout(GetPrinterValues.htmlTimeout).cookie(cookie_name, cookie).get();
            } else {
                document = Jsoup.connect(url).timeout(GetPrinterValues.htmlTimeout).get();
            }
            Elements answerers = document.select(tag);
            if (get.equals("text")) {
                value = answerers.get(valueNr).text().replaceAll("[_[^\\w\\däüöÄÜÖ\\+\\- ]]", "");
            } else {
                value = answerers.get(valueNr).data().replaceAll("[_[^\\w\\däüöÄÜÖ\\+\\- ]]", "");
            }

        } catch (IOException ex) {
            Logger.getLogger(GetPrinterValuesHTML.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < listReplaces.size(); i++) {
            value = value.replace(listReplaces.get(i).toString(), "");
            System.out.println("Replace: " + listReplaces.get(i).toString());
        }
        return value;
    }
}