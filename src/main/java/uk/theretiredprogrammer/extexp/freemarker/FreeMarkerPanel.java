/*
 * Copyright 2019 richard.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.extexp.freemarker;

import java.io.File;
import javax.swing.JFileChooser;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

final class FreeMarkerPanel extends javax.swing.JPanel {

    private final FreeMarkerOptionsPanelController controller;

    FreeMarkerPanel(FreeMarkerOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        freemarkerRootPath = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        browse = new javax.swing.JToggleButton();

        freemarkerRootPath.setText(org.openide.util.NbBundle.getMessage(FreeMarkerPanel.class, "FreeMarkerPanel.freemarkerRootPath.text")); // NOI18N
        freemarkerRootPath.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                freemarkerRootPathPropertyChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FreeMarkerPanel.class, "FreeMarkerPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browse, org.openide.util.NbBundle.getMessage(FreeMarkerPanel.class, "FreeMarkerPanel.browse.text")); // NOI18N
        browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(freemarkerRootPath, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(browse)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(freemarkerRootPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browse))
                .addContainerGap(28, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            freemarkerRootPath.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_browseActionPerformed

    private void freemarkerRootPathPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_freemarkerRootPathPropertyChange
        controller.changed();
    }//GEN-LAST:event_freemarkerRootPathPropertyChange

    void load() {
        // calculate the default root path - OS dependent values
        String defaultrootpath = Utilities.isWindows() ? "c:/" : "/";
        freemarkerRootPath.setText(NbPreferences.forModule(FreeMarkerPanel.class).get("FreeMarkerRootPath", defaultrootpath));
    }

    void store() {
        if (valid()) {
            NbPreferences.forModule(FreeMarkerPanel.class).put("FreeMarkerRootPath", freemarkerRootPath.getText());
        }
    }
    
    boolean valid() {
        return new File(freemarkerRootPath.getText()).canExecute();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton browse;
    private javax.swing.JTextField freemarkerRootPath;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
