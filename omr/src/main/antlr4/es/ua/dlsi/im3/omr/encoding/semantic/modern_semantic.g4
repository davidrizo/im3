grammar modern_semantic;
/*

 @pierre

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


songbeta : firstseq nextseq* ;
/*
firstseq : seq. of symbols of the first system in a song
*/
firstseq : firstSystemBeginning sequence  ;
/*
nextseq : any system but the first
*/
nextseq : anySystemBeginning sequence  ;
/*
firstSystemBeginning :
 - sequence of symbols that appear at the start of the first system before any notes/rests
 - MUST include at least a clef.
 - optional elements: vertical lines (must be the first element)
 - keysignature
 - timesignature

*/
firstSystemBeginning : (verticalLine SEP)? clef SEP (keySignature )? (timeSignature SEP)? ;
/*
anySystemBeginning :
 - sequence of symbols that appear at the start of any system but the first before any notes/rests
 - CAN include a clef.
 - CAN be empty
 - other optional elements: vertical lines (must be the first element)
 - keysignature
 - timesignature
*/
anySystemBeginning : (verticalLine SEP)? (clef SEP)? (keySignature)? (timeSignature SEP)? ;

verticalLine : VERTICALLINE SEPVERTICALPOSITION verticalPos
    | THICKBARLINE SEPVERTICALPOSITION verticalPos;
clef : CLEF SEPSYMBOL CLEFNOTE SEPVERTICALPOSITION verticalPos;
keySignature : (accidental SEP { /* max. 7 of the same kind */ })+ ;
accidental : ACCIDENTAL SEPSYMBOL ACCIDENTALS SEPVERTICALPOSITION verticalPos;
timeSignature :  meterSign
    | d1=digit SEPVERTICAL d2=digit {
        /* assert(d1.vp > d2.vp) vp=vertical position*/
        /* assert(d2.number is pow of two */
    } ;
meterSign : METERSIGN SEPSYMBOL METERSIGNS SEPVERTICALPOSITION verticalPos;
digit : DIGITWORD SEPSYMBOL INTEGER SEPVERTICALPOSITION verticalPos;


song : sequence+ ;
sequence : symbol (sep symbol)* NEWLINE+;
sep : SEP | SEPVERTICAL | SEPYUXTAPOSITION ;
symbol : specificSymbol SEPVERTICALPOSITION verticalPos ;
verticalPos : LINESPACE INTEGER ;
specificSymbol : CLEF SEPSYMBOL CLEFNOTE
    | NOTE SEPSYMBOL (COMMON_FIGURES | MENSURAL_NOTE_FIGURES | beam) (SEPSYMBOLPROPERTIES STEMDIRECTION)?
    | LIGATURE SEPSYMBOL (COMMON_FIGURES | MENSURAL_NOTE_FIGURES)
    | REST SEPSYMBOL (COMMON_FIGURES | MENSURAL_REST_FIGURES)
    | ACCIDENTAL SEPSYMBOL ACCIDENTALS
    | DOT
    | VERTICALLINE 
    | THICKBARLINE 
    | METERSIGN SEPSYMBOL METERSIGNS 
    | DIGITWORD SEPSYMBOL INTEGER
    | SLUR SEPSYMBOL STARTEND (SEPSYMBOLPROPERTIES POSITION)?
    | BRACKET SEPSYMBOL STARTEND (SEPSYMBOLPROPERTIES POSITION)?
    | FERMATA SEPSYMBOL POSITION 
    | TRILL 
    | DALSEGNO
    | CUSTOS
    | MULTIREST  
    | GRACENOTE SEPSYMBOL (COMMON_FIGURES | MENSURAL_NOTE_FIGURES | beam) (SEPSYMBOLPROPERTIES STEMDIRECTION)? ;
beam : BEAMS INTEGER ;


fragment DIGIT : ('0'..'9');
INTEGER : '-'? DIGIT+ ;
CLEFNOTE : 'C' | 'G' | 'F' | 'Fpetrucci' ;
STARTEND : 'start' | 'end' ;
POSITION : 'above' | 'below' ;
STEMDIRECTION : 'up' | 'down'  ;
TRILL : 'trill' ;
FERMATA : 'fermata' ;
CUSTOS : 'custos' ;
DALSEGNO : 'dalsegno' ;
CLEF : 'clef' ;
NOTE : 'note' ;
DOT : 'dot' ;
LIGATURE : 'ligature' ;
GRACENOTE : 'gracenote' ;
REST : 'rest' ;
ACCIDENTAL : 'accidental' ;
VERTICALLINE : 'verticalLine' ;
THICKBARLINE : 'thickbarline' ;
METERSIGN : 'metersign' ;
DIGITWORD: 'digit' ;
SLUR : 'slur' ;
MULTIREST : 'multirest' ;
BEAMS : 'beamedLeft' | 'beamedBoth' | 'beamedRight' ;
BRACKET : 'bracket' ;
SEP: ',' ;
SEPVERTICAL : '/' ;
SEPYUXTAPOSITION : '+' ;
SEPSYMBOL : '.' ;
SEPSYMBOLPROPERTIES : '_' ;
SEPVERTICALPOSITION: ':' ; // en v1 era -
LINESPACE : 'L' | 'S' ;
ACCIDENTALS : 'flat' | 'natural' | 'sharp' | 'double_sharp' ;
METERSIGNS : 'C_' | 'Ccut' | 'CZ' | 'CcutZ' | 'O' | 'Odot' | 'Cdot' ;
COMMON_FIGURES :  'longa' | 'breve' | 'whole' | 'half' | 'quarter' | 'eighth' | 'sixteenth' | 'thirtySecond' | 'sixtyFourth' | 'hundredTwentEighth' | 'twoHundredFiftySix'  ;
MENSURAL_NOTE_FIGURES : 'quadrupleWholeStem' | 'tripleWholeStem' | 'doubleWholeStem' | 'doubleWholeBlackStem' | 'doubleWhole' | 'longaBlack' | 'breveBlack' |  'wholeBlack' | 'eighthCut' | 'eighthVoid' | 'sixteenthVoid' ;
MENSURAL_REST_FIGURES : 'seminima' | 'fusa' | 'semifusa' ;
NEWLINE : '\r'? '\n';
WS : [ \t]+ -> skip ;