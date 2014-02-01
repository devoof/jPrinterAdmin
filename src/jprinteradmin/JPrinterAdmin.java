package jprinteradmin;

/**
 *
 * @author stefan
 */
public class JPrinterAdmin {

    /**
     */
    public static MainWindow mw;

    public static void main(String[] args) {
        Utility.setIniValues();
        Utility.setUser();
        try {
            Database.setConn();
        } catch (Exception ex) {
        }
        Database.databaseState = Utility.checkDB();
        String todo;
        if (args.length == 0) {

            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if (Utility.iniLF.equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jprinteradmin/language");
            System.out.println("locale: " + bundle.getLocale());
            mw = new MainWindow();
            mw.checkdb();
            if (Database.databaseState == 4) {
                mw.getDBValues();

            }

            mw.setVisible(true);
        } else {
            switch (args[0]) {
                case "planer":
                    System.out.println("planer");
                    PlanerWindow pl = new PlanerWindow();
                    pl.start();
                    break;
            }
        }

    }
}
