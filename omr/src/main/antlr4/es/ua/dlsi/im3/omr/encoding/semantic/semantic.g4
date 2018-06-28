grammar semantic;
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

sequence: symbol (SEP symbol)* SEP? newLine?;

newLine: NEWLINE;

symbol:
    clef
    |
    timeSignature
    |
    keySignature
    |
    note
    |
    tie
    |
    barline
    |
    rest
    |
    multirest;

//TODO clef: CLEF SEPSYMBOL CLEFNOTE LINENUMBER;
clef: CLEF SEPSYMBOL ('C' | 'G' | 'F') INTEGER;
//TODO timeSignature: TIMESIGNATURE SEPSYMBOL (METERSIGNS | (INTEGER SLASH INTEGER));
//timeSignature: TIMESIGNATURE SEPSYMBOL (('C' | 'C/') | (INTEGER SLASH INTEGER)); v1
timeSignature: TIMESIGNATURE SEPSYMBOL (('C' | 'Ccut' | 'C/') | (INTEGER '/' INTEGER)); // v2 and v1
//keySignature: KEYSIGNATURE SEPSYMBOL DIATONICPITCH ACCIDENTALS? (MAJOR|MINOR)?;
keySignature: KEYSIGNATURE SEPSYMBOL ('A'|'B'|'C'|'D'|'E'|'F'|'G') ACCIDENTALS? (MAJOR|MINOR)?;
note: (NOTE | GRACENOTE) SEPSYMBOL pitch SEPVALUES FIGURE dots? (SEPVALUES FERMATA)? (SEPVALUES TRILL)?;
tie: TIE;
barline: BARLINE;
rest: REST SEPSYMBOL FIGURE dots? (SEPVALUES FERMATA)?;
multirest: MULTIREST SEPSYMBOL INTEGER;



//TODO pitch : DIATONICPITCH ACCIDENTALS? octave;
pitch : ('A'|'B'|'C'|'D'|'E'|'F'|'G')  ACCIDENTALS? octave;

octave : INTEGER;

dots : DOT+;

SEP: ' ' | '\t'; //v2 is space, v1 was tab
SEPSYMBOL: '-';
SEPVALUES: '_';
//fragment SLASH: '/'; // to avoid problems with C/

//TODO LINENUMBER: ('1'..'5');
ACCIDENTALS: 'bb' | 'b' | 'n' | '#' | 'x';
TRILL: 'trill';
FERMATA: 'fermata';
CLEF: 'clef';
NOTE: 'note';
GRACENOTE: 'gracenote';
REST: 'rest';
MULTIREST: 'multirest';
BARLINE: 'barline';
THICKBARLINE: 'thickbarline';
FIGURE: 'quadruple_whole' | 'double_whole' | 'whole' | 'half' | 'quarter' | 'eighth' | 'sixteenth'
                 | 'thirty_second' | 'sixty_fourth' | 'hundred_twenty_eighth' | 'two_hundred_fifty_six';
DOT: '.';
TIE: 'tie';
//TODO DIATONICPITCH: ('A'..'G');
KEYSIGNATURE: 'keySignature';
TIMESIGNATURE: 'timeSignature';
//v1 MINOR: 'minor';
//v1 MAJOR: 'major';
MINOR: 'm'; //v2
MAJOR: 'M'; //v2


// TO-DO volver a añadir esto
// fragment CLEFNOTE: 'C' | 'G' | 'F';
// fragment METERSIGNS : 'C' | 'C/';
// fragment DIGIT: ('0'..'9');
INTEGER: ('0'..'9')+;

NEWLINE : '\r'? '\n';
