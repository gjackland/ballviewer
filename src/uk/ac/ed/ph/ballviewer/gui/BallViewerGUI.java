package uk.ac.ed.ph.ballviewer.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.ed.ph.ballviewer.Arrow;
import uk.ac.ed.ph.ballviewer.Ball;
import uk.ac.ed.ph.ballviewer.BallViewerFramework;
import uk.ac.ed.ph.ballviewer.CellLattice;
import uk.ac.ed.ph.ballviewer.ExperimentRecord;
import uk.ac.ed.ph.ballviewer.JpegCreator;
import uk.ac.ed.ph.ballviewer.SortTree;
import uk.ac.ed.ph.ballviewer.analysis.AnalyserOutput;
import uk.ac.ed.ph.ballviewer.analysis.SysObjAttribute;
import uk.ac.ed.ph.ballviewer.event.AttributeAttachEvent;
import uk.ac.ed.ph.ballviewer.event.AttributeAttachListener;
import uk.ac.ed.ph.ballviewer.event.NewExperimentEvent;
import uk.ac.ed.ph.ballviewer.event.NewExperimentListener;
import uk.ac.ed.ph.ballviewer.event.TimelineEvent;
import uk.ac.ed.ph.ballviewer.event.TimelineListener;
import uk.ac.ed.ph.ballviewer.math.Matrix3;
import uk.ac.ed.ph.ballviewer.math.Transform;
import uk.ac.ed.ph.ballviewer.math.Vector3;
import uk.ac.ed.ph.ballviewer.video.BufferedImageDataSource;
import uk.ac.ed.ph.ballviewer.video.BufferedImagePushBufferStream;
import uk.ac.ed.ph.ballviewer.video.ExportWizard;

/**
 * This is a viewer for 3d configurations of objects, especially balls. <br>
 * Gets input from mouse dragging and from input components (buttons,
 * textfields, checkboxes) in order to adjust the display.
 */
