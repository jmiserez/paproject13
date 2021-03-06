package ch.ethz.pa;

import soot.Local;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.internal.JimpleLocal;
import ch.ethz.pa.domain.AbstractDomain;
import ch.ethz.pa.domain.Domain;

public class StmtAnalyzer extends AbstractStmtSwitch {

	protected IntervalPerVar fallState;
	protected IntervalPerVar branchState;
	protected IntervalPerVar currentState;
	private ExprAnalyzer ea;
	private ObjectSetPerVar aliases;

	public StmtAnalyzer(IntervalPerVar currentState, IntervalPerVar fallState, IntervalPerVar branchState, ObjectSetPerVar aliases) {
		this.fallState = fallState;
		this.branchState = branchState;
		this.currentState = currentState;
		this.ea = new ExprAnalyzer(this, aliases);
		this.aliases = aliases;
	}

	@Override
	public void caseIfStmt(IfStmt stmt) {
		stmt.getCondition().apply(ea);
	}

	@Override
	public void caseAssignStmt(AssignStmt stmt) {
		handleAssign(stmt);
	}

	@Override
	public void caseIdentityStmt(IdentityStmt stmt) {
		handleAssign(stmt);
	}
	
	private void handleAssign(DefinitionStmt stmt) {
		Value lval = stmt.getLeftOp();
		Value rval = stmt.getRightOp();
		System.err.println(lval.getClass().getName() +" ("+lval.getType()+")" + " <- " + rval.getClass().getName());
		AbstractDomain rvar = new Domain();
		if (lval instanceof JimpleLocal) {
			JimpleLocal llocal = ((JimpleLocal)lval);
			String varName = llocal.getName();
			fallState.putIntervalForVar(varName, rvar);
		}
		ea.valueToInterval(rvar, rval);
	}

	public AbstractDomain getLocalVariable(Local v) {
		return currentState.getIntervalForVar(((Local)v).getName());
	}

	@Override
	public void caseInvokeStmt(InvokeStmt stmt) {
		// A method is called. e.g. AircraftControl.adjustValue
		// You need to check the parameters here.
		InvokeExpr expr = stmt.getInvokeExpr();
		expr.apply(ea); //visit expression, but we are not interested in the result here
	}

	@Override
	public void defaultCase(Object obj) {
		System.out.flush();
		System.err.println("Warning: StmtAnalyzer.defaultCase called for: "+obj);
		System.err.flush();
		//TODO: Set everything to TOP
	}

}
