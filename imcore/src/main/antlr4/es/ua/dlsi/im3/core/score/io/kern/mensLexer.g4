lexer grammar mensLexer;

@lexer::header {
import java.util.ArrayList;
}

// Non context free grammar needs semantic predicates to handle text spines
@lexer::members {
    // record whether each spine is **text
    private ArrayList<Boolean> textSpines = new ArrayList<>();
    private int currentSpine;
    private boolean inTextMode = false;
    public boolean inTextSpine() {
        if (currentSpine >= textSpines.size()) {
            return false;
        } else {
            return textSpines.get(currentSpine);
        }
    }
    private void incSpine() {
        currentSpine++;
        if (inTextSpine()) {
            pushMode(FREE_TEXT);
            inTextMode = true;
        } else if (inTextMode) {
            popMode();
            inTextMode = false;
        }
    }
    private void splitSpine() {
        textSpines.add(currentSpine, inTextSpine());
    }
    private void joinSpine() {
        textSpines.remove(currentSpine);
    }
    private void resetSpine() {
        currentSpine=0;
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
TANDEM_STAFF: ASTERISK_FRAGMENT 'staff';
TANDEM_CLEF: ASTERISK_FRAGMENT 'clef';
TANDEM_KEY: ASTERISK_FRAGMENT 'k';
TANDEM_MET: ASTERISK_FRAGMENT 'met';
METRONOME: ASTERISK_FRAGMENT 'MM';


CHAR_A: 'A';
CHAR_B: 'A';
CHAR_C: 'C';
CHAR_D: 'D';
CHAR_E: 'E';
CHAR_F: 'F';
CHAR_G: 'G';
CHAR_I: 'I';
CHAR_J: 'J';
CHAR_L: 'L';
CHAR_M: 'M';
CHAR_O: 'O';
CHAR_P: 'P';
CHAR_Q: 'Q';
CHAR_R: 'R';
CHAR_S: 'S';
CHAR_X: 'X';
CHAR_T: 'T';
CHAR_U: 'U';
CHAR_i: 'i';
CHAR_m: 'm';
CHAR_n: 'n';
CHAR_p: 'p';
CHAR_r: 'r';
CHAR_s: 's';
CHAR_t: 'r';
CHAR_u: 'u';
CHAR_v: 'v';
CHAR_x: 'x';

LOWERCASE_PITCH_CHARACTER: 'a' .. 'g';

DIGIT_1: '1';
DIGIT_2: '2';
DIGIT_3: '3';
DIGIT_4: '4';
DIGIT_5: '5';
DIGIT_6: '6';
DIGIT_7: '7';
DIGIT_8: '8';
DIGIT_9: '9';

ASTERISK: ASTERISK_FRAGMENT;

LEFT_BRACKET: '[';
RIGHT_BRACKET: ']';
OCTOTHORPE: '#';
MINUS: '-';
EQUAL: '=';
CENTER_DOT: '·';
DOT: '.';
PIPE: '|';
GRAVE_ACCENT: '`';
APOSTROPHE: '\'';
CIRCUMFLEX: '^';
//CHAR: ('a'..'l'| CHAR_m_FRAGMENT | CHAR_n_FRAGMENT | 'o'..'q' | CHAR_r_FRAGMENT | CHAR_s_FRAGMENT | CHAR_t_FRAGMENT | CHAR_u_FRAGMENT | CHAR_v_FRAGMENT | 'w' | CHAR_x_FRAGMENT | 'y..z');
TILDE: '~';
ANGLE_BRACKET_OPEN: '<';
ANGLE_BRACKET_CLOSE: '>';
SLASH: '/';
BACKSLASH: '\\';
UNDERSCORE: '_';

LEFT_PARENTHESIS: '(';
RIGHT_PARENTHESIS: ')';
COLON: ':';
SEMICOLON: ';';

// with pushMode, the lexer uses the rules below FREE_TEXT
TAB: '\t' {incSpine();}; // incSpine changes mode depending on the spine type
EOL : '\r'?'\n' {resetSpine();};

//REFERENCE_RECORD_COMPOSER: REFERENCE_RECORD 'COM'  -> pushMode(FREE_TEXT);
//REFERENCE_RECORD_PARENT_WORK: REFERENCE_RECORD 'OPT'  -> pushMode(FREE_TEXT);
//REFERENCE_RECORD_ENCODER: REFERENCE_RECORD 'ENC'  -> pushMode(FREE_TEXT);
//REFERENCE_RECORD_ENDENCODING: REFERENCE_RECORD 'END'  -> pushMode(FREE_TEXT);

SECTION_LABEL: ASTERISK_FRAGMENT '>' -> pushMode(FREE_TEXT);
INSTRUMENT: ASTERISK_FRAGMENT 'I'  -> pushMode(FREE_TEXT);
LAYOUT: EXCLAMATION_FRAGMENT 'LO:' -> pushMode(LAYOUT_MODE);
FIELD_COMMENT: EXCLAMATION_FRAGMENT -> pushMode(FREE_TEXT);
GLOBAL_COMMENT: EXCLAMATION_FRAGMENT EXCLAMATION_FRAGMENT .*? '\n' -> skip ;


mode FREE_TEXT;
FIELD_TEXT: ~[\t\n\r]+  -> popMode;

mode LAYOUT_MODE;
LAYOUT_NOTE_VISUAL_ACCIDENTAL: 'N:va='  -> popMode;
LAYOUT_REST_POSITION: 'R:v='  -> popMode;
