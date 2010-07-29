package uk.ac.ed.ph.ballviewer.video;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

import com.sun.media.util.JMFI18N;

public class PanelMediaTargetFile extends JPanel implements ActionListener
{

	private TextField	textFile;
	private Button		buttonBrowse;
	private Dialog		parent;

	public PanelMediaTargetFile( final Dialog parent )
	{
		super();

		try
		{
			this.parent = parent;
			init();
		}
		catch( Exception exception )
		{
			exception.printStackTrace();
		}
	}

	public String getFileName()
	{
		String strFile;

		strFile = textFile.getText();
		return( strFile );
	}

	private void init() throws Exception
	{
		Panel panel;
		Panel panelDescription;
		Panel panelEntry;
		Label label;

		this.setLayout( new BorderLayout( 12, 12 ) );

		panelDescription = new Panel( new GridLayout( 0, 1 ) );
		this.add( panelDescription, BorderLayout.NORTH );
		panelDescription.add( new Label( JMFI18N.getResource( "jmstudio.export.targetfile.label1" ) ) );
		panelDescription.add( new Label( JMFI18N.getResource( "jmstudio.export.targetfile.label2" ) ) );

		panel = new Panel( new BorderLayout( 6, 6 ) );
		this.add( panel, BorderLayout.CENTER );

		panelEntry = new Panel( new BorderLayout( 6, 6 ) );
		panel.add( panelEntry, BorderLayout.NORTH );

		textFile = new TextField();
		panelEntry.add( textFile, BorderLayout.CENTER );

		buttonBrowse = new Button( JMFI18N.getResource( "jmstudio.export.targetfile.browse" ) );
		buttonBrowse.addActionListener( this );
		panelEntry.add( buttonBrowse, BorderLayout.EAST );
	}

	@Override
	public void actionPerformed( ActionEvent event )
	{
		Object objSource;

		objSource = event.getSource();
		if( objSource == buttonBrowse )
		{
			browseFile();
		}
	}

	private void browseFile()
	{
		FileDialog dlgFile;
		String strFile;
		String strDir;

		strFile = textFile.getText();
		dlgFile = new FileDialog( parent, JMFI18N.getResource( "jmstudio.export.targetfile.filedialog" ), FileDialog.SAVE );
		dlgFile.setFile( strFile );
		dlgFile.show();

		strFile = dlgFile.getFile();
		strDir = dlgFile.getDirectory();
		if( strFile != null && strFile.length() > 0 )
		{
			strFile = strDir + strFile;
			textFile.setText( strFile );
		}
	}

}
