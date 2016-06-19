package org.metelsky.intellij.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeConvertAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        List<Caret> carets = editor.getCaretModel().getAllCarets();
        final Document document = editor.getDocument();
        for (int i = 0; i < carets.size(); i++) {
            convertTime(project, document, carets.get(i));
        }
    }

    private void convertTime(final Project project, final Document document, final Caret caret) {
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        String time = document.getText(new TextRange(caret.getSelectionStart(), caret.getSelectionEnd()));
                        if (time.matches("^\\d+$")) {
                            Date date = new Date(Long.parseLong(time) * 1000L);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                            sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                            time = sdf.format(date);
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                            long parsedUnix = 0;
                            try {
                                parsedUnix = sdf.parse(time).getTime() / 1000L;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            time = parsedUnix + "";
                        }
                        document.replaceString(caret.getSelectionStart(), caret.getSelectionEnd(), time);
                    }
                });
            }
        }, "convertTime", document, UndoConfirmationPolicy.DEFAULT, document);
    }
}