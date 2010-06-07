package uk.ac.ed.ph.ballviewer.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import uk.ac.ed.ph.ballviewer.StaticSystem;
import uk.ac.ed.ph.ballviewer.analysis.Analyser;

interface InputReader
{
	public String[]
	getSupportedExtensions();
	
	public StaticSystem
	getStaticSystem(
		final	File								inputFile,
		final	Collection< Analyser >	analysers
	)
	throws IOException;
}