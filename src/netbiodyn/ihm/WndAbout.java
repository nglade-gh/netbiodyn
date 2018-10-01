/*
 * WndAbout.java
 *
 * Created on 23 janvier 2008, 18:57
 */
package netbiodyn.ihm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import netbiodyn.util.Lang;

/**
 *
 * @author user
 */
public class WndAbout extends javax.swing.JDialog {

    /**
     * Creates new form WndAbout
     */
    public WndAbout() {
        this.setModal(true);
        initComponents();
        if (Lang.getInstance().getLang().equals("EN")) {
            jLabel5.setText("Design and implementation : Pascal Ballet & Jérémy RIVIERE");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton_fermer = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 0, 22)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NetBioDyn");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 520, 27);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Universite Europeenne de Bretagne - Brest");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 40, 360, 14);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Lab-STICC");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel4);
        jLabel4.setBounds(10, 60, 390, 14);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Licence OpenSource - GPL");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel8);
        jLabel8.setBounds(10, 180, 360, 14);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("Forum (available soon)");
        jLabel9.setEnabled(false);
        getContentPane().add(jLabel9);
        jLabel9.setBounds(10, 110, 400, 14);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Tutorial (video)");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel10);
        jLabel10.setBounds(10, 90, 400, 14);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Document");
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel11);
        jLabel11.setBounds(10, 130, 400, 14);

        jButton_fermer.setText("Fermer");
        jButton_fermer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_fermerMouseClicked(evt);
            }
        });
        getContentPane().add(jButton_fermer);
        jButton_fermer.setBounds(170, 200, 170, 23);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Conception et développement : Pascal BALLET & Jérémy RIVIERE");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel5);
        jLabel5.setBounds(10, 160, 460, 14);

        setSize(new java.awt.Dimension(533, 273));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_fermerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_fermerMouseClicked
        // TODO add your handling code here:
        fermer();
    }//GEN-LAST:event_jButton_fermerMouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        web("http://netbiodyn.tuxfamily.org/netbiodyn_2/Licence.txt");
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        web("http://pascalballet.com");
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        web("http://netbiodyn.tuxfamily.org/netbiodyn_doc_en_ligne.pdf");
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        web("http://fr.youtube.com/profile_videos?user=pballet&p=r");
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        web("http://www.lab-sticc.fr/");
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        web("http://virtulab.univ-brest.fr/");
    }//GEN-LAST:event_jLabel3MouseClicked

    public void fermer() {
        setVisible(false);
        dispose();
    }

    public void web(String url) {
        try {
            URI uri = new URI(url);
            java.awt.Desktop desk = java.awt.Desktop.getDesktop();
            boolean possible = desk.isSupported(java.awt.Desktop.Action.BROWSE);
            if (possible) {
                desk.browse(uri);
            }
        } catch (IOException ex) {
            Logger.getLogger(WndAbout.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(WndAbout.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_fermer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration//GEN-END:variables

}
