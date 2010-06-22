package uk.ac.ed.ph.ballviewer.io;

import java.io.File;
import java.io.IOException;

import java.util.Collection;

import uk.ac.ed.ph.ballviewer.ExperimentRecord;

import uk.ac.ed.ph.ballviewer.analysis.Analyser;

interface InputReader
{
	public String[]
	getSupportedExtensions();
	
	public ExperimentRecord
	getExperimentRecord(
		final	File							inputFile,
		final	Collection< Analyser >			analysers
	)
	throws IOException;
}