package ch.ethz.pa;

import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;

public class StmtAnalyzer extends AbstractStmtSwitch {
	
	private IntervalPerVar fallState;
	private IntervalPerVar branchState;
	private IntervalPerVar currentState;
	private ExprAnalyzer ea;

	public StmtAnalyzer(IntervalPerVar currentState, IntervalPerVar fallState, IntervalPerVar branchState) {
		this.fallState = fallState;
		this.branchState = branchState;
		this.currentState = currentState;
		this.ea = new ExprAnalyzer(this);
	}

	@Override
	public void caseIfStmt(IfStmt stmt) {
		// TODO: Write this
	}

	@Override
	public void caseAssignStmt(AssignStmt stmt) {
		handleAssign(stmt);
	}

	@Override
	public void caseIdentityStmt(IdentityStmt stmt) {
		handleAssign(stmt);
	}
	
	void handleAssign(DefinitionStmt stmt) {
		Value lval = stmt.getLeftOp();
		Value rval = stmt.getRightOp();
		System.out.println(lval.getClass().getName() + " " + rval.getClass().getName());
        Interval rvar = new Interval();
        if (lval instanceof JimpleLocal) {
        	String varName = ((JimpleLocal)lval).getName();
        	fallState.putIntervalForVar(varName, rvar);
        }
        ea.translateExpr(rvar, rval);
    }
}
