import java.awt.Frame;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;

public class KalicMarkoA1Q2 implements GLEventListener
{
	public static final boolean TRACE = true;

	public static final String MAZEFILE = "maze1.txt";

	public static final String WINDOW_TITLE = "A1Q2: Marko Kalic"; // TODO:
																	// change
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 640;

	final float DEGS_TO_RADS = (float)Math.PI / 180;
	
	public static void main( String[] args )
	{
		final Frame frame = new Frame( WINDOW_TITLE );

		frame.addWindowListener( new WindowAdapter( )
		{
			public void windowClosing( WindowEvent e )
			{
				System.exit( 0 );
			}
		} );

		final GLProfile profile = GLProfile.get( GLProfile.GL2 );
		final GLCapabilities capabilities = new GLCapabilities( profile );
		final GLCanvas canvas = new GLCanvas( capabilities );
		try
		{
			Object self = self( ).getConstructor( ).newInstance( );
			self.getClass( )
					.getMethod( "setup", new Class[] { GLCanvas.class } )
					.invoke( self, canvas );
			canvas.addGLEventListener( (GLEventListener)self );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			System.exit( 1 );
		}
		canvas.setSize( INITIAL_WIDTH, INITIAL_HEIGHT );
		canvas.setAutoSwapBufferMode( true );

		frame.add( canvas );
		frame.pack( );
		frame.setVisible( true );

		System.out.println( "\nEnd of processing." );
	}

	private static Class<?> self( )
	{
		// This ugly hack gives us the containing class of a static method
		return new Object( )
		{
		}.getClass( ).getEnclosingClass( );
	}

	private boolean[][] maze;
	private float width, height;

	public void setup( final GLCanvas canvas )
	{
		// Called for one-time setup
		if ( TRACE )
			System.out.println( "-> executing setup()" );

		maze = readMaze( MAZEFILE );
	}

	public boolean[][] readMaze( String filename )
	{
		boolean[][] map = null;
		BufferedReader input;
		String line, lines;
		int width, height;

		try
		{
			input = new BufferedReader( new FileReader( filename ) );

			width = 0;
			height = 0;
			lines = "";
			line = input.readLine( );
			while ( line != null )
			{
				width = Math.max( line.length( ), width );
				if ( line.length( ) > 0 )
				{
					height++;
					lines += line;
				}
				line = input.readLine( );
				if ( line != null )
					lines += '\n';
			}

			System.out.println( lines );

			map = new boolean[height][width];
			int r = height - 1;
			int c = 0;
			for ( int i = 0; i < lines.length( ); i++ )
			{
				if ( lines.charAt( i ) == '\n' )
				{
					r--;
					c = 0;
				}
				else
				{
					if ( lines.charAt( i ) != ' ' )
					{
						map[r][c] = true;
					}
					c++;
				}
			}

			input.close( );
		}
		catch ( IOException ioe )
		{
			System.out.println( ioe.getMessage( ) );
		}

		return map;
	}

	@Override
	public void init( GLAutoDrawable drawable )
	{
		// Called when the canvas is (re-)created - use it for initial GL setup
		if ( TRACE )
			System.out.println( "-> executing init()" );

		final GL2 gl = drawable.getGL( ).getGL2( );

		// TODO: choose your own background colour, and uncomment the lines
		// below to turn on line antialiasing
		gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
		// gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		// gl.glEnable(GL2.GL_LINE_SMOOTH);
		// gl.glEnable(GL2.GL_BLEND);
		// gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void display( GLAutoDrawable drawable )
	{
		// Draws the display
		if ( TRACE )
			System.out.println( "-> executing display()" );

		final GL2 gl = drawable.getGL( ).getGL2( );
		gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );

		// TODO: drawing code here
		
		// For resizing, take width/height of window, divide by numbers of rows/columns in maze
		// This will give width and height of each "square" in the maze		
		if ( maze != null )
		{
			float x, y;
			float deltaX, deltaY;
			
			deltaX = width / ( maze[0].length );
			deltaY = height / ( maze.length - 2 );
			
			//origin of first square
			x = -deltaX;
			y = -deltaY;
			
			for ( int row = 0; row < maze.length; row++ )
			{
				for ( int col = 0; col < maze[0].length; col++ )
				{
					// if not blank in file, then true in array
					// if false in array, draw square
					if ( !maze[row][col] )
					{
						gl.glColor3f( 1.0f, 0.0f, 0.0f );
						gl.glBegin( GL2.GL_QUADS );

							gl.glVertex2f( x, y );						//bottom left
							gl.glVertex2f( x + deltaX, y );				//bottom right
							gl.glVertex2f( x + deltaX, y + deltaY );	//top right
							gl.glVertex2f( x, y + deltaY );				//top left

						gl.glEnd( );
						
						roundBottomLeftCorner( gl, x, y, row, col );
						roundBottomRightCorner( gl, x + deltaX, y, row, col );
						roundTopRightCorner( gl, x + deltaX, y + deltaY, row, col );
						roundTopLeftCorner( gl, x, y + deltaY, row, col );
					}

					x += deltaX;
				}

				x = 0.0f;
				y += deltaY;
			}
			
			x = -deltaX;
			y = -deltaY;
		}
	}
	
