import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JScrollPane;

/**
* A small uncomplete implementation to find the shortest
* time to travel between two stops in G�teborg tram and
* bus system. Information of the lines and stops are
* taken from the files lines-gbg.txt and stops-gbg.txt
* in this directory.
* @author (Bror Bjerner)
* @author Some enhancements made 
* by Erland Holmstr�m (2010: Swedish letters, scrollable panes,
* 2011: completion of input)
* (@TODO: the stations should be clickable)
* @version (2011)
*/ 
public class ShortRoute extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// all needs to be visible outside the constructor
	NodeTable<BusStop>      noderna  = new NodeTable<BusStop>();
	DirectedGraph<BusEdge>  grafen; 
	PriorityQueue<String>   names;
	JTextField              from     = new JTextField("", 20);
	JTextField              to       = new JTextField("", 20);
	JTextArea               route;
	JScrollPane             stationList;
	String introText = "  V�lj starth�llplats och sluth�llplats,\n" +
		"  genom att skriva in dem till v�nster.\n " +
		"  Alternativen finns till h�ger.\n " +
		"  Anv�nd Return f�r att expandera ord \n" +
		"  och f�r att starta ber�kningen \n" +
		"  samt tab f�r att g� till n�sta";
	String felTextStart = "Angiven starth�llplats finns ej !!";
	String felTextSlut  = "Angiven sluth�llplats finns ej !!";
	String frome = "fr�n";
	DrawGraph karta = new DrawGraph(); 
	
	// ====================================================================

	/** 
	* The constructor creates the NodeTable and DirectedGraph taken the 
	* information from files lines-gbg.txt and stops-gbg.txt and makes itself 
	* visible.
	*/
	public ShortRoute() {
				
		// try to convert to UTF-8 across plattforms to make Swedish chars work
		//System.out.println("charset = " + java.nio.charset.Charset.defaultCharset()); 
		// MacRoman macintosh  Windows-1252 ISO 8859-1 UTF-8 
		try {
			// convert whatever this file is encoded in to UTF-8, 
			// kill the exception (can't happen)
			introText    = new String(introText.getBytes(java.nio.charset.Charset.defaultCharset().toString()), "UTF-8");
			felTextStart = new String(felTextStart.getBytes(java.nio.charset.Charset.defaultCharset().toString()), "UTF-8");
			felTextSlut  = new String(felTextSlut.getBytes(java.nio.charset.Charset.defaultCharset().toString()), "UTF-8");
			frome        = new String(frome.getBytes(java.nio.charset.Charset.defaultCharset().toString()), "UTF-8");
		} catch (UnsupportedEncodingException e) {System.exit(0);}

		// read the graph and draw it in a separate window
		// creates the graph and fills the p-queue "names"
		karta.setLocation(50, 250);
		readAndDrawGraph();
		System.out.println("graph drawn");   // debug
		// now to the graphics in the Frame
		// Left part
		// select is a panel for structuring the left part
		// i.e label, textfield for "from", label, textfield for "to"
		JPanel select = new JPanel( new GridLayout(4,1));
		select.setBackground(Color.yellow);
		select.add(new JLabel("Ange startpunkt !", JLabel.CENTER));
		select.add(from);
		select.add(new JLabel("Ange slutpunkt !", JLabel.CENTER));
		select.add(to);
		from.setBackground(Color.white);
		from.setForeground(Color.blue);
		to.setBackground(Color.white);
		to.setForeground(Color.blue);
		from.addActionListener(this);
		to.addActionListener(this);
		
		// Middle part
		// route is the middle text area part where messages are displayed
		// give the middle text area a scrollbar to the right
		route = new JTextArea(introText, 12, 40);
		route.setEditable(false);
		JScrollPane routeScrollPane = new JScrollPane(route); 
		routeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		route.setBackground(Color.white);
		route.setForeground(Color.blue);
		
		// Rigth part
		JLabel head  = new JLabel( "   ****        Alternativ     *****     " );
		head.setForeground(Color.red);
		
		// add all stations to the right scrollpane
		// tanken �r att dom skall vara valbara men det fungerar inte �n
		// En JList vill ha en ListModel som parameter 
		// En ListModel �r ett interface som implementeras av klassen AbstractListModel
		// DefaultListModel �rver AbstractListModel
		DefaultListModel valList = new DefaultListModel();
		JList alternativ  = new JList(valList);
		alternativ.setBackground(Color.white);
		alternativ.setForeground(Color.blue);
		// add a scroll pane to the station list in alternativ
		stationList  = new JScrollPane(alternativ); 
		// read names from p-queue and load them into the list 
		while ( ! names.isEmpty() )
			valList.addElement( names.poll() );
		
		// panel for structure
		JPanel valPanel = new JPanel(new BorderLayout()); 
		valPanel.setBackground(Color.white);
		valPanel.add(head, "North");
		valPanel.add(stationList, "Center");
		
		// put it all together in the frame
		add(select, "West");
		add(routeScrollPane, "Center");
		add(valPanel, "East");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	} // end ShortRoute
	// ====================================================================
	// ====================================================================

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == from) {
			String tmpFrom = from.getText().trim();
			String tmpTo   = to.getText().trim();
			if ( tmpFrom.equals("MST") || tmpFrom.equals("mst") )
				findMinSpan();
			else { 
				try{ // check if from exists
					//String str = from.getText();
					// fixa s� man kan skippa att skriva f�rsta med stor bokstav
					tmpFrom = Character.toUpperCase(tmpFrom.charAt(0)) + tmpFrom.substring(1);
					int pos = noderna.findLeading( tmpFrom ).getNodeNo(); 
					route.setText(introText + "\n");
					from.setText(noderna.find(pos).toString());
				}
				catch( NullPointerException npe ) {
					route.setText( felTextStart + "\n");
					return; 
				}
				if ( ! tmpTo.equals("") ) {
					findShort();
				}
			} 
		}
		else if (e.getSource() == to) {
			String tmpFrom = from.getText().trim();
			String tmpTo = to.getText().trim();
			if ( tmpTo.equals("MST") || tmpTo.equals("mst") )
				findMinSpan();
			else { 
				try{ // check if to exists
					//String str = to.getText();
					tmpTo = Character.toUpperCase(tmpTo.charAt(0)) + tmpTo.substring(1);
					int pos = noderna.findLeading( tmpTo ).getNodeNo(); 
					route.setText(introText + "\n");
					to.setText(noderna.find(pos).toString());
				}
				catch( NullPointerException npe ) {
					route.setText( felTextStart + "\n");
					return; 
				}
				if ( ! tmpFrom.equals("") ) {
					findShort();
				}
			}
			
			/*
			else if ( ! from.getText().equals("") ) 
				findShort();
			*/
		}
	}
	// ====================================================================
	private void findMinSpan() {
		route.setText("");
		Iterator<BusEdge> it = grafen.minimumSpanningTree();
		if ( it != null & it.hasNext()) {
			double totWeight = 0;  
			int    totNodes  = 0;  // only for easier testing
			karta.clearLayer(DrawGraph.Layer.OVERLAY);
			while ( it.hasNext() ) {
				BusEdge be = it.next();
				totNodes++;
				totWeight += be.getWeight();
				route.append(  makeText2(be) + "\n");
				// draw the MST
				BusStop from = noderna.find(be.from);
				BusStop to   = noderna.find(be.to);
				karta.drawLine(from.xpos, from.ypos, to.xpos, to.ypos, Color.red, 2.5, DrawGraph.Layer.OVERLAY);
			}
			karta.repaint();
			route.append( "Antal: " + totNodes + " totalvikt: " + totWeight + "\n");
		}
		else
			from.setText("Det fanns ej n�gon l�sning");
		to.setText("");
		from.setText("");

	} //  findMinSpan
	// ====================================================================
	private void findShort() {
		// @TODO tag bort gamla v�gen dvs rita om grafen
		// sv�rt att beh�lla linjef�rgerna?
		int start, slut;
		try{ // read from station
			String str = from.getText();
			str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
			start = noderna.find( str ).getNodeNo(); 
		}
		catch( NullPointerException npe ) {
			route.setText( felTextStart + "\n");
			return; 
		}
		try{ // read to station
			String str = to.getText();
			str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
			slut = noderna.find( str ).getNodeNo();
		}
		catch( NullPointerException npe ) {
			route.setText( felTextSlut + "\n");
			return; 
		}

		double totWeight = 0;
		int    totNodes  = 0;
		route.setText("");
		karta.clearLayer(DrawGraph.Layer.OVERLAY);
		Iterator<BusEdge> it = grafen.shortestPath( start, slut); 
		while ( it.hasNext() ) {
			BusEdge e = it.next();
			route.append( makeText1(e) + "\n");
			totNodes++;
			totWeight += e.getWeight();
			// draw the shortest path
			BusStop from = noderna.find(e.from),
			to = noderna.find(e.to);			
			karta.drawLine(from.xpos, from.ypos, to.xpos, to.ypos, Color.black, 4.0, DrawGraph.Layer.OVERLAY);
		}
		karta.repaint();
		route.append( "Antal: " + totNodes + " totalvikt: " + totWeight + "\n");
		from.setText("");
		to.setText("");

	}  // findShort 
	// ====================================================================
	
	private String makeText1(BusEdge be) {
		return "Tag " + be.line + " " + frome + " " + 
			noderna.find(be.from).name + " till " +
			noderna.find(be.to).name + 
			" framme efter " + be.getWeight();
	}
	private String makeText2(BusEdge be) {
		return noderna.find(be.from).name + " till " + 
			noderna.find(be.to).name + " tar " + 
			be.getWeight() + " minuter";
	}
	// ====================================================================
	// ====================================================================
	private void readAndDrawGraph() {
		// @TODO
		// hur rita flera linjer mellan 2 noder? (f�r flera linjer)
		// redraw should be done by saving the graph in Graph
		// and doing a repaint
		// reading of the graph should be done in the graph itself
		// it should be possible to get an iterator over nodes and one over edges
		// read in all the stops and lines and draw the lmap
		Scanner indata = null;
		// insert into p-queue to get them sorted
		names = new PriorityQueue<String>();
		try {
			// Read stops and put them in the node-table 
			// in order to give the user a list of possible stops
			// assume input file is correct
			indata = new Scanner(new File("stops.noBOM.txt"), "UTF-8");
			while (indata.hasNext()) {
				String hpl = indata.next().trim();
				/*
				try {
					// convert whatever this file is encoded in to UTF-8, 
					// kill the exception (can't happen)
					hpl    = new String(hpl.getBytes(java.nio.charset.Charset.defaultCharset().toString()), "UTF-8");
				} catch (UnsupportedEncodingException e) {System.exit(0);}
				*/
				int xco = indata.nextInt();
				int yco = indata.nextInt();
				noderna.add(new BusStop(hpl, xco, yco));
				names.add(hpl);
				// Draw
				// this is a fix: fixa att K�lltorp och Torp �r samma h�llplats
				if ( hpl.equals("Torp") ) {
					xco += 11;
					hpl = "   / Torp";
				}
				karta.drawString(hpl, xco, yco,DrawGraph.Layer.BASE); 
			}
			indata.close();
			
			//  Read in the lines and add to the graph
			indata =  new Scanner(new File("lines.noBOM.txt"), "UTF-8");
			grafen = new DirectedGraph<BusEdge>(noderna.noOfNodes());
			while ( indata.hasNext() ) {
				String lineNo = indata.next();
				int    antal  = indata.nextInt() -1;
				int    from   = noderna.find( indata.next() ).getNodeNo(); ///// fixa svenska
				// hur rita flera linjer mellan 2 noder?
				// enkel inc fungerar inte
				// f�rgen borde vara "�kta" dvs linjef�rg
				Color color = new Color((float)Math.random(), 
										(float)Math.random(), 
										(float)Math.random());
				for (int i = 0; i < antal; i++ ) {
					int to = noderna.find( indata.next() ).getNodeNo();
					grafen.addEdge(
						new BusEdge(from, to, indata.nextInt(), lineNo ));
					// Draw
					BusStop busFrom = noderna.find(from);
					BusStop busTo   = noderna.find(to);
					karta.drawLine(busFrom.xpos, busFrom.ypos, 
								   busTo.xpos, busTo.ypos, color, 2.0f, DrawGraph.Layer.BASE);
					from = to;
				}
			}
			indata.close();
		} 
		catch (FileNotFoundException fnfe) {
			throw new RuntimeException(
				" Indata till bussh�llplatserna saknas" );
		}
		karta.repaint();
	} // end readAndDrawGraph
	
	public static void main(String[] args) {
		new ShortRoute();
	}
}  // end ShortRoute

/* 
 * Hej,

h�r �r en enkel (men inte s�rskilt effektiv) l�sning p� grafuppritningen i den tredje labben.
Den ers�tter varje drawString() med en JLabel och skapar tv� "lager" (inre klasser som �rver JPanel) som tar hand om och ritar upp objekt med gr�nssnittet Shape.

Den fullst�ndiga grafen l�ggs i ett baslager och rutter ritas i ett lager ovanp�.

F�rdelar:
Kan �ndra storlek, flytta utanf�r ramar o.s.v utan att bilden f�rsvinner.
Gamla rutter ritas inte om (eller stannar inte kvar, f�r att vara petig).
F�r�ndrar bara tv� filer, varav 9 rader i ShortRoute.

Nackdelar:
Elegant som en dansande nosh�rning.
L�ngsam rendering.

Hoppas att det kan vara till n�gon nytta, eller ge uppslag till en b�ttre l�sning.

/Jesper

 */
