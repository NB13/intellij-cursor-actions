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

import java.util.List;

public class NumerateAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        List<Caret> carets = editor.getCaretModel().getAllCarets();
        final Document document = editor.getDocument();
        for( int i = 0; i < carets.size(); i++ ){
            numerateCaret(project, document, carets.get(i), i);
        }
    }

    private void numerateCaret(final Project project, final Document document, final Caret caret, final int caretID) {
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        document.replaceString(caret.getSelectionStart(), caret.getSelectionEnd(), caretID + "");
                    }
                });
            }
        }, "numerate", document, UndoConfirmationPolicy.DEFAULT, document);
    }
}