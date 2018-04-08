grammar kern;
/*
Don't use rules for lexer literals based on letters ('a'...) because they are ambiguos and
depend on the parser -- TODO Use fragments

TO-DO See this website for a solution:
https://stackoverflow.com/questions/28873463/no-context-sensitivity-in-antlr4

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

song: (METADATACOMMENT EOL)* header (EOL record)+ EOL? (METADATACOMMENT EOL?)* endOfFile;

endOfFile: EOF;

//TODO Comentarios de varias líneas en la cabecera, mejor hacer como los comentarios de java, quitarlos en el lexico

// with the number of elements we can initialize the number of spines
header: headerField (TAB headerField)*; //TODO Habilitar aqui las comprobaciones semanticas

headerField: headerKern | headerRoot | headerHarm | headerMens;

headerKern: KERN;

headerRoot: ROOT;

headerHarm: HARM;

headerMens: MENS;

record: (field (TAB field)*); 

//field: (graphicalToken editorialToken?) | '.';
field: graphicalToken
     | DOT // nothing is done, it is just a placeholder
     | EXCLAMATION_SIGN // empty comment
     | fieldComment
     ;

fieldComment: FIELDCCOMMENT;
//repeatToken: 

//graphicalToken: interpretation | tandemInterpretation | spineOperations | localComment | note | rest | barline;
graphicalToken: interpretation | tandemInterpretation | noteRestChord | barline | spineOperations | harm;

interpretation: INTERPRETATION;


tandemInterpretation:
    (TANDEM_CLEF clef) //| meter | key | metronome | instrument | instrumentClass | instrumentGroup;
    | (TANDEM_KEY LEFTBRACKET keysignature RIGHTBRACKET)
    | (TANDEM_METER meter)
    | (TANDEM_MET LEFTPAR meterSign RIGHTPAR)
    | (TANDEM_STAFF staff)
    | (ASTERISK keyChange COLON)
    | (UNKNOWN_KEY  // unknown key
    | ATONAL_PASSAGE // atonal passage
    | SECTIONLABEL // section labels
    | INSTRUMENT //TODO Add a ScorePart for each instrument
    | METRONOME
    | ASTERISK) // a null interpretation (placeholder) will have just an asterisk
    ;

meterSign: ('C' | 'c' | 'C|' | 'c|'); //TODO Como fragment
keyChange: (minorKey | majorKey) keyAccidental?;

//keyAccidental: (LETTER_n | OCTOTHORPE | MINUS);
keyAccidental: ('n' | OCTOTHORPE | MINUS);

//minorKey: LETTER_NOTES_LOWERCASE;
//minorKey: LETTER_NOTES_LOWERCASE; // 'a' | 'b' | 'c' | 'd' |'e' | 'f' | 'g'; //TODO ÀPor qu no va usando la regla esta?
minorKey: noteNameLowerCase;
//majorKey: LETTER_NOTES_UPPERCASE;
majorKey: noteNameUpperCase;

noteNameLowerCase: ('a'|'b'|'c'|'d'|'e'|'f'|'g');
noteNameUpperCase: ('A'|'B'|'C'|'D'|'E'|'F'|'G');

staff: NUMBER;

//TODO change C1....
clef: G2 | F2 | F3 | F4 | C5 | C4 | C3 | C2 | C1 | G1 | Gv2;

keysignature: keysignatureNote*; // natural, sharp, flat

//keysignatureNote: LETTER_NOTES_LOWERCASE keyAccidental?;
keysignatureNote: noteNameLowerCase keyAccidental?;

meter:
       meterKnown
       |
       (QUESTIONMARK // meterUknown
       |
       //LETER_X) //meterAmetric
	'X') //meterAmetric
       ;

meterKnown: numerator SLASH denominator;
//numerator: NUMBER ('+' NUMBER)*;
numerator: NUMBER (PLUS NUMBER)*;

denominator: NUMBER;

//duration: figure editorialTokenSignifier? ('.'* editorialTokenSignifier?);
duration: (modernDuration | mensuralDuration) augmentationDots;
augmentationDots: (DOT)*;
modernDuration: NUMBER;
mensuralDuration: ('X'|'L'|'S'|'s'|'M'|'m'|'U'|'u');


noteRestChord: chord | note | rest;


//see http://humdrum.org/Humdrum/representations/harm.rep.html
//Chord identifications may be characterized as (1) explicit, (2) implied, or (3) alternate

//note: duration pitch editorialTokenSignifier?;
chord: note (SPACE note)+;

note:  beforeNote duration noteName alteration? afterNote;

beforeNote:  //TODO Regla semantica (boolean) para que no se repitan
    (slurstart
    | tiestart)*
    ;

afterNote://TODO Regla semantica (boolean) para que no se repitan
	     (slurend | stem| tiemiddle | tieend| mordent| trill | beam| pause| partialbeam | tenuto)*;

tiestart: LEFTBRACKET;
slurstart: LEFTPAR;
slurend: RIGHTPAR;
tiemiddle: UNDERSCORE;
tieend: RIGHTBRACKET;
//partialbeam: LETTER_k;
partialbeam: 'k';
tenuto: TILDE;

//alteration: OCTOTHORPE | (OCTOTHORPE OCTOTHORPE) | MINUS | (MINUS MINUS) | LETTER_n;
alteration: OCTOTHORPE | (OCTOTHORPE OCTOTHORPE) | MINUS | (MINUS MINUS) | 'n';

stem:
    SLASH  // STEM_UP
    |
    BACKSLASH // STEM_DOWN;
    ;

beam:
    //LETTER_L //BEAM_START
	'L'
    |
    //LETTER_J; // BEAM_END
    'J';

mordent:
    //LETTER_W // MORDENT_INVERTED_TONE
	   'W'
    //LETTER_w?; // MORDENT_INVERTED_SEMITONE
       'w'?;

trill:
	 'T'
     |
     't';

noteName:  // todo - comprobar con semantico que todas las letras son iguales
    //(LETTER_NOTES_UPPERCASE+)  // BASS
	    (noteNameUpperCase+)	#bassNotes
    |
    //(LETTER_NOTES_LOWERCASE+)
	(noteNameLowerCase+)    #trebleNotes
	; // TREBLE

//rest: duration (LETTER_r LETTER_r?) pause?; // TODO the rr is used to denote whole measure rest
rest: duration ('r' 'r'?) pause?; // TODO the rr is used to denote whole measure rest -- see guide02-example2-2

pause: SEMICOLON; // fermata


barline: EQUAL_SIGN+ (NUMBER alternateMeasure?)? (COLON? barlineWidth? partialBarLine? COLON?) ; // COLON = repetition mark
//barlineWidth: (EXCLAMATION_SIGN PIPE_SIGN) | (PIPE_SIGN EXCLAMATION_SIGN) | (EXCLAMATION_SIGN | PIPE_SIGN);
barlineWidth: (EXCLAMATION_SIGN? PIPE_SIGN EXCLAMATION_SIGN?);
alternateMeasure: LETTER;
//alternateMeasure: ('a'..'z');
//repetitionMark: COLON;
partialBarLine:  
    GRAVE_ACCENT // partialBarLineTop 
    | APOSTROPHE // partialBarLineMid 
    | MINUS // no bar line;
	      ;


//globalComment: GLOBALCOMMENT;

spineOperations: spineTerminator | spineSplit | spineJoin; // terminator
spineTerminator: ASTERISK MINUS;
spineSplit: ASTERISK CIRCUMFLEX;
spineJoin: '*v'; // 'v' literal here to avoid confusion with 'v' degree //TODO Se puede hacer con fragment

///// ----------- HARM ---------
harm: harmChord alternateHarm? pause?;

harmChord: harmSpecification (SLASH harmSpecification)*;

harmSpecification: (romanNumberChordSpecification | specialChord | nonFunctionalChord | implicitChordSpecification);

implicitChordSpecification: LEFTPAR harmSpecification RIGHTPAR;

alternateHarm: LEFTBRACKET harmChord RIGHTBRACKET;

romanNumberChordSpecification: root extensions? inversion?;

root: rootAlteration? degree;

degree: majorDegree | minorDegree | augmentedDegree | diminishedDegree;

majorDegree: ('I' | 'II' | 'III' | 'IV' | 'V' | 'VI' | 'VII');

minorDegree: ('i' | 'ii' | 'iii' | 'iv' | 'v' | 'vi' | 'vii');

augmentedDegree: majorDegree PLUS;

diminishedDegree: minorDegree DIMINISHED;

rootAlteration: OCTOTHORPE | MINUS;

inversion: FIRSTINVERSION | SECONDINVERSION | THIRDINVERSION | FOURTHINVERSION | FIFTHINVERSION | SIXTHINVERSION;

extensions: extension+;

extension: intervalQuality NUMBER | NUMBER | NUMBER intervalQuality;

intervalQuality: MAJOR_INTERVAL | MINOR_INTERVAL | PERFECT_INTERVAL | DIMINISHED_INTERVAL DIMINISHED_INTERVAL? | AUGMENTED_INTERVAL AUGMENTED_INTERVAL?;

specialChord: TILDE? specialChordType inversion?;

specialChordType: (NEAPOLITAN | ITALIAN | FRENCH | 'Gr' | TRISTAN); // cannot use a TERMINAL because it crashes with a natural G

nonFunctionalChord: NONFUNCTIONALCHORD degree extensions?;


/// Lexer

KERN: '**kern';
MENS: '**mens';
ROOT: '**root';
HARM: '**harm';
INTERPRETATION: '**'TEXT;
SECTIONLABEL: '*>'TEXT;
INSTRUMENT: '*I'TEXT;
METRONOME: '*MM'NUMBER;
TANDEM_CLEF: '*clef';
TANDEM_KEY: '*k';
TANDEM_STAFF: '*staff';
TANDEM_MET: '*met';
TANDEM_METER: '*M';
ATONAL_PASSAGE: '*X:';
UNKNOWN_KEY: '*?:';
ASTERISK: '*';
EXCLAMATION_SIGN: '!';
METADATACOMMENT: '!!!' COMMENTTEXT;


//GLOBALCOMMENT: '!!' ~[\n\r];
NONFUNCTIONALCHORD: '?-';
QUESTIONMARK: '?';
EQUAL_SIGN: '=';
PIPE_SIGN: '|';
GRAVE_ACCENT: '`'; 
APOSTROPHE: '\'';
BACKSLASH: '\\';
UNDERSCORE: '_';
SPACE: ' ';
TAB: '\t';
DOT: '.';
LEFTPAR: '(';
RIGHTPAR: ')';
LEFTBRACKET: '[';
RIGHTBRACKET: ']';
SLASH: '/';
OCTOTHORPE: '#';
MINUS: '-';
COLON: ':';
SEMICOLON: ';';

G2:'G2';
F4:'F4';
F3:'F3';
F2:'F2';
C5:'C5';
C4:'C4';
C3:'C3';
C2:'C2';
C1:'C1';
G1:'G1';
Gv2: 'Gv2';

CIRCUMFLEX: '^';


EDITORIALSIGNIFIER
    :
	'y' -> skip //TODO 'X' que es tambin sin Mtrica
    ;
    // y = the previous disappears, yy = the whole graphicalToken disappears
    // X = to force accidentals or whatever to be shown
//	('y'Ê| 'Y' | 'x') -> skip //TODO 'X' que es tambin sin Mtrica


//// ------------------ HARM SPECIFIC
DIMINISHED	:	'o';
PLUS	:	'+';
FIRSTINVERSION: 'a';
SECONDINVERSION: 'b';
THIRDINVERSION: 'c';
FOURTHINVERSION: 'd';
FIFTHINVERSION: 'e';
SIXTHINVERSION: 'f';
MAJOR_INTERVAL: 'M';
MINOR_INTERVAL: 'm';
PERFECT_INTERVAL: 'P';
AUGMENTED_INTERVAL: 'A';
DIMINISHED_INTERVAL: 'D';
NEAPOLITAN: 'N';
ITALIAN: 'Lt';
FRENCH: 'Fr';
//GERMAN: 'Gn'; crashes with a natural G
TRISTAN: 'Tr';
//ENHARMONIC: '~';
TILDE: '~';

//////--------- END HARM

// most generic rules
LETTER: ('a'..'w'| 'z'); // 'x' and 'y' are editorial signifiers

EOL : '\r'?'\n';


NUMBER: ('0'..'9')+;

// discard comments
LINE_COMMENT
    :   ('!!' ~('\n'|'\r')* '\r'? '\n')
	-> channel(HIDDEN) // or  -> skip
    ;

FIELDCCOMMENT: EXCLAMATION_SIGN COMMENTTEXT?;

fragment COMMENTTEXT: ~[\t\n\r!|]+ ; // | and ! to avoid confusing a comment with a bar line
fragment TEXT: ~[\t\n\r]+ ;




// TODO Repetitions
// Pag. 378 Beyonjd Midi Codes, Staff Lining, Staff Possition
// Pag. 381 Pitch transposition
// Pag. 382 Octave transposition
// TODO Comprobar que todos los barlines - meter - key  ..... estanáalineados, eso lo podemos hacer con una regla de repeticion

 