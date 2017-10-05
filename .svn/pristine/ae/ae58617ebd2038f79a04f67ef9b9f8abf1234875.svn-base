/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jprinteradmin;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author stefan
 */
public class listComparer implements Comparator {

    public int compare(ArrayList o1, ArrayList o2) {
        String p1 = o1.get(0).toString();
        String p2 = o2.get(0).toString();
        return p1.compareTo(p2);
    }

    @Override
    public int compare(Object o1, Object o2) {
        ArrayList p1 = (ArrayList) o1;
        ArrayList p2 = (ArrayList) o1;
        return p1.get(0).toString().compareTo(p2.get(0).toString());
    }
}
