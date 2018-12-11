package btaw.shared.model;

import java.util.ArrayList;

import btaw.client.modules.function.FunctionView.Function;

public class FunctionData {
	private ArrayList<DefinitionData> defData;
	private String funcName;
	private Function func;
	
	public FunctionData(String funcName, String function) {
		this.funcName = funcName;
		if(function.equals("Arithmetic")) {
			func = Function.ARITHMETIC;
		} else if(function.equals("Kaplan-Meier Plot")) {
			func = Function.KAPLANMEIER;
		} else if(function.equals("Kaplan-Meier Test of Significance")) {
			func = Function.STATISTICAL;
		}
	}

	public ArrayList<DefinitionData> getDefData() {
		return defData;
	}

	public void setDefData(ArrayList<DefinitionData> defData) {
		this.defData = defData;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public Function getFunc() {
		return func;
	}

	public void setFunc(Function func) {
		this.func = func;
	}

}
