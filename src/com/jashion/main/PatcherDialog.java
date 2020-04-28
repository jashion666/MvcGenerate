package com.jashion.main;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class PatcherDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField;
    private AnActionEvent event;
    private JBList fieldList;

    PatcherDialog(final AnActionEvent event) {
        this.event = event;
        setTitle("Create Patcher Dialog");

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // 保存路径按钮事件
//        fileChooseBtn.addActionListener(e -> {
//            String userDir = System.getProperty("user.home");
//            JFileChooser fileChooser = new JFileChooser(userDir + "/Desktop");
//            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            int flag = fileChooser.showOpenDialog(null);
//            if (flag == JFileChooser.APPROVE_OPTION) {
//                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
//            }
//        });
    }

    private void onOK() {
        // 条件校验
        if (null == textField.getText() || "".equals(textField.getText())) {
            Messages.showErrorDialog(this, "Please Select Save Path!", "Error");
            return;
        }
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        try {

            if (file != null) {
                // 获取指定的目录
                String path = file.getPath() + "\\" + textField.getText();
                String text = getText(textField.getText());
                // 创建包
                Files.createDirectories(Paths.get(path));
                // 创建 Controller和文件
                Files.createDirectories(Paths.get(path + "\\controller"));
                createJavaFile(path + "\\controller", text + "Controller.java", getControllerTemplate(text));

                Files.createDirectories(Paths.get(path + "\\mapper"));
                createJavaFile(path + "\\mapper", text + "Mapper.java", getMapperTemplate(text));

                Files.createDirectories(Paths.get(path + "\\service"));
                createJavaFile(path + "\\service", text + "Service.java", getServiceTemplate(text));

                Files.createDirectories(Paths.get(path + "\\service\\impl"));
                createJavaFile(path + "\\service\\impl", text + "ServiceImpl.java", getServiceImplTemplate(text));

                Files.createDirectories(Paths.get(path + "\\model"));
                createJavaFile(path + "\\model", text + "Model.java", getModelTemplate(text));
            }
        } catch (Exception e) {
            Messages.showErrorDialog(this, "Create Patcher Error!", "Error");
            e.printStackTrace();
        }
        dispose();
    }

    private void createJavaFile(String path, String text, String template) throws IOException {
        String createPath = path + "\\" + text;
        Files.createFile(Paths.get(createPath));
        Files.write(Paths.get(createPath), template.getBytes());
    }

    private String getControllerTemplate(String text) {
        text = text + "Controller";
        return "package com.example.controller; \r\n \r\n" +
                "public class " + text + "{}\r\n";
    }

    private String getMapperTemplate(String text) {
        text = text + "Mapper";
        return "package com.example.mapper; \r\n \n" +
                "public interface " + text + "{}\r\n";
    }

    private String getServiceTemplate(String text) {
        text = text + "Service";
        return "package com.example.service; \r\n \n" +
                "public interface " + text + "{}\r\n";
    }

    private String getServiceImplTemplate(String text) {
        String service = text + "Service";
        String serviceImpl = text + "ServiceImpl";

        return "package com.example.service.impl; \r\n \n" +
                "public class " + serviceImpl + " implements " + service + " {}\r\n";
    }

    private String getModelTemplate(String text) {
        text = text + "Model";
        return "package com.example.model; \r\n \r\n" +
                "public class " + text + "{}\r\n";
    }

    private String getText(String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase());
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        VirtualFile[] data = event.getData(DataKeys.VIRTUAL_FILE_ARRAY);
        fieldList = new JBList(data);
        fieldList.setEmptyText("No File Selected!");
    }


    private static class FindJavaVisitor extends SimpleFileVisitor<Path> {
        private List<Path> result;

        FindJavaVisitor(List<Path> result) {
            this.result = result;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            result.add(file.toAbsolutePath());
            return FileVisitResult.CONTINUE;
        }
    }
}
