/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jprinteradmin;

import Settings.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;

/**
 *
 * @author stefan
 */
public class ViewPlanerLog extends javax.swing.JDialog {

    public ViewPlanerLog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        final ViewPlanerLog dialog = this;
        getDBValues(dialog);

        ListSelectionModel cellSelectionModel = jTable1.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.setLocation(100, 100);
        this.setVisible(true);


    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextBoxStartDate = new javax.swing.JTextField();
        jTextBoxFinishedDate = new javax.swing.JTextField();
        jTextBoxScheduleName = new javax.swing.JTextField();
        jTextBoxJobName = new javax.swing.JTextField();
        jButtonDelete = new javax.swing.JButton();
        statusBar = new javax.swing.JPanel();
        statusBarLabel = new javax.swing.JLabel();
        jButtonTruncate = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaResult = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldId = new javax.swing.JTextField();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jprinteradmin/language"); // NOI18N
        setTitle(bundle.getString("VIEW-LOG")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText(bundle.getString("START-DATE")); // NOI18N

        jLabel2.setText(bundle.getString("FINISHED-DATE")); // NOI18N

        jLabel3.setText(bundle.getString("SCHEDULE-NAME")); // NOI18N

        jLabel4.setText(bundle.getString("JOB-NAME")); // NOI18N

        jTextBoxStartDate.setEnabled(false);

        jTextBoxFinishedDate.setEnabled(false);

        jTextBoxScheduleName.setEnabled(false);

        jTextBoxJobName.setEnabled(false);

        jButtonDelete.setText(bundle.getString("DELETE")); // NOI18N
        jButtonDelete.setEnabled(false);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        statusBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout statusBarLayout = new javax.swing.GroupLayout(statusBar);
        statusBar.setLayout(statusBarLayout);
        statusBarLayout.setHorizontalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarLayout.createSequentialGroup()
                .addComponent(statusBarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        statusBarLayout.setVerticalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusBarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonTruncate.setText(bundle.getString("TRUNCATE")); // NOI18N
        jButtonTruncate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTruncateActionPerformed(evt);
            }
        });

        jTextAreaResult.setColumns(20);
        jTextAreaResult.setLineWrap(true);
        jTextAreaResult.setRows(5);
        jTextAreaResult.setWrapStyleWord(true);
        jTextAreaResult.setEnabled(false);
        jScrollPane2.setViewportView(jTextAreaResult);

        jLabel5.setText(bundle.getString("RESULT")); // NOI18N

        jTextFieldId.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jTextFieldId, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTruncate)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextBoxScheduleName, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(jTextBoxJobName)
                            .addComponent(jTextBoxFinishedDate)
                            .addComponent(jTextBoxStartDate, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextBoxStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1)
                            .addComponent(jTextFieldId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextBoxFinishedDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextBoxScheduleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextBoxJobName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonDelete)
                    .addComponent(jButtonTruncate))
                .addGap(31, 31, 31)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        jprinteradmin.JPrinterAdmin.mw.setEnabled(true);
        System.out.println("closing");
    }//GEN-LAST:event_formWindowClosing

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        try {
            try (Statement st = Database.conn.createStatement()) {
                String query = "DELETE FROM SCHEDULE_LOG WHERE id=" + jTextFieldId.getText(); //NOI18N 
                st.executeUpdate(query);
                statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATASET DELTED."));
            }
        } catch (SQLException ex) {
            Logger.getLogger(IpareaSettings.class.getName()).log(Level.SEVERE, null, ex);
        }

        getDBValues(this);

    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonTruncateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTruncateActionPerformed
        try {
            try (Statement st = Database.conn.createStatement()) {
                String query = "DELETE FROM SCHEDULE_LOG"; //NOI18N 
                st.executeUpdate(query);
                statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATASETS DELTED."));
            }
        } catch (SQLException ex) {
            Logger.getLogger(IpareaSettings.class.getName()).log(Level.SEVERE, null, ex);
        }

        getDBValues(this);
    }//GEN-LAST:event_jButtonTruncateActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (-1 != jTable1.getSelectedRow()) {
            this.jTextFieldId.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
            this.jTextBoxStartDate.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 1).toString());
            this.jTextBoxFinishedDate.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString());
            this.jTextBoxScheduleName.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 3).toString());
            this.jTextBoxJobName.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 4).toString());
            this.jTextAreaResult.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 5).toString());
            this.jButtonDelete.setEnabled(true);
        } else {
            this.jTextFieldId.setText("");
            this.jTextBoxStartDate.setText("");
            this.jTextBoxFinishedDate.setText("");
            this.jTextBoxScheduleName.setText("");
            this.jTextBoxJobName.setText("");
            this.jTextAreaResult.setText("");
            this.jButtonDelete.setEnabled(false);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    public static void getDBValues(ViewPlanerLog dialog) {
        try {
            Statement st = Database.conn.createStatement();
            ResultSet rs = st.executeQuery("select SCHEDULE_LOG.id, startDate, finishedDate, SCHEDULE." + Utility.dbQuotes + "name" + Utility.dbQuotes + ", JOBS." + Utility.dbQuotes + "name" + Utility.dbQuotes + ", " + Utility.dbQuotes + "result" + Utility.dbQuotes + " "
                    + "    from SCHEDULE_LOG "
                    + "    LEFT JOIN JOBS ON JOBS.id = SCHEDULE_LOG.job_id"
                    + "    LEFT JOIN SCHEDULE ON SCHEDULE.id = SCHEDULE_LOG.schedule_id"
                    + "    ORDER BY SCHEDULE_LOG.id desc"); //NOI18N
            ArrayList<String[]> list = new ArrayList<>();

            while (rs.next()) {
                //( (DefaultTableModel) dialog.jTable1.getModel() ).insertRow(icolumn+1, rs.getInt(1));
                list.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});


            }
            Object[][] obj = new Object[list.size()][5];
            for (int ii = 0; ii < list.size(); ii++) {
                obj[ii] = (String[]) list.get(ii);
            }
            dialog.jTable1.updateUI();
            //dialog.jTable1.removeAll();
            dialog.jTable1.setModel(new javax.swing.table.DefaultTableModel(
                    obj,
                    new String[]{
                java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("ID"), java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("START-DATE"), java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("FINISHED-DATE"), java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("SCHEDULE-NAME"), java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("JOB-NAME"), java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("RESULT")
            }) {
                Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                };
                boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false
                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(IpareaSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonTruncate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextAreaResult;
    private javax.swing.JTextField jTextBoxFinishedDate;
    private javax.swing.JTextField jTextBoxJobName;
    private javax.swing.JTextField jTextBoxScheduleName;
    private javax.swing.JTextField jTextBoxStartDate;
    private javax.swing.JTextField jTextFieldId;
    private javax.swing.JPanel statusBar;
    private javax.swing.JLabel statusBarLabel;
    // End of variables declaration//GEN-END:variables
}