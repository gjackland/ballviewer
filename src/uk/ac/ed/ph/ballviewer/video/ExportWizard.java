package uk.ac.ed.ph.ballviewer.video;

import java.awt.*;

import java.awt.event.*;

import java.io.IOException;

import java.lang.IllegalArgumentException;

import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.media.*;
import javax.media.format.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.datasink.*;
//import javax.media.rtp.*;

import com.sun.media.util.JMFI18N;
import com.sun.media.ui.PlayerWindow;

//import jmapps.ui.*;
//import jmapps.util.*;
//import jmapps.jmstudio.*;


public class ExportWizard extends WizardDialog implements ControllerListener, DataSinkListener
{
	private static final	String					STR_TARGET_TYPE		= JMFI18N.getResource("jmstudio.export.type.file");

    protected				PanelMediaTargetFormat	panelTargetFormat;
    protected				PanelMediaTargetFile    panelTargetFile;
              		
    protected				String					strTitle = JMFI18N.getResource("jmstudio.export.title");
              		
    protected 				Processor				processor = null;
              		
    protected 				String					strFailMessage = null;
                    		
	private final			DataSource				dataSource;
    protected 				DataSink				dataSinkSave = null;
    protected 				ProgressDialog			dlgProgressSave = null;
    protected 				ProgressThread			threadProgressSave = null;
              				
    protected 				Vector					vectorWindowsLeft = new Vector ();


    public ExportWizard(
    	final String		strTitle,
    	final Frame			frame,
    	final DataSource	source
    )
    {
    	super( frame, strTitle, true, "logo.gif" );
    	
    	if( source == null )
    	{
    		throw new IllegalArgumentException( "DataSource for video export was null" );
    	}

        this.strTitle = strTitle;
        this.setTitle( strTitle );
        this.dataSource	= source;
        
		// Create the media processor from the source
    	if( createProcessor() )
    	{
    		setupFormatSelectPanel();
    	}
    }

    public ExportWizard(
    	final Frame			frame,
    	final DataSource	source
    )
    {
    	this ( JMFI18N.getResource("jmstudio.export.title"), frame, source );
    }

    public Vector getWindowsLeft () {
        return ( vectorWindowsLeft );
    }
   

    protected void
    init()
    throws Exception
    {
    	super.init ();
    	panelTargetFormat	= new PanelMediaTargetFormat( this );
    	panelTargetFormat.setName( "Page 1" );
    	panelTargetFile		= new PanelMediaTargetFile( this );
    	panelTargetFile.setName( "Page 2" );
    	this.setSize ( 480, 480 );
    	this.setLocation ( 100, 100 );
    }
    
    private boolean
    createProcessor()
    {
    	// First connect to the data source
    	try
    	{
    		dataSource.connect();
    	}
    	catch( IOException e )
    	{
    		JOptionPane.showMessageDialog(
    			this,
    			"Failed to connect to data source \n" + e.getMessage(),
    			"Error",
    			JOptionPane.ERROR_MESSAGE
    		);
    		e.printStackTrace();
    		return false;
    	}
    	
    	
        // Set up the processor
        try
        {
            processor = Manager.createProcessor( dataSource );
        }
        catch ( Exception exception )
        {
        	JOptionPane.showMessageDialog(
        		this,
        		JMFI18N.getResource("jmstudio.error.processor.create") + "\n" + exception.getMessage(),
        		"Error",
        		JOptionPane.ERROR_MESSAGE
        	);
            exception.printStackTrace ();
            return false;
        }
        
        if ( processor != null )
        {
            processor.addControllerListener ( this );
            configureProcessor ();
        }

        if ( processor == null )
        {
            return false;
        }
        return true;
    }
    
    private void
    setupFormatSelectPanel()
    {
		setCursor ( new Cursor( Cursor.WAIT_CURSOR ) );
		final String strContentType = ( new ContentDescriptor( dataSource.getContentType() ) ).toString();
		panelTargetFormat.setProcessor ( processor, strContentType, STR_TARGET_TYPE );
		setCursor ( Cursor.getDefaultCursor() );
    }
    
    // INTERFACES /////////////////////////////////////////////////////////////

	@Override
    protected JPanel
    getFirstPage()
    {
    	return panelTargetFormat;
    }

	@Override
    protected JPanel
    getLastPage()
    {
	   	return panelTargetFile;
    }

