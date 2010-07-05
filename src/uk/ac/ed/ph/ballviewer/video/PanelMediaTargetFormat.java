package uk.ac.ed.ph.ballviewer.video;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.sun.media.util.JMFI18N;
import com.sun.media.ui.AudioFormatChooser;
import com.sun.media.ui.VideoFormatChooser;


public class PanelMediaTargetFormat extends JPanel implements ActionListener, ItemListener {

    private 		Processor               processor = null;
    private 		String                  strTargetType = JMFI18N.getResource("jmstudio.export.type.file");
    private 		ContentDescriptor       arrContentDescriptors [] = null;
    private 		Hashtable               hashtableContentDescriptors = null;
    private 		TrackControl            arrTrackControls [] = null;
    private 		String                  arrAllowContentType [] = null;
            		
    private 		JPanel				panelContent;
    private 		Choice      	    comboContentType;
    private 		JTabbedPane			tabControl;
    
    private 		Vector      	    vectorPanelsVideo;
    private 		Vector      	    vectorTracksVideo;
                                	
	private final	Dialog				parent;


    public PanelMediaTargetFormat( final Dialog parent )
    {
    	super ();
		
		this.parent	= parent;
    	try
    	{	
    	    init ();
    	}
    	catch ( Exception exception )
    	{
    	    exception.printStackTrace ();
    	}
    }

    public void setAllowContentType ( String arrAllowContentType[] ) {
        this.arrAllowContentType = arrAllowContentType;
    }

    private void init () throws Exception {
    	Panel	   panelDescription;

    	this.setLayout ( new BorderLayout(6,6) );

    	panelDescription = new Panel ( new GridLayout(0,1) );
    	this.add ( panelDescription, BorderLayout.NORTH );
    	panelDescription.add ( new JLabel(JMFI18N.getResource("jmstudio.export.format.label1")) );
    	panelDescription.add ( new JLabel(JMFI18N.getResource("jmstudio.export.format.label2")) );

        panelContent = new JPanel( new BorderLayout(6,6) );
        //panelContent.setEmptyBorder ( 6, 6, 6, 6 );
        this.add ( panelContent, BorderLayout.CENTER );
    }

    public void setProcessor(
    	final Processor		processor,
    	final String		strContType,
    	final String		strTargetType
    )
    {
        this.processor		= processor;
        this.strTargetType	= strTargetType;

        arrContentDescriptors = processor.getSupportedContentDescriptors ();
        arrTrackControls = processor.getTrackControls ();

        panelContent.removeAll ();
        buildPage ();

        if ( strContType != null )
            comboContentType.select ( strContType );
        changeContentType ();
    }

    public void updateProcessorFormat () {
        int                 i;
        int                 nCount;
        VideoFormatChooser  panelVideo;
        AudioFormatChooser  panelAudio;
        TrackControl        trackControl;
        Format              format;


        nCount = vectorPanelsVideo.size ();
        for ( i = 0;  i < nCount;  i++ ) {
            panelVideo = (VideoFormatChooser) vectorPanelsVideo.elementAt ( i );
            trackControl = (TrackControl) vectorTracksVideo.elementAt ( i );

            if ( panelVideo.isTrackEnabled() == false ) {
                trackControl.setEnabled ( false );
                continue;
            }
            format = panelVideo.getFormat ();
            if ( format == null )
            {
            	JOptionPane.showMessageDialog( parent, "Internal error. Unable to match choosen video format. Track will be disabled." );
                trackControl.setEnabled ( false );
            }
            else {
                trackControl.setEnabled ( true );
                trackControl.setFormat ( format );
            }
        }
    }

    public boolean[] getEnabledVideoTracks () {
        int                 i;
        int                 nCount;
        VideoFormatChooser  panelVideo;
        boolean             arrResult [];


        nCount = vectorPanelsVideo.size ();
        arrResult = new boolean [ nCount ];
        for ( i = 0;  i < nCount;  i++ ) {
            panelVideo = (VideoFormatChooser) vectorPanelsVideo.elementAt ( i );
            arrResult[i] = panelVideo.isTrackEnabled ();
        }
        return ( arrResult );
    }

	@Override
    public void
    itemStateChanged( final ItemEvent event )
    {
        final Object objectSource = event.getSource ();
        if ( objectSource == comboContentType ) {
            changeContentType ();
        }
    }
    
    @Override
    public void
    actionPerformed( final ActionEvent e )
    {}

