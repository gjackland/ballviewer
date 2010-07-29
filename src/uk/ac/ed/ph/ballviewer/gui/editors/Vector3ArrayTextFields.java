package uk.ac.ed.ph.ballviewer.gui.editors;

import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.ed.ph.ballviewer.math.Vector3;

public class Vector3ArrayTextFields implements ReflectionType< Vector3[] >, ActionListener
{
	private final ArrayList< Vector3TextFields >	vectors		= new ArrayList< Vector3TextFields >();

	private JPanel									vectorsPanel;
	private JTextField								x, y, z;

	private final JPanel							pButtons	= new JPanel();
	private final JButton							bAdd		= new JButton( "+" );
	private final JButton							bRemove		= new JButton( "-" );

	public Vector3ArrayTextFields()
	{
		vectorsPanel = new JPanel();
		BoxLayout layout = new BoxLayout( vectorsPanel, BoxLayout.Y_AXIS );
		vectorsPanel.setLayout( layout );

		bAdd.addActionListener( this );
		bRemove.addActionListener( this );

		pButtons.add( bAdd );
		pButtons.add( bRemove );
		addVector();

		vectorsPanel.add( pButtons );
	}

	@Override
	public JComponent getComponent()
	{
		return vectorsPanel;
	}

	@Override
	public void setValue( final Vector3[] value )
	{
		clearVectors();
		for( Vector3 newVec : value )
		{
			addVector( newVec );
		}
	}

	@Override
	public Vector3[] getValue()
	{
		final int num = vectors.size();
		Vector3[] returnVectors = new Vector3[ num ];

		for( int i = 0; i < num; ++i )
		{
			returnVectors[ i ] = vectors.get( i ).getValue();
		}

		return returnVectors;
	}

	private void addVector()
	{
		addVector( null );
	}

	private void addVector( final Vector3 newVec )
	{
		final Vector3TextFields newVecEditor = new Vector3TextFields();
		if( newVec != null )
		{
			newVecEditor.setValue( newVec );
		}
		vectors.add( newVecEditor );
		vectorsPanel.remove( pButtons );
		vectorsPanel.add( vectors.get( vectors.size() - 1 ).getComponent() );
		vectorsPanel.add( pButtons );
		vectorsPanel.revalidate();
		vectorsPanel.repaint();
	}

	private void removeVector()
	{
		if( vectors.size() > 0 )
		{
			final Vector3TextFields removedVector = vectors.remove( vectors.size() - 1 );
			vectorsPanel.remove( removedVector.getComponent() );
			vectorsPanel.revalidate();
			vectorsPanel.repaint();
		}
	}

	private void clearVectors()
	{
		vectorsPanel.removeAll();
		vectors.clear();
		vectorsPanel.revalidate();
		vectorsPanel.repaint();
	}

	@Override
	public void actionPerformed( ActionEvent evt )
	{
		Object source = evt.getSource();

		if( source == bAdd )
		{
			addVector();
		}
		else if( source == bRemove )
		{
			removeVector();
		}
	}
}