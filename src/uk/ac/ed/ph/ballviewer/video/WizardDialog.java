package uk.ac.ed.ph.ballviewer.video;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;

import com.sun.media.util.JMFI18N;


public class WizardDialog extends JMDialog /*implements WindowListener, ActionListener*/ {

    public static final String		ACTION_FINISH = JMFI18N.getResource("jmstudio.wizard.finish");
    public static final String		ACTION_CANCEL = JMFI18N.getResource("jmstudio.wizard.cancel");
    public static final String		ACTION_NEXT = JMFI18N.getResource("jmstudio.wizard.next");
    public static final String		ACTION_BACK = JMFI18N.getResource("jmstudio.wizard.back");

    private String      strResultAction = ACTION_CANCEL;
    private String      strImage;

    private JPanel		panelPages;
    private Button		buttonBack;
    private Button		buttonNext;
    private Button		buttonFinish;
    private Button		buttonCancel;

    private Vector< JPanel >	vectorPages = null;
    private JPanel				panelCurPage = null;
    private CardLayout  		layoutCard;

    protected Frame         	frameOwner = null;


    public WizardDialog ( Frame frameOwner, String strTitle, boolean boolModal, String strImage ) {
    	this ( frameOwner, strTitle, boolModal, strImage, null );
    }

    public WizardDialog(
    	final Frame		frameOwner,
    	final String	strTitle,
    	final boolean	boolModal,
    	final String	strImage,
    	final JPanel	arrPages[] )
    {
    	super ( frameOwner, strTitle, boolModal );

        this.frameOwner = frameOwner;
        this.strImage = strImage;
    	try {
    	    init ();
    	}
    	catch ( Exception exception ) {
    	    exception.printStackTrace ();
    	}
    	setPages ( arrPages );
    }

    public String getAction () {
    	return ( strResultAction );
    }

    public JPanel
    getCurrentPage ()
    {
    	return ( panelCurPage );
    }

    public void
    setPages( final JPanel arrPages[] ) {
    	int		i;
    	int		nCount;

    	if( arrPages != null )
    	{
    	    panelCurPage = null;
    	    nCount = arrPages.length;
    	    vectorPages = new Vector< JPanel >( nCount );
    	    for ( i = 0;  i < nCount;  i++ )
    	    {
    	    	vectorPages.addElement( arrPages[i] );
    	    	arrPages[ i ].setName( "Page " + i );
    	    }
    	    	
    	}

    	setNextPage ();
    }

    protected void
    init()
    throws Exception
    {
    	JPanel		panel;
    	JPanel		panelContent;
        ImageArea       imageArea;
        Image           image;

    	this.setLayout ( new BorderLayout(6,6) );
        this.setResizable ( false );
//    	this.addWindowListener ( this );

    	panelContent = new JPanel ( new BorderLayout(6,6) );
        panelContent.setBackground ( Color.lightGray );
    	this.add ( panelContent, BorderLayout.CENTER );

        if ( strImage != null ) {
            panel = new JPanel ( new BorderLayout() );
    	    panelContent.add ( panel, BorderLayout.WEST );

            image = ImageArea.loadImage ( strImage, this, true );
            imageArea = new ImageArea ( image );
            imageArea.setInsets ( 12, 12, 12, 12 );
    	    panel.add ( imageArea, BorderLayout.NORTH );
        }

    	layoutCard = new CardLayout ( 6, 6 );
    	panelPages = new JPanel ( layoutCard );
    	panelContent.add ( panelPages, BorderLayout.CENTER );

    	panel = createPanelButtons ();
    	panelContent.add ( panel, BorderLayout.SOUTH );
    }

    private JPanel
    createPanelButtons()
    {
    	JPanel	panel;
    	JPanel	panelButtons;

    	panelButtons = new JPanel ( new FlowLayout(FlowLayout.RIGHT) );

    	panel = new JPanel ( new GridLayout(1,0,6,6) );
    	panelButtons.add ( panel );

    	buttonBack = new Button ( ACTION_BACK );
    	buttonBack.addActionListener ( this );
    	panel.add ( buttonBack );

    	buttonNext = new Button ( ACTION_NEXT );
    	buttonNext.addActionListener ( this );
    	panel.add ( buttonNext );

    	buttonFinish = new Button ( ACTION_FINISH );
    	buttonFinish.addActionListener ( this );
    	panel.add ( buttonFinish );

    	buttonCancel = new Button ( ACTION_CANCEL );
    	buttonCancel.addActionListener ( this );
    	panel.add ( buttonCancel );

    	return ( panelButtons );
    }

    protected void
    setNextPage ()
    {
    	JPanel	panelPage;

    	if ( panelCurPage != null  &&  onPageDone(panelCurPage) == false )
            return;
    	panelPage = getNextPage ( panelCurPage );
    	setPage ( panelPage );
    }

