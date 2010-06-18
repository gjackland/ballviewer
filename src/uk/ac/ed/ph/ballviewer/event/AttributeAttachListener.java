package uk.ac.ed.ph.ballviewer.event;

import uk.ac.ed.ph.ballviewer.analysis.AnalyserOutput;
import uk.ac.ed.ph.ballviewer.analysis.SysObjAttribute;

public interface AttributeAttachListener
{
	public void
	attributeAttached(
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
	);
	
	public void
	attributeDetached(
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
	);
}