grammar mens;
/*
@author: David Rizo (drizo@dlsi.ua.es) May, 2018
20180528: first version from kern.g4 in IM3 library
*/

@lexer::header {
}

@parser::header {
}

@rulecatch {
    // ANTLR does not generate its normal rule try/catch
    catch(RecognitionException e) {
        throw e;
    }
}

// start rule
start: (REFERENCE_RECORD referenceRecord EOL)* header (EOL record)+ EOL? (REFERENCE_RECORD EOL?)* EOF;

referenceRecord:
    composer
    |
    composerBirthAndDeadthDates
    |
    parentWork
    |
    encoder
    |
    endEncoding
    ;

// e.g. (including REFERENCE_RECORD)  !!!COM: Stravinsky, Igor Fyodorovich
composer: 'COM:' FULL_LINE_TEXT;

// e.g. (including REFERENCE_RECORD)  !!!CDT: 1882/6/17/-1971/4/6
composerBirthAndDeadthDates: 'CDT:' FULL_LINE_TEXT;

parentWork: 'OPT' parentWorkData;

// e.g. (including REFERENCE_RECORD) !!ENC: David Rizo
encoder: 'ENC' FULL_LINE_TEXT;

// e.g. (including REFERENCE_RECORD) !!!END: 2004/12/13/
endEncoding: 'END' FULL_LINE_TEXT;

// e.g. (including REFERENCE_RECORD and parentWork)  !!!OPT@ESP: La consagración de la primavera
parentWorkData: AT AT? language; // double @ is used for encoding the preferred language

language: UPPERCASE_CHARACTER UPPERCASE_CHARACTER UPPERCASE_CHARACTER;

//TODO Which other codes do we want to include?

header: headerField (TAB headerField)*;

headerField: headerMens; // in full **kern specification it includes also headerKern | headerRoot | headerHarm

headerMens: MENS;

record:
    globalComment
    |
    fields;

fields: field (TAB field)*;

// e.g. !! This is a comment that spans several spines until the end of line
globalComment:
    ASTERISK ASTERISK FULL_LINE_TEXT;

field: graphicalToken
     |
     DOT // nothing is done, it is just a placeholder
     |
     fieldComment
     ;

fieldComment: EXCLAMATION FIELD_TEXT?;

graphicalToken:
    interpretation
    |
    tandemInterpretation
    |
    barline
    |
    spineOperation
    |
    rest
    |
    note
    |
    layout
    |
    lyricsText
    ;

interpretation: INTERPRETATION FIELD_TEXT;

tandemInterpretation:
    staff
    |
    clef
    |
    keysignature
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

staff: TANDEM_STAFF NUMBER;

clef: TANDEM_CLEF clefValue;
clefValue: ('G2' | 'F2' | 'F3' | 'F4' | 'C5' | 'C4' | 'C3' | 'C2' | 'C1' | 'G1');

keysignature: TANDEM_KEY LEFT_BRACKET keysignatureNote* RIGHT_BRACKET;
keysignatureNote: LOWERCASE_PITCH_CHARACTER keyAccidental?;
keyAccidental: ('n' | OCTOTHORPE | MINUS);
keyChange: ASTERISK (minorKey | majorKey) keyAccidental? COLON;
minorKey: LOWERCASE_PITCH_CHARACTER;
majorKey: UPPERCASE_PITCH_CHARACTER;


meterSign: TANDEM_MET LEFT_PARENTHESIS meterSignValue RIGHT_PARENTHESIS;
meterSignValue: ('C' | 'c|' | 'C·' | 'O' | 'O·' | 'C32' | 'C|32' );

sectionLabel: SECTION_LABEL FIELD_TEXT;

instrument: INSTRUMENT FIELD_TEXT;

metronome: METRONOME NUMBER;

nullInterpretation: ASTERISK; // a null interpretation (placeholder) will have just an ASTERISK_FRAGMENT

barline: EQUAL+ (NUMBER alternateMeasure?)? (COLON? barlineWidth? partialBarLine? COLON?) ; // COLON = repetition mark
barlineWidth: (EXCLAMATION? PIPE EXCLAMATION?);
alternateMeasure: CHAR;
partialBarLine:
    GRAVE_ACCENT // partialBarLineTop
    |
    APOSTROPHE // partialBarLineMid
    |
    MINUS // no bar line;
	;

spineOperation: spineTerminator | spineSplit | spineJoin;
spineTerminator: ASTERISK MINUS;
spineSplit: ASTERISK CIRCUMFLEX;
spineJoin: ASTERISK CHAR_v;

rest: duration (CHAR_r) pause?;

duration: mensuralDuration dots?;

pause: SEMICOLON; // fermata

mensuralDuration: mensuralFigure coloured? mensuralPerfection?;

coloured: TILDE;

mensuralFigure: ('X'|'L'|'S'|'s'|'M'|'m'|'U'|'u');

// p=perfect, i=imperfect, I=imperfect by alteratio
mensuralPerfection: 'p'
    | 'i'
    | 'I';

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
    UPPERCASE_PITCH_CHARACTER+  // BASS
    |
	LOWERCASE_PITCH_CHARACTER+ // TREBLE
	;

alteration: OCTOTHORPE | (OCTOTHORPE OCTOTHORPE) | MINUS | (MINUS MINUS) | 'n';

// x is show, xx is shows editorial
alterationVisualMode: CHAR_x CHAR_x?;

afterNote:
	     (slurEnd | stem| tieMiddle | tieEnd| ligatureEnd | beam | pause | barLineCrossedNoteEnd)*;

