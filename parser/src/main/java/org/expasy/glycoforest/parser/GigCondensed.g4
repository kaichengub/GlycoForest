// GigCondensed.g4
// Condensed structure representation that is use by Niclas Karlsson's Glyco Inflammatory Group (GIG)
// at the University of Gothenburg
grammar GigCondensed;

structureDb : labeledStructure+ EOF;
labeledStructure : label structure LineBreak;
structure : (extension+)? subTree;
extension : Number? subTree '+';
subTree : (linkedUnit | (OpenBracket subTree CloseBracket))+;
linkedUnit : Unit link?;
label : Id Sepparator;
link : Anomericity? Number? '-' Number?;

Id : [0-9][0-9]+'-'[0-9]|[0-9][0-9]+[a-z]?;
Anomericity : 'a' | 'b' | '\u03B1' /*alpha*/ | '\u03B2' /*beta*/;
Unit : 'Gal' |
       'Hex' |
       'Glc' |
       'GalNAc' |
       'GlcAc' |
       'GalNAcol' |
       'GlcNAc' |
       'GlcNAcol' |
       'HexNAcol' |
       'Fuc' |
       'HexNAc' |
       'NeuGc'  | 'Neu5Gc'  |
       'NeuAc'  | 'Neu5Ac' |
       'Kdn' |
       'S' |
       'Man' |
       'Xyl'
       ;
Number :  [0-9]+;
OpenBracket : '(' | '{' | '[';
CloseBracket : ')' | '}' | ']';
LineBreak :  '\r'? '\n' | '\r' ;
Sepparator : ',' | '\t' | ':' | ';';
WS : [ ]+ -> skip;