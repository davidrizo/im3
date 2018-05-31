/*
@author: David Rizo (drizo@dlsi.ua.es) May, 2018
20180528: first version from kern.g4 in IM3 library
*/
parser grammar mensParser;
options { tokenVocab=mensLexer; } // use tokens from mensLexer.g4

@parser::header {
}

@rulecatch {
    // ANTLR does not generate its normal rule try/catch
    catch(RecognitionException e) {
        throw e;
    }
}

// Non context free grammar needs semantic predicates to handle text spines
@parser::members {
    // record whether each spine is **text
    private ArrayList<Boolean> textSpines = new ArrayList<>();
    private int currentSpine;
    public boolean inTextSpine() {
        return textSpines.get(currentSpine);
    }
    private void incSpine() {
        currentSpine++;
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
}



// start rule
//start: (referenceRecord EOL)* header (EOL record)+ EOL? (referenceRecord EOL?)* EOF;
start: header (EOL record)+ EOL? EOF;

/*referenceRecord:
    composer
    |
    encoder
    |
    endEncoding
    ;

// e.g. (including REFERENCE_RECORD)  !!!COM: Stravinsky, Igor Fyodorovich
composer: REFERENCE_RECORD_COMPOSER FULL_LINE_TEXT;

// e.g. (including REFERENCE_RECORD) !!ENC: David Rizo
encoder: REFERENCE_RECORD_ENCODER FULL_LINE_TEXT;

// e.g. (including REFERENCE_RECORD) !!!END: 2004/12/13/
endEncoding: REFERENCE_RECORD_ENDENCODING FULL_LINE_TEXT;

//TODO Which other codes do we want to include?
*/
header: headerField (TAB headerField)*;

headerField: headerMens; // in full **kern specification it includes also headerKern | headerRoot | headerHarm

headerMens: MENS;

record
@before {resetSpine();}
    :
    globalComment
    |
    fields;

fields: field (TAB field)*;

// e.g. !! This is a comment that spans several spines until the end of line
globalComment:
    GLOBAL_COMMENT FIELD_TEXT?;

field
@after {incSpine(); }
    :
    graphicalToken
     |
     placeHolder // nothing is done, it is just a placeholder
     |
     fieldComment
     ;

placeHolder: DOT;

fieldComment: FIELD_COMMENT FIELD_TEXT?;

graphicalToken:
    tandemInterpretation
    |
    barLine
    |
    layout
    |
    rest
    |
    note
    |
    spineOperation
    /*|
    lyricsText*/
    ;

tandemInterpretation:
    staff
    |
    clef
    |
    keySignature
    |
    meterSign
    |
    keyChange
    |
    sectionLabel
    |
    instrument
    |
    metronome
    |
    nullInterpretation
    ;

number: (DIGIT_1 | DIGIT_2 | DIGIT_3 | DIGIT_4 | DIGIT_5 | DIGIT_6 | DIGIT_7 | DIGIT_8 | DIGIT_9)+;
lowerCasePitch: LOWERCASE_PITCH_CHARACTER;
upperCasePitch: CHAR_A | CHAR_B | CHAR_C | CHAR_D | CHAR_E | CHAR_F | CHAR_G;

staff: TANDEM_STAFF number;

clef: TANDEM_CLEF clefValue;
clefValue: clefNote clefLine;
clefNote: CHAR_C | CHAR_F | CHAR_G;
clefLine: DIGIT_1 | DIGIT_2 | DIGIT_3 | DIGIT_4 | DIGIT_5;

keySignature: TANDEM_KEY LEFT_BRACKET keySignatureNote* RIGHT_BRACKET;
keySignatureNote: lowerCasePitch keyAccidental?;

keyAccidental: (CHAR_n | OCTOTHORPE | MINUS);
keyChange: ASTERISK (minorKey | majorKey) keyAccidental? COLON;
minorKey: lowerCasePitch;
majorKey: upperCasePitch;


meterSign: TANDEM_MET LEFT_PARENTHESIS meterSignValue RIGHT_PARENTHESIS;
meterSignValue: CHAR_C | CHAR_C PIPE | CHAR_C CENTER_DOT | CHAR_O | CHAR_O CENTER_DOT | CHAR_C DIGIT_3 DIGIT_2 | CHAR_C PIPE DIGIT_3 DIGIT_2;

sectionLabel: SECTION_LABEL FIELD_TEXT;

instrument: INSTRUMENT FIELD_TEXT;

metronome: METRONOME number;

nullInterpretation: ASTERISK; // a null interpretation (placeholder) will have just an ASTERISK_FRAGMENT

//barline: EQUAL+ (NUMBER)? (COLON? barlineWidth? partialBarLine? COLON?) ; // COLON = repetition mark
barLine: EQUAL+ number? (COLON? partialBarLine? COLON?); // COLON = repetition mark

//barlineWidth: (EXCLAMATION? PIPE EXCLAMATION?);

partialBarLine:
    GRAVE_ACCENT // partialBarLineTop
    |
    APOSTROPHE // partialBarLineMid
    |
    MINUS // no bar line;
	;

spineOperation:
    spineTerminator
     |
     spineSplit {splitSpine();}
     |
     spineJoin {joinSpine(); }
     ;

spineTerminator: ASTERISK MINUS;
spineSplit: ASTERISK CIRCUMFLEX;
spineJoin: ASTERISK CHAR_v;

rest: duration (CHAR_r) pause?;

duration: mensuralDuration dots?;

pause: SEMICOLON; // fermata

mensuralDuration: mensuralFigure coloured? mensuralPerfection?;

coloured: TILDE;

mensuralFigure: CHAR_X | CHAR_L | CHAR_S | CHAR_s | CHAR_M | CHAR_m | CHAR_U | CHAR_u;

// p=perfect, i=imperfect, I=imperfect by alteratio
mensuralPerfection: CHAR_p | CHAR_i | CHAR_I;

dots: augmentationDots | graphicalDot;

augmentationDots: DOT+;

graphicalDot: COLON;

note:  beforeNote duration noteName (alteration alterationVisualMode?)? afterNote;

beforeNote:  //TODO Regla semantica (boolean) para que no se repitan
    (slurStart
    | tieStart
    | ligatureStart
    | barLineCrossedNoteStart
    )*
    ;

noteName:
    bassNotes // BASS
    |
    trebleNotes
	;

trebleNotes: lowerCasePitch+;
bassNotes: upperCasePitch+;

alteration: OCTOTHORPE | (OCTOTHORPE OCTOTHORPE) | MINUS | (MINUS MINUS) | CHAR_n;

// x is show, xx is shows editorial
alterationVisualMode: CHAR_x CHAR_x?;

afterNote:
	     (slurEnd | stem| tieMiddle | tieEnd | ligatureType | ligatureEnd | beam | pause | barLineCrossedNoteEnd)*;

//TODO SEQUENCES OF LIGATURE
//TEXT

tieStart: LEFT_BRACKET;
tieMiddle: UNDERSCORE;
tieEnd: RIGHT_BRACKET;
slurStart: LEFT_PARENTHESIS;
ligatureStart: ANGLE_BRACKET_OPEN;
ligatureEnd: ANGLE_BRACKET_CLOSE;
ligatureType: CHAR_R | CHAR_Q;
slurEnd: RIGHT_PARENTHESIS;
barLineCrossedNoteStart: CHAR_T;
barLineCrossedNoteEnd: CHAR_t;

stem:
    SLASH  // STEM_UP
    |
    BACKSLASH // STEM_DOWN;
    ;

beam:
    CHAR_L //BEAM_START
    |
    CHAR_J; // BEAM_END

layout: LAYOUT layoutCommand;

layoutCommand: layoutVisualAccidental | layoutRestPosition;

layoutVisualAccidental: LAYOUT_NOTE_VISUAL_ACCIDENTAL alteration;
layoutRestPosition: LAYOUT_REST_POSITION staffPosition;

// bottom line = L1, bottom space = S1, first bottom ledger line = L0, space between first ledger line and bottom line = S0, second bottom ledger line = L-1, first top ledger line = L6
staffPosition: lineSpace number;

lineSpace: CHAR_L | CHAR_S; // l = line, s = space

lyricsText: {inTextSpine()}? FIELD_TEXT;


