grammar paec;

@lexer::header {
//package es.ua.dlsi.im2.io.paec;
}

@parser::header {
//package es.ua.dlsi.im2.io.paec;
}


incipit 
	:	
	clef 
	timesignature?
	keysignature?
	separator 
	musicalcontent
	;


clef	
	:	
	PERCENT shape 
	(MINUS | PLUS)	// - modern, + mensural
	DIGIT // positionInStaff, starting from bottom STAFFLINE
	;



keysignature 
	:
	DOLLAR	
	((SHARP | FLAT )	notename+
	(LEFTBRACKET (SHARP | FLAT )? notename+ RIGHTBRACKET)? // rism [ ] sometimes appear, but are not documented	
	|
	(LEFTBRACKET (SHARP | FLAT )? notename+ RIGHTBRACKET) // rism [ ] sometimes appear, but are not documented	
	)
	;
	
timesignature
	:
	AT meter
	(SPACE meter)*
	;
		
	
meter
	:	
	((COMMON | PERFECT) ( DOT | SLASH)?
	fraction?)
	|
	fraction
	;	
	
fraction
	:	
	number (SLASH number)? 
	;

gracenote
	:
	//'g' note
	LETTER_g notepropschange* note
	|
	//'q' rhythmicvalue note 
	LETTER_q notepropschange* note 
	// rism nuevo Sometimes the rhythmicvalue is not given
	|
	//'qq' rhythmicvalue? note+ 'r' 
	LETTER_q LETTER_q items LETTER_r
	;


octave	:  OCTAVE7 | OCTAVE6 | OCTAVE5 | OCTAVE4 | OCTAVE3 | OCTAVE2 |OCTAVE1 
	;


rhythmicvalue
	:
	figure DOT*
	;


	
figure	:	
	DIGIT;


accidental
	:	
	SHARP | DOUBLESHARP | FLAT | DOUBLEFLAT | NATURAL 
	;


musicalcontent
	:
	bar (barlines bar)* barlines?
	;	

bar	
	:		
		items
		|
		MEASUREREPET // measure repet			
		|
		MEASUREREST number? // number of repets - in PAE the number is mandatory, but RISM contains some examples without it
	;

items	:	item+; 

clefkeytimechange
	:	
	(clef | timesignature | keysignature )+ SPACE
	;

item	
	:
	clefkeytimechange // key, clef, time? change (it can be in the middle of a bar)
	|
	irregulargroup
        |
        triplet
        |    
	notes
	|
        repetgroup
        | 
        notepropschange 
	;
	
repetgroup:
	REPETGRPDELIM 
	items 
	REPETGRPDELIM REPETMARK+ // first a set of elements is marked, the it is repeated several times
        ;

triplet:
    LEFTPAR items RIGHTPAR
;
// After '(' there must be the rhythmic value of the first note, even if it is equal to that of the group;
// In the example it appears 4('6DEFGA;5), the octave before the rhythm
irregulargroup:
	// rism, sometimes the figure with the total value of the group is omitted
	figure? LEFTPAR items SEMICOLON number RIGHTPAR
;

notepropschange 
	:
	octave 
	|
	rhythmicvalue
	|
	accidental	
	;

notes
    :
    note (CHORD notepropschange* note)* 
    |
    rest
    |
    beaming
    ;    

beaming
	:
	LEFTCURBRACES items RIGHTCURBRACES 
	;
		
note 
	:
        gracenote
        |    
	notevalue 
	|
	notefermata
	;

notefermata
	:
	LEFTPAR notevalue RIGHTPAR 
	;
	
rest 
	:	
	restvalue 
	|
	restfermata	
	;
	
restfermata
	:	
	LEFTPAR restvalue RIGHTPAR 
	;

notevalue 
	:		
	notename notesuffix? DOT* // rism contains some . following notes
	;		
	
restvalue	: 
	MINUS // measure rests are outside this definition
	;
	

notesuffix:
    TRILL slur? // + = slur
    |
    slur TRILL?
    ;

slur	:	PLUS;

barlines 
	:
	SLASH  //bar STAFFLINE
	|
	(SLASH SLASH ) //double bar STAFFLINE
	|
	(SLASH SLASH COLON) // double bar STAFFLINE with repeat sign on the right
	|
	(COLON SLASH SLASH)//double bar STAFFLINE with repeat sign on the left
	|
	(COLON SLASH SLASH COLON) //double bar STAFFLINE with repeat sign on the left and on the right	
    ;
	
shape	:	(LETTER_g | NOTENAMES); // check semantically it is G, C or F
notename:       NOTENAMES; //done with syntactic because some values are repeated
number	:	DIGIT+;

separator: (SPACE | QUESTIONMARK | SEMICOLON)+;


DIGIT	: ('0'..'9');	

OCTAVE7:	APOSTROPHE APOSTROPHE APOSTROPHE APOSTROPHE;
OCTAVE6	:	APOSTROPHE APOSTROPHE APOSTROPHE;
OCTAVE5	:	APOSTROPHE APOSTROPHE;
OCTAVE4	:	APOSTROPHE;

OCTAVE1	:	COMMA COMMA COMMA;
OCTAVE2	:	COMMA COMMA;
OCTAVE3	:	COMMA;



// letters (sorted lexigraphically) and following precedence
DOUBLEFLAT	: 'bb';
FLAT	:	'b';	
COMMON	:	'c';
REPETMARK: 'f';
LETTER_g: 'g';
MEASUREREPET   :   'i';
NATURAL	:	'n';
PERFECT	:	'o';
LETTER_q: 'q';
LETTER_r: 'r';
TRILL : 't';

DOUBLESHARP	: 'xx';
SHARP	:	'x';

NOTENAMES: ('A'..'G');

PERCENT	:	'%';
MINUS: '-';
PLUS: '+';
DOLLAR	:	'$';
LEFTBRACKET:'[';
RIGHTBRACKET:']';
AT	:	'@';
SPACE	:	' ';

CHORD	:	'^';	
TAB	:	'\t';
MEASUREREST:	'=';
LEFTPAR:   '(';
RIGHTPAR:   ')';
LEFTCURBRACES:'{';
RIGHTCURBRACES:'}';
SEMICOLON:';';
REPETGRPDELIM: '!';
SLASH	:	'/';
fragment
APOSTROPHE: '\'';
fragment
COMMA: ',';

COLON: ':';
QUESTIONMARK: '?';
DOT	:	'.';

EOL:   ('\n'|'\r') -> skip;


