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


// start rule
//start: (referenceRecord EOL)* header (EOL record)+ EOL? (referenceRecord EOL?)* EOF;
start: header (EOL record)+ EOL? EOF;

header: headerField (TAB headerField)*;

headerField: headerMens; // in full **kern specification it includes also headerKern | headerRoot | headerHarm

headerMens:
    MENS
    |
    TEXT
    ;

record
    :
    fields
    |
    fieldCommentLine;

fields: field (TAB field)*;

fieldCommentLine:
    fieldComment (TAB fieldComment)*;

field
    :
    placeHolder // nothing is done, it is just a placeholder
    |
    lyricsText; // used in TEXT MODE

placeHolder: DOT;

fieldComment: FIELD_COMMENT? FIELD_TEXT?; //FIELD_COMMENT is optional for allowing comments in **text spines

lyricsText: FIELD_TEXT;

