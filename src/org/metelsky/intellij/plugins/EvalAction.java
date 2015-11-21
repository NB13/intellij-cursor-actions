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
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.util.List;

public class EvalAction extends AnAction {
    private static final JexlEngine jexl = new JexlEngine();

    static {
        jexl.setCache(512);
        jexl.setLenient(false);
        jexl.setSilent(false);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        List<Caret> carets = editor.getCaretModel().getAllCarets();
        final Document document = editor.getDocument();
        for (Caret caret : carets) {
            evalCaret(project, document, caret);
        }
    }

    private void evalCaret(final Project project, final Document document, final Caret caret) {
        final String selectedText = caret.getSelectedText();

        String evalResult;
        try {
            Expression e = jexl.createExpression(selectedText);
            JexlContext context = new MapContext();
            evalResult = e.evaluate(context).toString();
            evalResult = evalResult.replaceAll("\\.0$", "");
        } catch (Exception e) {
            evalResult = "Eval error";
        }
        final String replaceText = evalResult;
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        document.replaceString(caret.getSelectionStart(), caret.getSelectionEnd(), replaceText);
                    }
                });
            }
        }, "eval", document, UndoConfirmationPolicy.DEFAULT, document);
    }
}