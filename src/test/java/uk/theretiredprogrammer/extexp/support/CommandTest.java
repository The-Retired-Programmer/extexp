/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.theretiredprogrammer.extexp.support;

import java.util.Optional;
import javax.json.JsonObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richard
 */
public class CommandTest {

    public CommandTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of sub method, of class Command - substituted in the middle of the
     * content.
     */
    @Test
    public void testSub_middle() {
        System.out.println("sub (middle insert)");
        String in = "test/${1}/end";
        Command instance = new CommandImpl();
        String expResult = "test/AAA-ZZZ/end";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }

    /**
     * Test of sub method, of class Command - substituted at the start of the
     * content.
     */
    @Test
    public void testSub_start() {
        System.out.println("sub (start insert)");
        String in = "${1}/end";
        Command instance = new CommandImpl();
        String expResult = "AAA-ZZZ/end";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }

    /**
     * Test of sub method, of class Command - substituted at the end of the
     * content.
     */
    @Test
    public void testSub_end() {
        System.out.println("sub (end insert)");
        String in = "test/${1}";
        Command instance = new CommandImpl();
        String expResult = "test/AAA-ZZZ";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }
    
    /**
     * Test of sub method, of class Command - recursively substituted in the middle of the
     * content.
     */
    @Test
    public void testSub_recursive_middle() {
        System.out.println("sub (recursive middle insert)");
        String in = "test/${2}/end";
        Command instance = new CommandImpl();
        String expResult = "test/QZZZ-AAAW/end";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }
    
    /**
     * Test of sub method, of class Command - substituted in the middle of the
     * content.
     */
    @Test
    public void testSub_missing_middle() {
        System.out.println("sub (missing middle insert)");
        String in = "test/${9}/end";
        Command instance = new CommandImpl();
        String expResult = "test//end";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }

    public Optional<String> getParam(String pname) {
        switch (pname){
            case "1": return Optional.ofNullable("AAA-ZZZ"); 
            case "2": return Optional.ofNullable("Q${3}W"); 
            case "3": return Optional.ofNullable("ZZZ-AAA"); 
            default: return Optional.empty();
        }
    }

    public class CommandImpl extends Command {

        @Override
        public String getWidgetImageName() {
            return "WIDGET";
        }

        @Override
        public String getDisplayName() {
            return "NAME";
        }

        @Override
        public void executecommand() {
        }

        @Override
        public void parse(JsonObject jobj) {
        }
    }

}
