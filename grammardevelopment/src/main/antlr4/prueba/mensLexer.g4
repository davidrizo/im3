lexer grammar mensLexer;

@lexer::header {
import java.util.ArrayList;
}

// Non context free grammar needs semantic predicates to handle text spines
@lexer::members {
    // record whether each spine is **text
    private ArrayList<Boolean> textSpines = new ArrayList<>();
    private int currentSpine;

    public boolean inTextSpine() {
        if (currentSpine >= textSpines.size()) {
            return false;
        } else {
            return textSpines.get(currentSpine);
        }
    }
    private void incSpine() {
        currentSpine++;
        System.out.print("New spine #" + currentSpine);
        if (inTextSpine()) {
            pushMode(FREE_TEXT);
            System.out.println("(FREE_TEXT)");
        } else {
            mode(0);
            System.out.println("(MODE 0)");
        }
    }
    private void splitSpine() {
        textSpines.add(currentSpine, inTextSpine());
    }
    private void joinSpine() {
        textSpines.remove(currentSpine);
    }

    private void resetMode() {
        mode(0);
    }

    private void resetSpineAndMode() {
        System.out.println("New line");
        resetMode();
        currentSpine=-1; // incSpine increments it
        incSpine();
    }
    public void addMensSpine() {
        textSpines.add(false);
    }
    public void addTextSpine() {
        textSpines.add(true);
    }

}


fragment ASTERISK_FRAGMENT : '*';
fragment EXCLAMATION_FRAGMENT : '!';

//fragment REFERENCE_RECORD: EXCLAMATION_FRAGMENT EXCLAMATION_FRAGMENT EXCLAMATION_FRAGMENT;

MENS: ASTERISK_FRAGMENT ASTERISK_FRAGMENT 'mens' {addMensSpine();};
TEXT: ASTERISK_FRAGMENT ASTERISK_FRAGMENT 'text' {addTextSpine();};

ASTERISK: ASTERISK_FRAGMENT;
DOT: '.';

// with pushMode, the lexer uses the rules below FREE_TEXT
TAB: '\t' {incSpine();}; // incSpine changes mode depending on the spine type
EOL : '\r'?'\n' {resetSpineAndMode();};

FIELD_COMMENT: EXCLAMATION_FRAGMENT -> mode(FREE_TEXT);

mode FREE_TEXT;
FIELD_TEXT: ~[\t\n\r]+ -> mode(0); // must reset mode here to let lexer recognize the tab or newline
