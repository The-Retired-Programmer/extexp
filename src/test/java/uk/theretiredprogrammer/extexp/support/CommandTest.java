/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.theretiredprogrammer.extexp.support;

import java.util.Optional;
import javax.json.JsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Tests for parameter substitution in Command
 *
 * @author richard
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommandTest {

    @Test
    @Order(1)
    @DisplayName("Parameter substitution in middle of string")
    void sub_middle(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        String in = "test/${1}/end";
        Command instance = new CommandImpl();
        String expResult = "test/AAA-ZZZ/end";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }

    @Test
    @Order(2)
    @DisplayName("Parameter substitution at start of string")
    void sub_start(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        String in = "${1}/end";
        Command instance = new CommandImpl();
        String expResult = "AAA-ZZZ/end";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }

    @Test
    @Order(3)
    @DisplayName("Parameter substitution at end of string")
    void testSub_end(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        String in = "test/${1}";
        Command instance = new CommandImpl();
        String expResult = "test/AAA-ZZZ";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }

    @Test
    @Order(4)
    @DisplayName("Parameter substitution in middle of string - with recusive substitution")
    void testSub_recursive_middle(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        String in = "test/${2}/end";
        Command instance = new CommandImpl();
        String expResult = "test/QZZZ-AAAW/end";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }

    @Test
    @Order(5)
    @DisplayName("Parameter substitution in middle of string - missing parameter")
    void testSub_missing_middle(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        String in = "test/${9}/end";
        Command instance = new CommandImpl();
        String expResult = "test//end";
        String result = instance.sub(in, (n) -> getParam(n));
        assertEquals(expResult, result);
    }

    private Optional<String> getParam(String pname) {
        switch (pname) {
            case "1":
                return Optional.ofNullable("AAA-ZZZ");
            case "2":
                return Optional.ofNullable("Q${3}W");
            case "3":
                return Optional.ofNullable("ZZZ-AAA");
            default:
                return Optional.empty();
        }
    }

    class CommandImpl extends Command {

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
