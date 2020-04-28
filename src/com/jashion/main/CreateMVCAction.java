package com.jashion.main;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author :wkh.
 * @date :2020/4/28.
 */
public class CreateMVCAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PatcherDialog dialog = new PatcherDialog(e);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();
    }
}
