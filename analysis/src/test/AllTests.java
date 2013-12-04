package test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.util.CloneOutputStream;
import ch.ethz.pa.Verifier;

public class AllTests {
	 
	@BeforeClass
    public static void setupOnce() {
    }

    @Before
    public void setup() {
    	
    }

    @After
    public void tearDown() {
    	
    }
    
    @Test
    public void testSimple() {
    	verify("MyTest");
    }
    
    @Test
    public void testPointerTest1() {
    	verify("PointerTest1");
    }
    
    @Test
    public void testPointerTest2() {
    	verify("PointerTest2");
    }
    
    @Test
    public void testLineNumberTest() {
    	verify("LineNumberTest");
    }

    @Test
    public void testReadValueWithoutAssignment() {
    	String result = verify("ReadValueWithoutAssignment");
    	assertEquals(result,  "Program is SAFE\n");
    }
    
    @Test
    public void testAircraftControl() {
    	String result = verify("AircraftControl");
    	assertEquals(result,  "Program is UNSAFE\n");
    }
	@Test
	public void testExampleTest() {
		String result = verify("ExampleTest");
		assertEquals(result,  "Program is UNSAFE\n");
	}
	@Test
	public void testExampleTest1Speed() {
		String result = verify("ExampleTest1");
		//value is in [-989, 989]
		assertEquals(result,  "Program is SAFE\n");
	}
	@Test
	public void testExampleTest2Height() {
		String result = verify("ExampleTest2");
		//program is safe as per comments
		assertEquals(result,  "Program is SAFE\n");
	}
	@Test
	public void testExampleTest3HeightWithBug() {
		String result = verify("ExampleTest3");
		//program has a BUG as per comments
		assertEquals(result,  "Program is UNSAFE\n");
	}
	
	@Test
	public void testExampleTest4Pressure() {
		String result = verify("ExampleTest4");
		//program is safe due to condition in the end
		assertEquals(result,  "Program is SAFE\n");
	}
	
	private String verify(String className){
		PrintStream stdout = System.out;
		ByteArrayOutputStream myOut = new ByteArrayOutputStream();
		CloneOutputStream cloningStream = new CloneOutputStream(System.out, myOut);
		System.setOut(new PrintStream(cloningStream));

    	Verifier.main(new String[]{className});

    	System.setOut(stdout);
    	String captured = myOut.toString();
    	return captured;
	}

}
