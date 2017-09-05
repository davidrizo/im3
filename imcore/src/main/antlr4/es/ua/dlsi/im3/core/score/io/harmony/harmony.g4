grammar harmony;
/**
The **kern **harm type is imported using the grammar kern.g4
**/
/*
Don't use rules for lexer literals based on letters ('a'...) because they are ambiguos and
depend on the parser
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

tonalFunction: tonalFunctionSpecification alternateTonalFunction?;

alternateTonalFunction:  LEFTBRACKET tonalFunctionSpecification RIGHTBRACKET;

tonalFunctionSpecification: ('T' | 'D' | 'S''D'?);

key: keySpecification alternateKey?;

alternateKey:  LEFTBRACKET keySpecification RIGHTBRACKET;

keySpecification: ('A' | 'B' | 'C' | 'D' | 'D' | 'E' | 'F' | 'G') ALTERATION? MODE?;

/// Lexer - these rules would add an ambiguous situation (D for dominant) or (D note).
//TONAL_FUNCTION: ('T' | 'D' | 'S''D'?);
//NOTE_NAME: ('A' .. 'G');
ALTERATION: ('#' | 'b');
MODE: ('M' | 'm');

TAB: '\t';
LEFTBRACKET: '[';
RIGHTBRACKET: ']';
SLASH: '/';

