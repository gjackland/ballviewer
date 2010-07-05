package uk.ac.ed.ph.ballviewer.video;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

import com.sun.media.util.JMFI18N;


public class ProgressDialog extends JMDialog {

    public static final String    ACTION_ABORT = JMFI18N.getResource("jmstudio.saveprogress.abort");
    public static final String    ACTION_STOP  = JMFI18N.getResource("jmstudio.saveprogress.stop");
    public static final String    ACTION_PAUSE = JMFI18N.getResource("jmstudio.saveprogress.pause");
    public static final String    ACTION_RESUME = JMFI18N.getResource("jmstudio.saveprogress.resume");

    private int               nMinPos = 0;
    private int               nMaxPos = 0;
    private String            strMessage = null;
    private ActionListener    listener;
    private ProgressBar       progressBar = null;
    private Label             labelProgress;
    private Button            buttonPause;
    private Component         component = null;


    public ProgressDialog(
    	final Frame				frame,
    	final String			strTitle,
    	final int				nMin,
    	final int				nMax,
    	final ActionListener	listener
    )
    {
        super ( frame, strTitle, false );

        nMinPos = nMin;
        nMaxPos = nMax;
        this.listener = listener;
        try {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProgressDialog (
    	final Frame				frame,
    	final String			strTitle,
    	final String			strMessage,
    	final Component			component,
    	final ActionListener	listener
    )
    {
        super ( frame, strTitle, false );

        this.strMessage = strMessage;
        this.listener = listener;
        this.component = component;
        try {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurPos ( int nPos ) {
        if ( progressBar != null ) {
            progressBar.setCurPos ( nPos );
            labelProgress.setText ( "" + progressBar.getCurPercent() + "%" );
            repaint ();
        }
        else {
            labelProgress.setText ( JMFI18N.getResource("jmstudio.saveprogress.saved")
                                + " " + nPos + " "
                                + JMFI18N.getResource("jmstudio.saveprogress.seconds") + "...");
        }
    }

    public void setPauseButtonText ( String strButton ) {
        buttonPause.setLabel ( strButton );
        this.repaint ();
    }

    private void init () throws Exception {
        JPanel          panel;
        JPanel          panelGrid;
        JPanel          panelComp;
        Button         button;
        Dimension      dimDialog;
        Dimension      dimScreen;


        this.setLayout ( new BorderLayout() );
        this.setBackground ( Color.lightGray );

        panel = new JPanel( new BorderLayout(6,6) );
        //((JMPanel)panel).setEmptyBorder ( 6, 6, 6, 6 );
        if ( nMaxPos > nMinPos ) {
            this.add ( panel, BorderLayout.CENTER );
            progressBar = new ProgressBar ( nMinPos, nMaxPos );
            panel.add ( progressBar, BorderLayout.CENTER );
            labelProgress = new Label ( "100%" );
            panel.add ( labelProgress, BorderLayout.EAST );
        }
        else if ( strMessage != null ) {
            this.add(panel, BorderLayout.CENTER);
            labelProgress = new Label ( strMessage );
            panel.add ( labelProgress, BorderLayout.NORTH );
            if (component != null) {
                panelComp = new JPanel( new FlowLayout() );
                panel.add(panelComp, BorderLayout.CENTER);
                panelComp.add(component);
            }
        }

        panel = new JPanel ( new FlowLayout(FlowLayout.CENTER) );
        this.add ( panel, BorderLayout.SOUTH );
        panelGrid = new JPanel ( new GridLayout(1,0,6,6) );
        panel.add ( panelGrid );
        buttonPause = new Button ( ACTION_PAUSE );
        buttonPause.addActionListener ( listener );
        panelGrid.add ( buttonPause );
        if (strMessage == null)
            button = new Button ( ACTION_ABORT );
        else
            button = new Button ( ACTION_STOP );
        button.addActionListener ( listener );
        panelGrid.add ( button );

        panel = new JPanel ();
        this.add ( panel, BorderLayout.NORTH );

        this.pack ();
        dimDialog = this.getPreferredSize ();
        this.setSize ( dimDialog );
        dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation ( (dimScreen.width - dimDialog.width) / 2, (dimScreen.height - dimDialog.height) / 2 );
        this.setResizable ( false );

        if (progressBar != null)
            labelProgress.setText ( "" + progressBar.getCurPercent() + "%" );
        repaint ();
    }

    public void windowClosing ( WindowEvent event ) {
        ActionEvent     eventAction;

        eventAction = new ActionEvent ( this, ActionEvent.ACTION_PERFORMED, ACTION_ABORT );
        listener.actionPerformed ( eventAction );
    }

}


