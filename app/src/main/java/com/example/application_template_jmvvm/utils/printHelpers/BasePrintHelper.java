package com.example.application_template_jmvvm.utils.printHelpers;

import com.example.application_template_jmvvm.utils.objects.SampleReceipt;
import com.token.printerlib.PrinterDefinitions;
import com.token.printerlib.StyledString;

public class BasePrintHelper {
    static void addText(StyledString styledText, String text, PrinterDefinitions.Alignment alignment) {
        addText(styledText, text, alignment, 11, 0);
    }

    static void addText(StyledString styledText, String text, PrinterDefinitions.Alignment alignment, int fontSize, float lineSpacing) {
        styledText.setFontFace(PrinterDefinitions.Font_E.SourceCodePro);
        styledText.setFontSize(fontSize);
        styledText.setLineSpacing(lineSpacing);
        styledText.addTextToLine(text, alignment);
    }

    static void addTextToNewLine(StyledString styledText, String text, PrinterDefinitions.Alignment alignment) {
        styledText.newLine();
        addText(styledText, text, alignment);
    }

    static void addTextToNewLine(StyledString styledText, String text, PrinterDefinitions.Alignment alignment, int fontSize) {
        styledText.newLine();
        addText(styledText, text, alignment, fontSize, 0);
    }

    static void printSlipHeader(StyledString styledText, SampleReceipt receipt) {
        styledText.setLineSpacing(0.5f);
        styledText.setFontSize(12);
        styledText.setFontFace(PrinterDefinitions.Font_E.SourceSansPro);
        styledText.addTextToLine(receipt.getMerchantName(), PrinterDefinitions.Alignment.Center);
    }
}
