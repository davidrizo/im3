package es.ua.dlsi.im3.core.score;

//TODO Integrar con ChordType
/**
 * We use the chord set from MusicXML. In order to be compatible to MusicXML - they have to be changed to lowercase and _ to -
@author drizo
@date 07/06/2011

MusicXML documentation:

    <xs:simpleType name="kind-tempo">
        <xs:annotation>
            <xs:documentation>A kind-tempo indicates the type of chord. Degree symbols can then add, subtract, or alter from these starting points. Values include:
    
Triads:
    major (major third, perfect fifth)
    minor (minor third, perfect fifth)
    augmented (major third, augmented fifth)
    diminished (minor third, diminished fifth)
Sevenths:
    dominant (major triad, minor seventh)
    major-seventh (major triad, major seventh)
    minor-seventh (minor triad, minor seventh)
    diminished-seventh (diminished triad, diminished seventh)
    augmented-seventh (augmented triad, minor seventh)
    half-diminished (diminished triad, minor seventh)
    major-minor (minor triad, major seventh)
Sixths:
    major-sixth (major triad, added sixth)
    minor-sixth (minor triad, added sixth)
Ninths:
    dominant-ninth (dominant-seventh, major ninth)
    major-ninth (major-seventh, major ninth)
    minor-ninth (minor-seventh, major ninth)
11ths (usually as the basis for alteration):
    dominant-11th (dominant-ninth, perfect 11th)
    major-11th (major-ninth, perfect 11th)
    minor-11th (minor-ninth, perfect 11th)
13ths (usually as the basis for alteration):
    dominant-13th (dominant-11th, major 13th)
    major-13th (major-11th, major 13th)
    minor-13th (minor-11th, major 13th)
Suspended:
    suspended-second (major second, perfect fifth)
    suspended-fourth (perfect fourth, perfect fifth)
Functional sixths:
    Neapolitan
    Italian
    French
    German
Other:
    pedal (pedal-point bass)
    power (perfect fifth)
    Tristan
    
The "other" kind is used when the harmony is entirely composed of add symbols. The "none" kind is used to explicitly encode absence of chords or functional harmony.</xs:documentation>
        </xs:annotation>
        <xs:restriction base="xs:string">
            <xs:enumeration tempo="major"/>
            <xs:enumeration tempo="minor"/>
            <xs:enumeration tempo="augmented"/>
            <xs:enumeration tempo="diminished"/>
            <xs:enumeration tempo="dominant"/>
            <xs:enumeration tempo="major-seventh"/>
            <xs:enumeration tempo="minor-seventh"/>
            <xs:enumeration tempo="diminished-seventh"/>
            <xs:enumeration tempo="augmented-seventh"/>
            <xs:enumeration tempo="half-diminished"/>
            <xs:enumeration tempo="major-minor"/>
            <xs:enumeration tempo="major-sixth"/>
            <xs:enumeration tempo="minor-sixth"/>
            <xs:enumeration tempo="dominant-ninth"/>
            <xs:enumeration tempo="major-ninth"/>
            <xs:enumeration tempo="minor-ninth"/>
            <xs:enumeration tempo="dominant-11th"/>
            <xs:enumeration tempo="major-11th"/>
            <xs:enumeration tempo="minor-11th"/>
            <xs:enumeration tempo="dominant-13th"/>
            <xs:enumeration tempo="major-13th"/>
            <xs:enumeration tempo="minor-13th"/>
            <xs:enumeration tempo="suspended-second"/>
            <xs:enumeration tempo="suspended-fourth"/>
            <xs:enumeration tempo="Neapolitan"/>
            <xs:enumeration tempo="Italian"/>
            <xs:enumeration tempo="French"/>
            <xs:enumeration tempo="German"/>
            <xs:enumeration tempo="pedal"/>
            <xs:enumeration tempo="power"/>
            <xs:enumeration tempo="Tristan"/>
            <xs:enumeration tempo="other"/>
            <xs:enumeration tempo="none"/>
        </xs:restriction>
    </xs:simpleType>

 **/
public enum HarmonyKind {
	// triads
	MAJOR,//(major third, perfect fifth)
	MINOR,//(minor third, perfect fifth)
	AUGMENTED,//(major third, augmented fifth)
	DIMINISHED,//(minor third, diminished fifth)
	
	// sevenths
	DOMINANT, //(major triad, minor seventh)
	MAJOR_SEVENTH, //(major triad, major seventh)
	MINOR_SEVENTH,//(minor triad, minor seventh)
	DIMINISHED_SEVENTH,//(diminished triad, diminished seventh)
	AUGMENTED_SEVENTH, //(augmented triad, minor seventh) //TODO Placido ver este
	HALF_DIMINISHED, //(diminished triad, minor seventh)	
	
	
	DOMINANT_11TH,
	DOMINANT_13TH,
	DOMINANT_NINTH,
	MAJOR_NINTH,
	MAJOR_SIXTH,
	MINOR_11TH,
	MINOR_NINTH,
	MINOR_SIXTH,
	SUSPENDED_FOURTH,
	SUSPENDED_SECOND, 
	
	MAJOR_OR_MINOR; // without the 3rd
}