grep codepoint muret_modern.json  | cut -f4 -d'"' | sed 's/U+/uni/g'
