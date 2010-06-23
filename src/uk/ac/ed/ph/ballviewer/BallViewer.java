package uk.ac.ed.ph.ballviewer;

import java.awt.*;

import java.awt.event.*;

import java.awt.geom.*;

import java.awt.image.*;

import java.text.*;

import javax.swing.*;

import java.util.Date;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.ed.ph.ballviewer.analysis.*;

import uk.ac.ed.ph.ballviewer.gui.*;

import uk.ac.ed.ph.ballviewer.io.*;

import uk.ac.ed.ph.ballviewer.math.*;

import uk.ac.ed.ph.ballviewer.event.AttributeAttachEvent;
import uk.ac.ed.ph.ballviewer.event.AttributeAttachListener;
import uk.ac.ed.ph.ballviewer.event.EventDispatcher;
import uk.ac.ed.ph.ballviewer.event.TimelineEvent;
import uk.ac.ed.ph.ballviewer.event.TimelineListener;

/** 
 * This is a viewer for 3d configurations of objects, especially balls. <br>
 * Gets input from mouse dragging and from input components
 * (buttons, textfields, checkboxes) in order to adjust the display.
 */
public class BallViewer extends JFrame implements
ActionListener, ItemListener, TextListener, MouseListener, MouseMotionListener, MouseWheelListener,
AttributeAttachListener, ChangeListener
{
	private static final	String							title						= "Ball Viewer";

	// The framework that underpins the GUI
	private final			BallViewerFramework				framework;
	                                                    	
	// The menu bar                 		            	
	private final			JMenuBar						mBar						= new JMenuBar();
	private final			JMenu							mFile						= new JMenu( "File" );
	private final			AnalysisMenu					mBarAnalysis;
	
	//Graphics//
	private					Canvas							canv;
	private					BufferStrategy					buff; 

	//Frame,Canvas,graph details//
	private	static			int 							offX = 50, offY = 0;			// position of frame (top,left corner) //
	protected				int								frw, frh, fr0x,fr0y;     		// canvas details //	
	protected				double							xmin, xmax, xrange, xsc;  		// graph details //
	protected				double							ymin, ymax, yrange, ysc;	
	protected				double							ballsize					= 1.0;		// scale factors//	
	protected				double							scale						= 1.0;
	//Ball sets are replaced by a CellLattice//
	protected				CellLattice						cells;
	
	//Transformation by mouse dragging and wheel
	protected final			double							pixPerRad		= 100;				//if dragged through pixPerRad, get a 1 rad rotation
	final					double							unitsPerClick	= -0.5; 			// if wheel clicks down, get a -0.2 translation
	protected 				Point 							anchor;								//start position of drag
	protected				Point 							pos;								//current/final position of drag
	protected				Transform						temp		= new Transform();		// the temporary (dragging) transform
	protected				Transform						base		= new Transform();		// the accumulation of previous temporary transforms
	protected				Transform						transform	= new Transform();		// the actual, current transform ( =temp.base )
	
	// GUI stuff //
	protected static final	Color							labelColor	= new Color ( 0, 64, 0 );
	
	//Control Panel - has 2 rows of components
	private final			Panel							control;
	private final			JSlider							sTimeline		= new JSlider( JSlider.HORIZONTAL, 0, 1, 0 );		// Timeline slider
	private final			Button							imgCaptureBtn	= new Button( "Save Image" );			// "Save Image"   1st row is always available
	private final			TextField						ballsizeTxt		= new TextField( Double.toString( ballsize ), 2);		// "ball size"
	private final			TextField						scaleTxt		= new TextField( Double.toString( scale ), 2 );			// "scale"
	private final			Checkbox						perspectiveChk	= new Checkbox( "Perspective", false );	// "perspective"
	private	final			DragPad							zARDP			= new DragPad();						// "z axis DragPad"
	private	final			Checkbox 						sliceChk		= new Checkbox( "Slice",false ),
															frontMarginChk	= new Checkbox( "Front margin", false ),
															backMarginChk	= new Checkbox( "Back margin", false );	// 2nd row - most can be disabled
	private final			Label 							sliceDepthLbl	= new Label( "Depth", Label.RIGHT ),
															sliceWidthLbl	= new Label( "Width", Label.RIGHT ),
															frontMarginLbl	= new Label( "Width", Label.RIGHT ),
															backMarginLbl	= new Label( "Width", Label.RIGHT );
	private	final			TextField						sliceDepthTxt,
															sliceWidthTxt,
															frontMarginTxt,
															backMarginTxt;
	                    	                    			
	//Info Panel        	                    			
	private final			Panel							info;
	private final			Button							setZdirBtn		= new Button( "Set Z dir" );
	private	final			Vector3Panel					xVctr 			= new Vector3Panel( "X", 4 ),
															yVctr 			= new Vector3Panel( "Y", 4 ),
															zVctr 			= new Vector3Panel( "Z", 4 ),
															pVctr 			= new Vector3Panel( "P", 3 );


	// Sidebar with auxilary information
	private final			JPanel							pSidebar		= new JPanel();
	
	// System objects side panel											
	private final			SystemObjectsPanel				pSystemObjects;
	
	protected 				boolean							sliceOn = false, ffadeOn = false, bfadeOn = false;
	protected 				boolean							perspective = false;
	
	public BallViewer()
	{
		this( -8, -5, 8, 5 );
	}
	
	public BallViewer(
		final File[]			inputFiles
	)
	{
		this();
		
		newExperimentRecordFromFiles( inputFiles );
	}
	
	/** 
	 * Creates a new BallViewer. <br>
	 * Assigns title. Displays contents of <code>cellsIn</code>. <br>
	 * Sets the dimensions of the projection screen, as if it was at z=0.0 relative to camera.
	 */
	protected BallViewer(
		final double		xmn,
		final double		ymn,
		final double		xmx,
		final double		ymx
	)
	{
		// set up the frame //
		super( title );
		framework		= new BallViewerFramework();
		
		// Register ourselves to receive message from the dispatcher
		BallViewerFramework.eventDispatcher.listen( AttributeAttachEvent.class, this );
		
		// Allow menus to overlap over drawing canvas
		JPopupMenu.setDefaultLightWeightPopupEnabled( false );
		
		this.setBackground( Color.green );
		this.setLocation( offX, offY ); offX+= 0; offY+=50;
		this.setResizable( false );
		this.addWindowListener( new WindowAdapter ()
		{ 
			public void windowOpened(WindowEvent e)
			{	// When opened for 1st time,
				if( buff==null )
				{
					canv.createBufferStrategy(2);   			//  make a buffer strategy - see canv below
					buff=canv.getBufferStrategy();  			//  (seem to have to do this after it is visible)
				}
				drawBalls();
			}
			public void windowClosing( WindowEvent e ) { e.getWindow().dispose(); System.exit(0); }
		});

		pSystemObjects = new SystemObjectsPanel( framework );
		
		// Set up the sidebar
		pSidebar.setLayout( new BoxLayout( pSidebar, BoxLayout.Y_AXIS ) );
		pSidebar.add( pSystemObjects );

		// set up graph details
		xmin=xmn; xmax=xmx; ymin=ymn; ymax=ymx; //graph corners
		xrange = xmax-xmin; yrange = ymax-ymin; //graph size	

		final int 		frwDefault		= 900;
		final int 		frhDefault		= 550;
		final double	aspectDefault	= ( double )frwDefault / frhDefault;
		final double	aspect			= xrange/yrange;		// canvas size must be proportional to graph, 	
		if( aspect > aspectDefault )		// but doesn't go outside default bounds
		{
		   	frw = (int)frwDefault;
		   	frh = (int)(frwDefault/aspect);
		}
		else
		{
			frh = (int)frhDefault;
			frw = (int)(frhDefault*aspect);
		}
		xsc	= frw/xrange; ysc = frh/yrange;
		fr0x = (int)(xmin*xsc); fr0y = (int)( ymin * ysc ); // frame coordinates of origin
		
		mBarAnalysis	= new AnalysisMenu( this, framework.getAnalysisManager() );
		// Set up the menu bar
		setupMenuBar();
				  	
		// set up a canvas //
		canv = new Canvas() { public void paint(Graphics g) { drawBalls(); } };
		canv.setSize(frw,frh);
		canv.setBackground(Color.white);
		canv.addMouseListener(this);
		canv.addMouseMotionListener(this);  //see methods below
		canv.addMouseWheelListener(this);   //see methods below
		canv.setVisible(true);
		
		// set up the info panel //
		info = new Panel (new GridLayout(1,5));
		info.setForeground( labelColor );

		updateInfoFields();
		info.add(xVctr); info.add(yVctr); info.add(zVctr); info.add(pVctr);
		
		// set up a control panel - looks absolutely horrendous//
		control = new Panel( new GridLayout( 3, 1 ) );
		control.setForeground( labelColor );
		
		// TIMELINE SLIDER /////////////////////
		sTimeline.setEnabled( false );
		sTimeline.setMajorTickSpacing( 1 );
		sTimeline.setSnapToTicks( true );
		sTimeline.setPaintLabels( true );
		sTimeline.addChangeListener( this );
		control.add( sTimeline );
		
		Panel row, p1,p2,p3,p4,p5;
		
		final GridBagLayout			gbl = new GridBagLayout();
		final GridBagConstraints	gbc = new GridBagConstraints();
		row = new Panel(gbl);
		p1 = new Panel();	p2 = new Panel(); p3 = new Panel(); p4 = new Panel(); p5 = new Panel();
		imgCaptureBtn.addActionListener(this);
		setZdirBtn.addActionListener(this);		
		ballsizeTxt.addTextListener(this); 
		scaleTxt.addTextListener(this);
		perspectiveChk.addItemListener(this); 
		zARDP.setForeground( labelColor );
		zARDP.addMouseListener(this);	zARDP.addMouseMotionListener(this);
		p1.add(imgCaptureBtn);
		p2.add(setZdirBtn);
		p3.add(new Label("Ball size:",Label.RIGHT)); p3.add(ballsizeTxt);
		p4.add(new Label("Scale:",Label.RIGHT)); p4.add(scaleTxt);
		p5.add(new Label("",Label.RIGHT)); p5.add(perspectiveChk);
		row.add(p1); row.add(p2); row.add(p3); row.add(p4); row.add(p5);
		gbc.weightx = 1.0; gbc.fill = gbc.BOTH; 
		gbl.setConstraints(zARDP,gbc); row.add(zARDP);  // All this just to get a decent size for zARDP
		control.add(row);
		
		row = new Panel( new GridLayout(1,3)); 
		p1 = new Panel();		p2 = new Panel();		p3 = new Panel();

		(sliceDepthTxt = new TextField(""+depthDefault)).addTextListener(this);
		(sliceWidthTxt = new TextField(""+widthDefault)).addTextListener(this);
		(frontMarginTxt= new TextField(""+marginDefault)).addTextListener(this);
		(backMarginTxt = new TextField(""+marginDefault)).addTextListener(this);
		sliceChk.addItemListener( this );
		frontMarginChk.addItemListener(this);
		backMarginChk.addItemListener(this);	
		p1.add(sliceChk); p1.add(sliceDepthLbl);p1.add(sliceDepthTxt); 
			p1.add(sliceWidthLbl);p1.add(sliceWidthTxt);
		p2.add(frontMarginChk); p2.add(frontMarginLbl);	p2.add(frontMarginTxt);	
		p3.add(backMarginChk); p3.add(backMarginLbl);	p3.add(backMarginTxt);
		row.add(p1); row.add(p2); row.add(p3);
		control.add(row);
		updateCheckboxes();
		readInputs();
		
		// put the infobar, canvas and control panel together
		final JPanel mainPanel = new JPanel( new BorderLayout() );
		mainPanel.add( info,BorderLayout.NORTH );
		mainPanel.add( canv,BorderLayout.CENTER );
		mainPanel.add( control,BorderLayout.SOUTH );
		
		this.add( mainPanel, BorderLayout.CENTER );
		this.add( pSidebar, BorderLayout.EAST );
		
		this.pack();    // this fits the frame around it all(neat)
		
		this.setVisible(true);
	}
	
	private void
	setupMenuBar()
	{
		mBar.add( mBarAnalysis );
		this.setJMenuBar( mBar );
	}
	
	
	private boolean
	newExperimentRecordFromFiles(
		final File[]		inputFiles
	)
	{
		ArrayList< Analyser >	analysers = new ArrayList< Analyser >();
		ExperimentRecord newExperimentRecord = framework.getReaderManager().getStaticSystem( inputFiles, analysers );
		if( newExperimentRecord != null )
		{
			return newExperimentRecord( newExperimentRecord, analysers );
		}
		return false;
	}
	
	private boolean
	newExperimentRecord(
		final ExperimentRecord			newExperimentRecord,
		final Collection< Analyser >	analysers
	)
	{
		// Tell the framework that we've got a new system
		framework.newExperimentRecord( newExperimentRecord );
		// Update any system specific analysers
		framework.getAnalysisManager().addAnalysers( analysers );
		// Get the menu bar to update its list of analysers
		mBarAnalysis.updateAnalysers();
		
		
		// TIMELINE //////////
		// Only enabled the timeline if an experiment has more than one sample
		final int numSamples = newExperimentRecord.getNumerOfSamples();
		sTimeline.setEnabled( numSamples > 1 );
		if( numSamples > 1 )
		{
			sTimeline.setMinimum( 0 );
			sTimeline.setMaximum( numSamples - 1 );
		}
		
		// associate a cellLattice with the viewer.
		cells = framework.getSystem().getCellLattice();
		// Set the camera position to the centre of the system
		this.setCameraPosition( framework.getSystem().getCentre() );
		
		// Update the system objects panel
		pSystemObjects.update();
		
		return true;
	}
	
	void updateCheckboxes()
	{
		boolean boo = sliceChk.getState();
		boolean boo2 = frontMarginChk.getState();		
		boolean boo3 = backMarginChk.getState();
		sliceDepthLbl.setEnabled(boo);	sliceWidthLbl.setEnabled(boo);
		sliceDepthTxt.setEnabled(boo);	sliceWidthTxt.setEnabled(boo); 
		frontMarginChk.setEnabled(boo); 			backMarginChk.setEnabled(boo);
		frontMarginLbl.setEnabled(boo&boo2);	backMarginLbl.setEnabled(boo&boo3);	
		frontMarginTxt.setEnabled(boo&boo2);	backMarginTxt.setEnabled(boo&boo3);
		sliceOn = boo;
		ffadeOn = boo2;
		bfadeOn = boo3;
		perspective = perspectiveChk.getState();
	}		

	final double depthDefault=0.0, 	widthDefault = 1.6, 		marginDefault = 1.0;
	double fslice = depthDefault -0.5*widthDefault; 	
	double bslice = depthDefault -0.5*widthDefault;
	double ffade = fslice -marginDefault;	
	double bfade = bslice +marginDefault; 	
	void readInputs()
	{	// if any part fails, just ignore it (use old value)
		double depth,width, fmargin,bmargin;
		try
		{
			scale = new Double(scaleTxt.getText()).doubleValue();
			xsc = scale * frw/xrange; ysc = scale * frh/yrange; 
		} catch (Exception e) {}
		try
		{
			ballsize = new Double(ballsizeTxt.getText()).doubleValue(); 
			Ball.newDiameter = ballsize*scale*Ball.DEFAULT_DIAMETER;
		} catch (Exception e) {}
		try
		{        
			depth = new Double(sliceDepthTxt.getText()).doubleValue(); 
			width = Math.abs(new Double(sliceWidthTxt.getText()).doubleValue() ); 
			fslice = depth -0.5*width;	bslice = depth +0.5*width;
		} catch (Exception e) {}
		try
		{
			fmargin = Math.abs(new Double(frontMarginTxt.getText()).doubleValue() ); 
			ffade = fslice - fmargin;
		} catch (Exception e) {}
		try
		{	
			bmargin = Math.abs(new Double(backMarginTxt.getText()).doubleValue() ); 
			bfade = bslice + bmargin;
		} catch (Exception e) {}
	}
	
	private void
	updateInfoFields()
	{
		double [][] r = transform.m.r;
		xVctr.updateFields(r[0][0],r[0][1],r[0][2]);
		yVctr.updateFields(r[1][0],r[1][1],r[1][2]);
		zVctr.updateFields(r[2][0],r[2][1],r[2][2]);
		pVctr.updateFields(getCameraPosition());
	}
	
	/** Sets the internal transformation as if the camera was placed at this position. */
	public void setCameraPosition(Vector3 camPos) {	// these are extremely messy, but do make sense
		base		= new Transform(base.m,transform.inverse().appliedTo(camPos.times(-1)));
		transform	= Transform.join(temp,base);
	}
	/** Gets the camera position that corresponds to the internal transformation. */
	public Vector3 getCameraPosition() {
		return transform.inverse().appliedTo(new Vector3()).times(-1);
	}
	
	public void setCameraDirection(Matrix3 dir) {	
		base = new Transform(dir,base.t);
		transform = Transform.join(temp,base);
		updateInfoFields();
		drawBalls();
	}
	public Matrix3 getCameraDirection() {
		return transform.m;
	}
	public void setPerspective(boolean boo) { perspective = boo; }
	
	// Draw the thing! called by canv.paint, 
	// windowActivated, mouseDragged, mouseWheelMoved, itemStateChanged, textValueChanged
	/** Updates the ball display. */
	public void drawBalls()
	{ 					 
		if( buff!=null )
		{ 
			drawBalls((Graphics2D)buff.getDrawGraphics()); 
			buff.show(); 
		}
	}
	/** Draws the balls in the given graphics context */
	public void drawBalls( Graphics2D g )    // used to display and also to "Save Image")
	{
		if( cells != null )
		{
			final Ball pinkMarker = new Ball(.01,Color.pink);
			final int n = 5;			
			
			g.clearRect( 0, 0, frw, frh );					// wipe it clean
			g.translate(-fr0x, -fr0y );
			
			final SortTree tr = new SortTree();	
			tr.addNode( pinkMarker, 0.0 );		      // adds a pink marker always at origin
			
			// Get whichever set of objects you wish, and add them to SortTree //
	
//			for( int ix=1; ix <= cells.X(); ix++ )
//			{
//				for( int iy=1; iy <=cells.Y(); iy++ )
//				{
//					for( int iz = 1; iz <=cells.Z(); iz++ )
//					{ 
//						final ArrayList< Positionable > temp = cells.getCellObjectList(ix,iy,iz); 
//						for( final Positionable obj : temp )
//						{
//							if( obj instanceof Ball )
//							{   // we transform Balls and Arrows differently
//								Ball b = ( Ball )obj;
//								b = new Ball( transform.appliedTo( b.pos ), b.getColour() ); 
//								tr.addNode( b,b.pos.z ); 			// and then put them into the SortTree
//							}
//							else if( obj instanceof Arrow )
//							{
//								Arrow a = ( Arrow )obj;
//								a = new Arrow (transform.appliedTo(a.v1),transform.appliedTo(a.v2));
//								tr.addNode(a,a.ctr.z-0.001*cells.cellSize());	
//							}		// ditto, but place arrow cameraside of the ball
//						}
//					}
//				}
//			}
			
			Ball[] balls = framework.getSystem().p;
			// TODO: Nasty nastyness here shouldn't be creating copies!
			// But probably best waiting till I change to renderer to fix this
			for( Ball b : balls )
			{
				final Ball bCopy	= new Ball( transform.appliedTo( b.pos ), b.getColour() );
				bCopy.setDiameterOffset( b.getDiameterOffset() );
				bCopy.setAlpha( b.getAlpha() );
				tr.addNode( bCopy, bCopy.pos.z );
			}
			
			// Alternatively can input a set of balls and just do tr.addNode(b[i],b[i].pos.z) //
			tr.resetTraverser();
			final double camdepth = -30.0;
			
			for( int i=0; i < tr.total; i++ )
			{
				tr.findNextLargest();
				final Object obj = tr.object();		
				double z=tr.value();
				
				if( obj instanceof Ball )
				{
					Ball b = (Ball)obj;
					double zsc;
					if (perspective)
					{
						if (z<0.5*camdepth) break;   		// we can dump the remainder (depthSort)
						zsc = -camdepth/(z-camdepth); 	// < this is such that zsc = 1.0 at z=0;
					}
					else zsc = 1.0;
				
					double d=( b.diameter + b.getDiameterOffset() ) * zsc;
					
					//System.out.println( "Diameter: " + b.diameter + " offset: " + b.getDiameterOffset() );
					
					final double r=d/2;
					final double cx = b.pos.x*zsc*xsc;
					final double cy = b.pos.y*zsc*ysc;
					final Color bColor = b.getColour();
					g.setColor( new Color( bColor.getRed(), bColor.getGreen(), bColor.getBlue(), ( int )( b.getAlpha() * 25.5d ) ) );
				
					if (!sliceOn || (z>fslice && z<bslice))
					{
						g.fillOval((int)(cx-r),(int)(cy-r),(int)d,(int)d);
						g.setColor(Color.black);
						g.drawOval((int)(cx-r),(int)(cy-r),(int)(d-1),(int)(d-1));
					}
					else if ( (ffadeOn && z>ffade && z<fslice) || (bfadeOn && z>bslice && z<bfade) )
					{
						g.drawOval((int)(cx-d/2)+1,(int)(cy-d/2)+1,(int)d-3,(int)d-3);
						g.setColor(Color.black);
						g.drawOval((int)(cx-d/2),(int)(cy-d/2),(int)d-1,(int)d-1);
					}
				}
				else if( obj instanceof Arrow )
				{
					Arrow a = (Arrow)obj;
					if (perspective) { // ends could be at different depths: should be done separately
						if (a.v1.z < 0.2*camdepth || a.v2.z < 0.2*camdepth) continue;   
						a.v1.perspectivise(camdepth); // we can merrily ruin these 
						a.v2.perspectivise(camdepth); //  temporary arrows
					}
					if (!sliceOn || (z>fslice && z<bslice)) {
						int x1 = (int)(a.v1.x*xsc);
						int y1 = (int)(a.v1.y*ysc);
						int x2 = (int)(a.v2.x*xsc);
						int y2 = (int)(a.v2.y*ysc);
						g.setColor(Color.yellow);
						g.drawLine(x1,y1,x2,y2);
					}
				}				
			}
			g.setColor( labelColor ); g.translate(fr0x,fr0y); // sets translation back to (0,0)
		}
		g.drawRect(0,0,frw-1,frh-1);
		
		g.dispose();
	}
	
	public static void main( String arg[] )
	{	
		System.out.println( "\nStarting App: " + new Date() );
		
		String inputname="", outputname="";
		if( arg.length==0 )
		{ 
			new BallViewer();
		}
		else if (arg.length==1) { 
			inputname = arg[0];
			outputname = changeFileExtension(arg[0],".dun");
			final File[] inputFiles = { new File( inputname ) };
			new BallViewer(  inputFiles );
		}
		else if (arg.length >= 2 )
		{
			final File[] inputFiles = new File[ arg.length ];
			for( int i = 0; i < arg.length; ++i )
			{
				inputFiles[ i ] = new File( arg[ i ] );
			}
			new BallViewer( inputFiles );
		}
	}
	
	private static String changeFileExtension( String fnm, String ext )
	{ // fnm = filename, ext = extension
		int di = fnm.lastIndexOf("."); 	// dot index
		if (di == -1 || di < fnm.lastIndexOf("/") ) {	// if there is no dot in the name //
			 return fnm+ext; 							// or if last dot isn't in a directory name //
		}
		else
		{
			return fnm.substring(0,di)+ext; 
		}
	}
	
	// INTERFACES /////////////////////////////////////////////////////////////
	
	@Override
	public void
	stateChanged( final ChangeEvent e )
	{
		if( e.getSource() == sTimeline )
		{
		    // The slider has changed value		    
		    framework.tmpSetCurrentSample( sTimeline.getValue() );
		    
		    // Fire a timeline changed event
		    framework.eventDispatcher.notify( new TimelineEvent( sTimeline.getValue() ) );
		    
		    drawBalls();
		}
	}
	
	// ActionListener interface //
	/** 
	 * Responds to the "Save Image" button by saving an image. 
	 * @see JpegCreator 
	 */
	@Override
	public void
	actionPerformed( final ActionEvent e )
	{
		if (e.getSource()==setZdirBtn) {
			try {
				Vector3 xAxis = xVctr.readFields();
				Vector3 yAxis = yVctr.readFields();
				Vector3 zAxis = zVctr.readFields();
				if (Math.abs(zAxis.dot(xAxis)) < Math.abs(zAxis.dot(yAxis))) {
					yAxis = zAxis.cross(xAxis);
					xAxis = yAxis.cross(zAxis);
				}
				else {
					xAxis = yAxis.cross(zAxis);
					yAxis = zAxis.cross(xAxis);
				}
				xAxis.normalise(); yAxis.normalise(); zAxis.normalise();
				base.m = new Matrix3(xAxis, yAxis, zAxis);
				transform = base;
				updateInfoFields();
				drawBalls();
			} catch (Exception err) {} // if it fails at any point, stop: normal to fail at readFields()	
		}
		else if (e.getSource()==imgCaptureBtn) {
			BufferedImage bimg = new BufferedImage(frw,frh,BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bimg.createGraphics();
			g.addRenderingHints( // antialiasing produces a much smoother picture
				new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON) );
			drawBalls(g); 						// draw the image 
			JpegCreator.saveImage(bimg);  // and save it
		}
	}
	
	// ItemListener interface //
	/** Responds to the checkboxes, reads inputs and updates display. */
	public void itemStateChanged(ItemEvent e)
	{  
		updateCheckboxes();
		readInputs();
		drawBalls();
	}
	
	// TextListener interface //
	/** Responds to the textfields by reading them and updates display. */
	public void textValueChanged (TextEvent e)
	{
		readInputs();
		drawBalls();
	}
	
	// MouseListener interface - unused methods must also be declared //
	/** Responds a mouse button press by creating an anchor point for dragging. */
	public void mousePressed(MouseEvent e)
	{	anchor = e.getPoint();	}
	
	/** Responds a mouse button release by saving the current transform. */
	public void mouseReleased(MouseEvent e)
	{ 
		mouseDragged(e);	base = transform; // do final drag then save this transform as the base
	}
	
		/** Not used. */ public void mouseClicked(MouseEvent e) {} 
		/** Not used. */ public void mouseEntered(MouseEvent e) {}
		/** Not used. */ public void mouseExited(MouseEvent e) {}
	
	// MouseMotionListener interface - unused methods must also be declared //
	/** 
	 * Responds to mouse dragging. <br> 
	 * If left-dragging on the canvas, does rotation. <br>
	 * If right-dragging on the canvas, does translation. <br>
	 * If dragging on the DragPad, does z-axis rotation. <br>
	 * Updates display, including direction/position vectors (top row).
	 */
   public void mouseDragged(MouseEvent e)
   {	// responds to mouse dragging
		pos=e.getPoint();
		if (e.getSource() == canv) {					
			if (pos.equals(anchor)) { temp = new Transform(); } //ie does nothing
			else {
				final int LEFT = MouseEvent.BUTTON1_DOWN_MASK;
				final int RIGHT = MouseEvent.BUTTON3_DOWN_MASK;
				int flags = e.getModifiersEx();
				if ((flags & LEFT)==LEFT) { // if left dragging
					Vector3 sweep = new Vector3(pos.x-anchor.x,pos.y-anchor.y,0);
					Vector3 axis = sweep.cross(new Vector3(0,0,1)); // get a perp. axis
					double angle=sweep.modulus()/pixPerRad;     // and rotate proportional to sweep
					temp = Transform.rotation(axis, angle);
				}
				else if ((flags & RIGHT)==RIGHT) { // if right dragging
					Vector3 t = new Vector3((pos.x-anchor.x)/xsc,(pos.y-anchor.y)/ysc,0);
					temp = Transform.translation(t); // it's a translation!
				}
			}	
		}
		else if (e.getSource() == zARDP) {  // if its on the rotation pad
			temp = Transform.rotation(new Vector3(0,0,-1),(pos.x-anchor.x)/pixPerRad); // do z axis rot.
		}						
		transform = Transform.join(temp,base); // finally add transform
		updateInfoFields();  
		drawBalls();
	}
		
		/** Not used. */ public void mouseMoved(MouseEvent e) {}
	
	// MouseWheelListener interface //
	/** Responds to mouse wheel with a z axis translation. Updates display, including position. */
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int clicks = e.getWheelRotation();
		temp = Transform.translation(new Vector3(0.0,0.0,clicks*unitsPerClick) );
		transform = Transform.join(temp,base); // finally add transform
		base = transform;     // mousewheel rotation is not transient
		updateInfoFields();   // shouldn't change, but just in case!
	   drawBalls();	
	}
	
	// Attribute attach listener interface //
	public void
	attributeAttached(
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
	)
	{
		drawBalls();
	}
	
	public void
	attributeDetached(
		final AnalyserOutput	output,
		final SysObjAttribute	attribute
	)
	{
		drawBalls();
	}

	
	// END INTERFACES ////////////////////////////////////////////////////////

}

