package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.IntConstant;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JNeExpr;
import soot.toolkits.scalar.Pair;
import ch.ethz.pa.ExprAnalyzer;
import ch.ethz.pa.ObjectSetPerVar;
import ch.ethz.pa.domain.AbstractDomain;
import ch.ethz.pa.domain.Domain;

public class DomainTest {
	
	ExprAnalyzer ea;
	static Value dummyVal;
	
	@Before
	public void setUp() throws Exception {
		ea = new ExprAnalyzer(null,new ObjectSetPerVar());
		dummyVal = IntConstant.v(0);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testContains() {
		assertTrue(new Domain(0,0).contains(new Domain(0,0)));
		assertTrue(new Domain(-2,2).contains(new Domain(0,0)));
		assertTrue(new Domain(-4,4).contains(new Domain(-2,2)));
		assertTrue(new Domain(1,4).contains(new Domain(2,4)));
		assertTrue(new Domain(0,10).contains(new Domain(1,1)));
		assertTrue(new Domain(0,15).contains(new Domain(1,1)));
		
	}
	
	@Test
	public void testPlus() {
		assertEquals(new Domain(2, 2), new Domain(1, 1).plus(new Domain(1, 1)));
		assertEquals(new Domain(0, 2), new Domain(-1, 1).plus(new Domain(1, 1)));
		assertEquals(new Domain(-2, -2), new Domain(-1, -1).plus(new Domain(-1, -1)));
	}

	@Test
	public void testMinus() {
		assertEquals(new Domain(0, 0), new Domain(1, 1).minus(new Domain(1, 1)));
		assertEquals(new Domain(-2, 0), new Domain(-1, 1).minus(new Domain(1, 1)));
		assertEquals(new Domain(0, 0), new Domain(-1, -1).minus(new Domain(-1, -1)));
		
	}

	@Test
	public void testMultiply() {
		assertEquals(new Domain(1, 1), new Domain(1, 1).multiply(new Domain(1, 1)));
		assertEquals(new Domain(-1, 1), new Domain(-1, 1).multiply(new Domain(1, 1)));
		assertEquals(new Domain(1, 1), new Domain(-1, -1).multiply(new Domain(-1, -1)));
	}
	
	@Test
	public void testBitwiseSoundnessAnd(){
		testBitwiseSoundness(1);
	}
	@Test
	public void testBitwiseSoundnessOr(){
		testBitwiseSoundness(2);
	}
	@Test
	public void testBitwiseSoundnessXor(){
		testBitwiseSoundness(3);
	}

	private void testBitwiseSoundness(int op){
		//test all 8-bit integers
		ArrayList<Domain> testObjs1 = new ArrayList<Domain>();
		ArrayList<Domain> testObjs2 = new ArrayList<Domain>();
//		int max = (1 << 8) - 1;
//		int min = (1 << 8) * -1;
//		for(int l = min; l <= max; l++){
//			for(int u = min; l <= max; l++){
//				testObjs.add(new Domain(l,u));
//			}
//		}
		testObjs1.add(new Domain(3,3));
		testObjs2.add(new Domain(3,3));

		Iterator<Domain> i1 = testObjs1.iterator();
		Iterator<Domain> i2 = testObjs2.iterator();
		while(i1.hasNext()){
			Domain a = i1.next();
			while(i2.hasNext()){
				Domain b = i2.next();
				//16*16 combinations
				AbstractDomain r = null;
				switch(op){
				case 1:
					r = a.copy().and(b.copy());
					break;
				case 2:
					r = a.copy().or(b.copy());
					break;
				case 3:
					r = a.copy().xor(b.copy());
					break;
				}
				int aStart = a.getLower();
				int aEnd = a.getUpper();
				int bStart =  b.getLower();
				int bEnd = b.getUpper();
				for(int aVal = aStart; aVal <= aEnd; aVal++){
					for(int bVal = bStart; bVal <= bEnd; bVal++){
						int rVal = 0;
						switch(op){
						case 1:
							rVal = aVal & bVal;
							break;
						case 2:
							rVal = aVal | bVal;
							break;
						case 3:
							rVal = aVal ^ bVal;
							break;
						}
						System.out.println(aVal+" & "+bVal+" = "+r+" ("+rVal+")");
						assertTrue(r.contains(new Domain(rVal)));
					}
				}
			}
		}
	}
	
	
	
	@Test
	public void testJoin() {
		assertEquals(new Domain(1, 1), new Domain(1, 1).join(new Domain(1, 1)));
		assertEquals(new Domain(-1, 1), new Domain(-1, 1).join(new Domain(1, 1)));
		assertEquals(new Domain(-2, 1), new Domain(-1, -1).join(new Domain(-2, 1)));
		assertEquals(new Domain(-5, 5), new Domain(-5, 5).join(new Domain(-2, 3)));
		assertEquals(new Domain().getTop(), new Domain(1, 1).join(new Domain().getTop()));
		assertEquals(new Domain(1, 1), new Domain(1, 1).join(new Domain().getBot()));
		assertEquals(new Domain().getBot(), new Domain().getBot().join(new Domain().getBot()));
	}
	
	@Test
	public void testMeet() {
		assertEquals(new Domain(1, 1), new Domain(1, 1).meet(new Domain(1, 1)));
		assertEquals(new Domain(1, 1), new Domain(-1, 1).meet(new Domain(1, 1)));
		assertEquals(new Domain(-1, -1), new Domain(-1, -1).meet(new Domain(-2, 1)));
		assertEquals(new Domain(-2, 3), new Domain(-5, 5).meet(new Domain(-2, 3)));
		assertEquals(new Domain(-6, 6), new Domain(-6, 6).meet(new Domain().getTop()));
		assertEquals(new Domain().getTop(), new Domain(-6, 6).join(new Domain().getTop()));
		assertEquals(new Domain(1, 1), new Domain(1, 1).meet(new Domain().getTop()));
		assertEquals(new Domain().getBot(), new Domain(1, 1).meet(new Domain(2, 2)));
		assertEquals(new Domain().getBot(), new Domain(1, 1).meet(new Domain().getBot()));
		assertEquals(new Domain().getBot(), new Domain().getBot().meet(new Domain().getBot()));
	}
	
	private static Pair<AbstractDomain, AbstractDomain> testPairEq(ConditionExpr expr, AbstractDomain a1, AbstractDomain a2) {
		Domain.PairSwitch ps = new Domain.PairSwitch(a1, a2);
		expr.apply(ps);
		return ps.branchOut;
	}
	
	private static Pair<AbstractDomain, AbstractDomain> getExpected(AbstractDomain a1, AbstractDomain a2) {
		return new Pair<AbstractDomain, AbstractDomain>(a1, a2);
	}
	
	@Test
	public void testPair() {
		JEqExpr expr = new JEqExpr(dummyVal, dummyVal);
		assertEquals(getExpected(new Domain(3,4), new Domain(3, 4)), 
				testPairEq(expr, new Domain(0, 4), new Domain(3,7)));
	}
	
	@Test
	public void testPairNe() {
		JNeExpr expr = new JNeExpr(dummyVal, dummyVal);
		assertEquals(getExpected(new Domain(0, 7), new Domain(0, 7)), 
				testPairEq(expr, new Domain(0, 4), new Domain(3, 7)));
	}
	
	@Test
	public void testPairLe() {
		JLeExpr expr = new JLeExpr(dummyVal, dummyVal);
		assertEquals(getExpected(new Domain(0, 4), new Domain(3, 7)), 
				testPairEq(expr, new Domain(0, 4), new Domain(3, 7)));
		assertEquals(getExpected(new Domain(0, 4), new Domain(0, 4)), 
				testPairEq(expr, new Domain(0, 4), new Domain(0, 4)));
		assertEquals(getExpected(new Domain(0, 7), new Domain(3, 7)), 
				testPairEq(expr, new Domain(0, 10), new Domain(3, 7)));
	}
	
	@Test
	public void testPairGe() {
		JGeExpr expr = new JGeExpr(dummyVal, dummyVal);
		assertEquals(getExpected(new Domain(3, 4), new Domain(3, 4)), 
				testPairEq(expr, new Domain(0, 4), new Domain(3, 7)));
		assertEquals(getExpected(new Domain(0, 4), new Domain(0, 4)), 
				testPairEq(expr, new Domain(0, 4), new Domain(0, 4)));
		assertEquals(getExpected(new Domain(3, 10), new Domain(3, 7)), 
				testPairEq(expr, new Domain(0, 10), new Domain(3, 7)));
	}
	
	@Test
	public void testPairGt() {
		JGtExpr expr = new JGtExpr(dummyVal, dummyVal);
		assertEquals(getExpected(new Domain(4, 4), new Domain(3, 3)), 
				testPairEq(expr, new Domain(0, 4), new Domain(3, 7)));
		assertEquals(getExpected(new Domain(1, 4), new Domain(0, 3)), 
				testPairEq(expr, new Domain(0, 4), new Domain(0, 4)));
		assertEquals(getExpected(new Domain(4, 10), new Domain(3, 7)), 
				testPairEq(expr, new Domain(0, 10), new Domain(3, 7)));
	}
	
	@Test
	public void testPairLt() {
		JLtExpr expr = new JLtExpr(dummyVal, dummyVal);
		assertEquals(getExpected(new Domain(0, 3), new Domain(1, 4)), 
				testPairEq(expr, new Domain(0, 4), new Domain(0, 4)));
		assertEquals(getExpected(new Domain(0, 4), new Domain(3, 7)), 
				testPairEq(expr, new Domain(0, 4), new Domain(3, 7)));
		assertEquals(getExpected(new Domain(0, 6), new Domain(3, 7)), 
				testPairEq(expr, new Domain(0, 10), new Domain(3, 7)));
	}

}