//TODO SEQUENCES OF LIGATURE
//TEXT

tieStart: LEFT_BRACKET;
tieMiddle: UNDERSCORE;
tieEnd: RIGHT_BRACKET;
slurStart: LEFT_PARENTHESIS;
ligatureStart: ANGLE_BRACKET_OPEN;
ligatureEnd: ANGLE_BRACKET_CLOSE;
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

layout: LAYOUT COLON layoutCommand;

layoutCommand: layoutVisualAccidental | layoutRestPosition;

layoutVisualAccidental: 'N:va=' alteration;
layoutRestPosition: 'R:v' staffPosition;

// bottom line = L1, bottom space = S1, first bottom ledger line = L0, space between first ledger line and bottom line = S0, second bottom ledger line = L-1, first top ledger line = L6
staffPosition: lineSpace NUMBER;

lineSpace: CHAR_L | CHAR_S; // l = line, s = space

lyricsText: DOUBLE_QUOTES lyricsTextContent DOUBLE_QUOTES;

lyricsTextContent: FIELD_TEXT;

/* ---- lexer ---- */
fragment ASTERISK_FRAGMENT : '*';
fragment EXCLAMATION_FRAGMENT : '!';
fragment FIELD_TEXT_FRAGMENT: ~[\t\n\r]+;
fragment CHAR_v_FRAGMENT: 'v';
fragment CHAR_r_FRAGMENT: 'r';
fragment CHAR_L_FRAGMENT: 'L';
fragment CHAR_J_FRAGMENT: 'J';
fragment CHAR_S_FRAGMENT: 'S';
fragment CHAR_T_FRAGMENT: 'T';
fragment CHAR_t_FRAGMENT: 't';
fragment CHAR_x_FRAGMENT: 'x';
fragment UPPERCASE_PITCH_CHARACTER_FRAGMENT: 'A'..'G';
fragment UPPERCASE_CHARACTER_FRAGMENT: UPPERCASE_PITCH_CHARACTER_FRAGMENT | 'H' | 'I' | CHAR_J_FRAGMENT | CHAR_L_FRAGMENT | 'M'..'R' | CHAR_S_FRAGMENT | CHAR_T_FRAGMENT | 'U' .. 'Z';
fragment LOWERCASE_PITCH_CHARACTER_FRAGMENT: 'a'..'g';

UPPERCASE_PITCH_CHARACTER: UPPERCASE_PITCH_CHARACTER_FRAGMENT;
UPPERCASE_CHARACTER: UPPERCASE_CHARACTER_FRAGMENT;
LOWERCASE_PITCH_CHARACTER: LOWERCASE_PITCH_CHARACTER_FRAGMENT;

CHAR_J: CHAR_J_FRAGMENT;
CHAR_L: CHAR_L_FRAGMENT;
CHAR_S: CHAR_S_FRAGMENT;
CHAR_T: CHAR_T_FRAGMENT;
CHAR_t: CHAR_t_FRAGMENT;
CHAR_x: CHAR_x_FRAGMENT;

ASTERISK: ASTERISK_FRAGMENT;
EXCLAMATION: EXCLAMATION_FRAGMENT;

MENS: ASTERISK_FRAGMENT ASTERISK_FRAGMENT 'mens';
TEXT: ASTERISK_FRAGMENT ASTERISK_FRAGMENT 'text';
TANDEM_STAFF: ASTERISK_FRAGMENT 'staff';
TANDEM_CLEF: ASTERISK_FRAGMENT 'clef';
TANDEM_KEY: ASTERISK_FRAGMENT 'k';
TANDEM_MET: ASTERISK_FRAGMENT 'met';
SECTION_LABEL: ASTERISK_FRAGMENT '>';
METRONOME: ASTERISK_FRAGMENT 'MM';
INSTRUMENT: ASTERISK_FRAGMENT 'I';
INTERPRETATION: ASTERISK_FRAGMENT ASTERISK_FRAGMENT;
REFERENCE_RECORD: EXCLAMATION_FRAGMENT EXCLAMATION_FRAGMENT EXCLAMATION_FRAGMENT;
LAYOUT: EXCLAMATION_FRAGMENT 'LO';

LEFT_BRACKET: '[';
RIGHT_BRACKET: ']';
OCTOTHORPE: '#';
MINUS: '-';
EQUAL: '=';
NUMBER: ('0'..'9')+;
DOT: '.';
AT: '@';
PIPE: '|';
GRAVE_ACCENT: '`';
APOSTROPHE: '\'';
DOUBLE_QUOTES: '"';
EOL : '\r'?'\n';
CIRCUMFLEX: '^';
CHAR: ('a'..'q'| CHAR_r_FRAGMENT | 's' | CHAR_t_FRAGMENT | 'u' | CHAR_v_FRAGMENT | 'w' | CHAR_x_FRAGMENT | 'y..z');
CHAR_v: CHAR_v_FRAGMENT;
CHAR_r: CHAR_r_FRAGMENT;
TILDE: '~';
ANGLE_BRACKET_OPEN: '<';
ANGLE_BRACKET_CLOSE: '>';
SLASH: '/';
BACKSLASH: '\\';
UNDERSCORE: '_';

TAB: '\t';
LEFT_PARENTHESIS: '(';
RIGHT_PARENTHESIS: ')';
COLON: ':';
SEMICOLON: ';';
FIELD_TEXT: FIELD_TEXT_FRAGMENT;
FULL_LINE_TEXT: '~[\n\r]'+;