public class BallViewerGUI extends JFrame implements ActionListener, ItemListener, TextListener, MouseListener, MouseMotionListener, MouseWheelListener, AttributeAttachListener,
		TimelineListener, NewExperimentListener
{
	private class ControlPanel extends JPanel implements ActionListener, ChangeListener, ItemListener
	{
		private static final double			DEFAULT_DEPTH	= 0.0;
		private static final double			DEFAULT_WIDTH	= 1.6;
		private static final double			DEFAULT_MARGIN	= 1.0;

		private final BallViewerGUI			ballViewer;
		private final BallViewerFramework	framework;

		// STATE VARIABLES //////
		private double						ballsize		= 1.0;														// scale
																														// factors//
		private double						scale			= 1.0;
		private double						fslice			= DEFAULT_DEPTH - 0.5 * DEFAULT_WIDTH;
		private double						bslice			= DEFAULT_DEPTH - 0.5 * DEFAULT_WIDTH;
		private double						ffade			= fslice - DEFAULT_MARGIN;
		private double						bfade			= bslice + DEFAULT_MARGIN;

		private boolean						sliceOn			= false;
		private boolean						frontMarginOn	= false;
		private boolean						backMarginOn	= false;
		private boolean						perspectiveOn	= false;

		private final JLabel				lSliceDepth		= new JLabel( "Depth", Label.RIGHT );
		private final JLabel				lSliceWidth		= new JLabel( "Width", Label.RIGHT );
		private final JLabel				lFrontMargin	= new JLabel( "Width", Label.RIGHT );
		private final JLabel				lBackMargin		= new JLabel( "Width", Label.RIGHT );

		private final JSlider				sTimeline		= new JSlider( JSlider.HORIZONTAL, 0, 1, 0 );				// Timeline
																														// slider

		private final JButton				bSaveImage		= new JButton( "Save Image" );								// "Save Image"
																														// 1st
																														// row
																														// is
																														// always
																														// available
		private final JButton				bSetZDir		= new JButton( "Set Z dir" );

		private final DragPad				zARDP			= new DragPad();											// "z axis DragPad"

		private final JCheckBox				cbPerspective	= new JCheckBox( "Perspective", false );					// "perspective"
		private final JCheckBox				cbSlice			= new JCheckBox( "Slice", false );
		private final JCheckBox				cbFrontMargin	= new JCheckBox( "Front margin", false );
		private final JCheckBox				cbBackMargin	= new JCheckBox( "Back margin", false );					// 2nd
																														// row
																														// -
																														// most
																														// can
																														// be
																														// disabled

		private final JTextField			tfBallSize		= new JTextField( Double.toString( ballsize ), 4 );		// "ball size"
		private final JTextField			tfScale			= new JTextField( Double.toString( scale ), 4 );			// "scale"
		private final JTextField			tfSliceDepth	= new JTextField( Double.toString( DEFAULT_DEPTH ), 4 );
		private final JTextField			tfSliceWidth	= new JTextField( Double.toString( DEFAULT_WIDTH ), 4 );
		private final JTextField			tfFrontMargin	= new JTextField( Double.toString( DEFAULT_MARGIN ), 4 );
		private final JTextField			tfBackMargin	= new JTextField( Double.toString( DEFAULT_MARGIN ), 4 );

		ControlPanel( final BallViewerGUI ballViewer, final BallViewerFramework framework )
		{
			this.ballViewer = ballViewer;
			this.framework = framework;

			this.setLayout( new GridLayout( 3, 1 ) );

			// LISTENERS //////////////////////////
			sTimeline.addChangeListener( this );
			bSaveImage.addActionListener( this );
			bSetZDir.addActionListener( this );
			tfBallSize.addActionListener( this );
			tfScale.addActionListener( this );
			cbPerspective.addItemListener( this );
			cbSlice.addItemListener( this );
			cbFrontMargin.addItemListener( this );
			cbBackMargin.addItemListener( this );
			tfSliceDepth.addActionListener( this );
			tfSliceWidth.addActionListener( this );
			tfFrontMargin.addActionListener( this );
			tfBackMargin.addActionListener( this );

			// TIMELINE SLIDER /////////////////////
			sTimeline.setEnabled( false );
			sTimeline.setMajorTickSpacing( 1 );
			sTimeline.setSnapToTicks( true );
			sTimeline.setPaintLabels( true );

			// COMPONENTS ADDING ///////////////////
			this.add( sTimeline );
			JPanel row = new JPanel();
			row.add( bSaveImage );
			row.add( bSetZDir );
			row.add( new JLabel( "Ball size: ", SwingConstants.RIGHT ) );
			row.add( tfBallSize );
			row.add( new JLabel( "Scale: ", SwingConstants.RIGHT ) );
			row.add( tfScale );
			row.add( cbPerspective );
			row.add( zARDP );
			this.add( row );

			// Next row
			row = new JPanel( new GridLayout( 1, 3 ) );
			JPanel p1 = new JPanel();
			JPanel p2 = new JPanel();
			JPanel p3 = new JPanel();

			p1.add( cbSlice );
			p1.add( lSliceDepth );
			p1.add( tfSliceDepth );
			p1.add( lSliceWidth );
			p1.add( tfSliceWidth );
			p2.add( cbFrontMargin );
			p2.add( lFrontMargin );
			p2.add( tfFrontMargin );
			p3.add( cbBackMargin );
			p3.add( lBackMargin );
			p3.add( tfBackMargin );
			row.add( p1 );
			row.add( p2 );
			row.add( p3 );
			this.add( row );
			updateCheckboxes();
			readInputs();
		}

		private void updateCheckboxes()
		{
			final boolean slice = cbSlice.isSelected();
			final boolean frontMargin = cbFrontMargin.isSelected();
			final boolean backMargin = cbBackMargin.isSelected();

			lSliceDepth.setEnabled( slice );
			lSliceWidth.setEnabled( slice );
			tfSliceDepth.setEnabled( slice );
			tfSliceWidth.setEnabled( slice );
			cbFrontMargin.setEnabled( slice );
			cbBackMargin.setEnabled( slice );
			lFrontMargin.setEnabled( slice && frontMargin );
			lBackMargin.setEnabled( slice && backMargin );
			tfFrontMargin.setEnabled( slice && frontMargin );
			tfBackMargin.setEnabled( slice && backMargin );

			// Set state variables
			sliceOn = slice;
			frontMarginOn = frontMargin;
			backMarginOn = backMargin;
			perspectiveOn = cbPerspective.isSelected();
		}

		private void readInputs()
		{ // if any part fails, just ignore it (use old value)

			try
			{
				scale = Double.parseDouble( tfScale.getText() );
				xsc = scale * frw / xrange;
				ysc = scale * frh / yrange;
			}
			catch( Exception e )
			{
			}
			try
			{
				ballsize = Double.parseDouble( ballsizeTxt.getText() );
				Ball.newDiameter = ballsize * scale * Ball.DEFAULT_DIAMETER;
			}
			catch( Exception e )
			{
			}
			try
			{
				final double depth = Double.parseDouble( sliceDepthTxt.getText() );
				final double width = Math.abs( Double.parseDouble( sliceWidthTxt.getText() ) );
				fslice = depth - 0.5 * width;
				bslice = depth + 0.5 * width;
			}
			catch( Exception e )
			{
			}
			try
			{
				final double fmargin = Math.abs( Double.parseDouble( frontMarginTxt.getText() ) );
				ffade = fslice - fmargin;
			}
			catch( Exception e )
			{
			}
			try
			{
				final double bmargin = Math.abs( Double.parseDouble( backMarginTxt.getText() ) );
				bfade = bslice + bmargin;
			}
			catch( Exception e )
			{
			}
		}

		// INTERFACES ////////////////////////////////////////////////

		// ACTION LISTENER ///////////////////////////////////////////
		/**
		 * Responds to the "Save Image" button by saving an image.
		 * 
		 * @see JpegCreator
		 */
		@Override
		public void actionPerformed( final ActionEvent e )
		{
			// if (e.getSource()==setZdirBtn) {
			// try {
			// Vector3 xAxis = xVctr.readFields();
			// Vector3 yAxis = yVctr.readFields();
			// Vector3 zAxis = zVctr.readFields();
			// if (Math.abs(zAxis.dot(xAxis)) < Math.abs(zAxis.dot(yAxis))) {
			// yAxis = zAxis.cross(xAxis);
			// xAxis = yAxis.cross(zAxis);
			// }
			// else {
			// xAxis = yAxis.cross(zAxis);
			// yAxis = zAxis.cross(xAxis);
			// }
			// xAxis.normalise(); yAxis.normalise(); zAxis.normalise();
			// base.m = new Matrix3(xAxis, yAxis, zAxis);
			// transform = base;
			// updateInfoFields();
			// drawBalls();
			// } catch (Exception err) {} // if it fails at any point, stop:
			// normal to fail at readFields()
			// }
			// else if (e.getSource()==imgCaptureBtn) {
			// BufferedImage bimg = new
			// BufferedImage(frw,frh,BufferedImage.TYPE_INT_RGB);
			// Graphics2D g = bimg.createGraphics();
			// g.addRenderingHints( // antialiasing produces a much smoother
			// picture
			// new
			// RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON)
			// );
			// drawBalls(g); // draw the image
			// JpegCreator.saveImage(bimg); // and save it
			// }
		}

		// CHANGE LISTENER
		// ////////////////////////////////////////////////////////
		@Override
		public void stateChanged( final ChangeEvent e )
		{
			if( e.getSource() == sTimeline )
			{
				// The slider has changed value
				framework.tmpSetCurrentSample( sTimeline.getValue() );

				// Fire a timeline changed event
				BallViewerFramework.eventDispatcher.notify( new TimelineEvent( sTimeline.getValue() ) );

				ballViewer.drawBalls();
			}
		}

		// ITEM LISTENER
		// //////////////////////////////////////////////////////////
		@Override
		public void itemStateChanged( final ItemEvent e )
		{
			updateCheckboxes();
			readInputs();
			ballViewer.drawBalls();
		}

	}

	private static final String			title			= "Ball Viewer";

	// The framework that underpins the GUI
	private final BallViewerFramework	framework;

	// The menu bar
	private final JMenuBar				mBar			= new JMenuBar();
	private final JMenu					mBarFile;
	private final AnalysisMenu			mBarAnalysis;

	// Graphics//
	private Canvas						canv;
	private BufferStrategy				buff;

	// Frame,Canvas,graph details//
	private static int					offX			= 50, offY = 0;									// position
																											// of
																											// frame
																											// (top,left
																											// corner)
																											// //
	protected int						frw, frh, fr0x, fr0y;												// canvas
																											// details
																											// //
	protected double					xmin, xmax, xrange, xsc;											// graph
																											// details
																											// //
	protected double					ymin, ymax, yrange, ysc;
	protected double					ballsize		= 1.0;												// scale
																											// factors//
	protected double					scale			= 1.0;
	// Ball sets are replaced by a CellLattice//
	protected CellLattice				cells;

	// Transformation by mouse dragging and wheel
	protected final double				pixPerRad		= 100;												// if
																											// dragged
																											// through
																											// pixPerRad,
																											// get
																											// a
																											// 1
																											// rad
																											// rotation
	final double						unitsPerClick	= -0.5;											// if
																											// wheel
																											// clicks
																											// down,
																											// get
																											// a
																											// -0.2
																											// translation
	protected Point						anchor;															// start
																											// position
																											// of
																											// drag
	protected Point						pos;																// current/final
																											// position
																											// of
																											// drag
	protected Transform					temp			= new Transform();									// the
																											// temporary
																											// (dragging)
																											// transform
	protected Transform					base			= new Transform();									// the
																											// accumulation
																											// of
																											// previous
																											// temporary
																											// transforms
	protected Transform					transform		= new Transform();									// the
																											// actual,
																											// current
																											// transform
																											// (
																											// =temp.base
																											// )

	// GUI stuff //
	protected static final Color		labelColor		= new Color( 0, 64, 0 );

	// Control Panel - has 2 rows of components
	private final Panel					control;
	
	/**
	 * Timeline slider.
	 */
	private final JSlider				sTimeline;
	
	/**
	 * "Save Image" button
	 */
	private final Button				imgCaptureBtn	= new Button( "Save Image" ); 
																											// 1st
																											// row
																											// is
																											// always
																											// available
	private final JButton				bSaveVideo		= new JButton( "Save Video" );
	private final TextField				ballsizeTxt		= new TextField( Double.toString( ballsize ), 2 );	// "ball size"
	private final TextField				scaleTxt		= new TextField( Double.toString( scale ), 2 );	// "scale"
	private final Checkbox				perspectiveChk	= new Checkbox( "Perspective", false );			// "perspective"
	private final DragPad				zARDP			= new DragPad();									// "z axis DragPad"
	private final Checkbox				sliceChk		= new Checkbox( "Slice", false ), frontMarginChk = new Checkbox( "Front margin", false ), backMarginChk = new Checkbox(
																"Back margin", false );					// 2nd
																											// row
																											// -
																											// most
																											// can
																											// be
																											// disabled
	private final Label					sliceDepthLbl	= new Label( "Depth", Label.RIGHT ), sliceWidthLbl = new Label( "Width", Label.RIGHT ), frontMarginLbl = new Label(
																"Width", Label.RIGHT ), backMarginLbl = new Label( "Width", Label.RIGHT );
	private final TextField				sliceDepthTxt, sliceWidthTxt, frontMarginTxt, backMarginTxt;

	// Info Panel
	private final Panel					info;
	private final Button				setZdirBtn		= new Button( "Set Z dir" );
	private final Vector3Panel			xVctr			= new Vector3Panel( "X", 4 ), yVctr = new Vector3Panel( "Y", 4 ), zVctr = new Vector3Panel( "Z", 4 ),
			pVctr = new Vector3Panel( "P", 4 );

	// Sidebar with auxilary information
	private final JPanel				pSidebar		= new JPanel();

	// System objects side panel
	private final SystemObjectsPanel	pSystemObjects;

	protected boolean					sliceOn			= false, ffadeOn = false, bfadeOn = false;
	protected boolean					perspective		= false;

	final double						depthDefault	= 0.0, widthDefault = 1.6, marginDefault = 1.0;
	double								fslice			= depthDefault - 0.5 * widthDefault;
	double								bslice			= depthDefault - 0.5 * widthDefault;
	double								ffade			= fslice - marginDefault;
	double								bfade			= bslice + marginDefault;

	public BallViewerGUI()
	{
		this( -8, -5, 8, 5 );
	}

	public BallViewerGUI( final File[] inputFiles )
	{
		this();

		framework.loadExperimentRecord( inputFiles );
	}

	/**
	 * Creates a new BallViewer. <br>
	 * Assigns title. Displays contents of <code>cellsIn</code>. <br>
	 * Sets the dimensions of the projection screen, as if it was at z=0.0
	 * relative to camera.
	 */
	protected BallViewerGUI( final double xmn, final double ymn, final double xmx, final double ymx )
	{
		// set up the frame //
		super( title );
		framework = new BallViewerFramework();

		// Register ourselves to receive message from the dispatcher
		BallViewerFramework.eventDispatcher.listen( AttributeAttachEvent.class, this );
		BallViewerFramework.eventDispatcher.listen( TimelineEvent.class, this );
		BallViewerFramework.eventDispatcher.listen( NewExperimentEvent.class, this );

		// Allow menus to overlap over drawing canvas
		JPopupMenu.setDefaultLightWeightPopupEnabled( false );

		this.setBackground( Color.green );
		this.setLocation( offX, offY );
		offX += 0;
		offY += 50;
		this.setResizable( false );
		this.addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowOpened( WindowEvent e )
			{ // When opened for 1st time,
				if( buff == null )
				{
					canv.createBufferStrategy( 2 ); // make a buffer strategy -
													// see canv below
					buff = canv.getBufferStrategy(); // (seem to have to do this
														// after it is visible)
				}
				drawBalls();
			}

			@Override
			public void windowClosing( WindowEvent e )
			{
				e.getWindow().dispose();
				System.exit( 0 );
			}
		} );

		pSystemObjects = new SystemObjectsPanel( framework );

		// Set up the sidebar
		pSidebar.setLayout( new BoxLayout( pSidebar, BoxLayout.Y_AXIS ) );
		pSidebar.add( pSystemObjects );

		// set up graph details
		xmin = xmn;
		xmax = xmx;
		ymin = ymn;
		ymax = ymx; // graph corners
		xrange = xmax - xmin;
		yrange = ymax - ymin; // graph size

		final int frwDefault = 900;
		final int frhDefault = 550;
		final double aspectDefault = ( double )frwDefault / frhDefault;
		final double aspect = xrange / yrange; // canvas size must be
												// proportional to graph,
		if( aspect > aspectDefault ) // but doesn't go outside default bounds
		{
			frw = frwDefault;
			frh = ( int )( frwDefault / aspect );
		}
		else
		{
			frh = frhDefault;
			frw = ( int )( frhDefault * aspect );
		}
		xsc = frw / xrange;
		ysc = frh / yrange;
		fr0x = ( int )( xmin * xsc );
		fr0y = ( int )( ymin * ysc ); // frame coordinates of origin

		sTimeline = new TimelineSlider( framework );

		mBarFile = new FileMenu( this, framework );
		mBarAnalysis = new AnalysisMenu( this, framework.getAnalysisManager() );

		// Set up the menu bar
		setupMenuBar();

		// set up a canvas //
		canv = new Canvas()
		{
			@Override
			public void paint( Graphics g )
			{
				drawBalls();
			}
		};
		canv.setSize( frw, frh );
		canv.setBackground( Color.white );
		canv.addMouseListener( this );
		canv.addMouseMotionListener( this ); // see methods below
		canv.addMouseWheelListener( this ); // see methods below
		canv.setVisible( true );

		// set up the info panel //
		info = new Panel( new GridLayout( 1, 5 ) );
		info.setForeground( labelColor );

		updateInfoFields();
		info.add( xVctr );
		info.add( yVctr );
		info.add( zVctr );
		info.add( pVctr );

		// set up a control panel - looks absolutely horrendous//
		control = new Panel( new GridLayout( 3, 1 ) );
		control.setForeground( labelColor );

		// TIMELINE SLIDER /////////////////////
		control.add( sTimeline );

		Panel row, p1, p2, p3, p4, p5;

		final GridBagLayout gbl = new GridBagLayout();
		final GridBagConstraints gbc = new GridBagConstraints();
		row = new Panel( gbl );
		p1 = new Panel();
		p2 = new Panel();
		p3 = new Panel();
		p4 = new Panel();
		p5 = new Panel();
		imgCaptureBtn.addActionListener( this );
		bSaveVideo.addActionListener( this );
		setZdirBtn.addActionListener( this );
		ballsizeTxt.addTextListener( this );
		scaleTxt.addTextListener( this );
		perspectiveChk.addItemListener( this );
		zARDP.setForeground( labelColor );
		zARDP.addMouseListener( this );
		zARDP.addMouseMotionListener( this );
		p1.add( imgCaptureBtn );
		p1.add( bSaveVideo );
		p2.add( setZdirBtn );
		p3.add( new Label( "Ball size:", Label.RIGHT ) );
		p3.add( ballsizeTxt );
		p4.add( new Label( "Scale:", Label.RIGHT ) );
		p4.add( scaleTxt );
		p5.add( new Label( "", Label.RIGHT ) );
		p5.add( perspectiveChk );
		row.add( p1 );
		row.add( p2 );
		row.add( p3 );
		row.add( p4 );
		row.add( p5 );
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints( zARDP, gbc );
		row.add( zARDP ); // All this just to get a decent size for zARDP
		control.add( row );

		row = new Panel( new GridLayout( 1, 3 ) );
		p1 = new Panel();
		p2 = new Panel();
		p3 = new Panel();

		( sliceDepthTxt = new TextField( "" + depthDefault ) ).addTextListener( this );
		( sliceWidthTxt = new TextField( "" + widthDefault ) ).addTextListener( this );
		( frontMarginTxt = new TextField( "" + marginDefault ) ).addTextListener( this );
		( backMarginTxt = new TextField( "" + marginDefault ) ).addTextListener( this );
		sliceChk.addItemListener( this );
		frontMarginChk.addItemListener( this );
		backMarginChk.addItemListener( this );
		p1.add( sliceChk );
		p1.add( sliceDepthLbl );
		p1.add( sliceDepthTxt );
		p1.add( sliceWidthLbl );
		p1.add( sliceWidthTxt );
		p2.add( frontMarginChk );
		p2.add( frontMarginLbl );
		p2.add( frontMarginTxt );
		p3.add( backMarginChk );
		p3.add( backMarginLbl );
		p3.add( backMarginTxt );
		row.add( p1 );
		row.add( p2 );
		row.add( p3 );
		control.add( row );
		updateCheckboxes();
		readInputs();

		// put the infobar, canvas and control panel together
		final JPanel mainPanel = new JPanel( new BorderLayout() );
		mainPanel.add( info, BorderLayout.NORTH );
		mainPanel.add( canv, BorderLayout.CENTER );
		JPanel tmpPanel = new JPanel( new BorderLayout() );
		tmpPanel.add( control, BorderLayout.NORTH );
		// tmpPanel.add( new ControlPanel( this, framework ), BorderLayout.SOUTH
		// );
		// mainPanel.add( control,BorderLayout.SOUTH );
		mainPanel.add( tmpPanel, BorderLayout.SOUTH );

		this.add( mainPanel, BorderLayout.CENTER );
		this.add( pSidebar, BorderLayout.EAST );

		this.pack(); // this fits the frame around it all(neat)

		this.setVisible( true );
	}

	private void setupMenuBar()
	{
		mBar.add( mBarFile );
		mBar.add( mBarAnalysis );
		this.setJMenuBar( mBar );
	}

	// void
	// setRenderVariables(
	// final double ballSize,
	// final double scale,
	// final boolean perspectiveOn,
	// final boolean sliceOn,
	// final double sliceDepth,
	// final double sliceWidth,
	// final boolean frontMarginOn,
	// final double frontMargin,
	// final boolean backMarginOn
	// final double backMargin
	// )
	// {
	// this.ballsize = ballsize;
	// this.scale = scale;
	// this.perspectiveOn = perspectiveOn;
	// this.sliceOn = sliceOn;
	// this.sliceDepth = sliceDepth;
	// this.sliceWidth = sliceWidth;
	// this.frontMarginOn = frontMarginOn;
	// this.frontMargin = frontMargin;
	// this.backMarginOn = backMarginOn;
	// this.backMargin = backMargin;
	// }

	void updateCheckboxes()
	{
		boolean boo = sliceChk.getState();
		boolean boo2 = frontMarginChk.getState();
		boolean boo3 = backMarginChk.getState();
		sliceDepthLbl.setEnabled( boo );
		sliceWidthLbl.setEnabled( boo );
		sliceDepthTxt.setEnabled( boo );
		sliceWidthTxt.setEnabled( boo );
		frontMarginChk.setEnabled( boo );
		backMarginChk.setEnabled( boo );
		frontMarginLbl.setEnabled( boo & boo2 );
		backMarginLbl.setEnabled( boo & boo3 );
		frontMarginTxt.setEnabled( boo & boo2 );
		backMarginTxt.setEnabled( boo & boo3 );
		sliceOn = boo;
		ffadeOn = boo2;
		bfadeOn = boo3;
		perspective = perspectiveChk.getState();
	}

	void readInputs()
	{ // if any part fails, just ignore it (use old value)
		double depth, width, fmargin, bmargin;
		try
		{
			scale = new Double( scaleTxt.getText() ).doubleValue();
			xsc = scale * frw / xrange;
			ysc = scale * frh / yrange;
		}
		catch( Exception e )
		{
		}
		try
		{
			ballsize = new Double( ballsizeTxt.getText() ).doubleValue();
			Ball.newDiameter = ballsize * scale * Ball.DEFAULT_DIAMETER;
		}
		catch( Exception e )
		{
		}
		try
		{
			depth = new Double( sliceDepthTxt.getText() ).doubleValue();
			width = Math.abs( new Double( sliceWidthTxt.getText() ).doubleValue() );
			fslice = depth - 0.5 * width;
			bslice = depth + 0.5 * width;
		}
		catch( Exception e )
		{
		}
		try
		{
			fmargin = Math.abs( new Double( frontMarginTxt.getText() ).doubleValue() );
			ffade = fslice - fmargin;
		}
		catch( Exception e )
		{
		}
		try
		{
			bmargin = Math.abs( new Double( backMarginTxt.getText() ).doubleValue() );
			bfade = bslice + bmargin;
		}
		catch( Exception e )
		{
		}
	}

	private void updateInfoFields()
	{
		double[][] r = transform.m.r;
		xVctr.updateFields( r[ 0 ][ 0 ], r[ 0 ][ 1 ], r[ 0 ][ 2 ] );
		yVctr.updateFields( r[ 1 ][ 0 ], r[ 1 ][ 1 ], r[ 1 ][ 2 ] );
		zVctr.updateFields( r[ 2 ][ 0 ], r[ 2 ][ 1 ], r[ 2 ][ 2 ] );
		pVctr.updateFields( getCameraPosition() );
	}

	/**
	 * Sets the internal transformation as if the camera was placed at this
	 * position.
	 */
	void setCameraPosition( Vector3 camPos )
	{ // these are extremely messy, but do make sense
		base = new Transform( base.m, transform.inverse().appliedTo( camPos.times( -1 ) ) );
		transform = Transform.join( temp, base );
	}

	/**
	 * Gets the camera position that corresponds to the internal transformation.
	 */
	public Vector3 getCameraPosition()
	{
		return transform.inverse().appliedTo( new Vector3() ).times( -1 );
	}

	public void setCameraDirection( Matrix3 dir )
	{
		base = new Transform( dir, base.t );
		transform = Transform.join( temp, base );
		updateInfoFields();
		drawBalls();
	}

	public Matrix3 getCameraDirection()
	{
		return transform.m;
	}

	public void setPerspective( boolean boo )
	{
		perspective = boo;
	}

	private void saveImage()
	{

	}

	// Draw the thing! called by canv.paint,
	// windowActivated, mouseDragged, mouseWheelMoved, itemStateChanged,
	// textValueChanged
	/** Updates the ball display. */
	void drawBalls()
	{
		if( buff != null )
		{
			drawBalls( ( Graphics2D )buff.getDrawGraphics() );
			buff.show();
		}
	}

	/** Draws the balls in the given graphics context */
	public void drawBalls( Graphics2D g ) // used to display and also to
											// "Save Image")
	{
		if( cells != null )
		{
			final Ball pinkMarker = new Ball( .01, Color.pink );
			final int n = 5;

			g.clearRect( 0, 0, frw, frh ); // wipe it clean
			g.translate( -fr0x, -fr0y );

			final SortTree tr = new SortTree();
			tr.addNode( pinkMarker, 0.0 ); // adds a pink marker always at
											// origin

			// Get whichever set of objects you wish, and add them to SortTree
			// //

			// for( int ix=1; ix <= cells.X(); ix++ )
			// {
			// for( int iy=1; iy <=cells.Y(); iy++ )
			// {
			// for( int iz = 1; iz <=cells.Z(); iz++ )
			// {
			// final ArrayList< Positionable > temp =
			// cells.getCellObjectList(ix,iy,iz);
			// for( final Positionable obj : temp )
			// {
			// if( obj instanceof Ball )
			// { // we transform Balls and Arrows differently
			// Ball b = ( Ball )obj;
			// b = new Ball( transform.appliedTo( b.pos ), b.getColour() );
			// tr.addNode( b,b.pos.z ); // and then put them into the SortTree
			// }
			// else if( obj instanceof Arrow )
			// {
			// Arrow a = ( Arrow )obj;
			// a = new Arrow
			// (transform.appliedTo(a.v1),transform.appliedTo(a.v2));
			// tr.addNode(a,a.ctr.z-0.001*cells.cellSize());
			// } // ditto, but place arrow cameraside of the ball
			// }
			// }
			// }
			// }

			Ball[] balls = framework.getSystem().p;
			// TODO: Nasty nastyness here shouldn't be creating copies!
			// But probably best waiting till I change to renderer to fix this
			for( Ball b : balls )
			{
				final Ball bCopy = new Ball( transform.appliedTo( b.getPosition() ), b.getColour() );
				bCopy.setDiameterOffset( b.getDiameterOffset() );
				bCopy.setAlpha( b.getAlpha() );
				tr.addNode( bCopy, bCopy.getPosition().z );
			}

			// Alternatively can input a set of balls and just do
			// tr.addNode(b[i],b[i].pos.z) //
			tr.resetTraverser();
			final double camdepth = -30.0;

			for( int i = 0; i < tr.total; i++ )
			{
				tr.findNextLargest();
				final Object obj = tr.object();
				double z = tr.value();

				if( obj instanceof Ball )
				{
					Ball b = ( Ball )obj;
					double zsc;
					if( perspective )
					{
						if( z < 0.5 * camdepth )
							break; // we can dump the remainder (depthSort)
						zsc = -camdepth / ( z - camdepth ); // < this is such
															// that zsc = 1.0 at
															// z=0;
					}
					else
						zsc = 1.0;

					double d = ( b.getDiameter() + b.getDiameterOffset() ) * zsc;

					final double r = d / 2;
					final double cx = b.getPosition().x * zsc * xsc;
					final double cy = b.getPosition().y * zsc * ysc;
					final Color bColor = b.getColour();
					g.setColor( new Color( bColor.getRed(), bColor.getGreen(), bColor.getBlue(), ( int )( b.getAlpha() * 25.5d ) ) );

					if( !sliceOn || ( z > fslice && z < bslice ) )
					{
						g.fillOval( ( int )( cx - r ), ( int )( cy - r ), ( int )d, ( int )d );
						g.setColor( Color.black );
						g.drawOval( ( int )( cx - r ), ( int )( cy - r ), ( int )( d - 1 ), ( int )( d - 1 ) );
					}
					else if( ( ffadeOn && z > ffade && z < fslice ) || ( bfadeOn && z > bslice && z < bfade ) )
					{
						g.drawOval( ( int )( cx - d / 2 ) + 1, ( int )( cy - d / 2 ) + 1, ( int )d - 3, ( int )d - 3 );
						g.setColor( Color.black );
						g.drawOval( ( int )( cx - d / 2 ), ( int )( cy - d / 2 ), ( int )d - 1, ( int )d - 1 );
					}
				}
				else if( obj instanceof Arrow )
				{
					Arrow a = ( Arrow )obj;
					if( perspective )
					{ // ends could be at different depths: should be done
						// separately
						if( a.v1.z < 0.2 * camdepth || a.v2.z < 0.2 * camdepth )
							continue;
						a.v1.perspectivise( camdepth ); // we can merrily ruin
														// these
						a.v2.perspectivise( camdepth ); // temporary arrows
					}
					if( !sliceOn || ( z > fslice && z < bslice ) )
					{
						int x1 = ( int )( a.v1.x * xsc );
						int y1 = ( int )( a.v1.y * ysc );
						int x2 = ( int )( a.v2.x * xsc );
						int y2 = ( int )( a.v2.y * ysc );
						g.setColor( Color.yellow );
						g.drawLine( x1, y1, x2, y2 );
					}
				}
			}
			g.setColor( labelColor );
			g.translate( fr0x, fr0y ); // sets translation back to (0,0)
		}
		g.drawRect( 0, 0, frw - 1, frh - 1 );

		g.dispose();
	}

	private BufferedImage[] generateSystemImages()
	{
		final BufferedImage[] images = new BufferedImage[ framework.getExperimentRecord().getNumerOfSamples() ];
		for( int i = 0; i < images.length; ++i )
		{
			framework.tmpSetCurrentSample( i );
			final BufferedImage bimg = new BufferedImage( frw, frh, BufferedImage.TYPE_INT_RGB );
			final Graphics2D g = bimg.createGraphics();

			g.addRenderingHints( // antialiasing produces a much smoother
									// picture
			new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

			drawBalls( g ); // draw the image
			images[ i ] = bimg;

		}
		return images;
	}

	// INTERFACES /////////////////////////////////////////////////////////////

	@Override
	public void newExperiment( final ExperimentRecord newExperiment )
	{
		// Get the menu bar to update its list of analysers
		mBarAnalysis.updateAnalysers();

		// Update the system objects panel
		pSystemObjects.update();

		if( newExperiment != null )
		{
			// associate a cellLattice with the viewer.
			cells = framework.getSystem().getCellLattice();
			// Set the camera position to the centre of the system
			setCameraPosition( framework.getSystem().getCentre() );
		}
		else
		{
			cells = null;
			setCameraPosition( new Vector3() );
		}
	}

	@Override
	public void timelineChanged( final int currentSample )
	{
		drawBalls();
	}

	// ActionListener interface //
	/**
	 * Responds to the "Save Image" button by saving an image.
	 * 
	 * @see JpegCreator
	 */
	@Override
	public void actionPerformed( final ActionEvent e )
	{
		if( e.getSource() == setZdirBtn )
		{
			try
			{
				Vector3 xAxis = xVctr.readFields();
				Vector3 yAxis = yVctr.readFields();
				Vector3 zAxis = zVctr.readFields();
				if( Math.abs( zAxis.dot( xAxis ) ) < Math.abs( zAxis.dot( yAxis ) ) )
				{
					yAxis = zAxis.cross( xAxis );
					xAxis = yAxis.cross( zAxis );
				}
				else
				{
					xAxis = yAxis.cross( zAxis );
					yAxis = zAxis.cross( xAxis );
				}
				xAxis.normalise();
				yAxis.normalise();
				zAxis.normalise();
				base.m = new Matrix3( xAxis, yAxis, zAxis );
				transform = base;
				updateInfoFields();
				drawBalls();
			}
			catch( Exception err )
			{
			} // if it fails at any point, stop: normal to fail at readFields()
		}
		else if( e.getSource() == imgCaptureBtn )
		{
			BufferedImage bimg = new BufferedImage( frw, frh, BufferedImage.TYPE_INT_RGB );
			Graphics2D g = bimg.createGraphics();
			g.addRenderingHints( // antialiasing produces a much smoother
									// picture
			new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
			drawBalls( g ); // draw the image
			JpegCreator.saveImage( bimg ); // and save it
		}
		else if( e.getSource() == bSaveVideo )
		{
			final BufferedImage[] images = generateSystemImages();
			final ExportWizard wiz = new ExportWizard( this, new BufferedImageDataSource( new BufferedImagePushBufferStream( images ) ) );
			wiz.setVisible( true );
		}
	}

	// ItemListener interface //
	/** Responds to the checkboxes, reads inputs and updates display. */
	@Override
	public void itemStateChanged( ItemEvent e )
	{
		updateCheckboxes();
		readInputs();
		drawBalls();
	}

	// TextListener interface //
	/** Responds to the textfields by reading them and updates display. */
	@Override
	public void textValueChanged( TextEvent e )
	{
		readInputs();
		drawBalls();
	}

	// MouseListener interface - unused methods must also be declared //
	/** Responds a mouse button press by creating an anchor point for dragging. */
	@Override
	public void mousePressed( MouseEvent e )
	{
		anchor = e.getPoint();
	}

	/** Responds a mouse button release by saving the current transform. */
	@Override
	public void mouseReleased( MouseEvent e )
	{
		mouseDragged( e );
		base = transform; // do final drag then save this transform as the base
	}

	/** Not used. */
	@Override
	public void mouseClicked( MouseEvent e )
	{
	}

	/** Not used. */
	@Override
	public void mouseEntered( MouseEvent e )
	{
	}

	/** Not used. */
	@Override
	public void mouseExited( MouseEvent e )
	{
	}

	// MouseMotionListener interface - unused methods must also be declared //
	/**
	 * Responds to mouse dragging. <br>
	 * If left-dragging on the canvas, does rotation. <br>
	 * If right-dragging on the canvas, does translation. <br>
	 * If dragging on the DragPad, does z-axis rotation. <br>
	 * Updates display, including direction/position vectors (top row).
	 */
	@Override
	public void mouseDragged( MouseEvent e )
	{ // responds to mouse dragging
		pos = e.getPoint();
		if( e.getSource() == canv )
		{
			if( pos.equals( anchor ) )
			{
				temp = new Transform();
			} // ie does nothing
			else
			{
				final int LEFT = InputEvent.BUTTON1_DOWN_MASK;
				final int RIGHT = InputEvent.BUTTON3_DOWN_MASK;
				int flags = e.getModifiersEx();
				if( ( flags & LEFT ) == LEFT )
				{ // if left dragging
					Vector3 sweep = new Vector3( pos.x - anchor.x, pos.y - anchor.y, 0 );
					Vector3 axis = sweep.cross( new Vector3( 0, 0, 1 ) ); // get
																			// a
																			// perp.
																			// axis
					double angle = sweep.modulus() / pixPerRad; // and rotate
																// proportional
																// to sweep
					temp = Transform.rotation( axis, angle );
				}
				else if( ( flags & RIGHT ) == RIGHT )
				{ // if right dragging
					Vector3 t = new Vector3( ( pos.x - anchor.x ) / xsc, ( pos.y - anchor.y ) / ysc, 0 );
					temp = Transform.translation( t ); // it's a translation!
				}
			}
		}
		else if( e.getSource() == zARDP )
		{ // if its on the rotation pad
			temp = Transform.rotation( new Vector3( 0, 0, -1 ), ( pos.x - anchor.x ) / pixPerRad ); // do
																									// z
																									// axis
																									// rot.
		}
		transform = Transform.join( temp, base ); // finally add transform
		updateInfoFields();
		drawBalls();
	}

	/** Not used. */
	@Override
	public void mouseMoved( MouseEvent e )
	{
	}

	// MouseWheelListener interface //
	/**
	 * Responds to mouse wheel with a z axis translation. Updates display,
	 * including position.
	 */
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		int clicks = e.getWheelRotation();
		temp = Transform.translation( new Vector3( 0.0, 0.0, clicks * unitsPerClick ) );
		transform = Transform.join( temp, base ); // finally add transform
		base = transform; // mousewheel rotation is not transient
		updateInfoFields(); // shouldn't change, but just in case!
		drawBalls();
	}

	// Attribute attach listener interface //
	@Override
	public void attributeAttached( final AnalyserOutput output, final SysObjAttribute attribute )
	{
		drawBalls();
	}

	@Override
	public void attributeDetached( final AnalyserOutput output, final SysObjAttribute attribute )
	{
		drawBalls();
	}

	// END INTERFACES ////////////////////////////////////////////////////////

}