	@Override
    protected JPanel
    getNextPage( final JPanel panelPage )
    {
    	JPanel	panelPageNext = null;

    	if ( panelPage == null ) {
    	    panelPageNext = getFirstPage ();
    	}
    	else if ( panelPage == panelTargetFormat ) {
    	    	panelPageNext = panelTargetFile;
    	}
    	else {
    	    panelPageNext = null;
    	}

    	return panelPageNext;
    }

	@Override
    protected JPanel
    getPrevPage( final JPanel panelPage )
    {
    	JPanel	panelPagePrev = null;

    	if ( panelPage == null )
    	    panelPagePrev = getLastPage ();
    	else if ( panelPage == panelTargetFile )
    	    panelPagePrev = panelTargetFormat;
    	else
    	    panelPagePrev = null;

    	return ( panelPagePrev );
    }

	@Override
    protected boolean
    onPageActivate( final JPanel panelPage ) {
        String    strContentType;

    	if ( panelPage == panelTargetFormat )
    	{
            // ...
    	}
    	else if ( panelPage == panelTargetFile ) {
    	    // ...
    	}
        return ( true );
    }

	@Override
    protected boolean
    onFinish ()
    {
        boolean    boolResult;

        setCursor ( new Cursor(Cursor.WAIT_CURSOR) );
		boolResult = doSaveFile ();
        setCursor ( Cursor.getDefaultCursor() );

        return ( boolResult );
    }

    private void configureProcessor () {
        boolean         boolResult;

        if ( processor == null )
            return;

        // wait for processor to be configured
        boolResult = waitForState ( processor, Processor.Configured );
        System.out.println( "Configured processor" );
        if ( boolResult == false ) {
            JOptionPane.showMessageDialog(
            	frameOwner,
            	strFailMessage,
            	"Error",
            	JOptionPane.ERROR_MESSAGE
            );
            destroyProcessor ();
        }
    }

    private void
    realizeProcessor()
    {
        boolean         boolResult;

        if ( processor == null )
            return;

        // wait for processor to be configured
        boolResult = waitForState( processor, Processor.Realized );
        if ( boolResult == false ) {
            JOptionPane.showMessageDialog(
            	frameOwner,
            	strFailMessage,
            	"Error",
            	JOptionPane.ERROR_MESSAGE
            );
            destroyProcessor ();
        }
    }

    private void destroyProcessor ()
    {
        if ( processor == null )
            return;
		processor.removeControllerListener(this);
        processor.close ();
        processor = null;
    }

    Object stateLock = new Object();
    boolean stateFailed = false;

    private synchronized boolean
    waitForState(Processor p, int state)
    {
		StateListener sl;
		p.addControllerListener(sl = new StateListener());
        stateFailed = false;

        if (state == Processor.Configured) {
            p.configure();
        }
        else if (state == Processor.Realized) {
            p.realize();
        }

        while (p.getState() < state && !stateFailed) {
            synchronized (stateLock) {
                try {
                    stateLock.wait();
                }
                catch (InterruptedException ie) {
                    return false;
                }
            }
        }
		p.removeControllerListener(sl);
        return !stateFailed;
    }


    private boolean doSaveFile () {
        int                 i;
        DataSource          dsOutput;
        MediaLocator        mediaDest;
        Object              arrControls[];
        int                 nMediaDuration;
        String              strFileName;

        if ( processor == null )
            return ( false );

        panelTargetFormat.updateProcessorFormat ();
        realizeProcessor ();
        if ( processor == null )
            return ( false );

        dsOutput = processor.getDataOutput ();
        if ( dsOutput == null ) {
            JOptionPane.showMessageDialog(
            	frameOwner,
            	JMFI18N.getResource("jmstudio.error.processor.creatednooutput"),
            	"Error",
            	JOptionPane.ERROR_MESSAGE
            );
            
            destroyProcessor ();
            return ( false );
        }

        try {
            strFileName = panelTargetFile.getFileName ();
            mediaDest = new MediaLocator ( "file:" + strFileName );
            dataSinkSave = javax.media.Manager.createDataSink ( dsOutput, mediaDest );
        }
        catch ( Exception exception ) {
            stopSaving ();
            JOptionPane.showMessageDialog(
            	frameOwner,
            	exception,
            	"Error",
            	JOptionPane.ERROR_MESSAGE
            );
            return ( false );
        }

        try {
            dataSinkSave.addDataSinkListener( this );
            final MonitorControl monitorControl = ( MonitorControl )processor.getControl( "javax.media.control.MonitorControl" );
            
            Component monitor = null;
            if (monitorControl != null)
            {
            	monitor = monitorControl.getControlComponent();
            }

            final Time		duration		= processor.getDuration();
            final double	durationSecs	= duration.getSeconds();
            
            boolean	useMilli;
            if( durationSecs < 1d && durationSecs != 0d )
            {
            	useMilli = true;
            	nMediaDuration = ( int )( duration.getSeconds() * 1000d );
            }
            else
            {
            	useMilli = false;
            	nMediaDuration = ( int )duration.getSeconds();
            }
            

            dataSinkSave.open ();
            dataSinkSave.start ();
            processor.start ();

			System.out.println( "Duration is " + nMediaDuration );

            if ( nMediaDuration > 0
                    &&  duration != Duration.DURATION_UNBOUNDED
                    &&  duration != Duration.DURATION_UNKNOWN )
                dlgProgressSave = new ProgressDialog ( frameOwner,
						       JMFI18N.getResource("jmstudio.saveprogress.title"),
						       0, nMediaDuration, this );
            else
                dlgProgressSave = new ProgressDialog ( frameOwner,
						       JMFI18N.getResource("jmstudio.saveprogress.title"),
						       JMFI18N.getResource("jmstudio.saveprogress.label"),
						       monitor, this );

            dlgProgressSave.setVisible ( true );

            threadProgressSave = new ProgressThread ( processor, dlgProgressSave, useMilli );
            threadProgressSave.start ();

            vectorWindowsLeft.addElement ( dlgProgressSave );
        }
        catch ( Exception exception ) {
            stopSaving ();
            JOptionPane.showMessageDialog(
            	frameOwner,
            	exception,
            	"Error",
            	JOptionPane.ERROR_MESSAGE
            );
        }

        return ( true );
    }

