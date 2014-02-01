/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reports;

import java.awt.Color;
import java.awt.Component;
import java.awt.print.PrinterException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import static jprinteradmin.MainWindow.packColumn;

/**
 *
 * @author stefan
 */
public class Preview extends javax.swing.JDialog  {

    /**
     * Creates new form Preview
     * @param parent
     * @param modal
     */
    public Preview(javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getDBValues();
    }
    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {                                    
        System.out.println("hallo" + evt.getNewValue().toString()); //NOI18N
    }                                   

    public class ColorTableCellRenderer implements TableCellRenderer
    {
    private HashMap<Integer,Color> cellData=new HashMap<Integer,Color>();
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column)
        {
        JLabel label=new JLabel((String)value);
        int key=((row+1)*1000)+column;
        label.setOpaque(true);
        Color color = UIManager.getColor("Table.getGridColor");
        MatteBorder border = new MatteBorder(4,4,1,1,color);
        label.setBorder(border);
        if(ManagePrinterReports.actualPrinterReport.reportKinds.get(column).equals("Integer")){
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        if(cellData.containsKey(key))
            {
            label.setBackground(cellData.get(key));
            }
        else //Standardfarbe setzen
            {
            label.setBackground(label.getBackground());
            }
        return label;
        }
    public void setColor(int row,int column,Color color)
        {
        int key=((row+1)*1000)+column;
        cellData.put(key,color);
        }
    }    
    public static void packColumn(JTable table, int vColIndex, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(vColIndex);
        int width = 0;

        // Get width of column header
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        java.awt.Component comp = renderer.getTableCellRendererComponent(
                table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        // Get maximum width of column data
        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, vColIndex);
            comp = renderer.getTableCellRendererComponent(
                    table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // Add margin
        width += 2 * margin;

        // Set the width
        col.setPreferredWidth(width);
    }    
    
    private void getDBValues() {
        Calendar cal  = Calendar.getInstance();
        Date     time = cal.getTime();
        //DateFormat formatter = new SimpleDateFormat();
        
        this.jLabelDate.setText(time.toLocaleString());
        this.jLabelHeading.setText(ManagePrinterReports.actualPrinterReport.heading);
        this.jLabelName.setText(ManagePrinterReports.actualPrinterReport.name);
        int size = ManagePrinterReports.actualPrinterReport.reportHeadings.size();
        final Class[] types = new Class[size];
        String[] ins2 = new String[size];
        final boolean[] canEdit = new boolean[size];
        for (int i = 0; i < size; i++) {
            ins2[i] = ManagePrinterReports.actualPrinterReport.reportHeadings.get(i);
            canEdit[i] = false;
            if (ManagePrinterReports.actualPrinterReport.reportKinds.get(i).equals("Integer")) {
                types[i] = java.lang.Number.class;
            } else {
                types[i] = java.lang.String.class;
            }
        }


        Object[][] obj = new Object[ManagePrinterReports.actualPrinterReport.reportAsList.size()][size];
        for (int ii = 0; ii < ManagePrinterReports.actualPrinterReport.reportAsList.size(); ii++) {
            obj[ii] = (String[]) ManagePrinterReports.actualPrinterReport.reportAsList.get(ii);

        }
        jTable1.setAutoCreateRowSorter(true);
        jTable1.updateUI();
        //dialog.jTable1.removeAll();
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        });
        int width = 0;
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            packColumn(jTable1, i, 1);
        }
        jTable1.setUpdateSelectionOnSort(true);
        Preview.ColorTableCellRenderer ctr=new Preview.ColorTableCellRenderer();
        for ( int i2 = 0 ; i2 < ManagePrinterReports.actualPrinterReport.outputFields.size() + ManagePrinterReports.actualPrinterReport.counterValues.size() ; i2++ ){
            jTable1.getColumnModel().getColumn(i2).setCellRenderer(ctr);
        }        
              
        for ( int i = 0 ; i < ManagePrinterReports.actualPrinterReport.reportLinesSubTotal.size() ; i++ ){
            for ( int i2 = 0 ; i2 < ManagePrinterReports.actualPrinterReport.outputFields.size() + ManagePrinterReports.actualPrinterReport.counterValues.size() ; i2++ ){
                ctr.setColor(ManagePrinterReports.actualPrinterReport.reportLinesSubTotal.get(i), i2, Color.yellow);
            }
        }
        for ( int i = 0 ; i < ManagePrinterReports.actualPrinterReport.reportLinesTotal.size() ; i++ ){
            for ( int i2 = 0 ; i2 < ManagePrinterReports.actualPrinterReport.outputFields.size() + ManagePrinterReports.actualPrinterReport.counterValues.size() ; i2++ ){
                ctr.setColor(ManagePrinterReports.actualPrinterReport.reportLinesTotal.get(i), i2, Color.GREEN);
            }
        }        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelName = new javax.swing.JLabel();
        jLabelHeading = new javax.swing.JLabel();
        jLabelDate = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jprinteradmin/language"); // NOI18N
        setTitle(bundle.getString("PREVIEW")); // NOI18N

        jLabelHeading.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N

        jLabelDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText(bundle.getString("PRINT")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelName, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelDate, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelDate, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            this.jTable1.print();
        } catch (PrinterException ex) {
            Logger.getLogger(Preview.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Preview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Preview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Preview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Preview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Preview dialog = new Preview(new javax.swing.JDialog(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabelDate;
    private javax.swing.JLabel jLabelHeading;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