    private void buildPage()
    {
        String   strContType;

        final JPanel panelFormat = new JPanel ( new BorderLayout(6,6) );
        panelContent.add ( panelFormat, BorderLayout.NORTH );

        final JLabel label = new JLabel ( JMFI18N.getResource("jmstudio.export.format.format") );
        panelFormat.add ( label, BorderLayout.WEST );
        comboContentType = new Choice ();
        comboContentType.addItemListener ( this );
        final int nCount = arrContentDescriptors.length;
        hashtableContentDescriptors = new Hashtable ();
        for ( int i = 0;  i < nCount;  i++ )
        {
            strContType = arrContentDescriptors[i].getContentType();
            // filter
            if ( !isContentTypeAllowed(strContType) )
                continue;

            strContType = arrContentDescriptors[i].toString ();
            comboContentType.addItem ( strContType );
            hashtableContentDescriptors.put ( strContType, arrContentDescriptors[i] );
        }
        panelFormat.add ( comboContentType, BorderLayout.CENTER );

        final JTabbedPane panel = buildTrackFormatPanel ();
        panelContent.add ( panel, BorderLayout.CENTER );
    }

    private JTabbedPane buildTrackFormatPanel () {

        VideoFormatChooser  chooserVideo;
        int                 nCount;
        int                 nIndexVideo;
        int                 nVideoTrackCount = 0;
        Format              format;
        String              strTitle;
        String              strEncoding;
        final String              strVideo = JMFI18N.getResource("jmstudio.export.format.video");
        final String              strHinted = JMFI18N.getResource("jmstudio.export.format.hinted");


        tabControl = new JTabbedPane( JTabbedPane.TOP );

        nIndexVideo = 0;
        nVideoTrackCount = 0;
        vectorPanelsVideo = new Vector ();
        vectorTracksVideo = new Vector ();

        nCount = arrTrackControls.length;
        for ( int i = 0;  i < nCount;  i++ ) {
            format = arrTrackControls[i].getFormat ();
            if ( format instanceof VideoFormat )
                nVideoTrackCount++;
        }

        for( int i = 0;  i < nCount;  i++ ) {
            format = arrTrackControls[i].getFormat ();
            if ( format instanceof VideoFormat ) {
                if ( nVideoTrackCount < 2 )
                    strTitle = new String ( strVideo );
                else {
                    nIndexVideo++;
                    strTitle = new String ( strVideo + " " + nIndexVideo );
                }
                strEncoding = format.getEncoding ();
                if ( strEncoding.endsWith("/rtp") )
                    strTitle = strTitle + " " + strHinted;
                chooserVideo = new VideoFormatChooser ( arrTrackControls[i].getSupportedFormats(), (VideoFormat)format, true, this );
                chooserVideo.setTrackEnabled ( arrTrackControls[i].isEnabled() );
                tabControl.addTab ( strTitle, chooserVideo );
                vectorPanelsVideo.addElement ( chooserVideo );
                vectorTracksVideo.addElement ( arrTrackControls[i] );
            }
        }

        return tabControl;
    }

    private void changeContentType () {
        int                 i;
        int                 nCount;
        VideoFormatChooser  panelVideo;
        TrackControl        trackControl;
        String              strContentType;
        ContentDescriptor   contentDescriptor;


        strContentType = comboContentType.getSelectedItem ();
        contentDescriptor = (ContentDescriptor) hashtableContentDescriptors.get ( strContentType );

        if ( processor.setContentDescriptor(contentDescriptor) == null ) {
            System.err.println ( "Error setting content descriptor on " + "processor" );
        }

        nCount = vectorPanelsVideo.size ();
        for ( i = 0;  i < nCount;  i++ ) {
            panelVideo = (VideoFormatChooser) vectorPanelsVideo.elementAt ( i );
            trackControl = (TrackControl) vectorTracksVideo.elementAt ( i );
            panelVideo.setSupportedFormats ( trackControl.getSupportedFormats(), (VideoFormat)trackControl.getFormat() );
        }
    }

    private boolean isContentTypeAllowed ( String strContType ) {
        int        i;
        boolean    boolResult = false;
        String     strTypeRaw;
        String     strTypeRawRtp;

        if ( arrAllowContentType != null ) {
            for ( i = 0;  i < arrAllowContentType.length  &&  boolResult == false;  i++ ) {
                if ( arrAllowContentType[i].equalsIgnoreCase(strContType) )
                    boolResult = true;
            }
        }
        else
            boolResult = true;

        return ( boolResult );
    }

}