    public void
    actionPerformed( final ActionEvent event )
    {
        String        strCmd;
        Object        objectSource;

        strCmd = event.getActionCommand ();
        if ( (strCmd.equals(ProgressDialog.ACTION_ABORT)
                        ||  strCmd.equals(ProgressDialog.ACTION_STOP))
                        &&  dataSinkSave != null ) {
            stopSaving ();
        }
        else if ( strCmd.equals(ProgressDialog.ACTION_PAUSE) &&  dataSinkSave != null ) {
            processor.stop ();
            dlgProgressSave.setPauseButtonText ( ProgressDialog.ACTION_RESUME );
            threadProgressSave.pauseThread ();
        }
        else if ( strCmd.equals(ProgressDialog.ACTION_RESUME) &&  dataSinkSave != null ) {
            processor.start ();
            dlgProgressSave.setPauseButtonText ( ProgressDialog.ACTION_PAUSE );
            threadProgressSave.resumeThread ();
        }
        else
            super.actionPerformed ( event );
    }

    public void controllerUpdate ( ControllerEvent event )
    {
        if ( event instanceof ControllerErrorEvent ) {
            strFailMessage = JMFI18N.getResource ( "jmstudio.error.controller" )
                    + "\n" + ((ControllerErrorEvent)event).getMessage ();
        }
        else if( event instanceof EndOfMediaEvent )
        {
			stopSaving();
        }
    }

    public void dataSinkUpdate(DataSinkEvent event)
    {
        if ( event instanceof EndOfStreamEvent )
        {
            closeDataSink();
//            MessageDialog.createInfoDialog ( frameOwner, "File has been saved." );
        } else if ( event instanceof DataSinkErrorEvent ) {
            stopSaving();
            JOptionPane.showMessageDialog(
            	frameOwner,
            	JMFI18N.getResource("jmstudio.error.processor.writefile"),
            	"Error",
            	JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void windowClosed ( WindowEvent event ) {
        Object          objSource;

        objSource = event.getSource ();
        if ( getAction().equals(WizardDialog.ACTION_CANCEL) )
            destroyProcessor ();
    }

    private void stopSaving () {
        if ( threadProgressSave != null ) {
            threadProgressSave.terminateNormaly ();
            threadProgressSave = null;
        }
        if ( processor != null ) {
            processor.stop ();
            destroyProcessor ();
        }
        if ( dlgProgressSave != null ) {
            dlgProgressSave.dispose ();
            dlgProgressSave = null;
        }
    }

    private synchronized void closeDataSink() {
        if (dataSinkSave != null) {
            dataSinkSave.close ();
            dataSinkSave = null;
        }
    }

    class StateListener implements ControllerListener {
        public void controllerUpdate(ControllerEvent ce) {
            if ( ce instanceof ControllerClosedEvent )
                stateFailed = true;
            if ( ce instanceof ControllerEvent ) {
                synchronized (stateLock) {
                    stateLock.notifyAll();
                }
            }
        }
    }

}


