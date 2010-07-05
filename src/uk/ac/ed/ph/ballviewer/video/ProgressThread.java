package uk.ac.ed.ph.ballviewer.video;

import javax.media.*;


/**
* This thread class is used by SaveAsDialog to monitor the progress of saving
* the file and updating the Progress dialog.
*/
public class ProgressThread extends Thread {

    private final	Processor       processor;
    private final	ProgressDialog  dlgProgress;
    private final 	boolean 		useMilli;
    private 		boolean         boolTerminate = false;
    private 		boolean         boolSuspended = false;


    /**
    * This constructor creates object ProgressThread.
    * @param    processor      processor, that does file save
    * @param    dlgProgress    Progress dialog
    */
    public ProgressThread(
    	final Processor			processor,
    	final ProgressDialog	dlgProgress,
    	final boolean 			useMilli
    )
    {
        this.processor		= processor;
        this.dlgProgress	= dlgProgress;
        this.useMilli		= useMilli;
    }

    public synchronized void terminateNormaly () {
        boolTerminate = true;
	try {
	    this.interrupt();
	} catch (Exception ex) {}
    }
    
    public synchronized void pauseThread () {
        boolSuspended = true;
    }
    
    public synchronized void resumeThread () {
        boolSuspended = false;
        notify ();
    }
    
    
    /**
     *
     */
    public void run () {
        int    nPos;
	
        boolTerminate = false;
        while ( !boolTerminate && !this.isInterrupted() )
        {
            try {
                sleep ( 200 );
                if ( boolSuspended == true ) {
                    synchronized ( this ) {
                        while ( boolSuspended )
                            wait ();
                    }
                }
				if( useMilli )
				{
					nPos = ( int )( processor.getMediaTime().getSeconds() * 1000d );
				}
				else
				{
					nPos = ( int )processor.getMediaTime().getSeconds();
				}
		
				dlgProgress.setCurPos ( nPos );
            }
            catch ( Exception exception )
            {
				boolTerminate = true;
				break;
            }
        }
    }
}


