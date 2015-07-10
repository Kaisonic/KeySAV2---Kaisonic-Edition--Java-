/*
 * More license nonsense. It's open source.
 */

package keysav2kai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.swing.JOptionPane;

/**
 *
 * @author Kaisonic
 */
public class Help extends javax.swing.JFrame {

    /**
     * Creates new form Help
     */
    public Help() {
        initComponents();
        loadHelp(null);
    }
    
    private static String[] topics = {
        "formatting",
        "howto",
        "howto_getsave",
        "howto_breaksave",
        "howto_getvideo",
        "howto_breakvideo",
        "howto_encrypted",
        "howto_decrypted",
        "howto_videos",
        "options",
        "about",
        "changelog"
    };
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        CB_HelpSelector = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        RTB_Help = new javax.swing.JTextArea();

        setTitle("KeySAV2 - Kaisonic Edition Help");

        jLabel1.setText("Topic: ");

        CB_HelpSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "String Formatting", "How to Use This Program", "How to Get Your Saves", "How to Break Save Encryption", "How to Get Your Battle Videos", "How to Break Battle Video Encryption", "How to Open Encrypted Saves (Digital copy, PowerSaves, CyberGadget)", "How to Open Decrypted Saves (YABD, PCEdit, RAM2Sav)", "How to Use Battle Videos", "Export Options", "About KeySAV2 - Kaisonic Edition", "Changelog" }));
        CB_HelpSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadHelp(evt);
            }
        });

        RTB_Help.setColumns(20);
        RTB_Help.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        RTB_Help.setLineWrap(true);
        RTB_Help.setRows(5);
        jScrollPane1.setViewportView(RTB_Help);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CB_HelpSelector, 0, 427, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CB_HelpSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadHelp(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadHelp
        RTB_Help.setText("");
        try (InputStream in = this.getClass().getResourceAsStream("/keysav2kai/resources/text/help_en/" + Help.topics[CB_HelpSelector.getSelectedIndex()] + ".txt"))
        {
            BufferedReader r = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            String line;
            while ((line = r.readLine()) != null)
                RTB_Help.append(line + "\n");
            in.close();
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Failed to load help file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
        RTB_Help.setCaretPosition(0);
    }//GEN-LAST:event_loadHelp


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox CB_HelpSelector;
    private javax.swing.JTextArea RTB_Help;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