    protected void
    setPrevPage () {
    	JPanel	panelPage;

    	panelPage = getPrevPage ( panelCurPage );
    	setPage ( panelPage );
    }

    private void
    setPage( final JPanel panelPage )
    {
    	if ( panelPage == null )
    	    return;

		System.out.println( "Set page got called with " + panelPage );

    	panelCurPage = panelPage;
    	onPageActivate ( panelCurPage );

    	panelPages.add ( panelCurPage, panelCurPage.getName() );
    	layoutCard.show ( panelPages, panelCurPage.getName() );

    	if ( isFirstPage(panelCurPage) ) {
    	    buttonBack.setEnabled ( false );
    	    if ( getFocusOwner() == buttonBack )
    	    	buttonNext.requestFocus ();
    	}
    	else {
    	    buttonBack.setEnabled ( true );
    	}

    	if ( isLastPage(panelCurPage) ) {
    	    buttonNext.setEnabled ( false );
            buttonFinish.setEnabled ( true );
    	    if ( getFocusOwner() == buttonNext )
    	    	buttonFinish.requestFocus ();
    	}
    	else {
    	    buttonNext.setEnabled ( true );
            buttonFinish.setEnabled ( false );
    	}

    	this.validate ();
    	panelCurPage.validate ();
    }

    protected boolean
    onPageDone( final JPanel panelPage )
    {
        return ( true );
    }

    protected boolean
    onPageActivate( final JPanel panelPage ) {
        return ( true );
    }

    protected boolean onFinish () {
        return ( true );
    }

    protected JPanel
    getFirstPage() {
    	JPanel	panelPage = null;

    	if ( vectorPages != null  &&  !vectorPages.isEmpty() ) {
    	    panelPage = ( JPanel ) vectorPages.firstElement ();
    	}
    	return ( panelPage );
    }

    protected JPanel
    getLastPage() {
    	JPanel	panelPage = null;

    	if ( vectorPages != null  &&  !vectorPages.isEmpty() ) {
    	    panelPage = vectorPages.lastElement ();
    	}
    	return ( panelPage );
    }

    protected JPanel
    getNextPage( final JPanel panelPage ) {
    	int		nIndex;
    	JPanel	panelPageNext = null;

    	if ( panelPage == null ) {
    	    panelPageNext = getFirstPage ();
    	}
    	else if ( vectorPages != null  &&  !vectorPages.isEmpty() ) {
    	    nIndex = vectorPages.indexOf ( panelPage );
    	    if ( nIndex >= 0  &&  nIndex < vectorPages.size() - 1 )
    	    	panelPageNext = vectorPages.elementAt ( nIndex + 1 );
    	}
    	return ( panelPageNext );
    }

    protected JPanel
    getPrevPage( final JPanel panelPage ) {
    	int		nIndex;
    	JPanel	panelPagePrev = null;

    	if ( panelPage == null ) {
    	    panelPagePrev = getLastPage ();
    	}
    	else if ( vectorPages != null  &&  !vectorPages.isEmpty() ) {
    	    nIndex = vectorPages.indexOf ( panelPage );
    	    if ( nIndex > 0  &&  nIndex < vectorPages.size() )
    	    	panelPagePrev = vectorPages.elementAt ( nIndex - 1 );
    	}
    	return ( panelPagePrev );
    }

    protected boolean
    isFirstPage( final JPanel panelPage )
    {
    	boolean		boolResult;

    	boolResult = (panelPage == getFirstPage());
    	return ( boolResult );
    }

    protected boolean
    isLastPage( final JPanel panelPage )
    {
    	boolean		boolResult;

    	boolResult = (panelPage == getLastPage());
    	return ( boolResult );
    }

    public void actionPerformed ( ActionEvent event ) {
    	String		strCmd;

    	strCmd = event.getActionCommand ();
    	if ( strCmd.equals(ACTION_FINISH)  ||  strCmd.equals(ACTION_CANCEL) ) {
    	    if ( strCmd.equals(ACTION_FINISH) ) {
    	    	if ( onPageDone(panelCurPage) == false )
                    return;
    	    	if ( onFinish() == false ) {
                    setPage ( getFirstPage() );
                    return;
                }
    	    }
    	    strResultAction = strCmd;
    	    this.dispose ();
    	}
    	else if ( strCmd.equals(ACTION_BACK) ) {
    	    setPrevPage ();
    	}
    	else if ( strCmd.equals(ACTION_NEXT) ) {
    	    setNextPage ();
    	}
    }

    public void windowOpened ( WindowEvent event ) {
    }

    public void windowClosing ( WindowEvent event ) {
    	this.dispose ();
    }

    public void windowClosed ( WindowEvent event ) {
    }

    public void windowIconified ( WindowEvent event ) {
    }

    public void windowDeiconified ( WindowEvent event ) {
    }

    public void windowActivated ( WindowEvent event ) {
    }

    public void windowDeactivated ( WindowEvent event ) {
    }


}


