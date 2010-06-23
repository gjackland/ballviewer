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
	
	/**
	 *
	 *	@param inputFiles	Pass in array as some readers can handle multiple files
	 *	@param analysers	The reader can optionally output a collection of analysers
	 */
	public ExperimentRecord
	getExperimentRecord(
		final	File[]							inputFiles,
		final	Collection< Analyser >			analysers
	)
	throws IOException;
}