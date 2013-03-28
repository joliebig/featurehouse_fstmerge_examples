

package org.jmol.jcamp;

import java.util.*;
import java.text.DecimalFormat;

public class GraphCharacteristics{
	private static final String INTEGRATION_VALUES_DELIM = ":";
	private static final String INTEGRATION_DELIM = ",";
	private static final int MAX_DECIMALS = 2;

    private boolean _zoomIn; 
	private boolean _integrate; 
    private boolean _grid; 
    private boolean _reverse; 
    private String _allIntegrationValues = null;
	private Hashtable _integrationValues = null; 
    private ArrayList _unsortedIntegrationValues = null; 

	
	private String _textColor = null;
    private String _axisColor = null;
    private String _axisTextColor = null;
    private String _integrateCurveColor = null;
    private String _integrateTextColor = null;
	private String _graphCurveColor = null;
	private DecimalFormat _decForm = null;
	private String _lastPoint = null;

	public GraphCharacteristics(){
		this._zoomIn = false;
		this._integrate = false;
		this._grid = false;
		this._reverse = false;
		this._allIntegrationValues = new String();
		this._integrationValues = new Hashtable();
		this._unsortedIntegrationValues = new ArrayList();
		this._decForm = null;
	}

    public GraphCharacteristics(boolean zoomIn,boolean integrate,boolean grid,
    					   boolean reverse,String allIntegrationValues,String axisColor,
    					    String axisTextColor,String integrateCurveColor,String graphCurveColor,
    					     String textColor, String integrateTextColor){
		this._zoomIn = zoomIn;
		this._integrate = integrate;
		this._grid = grid;
		this._reverse = reverse;
		this._allIntegrationValues = allIntegrationValues;
		setIntegrationValues(this._unsortedIntegrationValues);
		this._axisColor = axisColor;
		this._axisTextColor = axisTextColor;
		this._integrateCurveColor = integrateCurveColor;
		this._integrateTextColor = integrateTextColor;
		this._graphCurveColor = graphCurveColor;
		this._textColor = textColor;
		this._decForm = null;
	}

    
	public void setIntegrationValues(ArrayList unsortedIntegrationValues){
		String[] _temp = null; 
		String _tempString = new String();
		Hashtable _tempTable = new Hashtable(); 

		for(int i=0;i<unsortedIntegrationValues.size();i++){
			_tempString = (String) unsortedIntegrationValues.get(i);
			_temp = _tempString.split(INTEGRATION_VALUES_DELIM);
			_tempTable.put(_temp[0],_temp[1]);
		}

		this._integrationValues = _tempTable;
	}

    
	public void setUnsortedIntegrationValues(String unsortedIntegrationValues){
		String[] _temp = unsortedIntegrationValues.split(INTEGRATION_DELIM);
		ArrayList _tempList = new ArrayList();

		for(int i=0;i<_temp.length;i++){
			_tempList.add(_temp[i]);
		}

		this._unsortedIntegrationValues = _tempList;
		setIntegrationValues(_tempList);
	}

	
	private String isIntegrationCurvePoint(Double point){
		String[] formats = {"####.00","####.0","####"};
		for(int i=0;i<=MAX_DECIMALS;i++){
			_decForm = new DecimalFormat(formats[i]);
			if(this._integrationValues.containsKey(_decForm.format(point)))	{
				if(_lastPoint == null || !_lastPoint.equalsIgnoreCase(_decForm.format(point))){
					_lastPoint = _decForm.format(point);
					return _decForm.format(point);
				}else{
					return null;
				}
			}
		}
		return null;
	}

	
	public String getIntegrationCurveAreaValue(Double point){
		String integratePeakValue = isIntegrationCurvePoint(point);

		if(integratePeakValue != null){
			return (String) this._integrationValues.get(integratePeakValue);
		}
		return null;
	}

	
	public void setZoomIn(boolean zoomIn){
		this._zoomIn = zoomIn;
	}

	public boolean getZoomIn(){
		return this._zoomIn;
	}

	
	public void setGrid(boolean grid){
		this._grid = grid;
	}

	public boolean getGrid(){
		return this._grid;
	}

	
	public void setIntegrate(boolean integrate){
		this._integrate = integrate;
	}

	public boolean getIntegrate(){
		return this._integrate;
	}

	
	public void setReverse(boolean reverse){
		this._reverse = reverse;
	}

	public boolean getReverse(){
		return this._reverse;
	}

	
	public void setIntegrationValues(){

	}

	
	public void setAxisColor(String axisColor){
		this._axisColor = axisColor;
	}

	public String getAxisColor(){
		return this._axisColor;
	}

	
	public void setAxisTextColor(String axisTextColor){
		this._axisTextColor = axisTextColor;
	}

	public String getAxisTextColor(){
		return this._axisTextColor;
	}

	
	public void setIntegrateCurveColor(String integrateCurveColor){
		this._integrateCurveColor = integrateCurveColor;
	}

	public String getIntegrateCurveColor(){
		return this._integrateCurveColor;
	}

	
	public void setGraphCurveColor(String graphCurveColor){
		this._graphCurveColor = graphCurveColor;
	}

	public String getGraphCurveColor(){
		return this._graphCurveColor;
	}

	
	public void setTextColor(String textColor){
		this._textColor = textColor;
	}

	public String getTextColor(){
		return this._textColor;
	}

	public void setIntegrateTextColor(String integrateTextColor){
		this._integrateTextColor = integrateTextColor;
	}

	public String getIntegrateTextColor(){
		return _integrateTextColor;
	}
}
