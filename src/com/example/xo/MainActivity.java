package com.example.xo;

import android.os.Bundle;
import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button[][] buttons;
	private Game game;
	private TableLayout layout; 
	private TextView textview;
	private Button btnReset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		layout = (TableLayout) findViewById(R.id.main);
		textview = (TextView) findViewById(R.id.textView1);
		btnReset = (Button) findViewById(R.id.btn_reset);
		btnReset.setOnClickListener(new ResetListener());
	
		game = new Game(3);
		drowField();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void drowField () {
		int size = game.getSideSize();
		Item[][] field = game.getField();
		
		buttons = new Button[size][size];
		for (int i = 0; i < field.length; i++) {
			TableRow row = new TableRow(this);
			for (int j = 0; j < field[i].length; j++) {
                buttons[i][j] = new Button(this);
                buttons[i][j].setOnClickListener(new Listener(i, j));
                row.addView(buttons[i][j], new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                buttons[i][j].setWidth(100);
                buttons[i][j].setHeight(100);
			}
		layout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
		}
	}
	
	public void blockAllButtons () {
		for (Button[] i:buttons)
			for (Button j:i) 
				j.setEnabled(false);
	}
	
	public class ResetListener implements View.OnClickListener {
		public void onClick (View view) {
			game.reset();
			textview.setText("");
			for (Button[] i:buttons)
				for (Button j:i) {
					j.setText("");
					j.setEnabled(true);
				}
			
		}
	}
	
	public class Listener implements View.OnClickListener {
	    private int i = 0;
	    private int j = 0;

	    public Listener (int i, int j) {
	        this.i = i;
	        this.j = j;
	    }

	    public void onClick (View view) {
	        Button button = (Button) view;
	        Item[][] field = game.getField();
	        Player player = game.getActivePlayer();
	        
	        field[i][j].setOwner(player);
	        button.setText(player.getName());
	        button.setEnabled(false);
	        
	        Player winner = game.whoWinner();
	        if (winner != null) {
	        	textview.setText("Победитель - "+winner.getName());
	        	blockAllButtons();
	        }
	        else if (game.isEnd())
	        	textview.setText("Ничья :(");
	    }
	}
}



class Player {
	private String name;
	
	public Player (String name) {
		this.name = name;
	}
	
	public String getName () {
		return name;
	}
}

class Item {
	private Player owner;
	
	public void setOwner (Player player) {
		this.owner = player;
	}
	
	public Player getOwner () {
		return owner;
	}
}

class Game {
	private Player[] players;
	private Item[][] field;
	
	private int sideSize;
	private Player activePlayer;
	private int cSetFields = 0; //счетчик заполненных клеток
	private Player firstHod;
	
	public Game (int size) {
		sideSize = size;
		
		players = new Player[2];
		players[0] = new Player ("X");
		players[1] = new Player ("O");
		
		activePlayer = players[(int) Math.round(Math.random())];
		firstHod = activePlayer;
		
		field = new Item[sideSize][sideSize];
		
		for (int i = 0; i < field.length; i++)
			for (int j = 0; j < field[i].length; j++)
				field[i][j] = new Item ();
	}
	
	public int getSideSize () {
		return sideSize;
	}
	
	public Item[][] getField () {
		return field;
	}
	
	public Player getActivePlayer () {
		return activePlayer;
	}
	
	private void changePlayer () {
		activePlayer = (activePlayer == players[0]) ? players[1] : players[0];	
	}
	
	public boolean isEnd () {
		cSetFields++;
		if (cSetFields == sideSize*sideSize)
			return true;
		else {
			changePlayer();
			return false;
		}
		
	}
	
	public Player whoWinner () {
		Item tempGl = field[0][0];
		Item tempNgl = field[0][sideSize-1];
		boolean fGl = false;
		boolean fNgl = false;
		
		for (int i = 0; i < field.length; i++) {
			if ((field[i][i].getOwner() == null) || (field[i][i].getOwner() != tempGl.getOwner()))
				fGl = true; 
			if ((field[sideSize-i-1][i].getOwner() == null)
				|| (field[sideSize-i-1][i].getOwner() != tempNgl.getOwner()))
				fNgl = true;
		}
		
		if (!fGl)
			return tempGl.getOwner();
		else if (!fNgl)
			return tempNgl.getOwner();
		
		for (int i = 0; i < field.length; i++) {
			Item tempCol = field[i][0];
			Item tempRow = field[0][i];
			boolean fCol = false;
			boolean fRow = false;
			
			for (int j = 0; j < field[i].length; j++) {
				if ((field[i][j].getOwner() == null) || (field[i][j].getOwner() != tempCol.getOwner()))
					fCol = true; 
				if ((field[j][i].getOwner() == null) || (field[j][i].getOwner() != tempRow.getOwner()))
					fRow = true;
				if (fCol && fRow)
					break;
			}
				
			if (!fCol)
				return tempCol.getOwner();
			else if (!fRow)
				return tempRow.getOwner();
		}
		return null;
	}
	
	public void reset () {
		activePlayer = firstHod;
		changePlayer();
		firstHod = activePlayer;
		
		cSetFields = 0;
		
		for (Item[] i:field)
			for (Item j:i)
				j.setOwner(null);
	}
	
}
