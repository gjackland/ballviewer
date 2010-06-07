//package uk.ac.ed.ph.ballviewer.gui.editors;
//
//import javax.swing.JPanel;
//import javax.swing.JComponent;
//import javax.swing.JFileChooser;
//import javax.swing.JTextField;
//import javax.swing.JButton;
//import java.io.File;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
//
//
//public class FileTextFieldWithFileChooser implements ReflectionType, ActionListener
//{
//	private final JPanel			pane		= new JPanel();		// Use FlowLayout
//	private final JTextField		textField	= new JTextField( 20 );
//	private final JButton			bOpenFC		= new JButton("...");
//	private final JFileChooser		fc			= new JFileChooser();
//	private final boolean 			allowMultiFiles;
//	
//	public FileTextFieldWithFileChooser( String defaultValue, boolean allowMultipleFileSelection )
//	{
//		int selectionMode;
//		if( defaultValue.contains( "DIRECTORIES_ONLY" ) )
//		{
//			selectionMode = JFileChooser.DIRECTORIES_ONLY;
//		}
//		else if( defaultValue.contains( "FILES_AND_DIRECTORIES" ) )
//		{
//			selectionMode = JFileChooser.FILES_AND_DIRECTORIES;
//		}
//		else	// Default selection mode is files only
//		{
//			selectionMode = JFileChooser.FILES_ONLY;
//		}
//		try
//		{
//			fc.setFileSelectionMode( selectionMode );
//		}
//		catch( Exception exception )
//		{
//			System.err.println( "FileTextFieldWithFileChooser failed to set selection mode for FileChooser" );
//		}
//		allowMultiFiles = allowMultipleFileSelection;
//		fc.setMultiSelectionEnabled( allowMultipleFileSelection );
//		
//		bOpenFC.addActionListener( this );
//		pane.add( textField );
//		pane.add( bOpenFC );
//	}
//	
//	public JComponent
//	getComponent()
//	{
//		return pane;
//	}
//	
//	public Object
//	getValue()
//	{
//		if( !allowMultiFiles )
//		{
//			return ( Object )new File( textField.getText() );
//		}
//		else
//		{
//			return ( Object )getFilesFromString( textField.getText() );
//		}
//	}
//	
//	public void
//	actionPerformed( ActionEvent e )
//	{
//		if( e.getSource() == bOpenFC )
//		{
//			if( !allowMultiFiles )
//			{
//				fc.setCurrentDirectory( new File( textField.getText() ) );
//			}
//			else
//			{
//				fc.setCurrentDirectory( getFilesFromString( textField.getText() )[ 0 ] );
//			}
//			
//			
//			// TODO: Make the fc modal i.e. can't unfocus
//			int returnVal = fc.showOpenDialog( pane );
//			
//			if( returnVal == JFileChooser.APPROVE_OPTION )
//			{
//				if( !allowMultiFiles )
//				{
//					textField.setText( fc.getSelectedFile().toString() );
//				}
//				else
//				{
//					String filesList = new String();
//					for( File f : fc.getSelectedFiles() )
//					{
//						filesList += "\"" + f + "\"";
//					}
//					textField.setText( filesList );
//				}
//				
//			}
//		}
//	}
//	
//	private File[]
//	getFilesFromString( String filesString )
//	{
//		String[] strings = null;
//		try
//		{
//			strings = filesString.split( "\"\"|(?<=.)\"" );  // Split in single or double "
//		}
//		catch( java.util.regex.PatternSyntaxException e )
//		{
//			System.err.println( "FileTextFieldWithFileChooser.getFilesFromString failed as pattern matching syntax is wrong" );
//			return new File[0];
//		}
//		
//		File[] files = new File[ strings.length ];
//		for( int i = 0; i < strings.length; ++i )
//		{
//			files[ i ] = new File( strings[ i ].replaceAll( "\"", "" ) );
//		}
//		return files;
//	}
//}