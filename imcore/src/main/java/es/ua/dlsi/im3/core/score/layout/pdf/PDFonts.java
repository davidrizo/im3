package es.ua.dlsi.im3.core.score.layout.pdf;

import org.apache.pdfbox.pdmodel.font.PDFont;

public class PDFonts {
    private final PDFont musicFont;
    private final PDFont textFont;

    public PDFonts(PDFont musicFont, PDFont textFont) {
        this.musicFont = musicFont;
        this.textFont = textFont;
    }

    public PDFont getMusicFont() {
        return musicFont;
    }

    public PDFont getTextFont() {
        return textFont;
    }
}
