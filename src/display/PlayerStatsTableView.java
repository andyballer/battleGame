package display;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import display.util.TableView;
import jgame.JGPoint;
import engine.Level;
import objects.objects.Unit;

public class PlayerStatsTableView extends TableView {
	
	private AbstractTableModel myModel;
	private Map<JGPoint, Unit> myPlayers;

	public PlayerStatsTableView(String _label, EmptyBorder border,
			Dimension dim, boolean isMultiSelection, boolean isToggle,
			boolean hasButton) {
		super(_label, border, dim, isMultiSelection, isToggle, hasButton);	
		
		myModel = new AbstractTableModel() {

			private static final long serialVersionUID = 739683443345756054L;

			@Override
			public String getColumnName(int column){
				return Constant.statTableColumns[column];
			}
			
			@Override
			public int getColumnCount() {
				return Constant.statTableColumns.length;
			}

			@Override
			public int getRowCount() {
				if (myPlayers == null)
					return 0;
				return myPlayers.size();
			}

			@Override
			public Object getValueAt(int row, int column) {
				for(Unit player : myPlayers.values()){
					if(row == 0){
						switch(column) {
							case 0: return player.getObjName();
							case 1: return player.getRow();
							case 2: return player.getCol();
							case 3: return player.getLevel();
							case 4: return player.getMoveRate();
							case 5: return player.getAttackRate();
							default: return 0;
						}
					}
					row--;
				}
				return 0;			
			}
		};
		
		setTableModel(myModel);
	}	
	
	public void updateStats(){
		myModel.fireTableDataChanged();
		setTableModel(myModel);
	}
	
	public void setPlayerStats(Level l){
		myPlayers = l.getPlayers();	
		updateStats();
	}	
	
}