class Vector3Panel extends Panel { // Convenience class - 3 TextFields in a Panel //
	static final DecimalFormat nf = new DecimalFormat("0.0000");
	TextField xTxt, yTxt, zTxt;
		
		Vector3Panel(String name, int len) { // 'len' is spaces per TextField
			Label label = new Label(name); 
			xTxt = new TextField(len);	yTxt = new TextField(len);	zTxt = new TextField(len);
			add(label); add(xTxt); add(yTxt); add(zTxt);
		}
		
		public void updateFields(Vector3 v) { updateFields(v.x,v.y,v.z); }
		public void updateFields(double x, double y, double z) {
			xTxt.setText(nf.format(x)); 
			yTxt.setText(nf.format(y));
			zTxt.setText(nf.format(z));
		}
		public Vector3 readFields() throws Exception {
			double x = new Double(xTxt.getText()).doubleValue(); 
			double y = new Double(yTxt.getText()).doubleValue(); 
			double z = new Double(zTxt.getText()).doubleValue(); 
			return new Vector3(x,y,z);
		} 
	
}

class DragPad extends Panel
{  // Coloured panel decorated with text //
	private final int gap = 4;
	private final int texthalfwidth  =35;	
	private final int texthalfheight =4;
	
	DragPad() {	super();	repaint();	}
	
	public void paint (Graphics g)
	{
		Dimension d = getSize();
		g.setColor(Color.green.darker());
		g.fillRect(gap,gap,d.width-2*gap,d.height-2*gap);
		g.setColor(getForeground());
		g.drawString("z axis DragPad",d.width/2-texthalfwidth,d.height/2+texthalfheight);
	}
}