// Convenience class - 3 TextFields in a Panel //
final class Vector3Panel extends JPanel
{
	static final DecimalFormat	nf	= new DecimalFormat( "0.0000" );
	private TextField			xTxt, yTxt, zTxt;

	Vector3Panel( final String name, final int len // 'len' is spaces per
													// TextField
	)
	{
		Label label = new Label( name );
		xTxt = new TextField( len );
		yTxt = new TextField( len );
		zTxt = new TextField( len );
		add( label );
		add( xTxt );
		add( yTxt );
		add( zTxt );
	}

	public final void updateFields( final Vector3 v )
	{
		updateFields( v.x, v.y, v.z );
	}

	public final void updateFields( final double x, final double y, final double z )
	{
		xTxt.setText( nf.format( x ) );
		yTxt.setText( nf.format( y ) );
		zTxt.setText( nf.format( z ) );
	}

	public final Vector3 readFields() throws Exception
	{
		final double x = new Double( xTxt.getText() ).doubleValue();
		final double y = new Double( yTxt.getText() ).doubleValue();
		final double z = new Double( zTxt.getText() ).doubleValue();
		return new Vector3( x, y, z );
	}

}

class DragPad extends JPanel
{ // Coloured panel decorated with text //
	private final int	gap				= 4;
	private final int	texthalfwidth	= 35;
	private final int	texthalfheight	= 4;

	DragPad()
	{
		super();

		// setPreferredSize( new Dimension( 430, 34 ) );

		repaint();
	}

	@Override
	public void paint( Graphics g )
	{
		Dimension d = getSize();
		g.setColor( Color.green.darker() );
		g.fillRect( gap, gap, d.width - 2 * gap, d.height - 2 * gap );
		g.setColor( getForeground() );
		g.drawString( "z axis DragPad", d.width / 2 - texthalfwidth, d.height / 2 + texthalfheight );
	}
}