	private void roundBottomLeftCorner( GL2 gl, float x, float y, int row, int col )
	{
		float arcX, arcY;
		float rX = ( width / maze.length ) / 4;
		float rY = ( height / maze[0].length ) / 4;
		
		if ( ( col == 0 || maze[row][col - 1] ) && ( row == 0 || maze[row - 1][col] ) )
		{		
			gl.glColor3f( 0.0f, 0.0f, 0.0f );
			gl.glBegin( GL2.GL_QUADS );
			
				gl.glVertex2f( x, y );
				gl.glVertex2f( x, y + rY );
				gl.glVertex2f( x + rX, y + rY );
				gl.glVertex2f( x + rX, y );
			
			gl.glEnd( );
			
			gl.glColor3f( 1.0f, 0.0f, 0.0f );
			gl.glBegin( GL2.GL_TRIANGLE_FAN );
			
				gl.glVertex2f( x + rX, y + rY );
			
				for ( int degs = 180; degs <= 270; degs += 1 )
				{
					arcX = (float)( rX * Math.cos( degs * DEGS_TO_RADS ) + x + rX );
					arcY = (float)( rY * Math.sin( degs * DEGS_TO_RADS ) + y + rY );
					
					gl.glVertex2f( arcX, arcY );
				}
			
			gl.glEnd( );		
		}
	}
	
	private void roundBottomRightCorner( GL2 gl, float x, float y, int row, int col )
	{		
		float arcX, arcY;
		float rX = ( width / maze.length ) / 4;
		float rY = ( height / maze[0].length ) / 4;
		
		if ( ( col == maze[0].length || maze[row][col + 1] ) && ( row == 0 || maze[row - 1][col] ) )
		{		
			gl.glColor3f( 0.0f, 0.0f, 0.0f );
			gl.glBegin( GL2.GL_QUADS );
			
				gl.glVertex2f( x, y );
				gl.glVertex2f( x, y + rY );
				gl.glVertex2f( x - rX, y + rY );
				gl.glVertex2f( x - rX, y );
			
			gl.glEnd( );
			
			gl.glColor3f( 1.0f, 0.0f, 0.0f );
			gl.glBegin( GL2.GL_TRIANGLE_FAN );
			
				gl.glVertex2f( x - rX, y + rY );
				
				for ( int degs = 270; degs <= 360; degs += 1 )
				{
					arcX = (float)( rX * Math.cos( degs * DEGS_TO_RADS ) + x - rX );
					arcY = (float)( rY * Math.sin( degs * DEGS_TO_RADS ) + y + rY );
					
					gl.glVertex2f( arcX, arcY );
				}
				
			gl.glEnd( );
		}
	}
	
	private void roundTopRightCorner( GL2 gl, float x, float y, int row, int col )
	{
		float arcX, arcY;
		float rX = ( width / maze.length ) / 4;
		float rY = ( height / maze[0].length ) / 4;
		
		if ( ( col == maze[0].length || maze[row][col + 1] ) && ( row == maze.length || maze[row + 1][col] ) )
		{		
			gl.glColor3f( 0.0f, 0.0f, 0.0f );
			gl.glBegin( GL2.GL_QUADS );
			
				gl.glVertex2f( x, y );
				gl.glVertex2f( x, y - rY );
				gl.glVertex2f( x - rX, y - rY );
				gl.glVertex2f( x - rX, y );
			
			gl.glEnd( );
			
			gl.glColor3f( 1.0f, 0.0f, 0.0f );
			gl.glBegin( GL2.GL_TRIANGLE_FAN );
			
				gl.glVertex2f( x - rX, y - rY );
			
				for ( int degs = 0; degs <= 90; degs += 1 )
				{
					arcX = (float)( rX * Math.cos( degs * DEGS_TO_RADS ) + x - rX );
					arcY = (float)( rY * Math.sin( degs * DEGS_TO_RADS ) + y - rY );
					
					gl.glVertex2f( arcX, arcY );
				}
			
			gl.glEnd( );		
		}
	}
	
	private void roundTopLeftCorner( GL2 gl, float x, float y, int row, int col )
	{
		float arcX, arcY;
		float rX = ( width / maze.length ) / 4;
		float rY = ( height / maze[0].length ) / 4;
		
		if ( ( col == 0 || maze[row][col - 1] ) && ( row == maze.length || maze[row + 1][col] ) )
		{		
			gl.glColor3f( 0.0f, 0.0f, 0.0f );
			gl.glBegin( GL2.GL_QUADS );
			
				gl.glVertex2f( x, y );
				gl.glVertex2f( x, y - rY );
				gl.glVertex2f( x + rX, y - rY );
				gl.glVertex2f( x + rX, y );
			
			gl.glEnd( );
			
			gl.glColor3f( 1.0f, 0.0f, 0.0f );
			gl.glBegin( GL2.GL_TRIANGLE_FAN );
			
				gl.glVertex2f( x + rX, y - rY );
			
				for ( int degs = 90; degs <= 180; degs += 1 )
				{
					arcX = (float)( rX * Math.cos( degs * DEGS_TO_RADS ) + x + rX );
					arcY = (float)( rY * Math.sin( degs * DEGS_TO_RADS ) + y - rY );
					
					gl.glVertex2f( arcX, arcY );
				}
			
			gl.glEnd( );		
		}
	}

	@Override
	public void dispose( GLAutoDrawable drawable )
	{
		// Called when the canvas is destroyed (reverse anything from init)
		if ( TRACE )
			System.out.println( "-> executing dispose()" );
	}

	@Override
	public void reshape( GLAutoDrawable drawable, int x, int y, int width,
			int height )
	{
		// Called when the canvas has been resized
		// Note: glViewport(x, y, width, height) has already been called so
		// don't bother if that's what you want
		if ( TRACE )
			System.out.println( "-> executing reshape(" + x + ", " + y + ", "
					+ width + ", " + height + ")" );

		final GL2 gl = drawable.getGL( ).getGL2( );

		gl.glViewport( x, y, width, height );

		gl.glMatrixMode( GL2.GL_PROJECTION );
		gl.glLoadIdentity( );
		gl.glOrthof( 0, width, 0, height, 0.0f, 1.0f );
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glLoadIdentity( );

		this.width = width;
		this.height = height;
	}
}
