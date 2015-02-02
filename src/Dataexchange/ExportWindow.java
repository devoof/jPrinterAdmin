/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dataexchange;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import jprinteradmin.Database;
import jprinteradmin.Utility;

/**
 *
 * @author stefan
 */
public class ExportWindow extends javax.swing.JDialog  {

    /**
     * Creates new form export
     * @param parent
     * @param modal
     */
    public ExportWindow(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getDBValues();
        this.setLocation(100, 100);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextFieldFolder = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        statusBar = new javax.swing.JPanel();
        statusBarLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldJobName = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxJob = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jFileChooser1 = new javax.swing.JFileChooser();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldId = new javax.swing.JTextField();
        jButtonDelete = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jprinteradmin/language"); // NOI18N
        setTitle(bundle.getString("EXPORT")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText(bundle.getString("FOLDER")); // NOI18N

        jTextFieldFolder.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldFolderKeyReleased(evt);
            }
        });

        jButton1.setText(bundle.getString("EXECUTE")); // NOI18N
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        statusBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout statusBarLayout = new javax.swing.GroupLayout(statusBar);
        statusBar.setLayout(statusBarLayout);
        statusBarLayout.setHorizontalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(statusBarLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        statusBarLayout.setVerticalGroup(
            statusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statusBarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel2.setText(bundle.getString("JOB-NAME")); // NOI18N

        jTextFieldJobName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldJobNameKeyReleased(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "all" }));

        jButton2.setText(bundle.getString("SAVE")); // NOI18N
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText(bundle.getString("KEEP LAST")); // NOI18N

        jComboBoxJob.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxJob.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxJobItemStateChanged(evt);
            }
        });

        jLabel4.setText(bundle.getString("CHOOSE EXPORT-JOB")); // NOI18N

        jFileChooser1.setDialogType(javax.swing.JFileChooser.CUSTOM_DIALOG);
        jFileChooser1.setApproveButtonText(bundle.getString("CHOOSE DIRECTORY")); // NOI18N
        jFileChooser1.setBackground(new java.awt.Color(255, 255, 255));
        jFileChooser1.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });

        jLabel5.setText(bundle.getString("id")); // NOI18N

        jTextFieldId.setEnabled(false);

        jButtonDelete.setText(bundle.getString("DELETE")); // NOI18N
        jButtonDelete.setEnabled(false);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBoxJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldId, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextFieldFolder, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(jTextFieldJobName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButtonDelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldJobName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    List<Integer> jobIds = new ArrayList();
    
    private void getDBValues() {
        String statement = "SELECT id, " + Utility.dbQuotes + "name" + Utility.dbQuotes + " FROM EXPORT_JOBS";
        this.jComboBoxJob.removeAllItems();
        jobIds.clear();
        try {
            Statement stat = Database.conn.createStatement();
            ResultSet rs = stat.executeQuery(statement);
            while ( rs.next() ) {
                this.jComboBoxJob.addItem(rs.getString(2) + " (" + rs.getString(1) + ")");
                jobIds.add(rs.getInt(1));
            }
            updateFields();
        } catch (SQLException ex) {
            Logger.getLogger(ExportWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        jobIds.add(-1);
        this.jComboBoxJob.addItem(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("NEW"));
        
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new ExportJob());        

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextFieldFolderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFolderKeyReleased
        checkFields();
    }//GEN-LAST:event_jTextFieldFolderKeyReleased

    private void jTextFieldJobNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldJobNameKeyReleased
        checkFields();
    }//GEN-LAST:event_jTextFieldJobNameKeyReleased

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        this.jTextFieldFolder.setText(this.jFileChooser1.getSelectedFile().getAbsolutePath());
        checkFields();
    }//GEN-LAST:event_jFileChooser1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ExportJob ej = new ExportJob();
        ej.exportName = this.jTextFieldJobName.getText();
        ej.folder = this.jTextFieldFolder.getText();
        if ( this.jTextFieldId.getText().length() > 0 ) {
            ej.id = Integer.valueOf(this.jTextFieldId.getText());
        }
        if (this.jComboBox1.getSelectedItem().toString().equals("all")) {
            ej.keepExports = -1;
        } else {
            ej.keepExports = Integer.valueOf(this.jComboBox1.getSelectedItem().toString());
        }
        ej.save();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBoxJobItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxJobItemStateChanged
        updateFields();

    }//GEN-LAST:event_jComboBoxJobItemStateChanged

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        ExportJob ej = new ExportJob();
        ej.delete(Integer.valueOf(this.jTextFieldId.getText()));
        this.getDBValues();
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (lockedJob != -1) {
            Database.delLock("EXPORT_JOBS", lockedJob);
        }
    }//GEN-LAST:event_formWindowClosing

    int lockedJob = -1;
    
    Database.lock currentLock;
    
    private void updateFields(){
        if (lockedJob != -1) {
            Database.delLock("EXPORT_JOBS", lockedJob);
        }
        this.statusBarLabel.setText("");
        lockedJob = -1;
        currentLock = null;        
        if ( this.jobIds.size() > 0 && this.jComboBoxJob.getSelectedIndex() > -1 && this.jobIds.get(this.jComboBoxJob.getSelectedIndex()) > -1) {
            currentLock =  Database.getLock("EXPORT_JOBS", this.jobIds.get(this.jComboBoxJob.getSelectedIndex()) );
            if (!currentLock.isLocked) {
                lockedJob= this.jobIds.get(this.jComboBoxJob.getSelectedIndex());
                Database.setLock("EXPORT_JOBS", lockedJob);
                
            }
            try {
                Statement stat = Database.conn.createStatement();
                int newId = this.jobIds.get(this.jComboBoxJob.getSelectedIndex());
                String statement = "SELECT " + Utility.dbQuotes + "name" + Utility.dbQuotes + ", folder, keepLast FROM EXPORT_JOBS WHERE id = " + newId ;
                ResultSet rs = stat.executeQuery(statement);
                while (rs.next()) {
                    this.jTextFieldId.setText(String.valueOf(newId));
                    this.jTextFieldJobName.setText(rs.getString(1));
                    this.jTextFieldFolder.setText(rs.getString(2));
                    if (rs.getInt(3) == -1 ) {
                        this.jComboBox1.setSelectedItem("all");
                    } else {
                        this.jComboBox1.setSelectedItem(rs.getString(3));
                    }
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(ExportWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
                  
        } else {
            this.jTextFieldId.setText("");
            this.jTextFieldJobName.setText("");
            this.jTextFieldFolder.setText("");
            this.jComboBox1.setSelectedIndex(0);
        }   
        checkFields();  
    }
    
    private void checkFields() {
        if ( this.jTextFieldFolder.getText().length() > 0 && this.jTextFieldJobName.getText().length() > 0 && (currentLock == null || !currentLock.isLocked)) {
            this.jButton1.setEnabled(true);
            this.jButton2.setEnabled(true);           
            this.jButtonDelete.setEnabled(true);
        } else {
            this.jButtonDelete.setEnabled(false);
            this.jButton2.setEnabled(false);
            if (currentLock != null && currentLock.isLocked) {
                this.jButton1.setEnabled(true);     
                this.statusBarLabel.setText(java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("DATASET IS LOCKED. USER:") + currentLock.user + " " + java.util.ResourceBundle.getBundle("jprinteradmin/language").getString("TIME") + " " + currentLock.datetime);
            } else {
                this.jButton1.setEnabled(false);
            }
        }        
    }
    /**
     * @param args the command line arguments
     */

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonDelete;
    public javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBoxJob;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    public javax.swing.JTextField jTextFieldFolder;
    public javax.swing.JTextField jTextFieldId;
    public javax.swing.JTextField jTextFieldJobName;
    private javax.swing.JPanel statusBar;
    public javax.swing.JLabel statusBarLabel;
    // End of variables declaration//GEN-END:variables
}