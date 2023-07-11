package com.example.application_template_jmvvm.utils.objects;

import com.token.uicomponents.infodialog.InfoDialog;

public class InfoDialogData {
    private InfoDialog.InfoType type;
    private String text;

    public InfoDialogData(InfoDialog.InfoType type, String text) {
        this.type = type;
        this.text = text;
    }

    public InfoDialog.InfoType getType() {
        return type;
    }

    public void setType(InfoDialog.InfoType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
