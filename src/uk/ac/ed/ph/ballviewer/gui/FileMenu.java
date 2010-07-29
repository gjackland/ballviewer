package uk.ac.ed.ph.ballviewer.gui;

import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;

import uk.ac.ed.ph.ballviewer.BallViewerFramework;

class FileMenu extends JMenu implements ActionListener
{
	private final BallViewerFramework	framework;
	private final Component				parent;

	private final JMenuItem				miOpen	= new JMenuItem( "Open" );
	private final JMenuItem				miClose	= new JMenuItem( "Close" );
	private final JFileChooser			fc		= new JFileChooser();

	FileMenu( final Component parent, final BallViewerFramework framework )
	{
		super( "File" );

		this.parent = parent;
		this.framework = framework;

		// Set up the menu items
		miClose.setEnabled( false );

		// Listeners
		miOpen.addActionListener( this );
		miClose.addActionListener( this );

		// Set up all the menu items etc.
		add( miOpen );
		add( miClose );
		fc.setMultiSelectionEnabled( true );
		fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
	}

	// INTERFACES //////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed( final ActionEvent e )
	{
		final Object source = e.getSource();
		if( source == miOpen )
		{
			final int retVal = fc.showOpenDialog( parent );
			if( retVal == JFileChooser.APPROVE_OPTION )
			{
				framework.loadExperimentRecord( fc.getSelectedFiles() );

				miClose.setEnabled( true );
			}
		}
		else if( source == miClose )
		{
			if( miClose.isEnabled() )
			{
				framework.loadExperimentRecord( null );

				miClose.setEnabled( false );
			}
		}
	}

}