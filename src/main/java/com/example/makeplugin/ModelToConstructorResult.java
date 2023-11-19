package com.example.makeplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class ModelToConstructorResult extends AnAction {
    private final ResultMapBuilder resultMapBuilder = new ResultMapBuilder();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);


        if (psiFile == null || editor == null) return;

        PsiElement currentCaretElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
        String menuText = e.getPresentation().getText();
        ResultMapMenu resultMapMenu = ResultMapMenu.findResultMapMenu(menuText);

        if (resultMapMenu == null) return;

        PsiClass psiClass = PsiTreeUtil.getParentOfType(currentCaretElement, PsiClass.class);

        String resultMapString = resultMapBuilder.createResultMapConstructor(psiClass);
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(resultMapString), null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean visible = false;
        if (psiFile != null && editor != null) {
            visible = isClassElement(psiFile, editor);
        }
        e.getPresentation().setEnabledAndVisible(visible);
    }

    private boolean isClassElement(PsiFile psiFile, Editor editor) {
        PsiElement currentCaretElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass psiClass = PsiTreeUtil.getParentOfType(currentCaretElement, PsiClass.class);
        return psiClass != null;
    }
}
