/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
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

package org.constellation.swing;

import org.constellation.ServiceDef;
import org.constellation.admin.service.ConstellationClient;
import org.constellation.configuration.Instance;
import org.openide.util.Exceptions;

import java.awt.*;
import java.io.IOException;

/**
 * Edit a service.
 *
 * @author Johann Sorel (geomatys)
 */
public class JServiceEditPane extends javax.swing.JPanel {

    private final ConstellationClient serverV2;
    private final String serviceType;
    private final Instance serviceInstance;
    private final JServiceEditionPane serviceEditionPanel;

    public JServiceEditPane(final ConstellationClient serverV2, final String serviceTyp, final Instance serviceInstance) throws IOException {
        this.serverV2 = serverV2;
        this.serviceType = serviceTyp.toUpperCase();
        this.serviceInstance = serviceInstance;
        Object configuration = serverV2.services.getInstanceConfiguration(ServiceDef.Specification.valueOf(serviceType), serviceInstance.getIdentifier());
        initComponents();
        guiName.setText(serviceInstance.getIdentifier());
        if ("WMS".equals(serviceType) || "WMTS".equals(serviceType) || "WCS".equals(serviceType) || "WFS".equals(serviceType)) {
            serviceEditionPanel =  new JServiceMapEditPane(serverV2, serviceType, configuration, serviceInstance.getIdentifier());
            guiInternalPane.add(BorderLayout.CENTER, serviceEditionPanel);
        } else if ("CSW".equals(serviceType)){
            serviceEditionPanel =  new JServiceCswEditPane(serverV2, serviceInstance, configuration);
            guiInternalPane.add(BorderLayout.CENTER, serviceEditionPanel);
        } else if ("WPS".equals(serviceType)){
            serviceEditionPanel =  new JServiceWpsPane(serverV2, configuration);
            guiInternalPane.add(BorderLayout.CENTER, serviceEditionPanel);
        } else if ("WEBDAV".equals(serviceType)){
            serviceEditionPanel =  new JServiceWebDavPane(configuration);
            guiInternalPane.add(BorderLayout.CENTER, serviceEditionPanel);
        } else if ("SOS".equals(serviceType)){
            serviceEditionPanel =  new JServiceSosEditPane(serverV2, serviceInstance, configuration);
            guiInternalPane.add(BorderLayout.CENTER, serviceEditionPanel);
        } else {
            serviceEditionPanel = null;
        }

    }

    public String getServiceType() {
        return serviceType;
    }

    public Instance getServiceInstance() {
        return serviceInstance;
    }

    private void correctName(){
        int pos = guiName.getCaretPosition();
        guiName.setText(guiName.getText().replace(' ', '_'));
        guiName.setCaretPosition(pos);
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
        guiDelete = new javax.swing.JButton();
        guiSave = new javax.swing.JButton();
        guiName = new javax.swing.JTextField();
        guiInternalPane = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(610, 452));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/constellation/swing/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("name")); // NOI18N

        guiDelete.setText(bundle.getString("delete")); // NOI18N
        guiDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiDeleteActionPerformed(evt);
            }
        });

        guiSave.setText(bundle.getString("save")); // NOI18N
        guiSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiSaveActionPerformed(evt);
            }
        });

        guiName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                guiNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                guiNameKeyTyped(evt);
            }
        });

        guiInternalPane.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiInternalPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(guiDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 487, Short.MAX_VALUE)
                        .addComponent(guiSave))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiName)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {guiDelete, guiSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(guiName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiInternalPane, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiDelete)
                    .addComponent(guiSave))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiSaveActionPerformed
        try {
            correctName();
            if (!serviceInstance.getIdentifier().equals(guiName.getText())) {
                serverV2.services.rename(ServiceDef.Specification.valueOf(serviceType), serviceInstance.getIdentifier(), guiName.getText());
                serviceInstance.setIdentifier(guiName.getText());
            }
            if (serviceEditionPanel != null) {
                serverV2.services.setInstanceConfiguration(ServiceDef.Specification.fromShortName(serviceType), serviceInstance.getIdentifier(), serviceEditionPanel.getConfiguration());
            }
            serverV2.services.start(ServiceDef.Specification.valueOf(serviceType), serviceInstance.getIdentifier());
            firePropertyChange("update", 0, 1);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_guiSaveActionPerformed

    private void guiDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiDeleteActionPerformed
        try {
            serverV2.services.delete(ServiceDef.Specification.valueOf(serviceType), serviceInstance.getIdentifier());
            firePropertyChange("update", 0, 1);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_guiDeleteActionPerformed

    private void guiNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_guiNameKeyTyped
        correctName();
    }//GEN-LAST:event_guiNameKeyTyped

    private void guiNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_guiNameKeyPressed
        correctName();
    }//GEN-LAST:event_guiNameKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton guiDelete;
    private javax.swing.JPanel guiInternalPane;
    private javax.swing.JTextField guiName;
    private javax.swing.JButton guiSave;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables



}
