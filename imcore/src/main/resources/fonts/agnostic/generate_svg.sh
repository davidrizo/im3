for f in $(ls *otf); do
	/Applications/FontForge.app/Contents/MacOS/FontForge -script generate_svg.pe $f
done