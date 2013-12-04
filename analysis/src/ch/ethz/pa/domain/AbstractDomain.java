package ch.ethz.pa.domain;

import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.LeExpr;
import soot.jimple.LtExpr;
import soot.jimple.NeExpr;
import soot.toolkits.scalar.Pair;

public abstract class AbstractDomain {

	public boolean contains(AbstractDomain other) {
		return this.equals(join(other));
	}
	
	public abstract void copyFrom(AbstractDomain other);
	public abstract AbstractDomain copy();
	public abstract AbstractDomain join(AbstractDomain other);
	public abstract AbstractDomain meet(AbstractDomain other);
	
	public abstract AbstractDomain plus(AbstractDomain other);
	public abstract AbstractDomain minus(AbstractDomain other);
	public abstract AbstractDomain multiply(AbstractDomain other);

	public abstract AbstractDomain getTop();
	public abstract AbstractDomain getBot();
	public abstract boolean isTop();
	public abstract boolean isBot();

	static public abstract class PairSwitch extends AbstractJimpleValueSwitch {
		public Pair<AbstractDomain, AbstractDomain> fallOut;
		public Pair<AbstractDomain, AbstractDomain> branchOut;

		/*
		 * These are public for JUnit reasons. Do NOT call them outside of this package.
		 */
		public abstract Pair<AbstractDomain, AbstractDomain> doEqExpr(ConditionExpr v);
		public abstract Pair<AbstractDomain, AbstractDomain> doNeExpr(ConditionExpr v);
		public abstract Pair<AbstractDomain, AbstractDomain> doGeExpr(ConditionExpr v);
		public abstract Pair<AbstractDomain, AbstractDomain> doGtExpr(ConditionExpr v);
		public abstract Pair<AbstractDomain, AbstractDomain> doLeExpr(ConditionExpr v);
		public abstract Pair<AbstractDomain, AbstractDomain> doLtExpr(ConditionExpr v);

		/*
		 * fallOut = inverse of OP
		 * branchOut = OP
		 */

		public void caseEqExpr(EqExpr v) {
			branchOut = doEqExpr(v);
			fallOut = doNeExpr(v);
		}

		public void caseNeExpr(NeExpr v) {
			branchOut = doNeExpr(v);
			fallOut = doEqExpr(v);
		}

		public void caseGeExpr(GeExpr v) {
			branchOut = doGeExpr(v);
			fallOut = doLtExpr(v);
		}

		public void caseGtExpr(GtExpr v) {
			branchOut = doGtExpr(v);
			fallOut = doLeExpr(v);
		}

		public void caseLeExpr(LeExpr v) {
			branchOut = doLeExpr(v);
			fallOut = doGtExpr(v);
		}

		public void caseLtExpr(LtExpr v) {
			branchOut = doLtExpr(v);
			fallOut = doGeExpr(v);
		}
	}
}