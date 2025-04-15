import Evaluator.ExpressionEvaluator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса ExpressionEvaluator.
 */
class ExpressionEvaluatorTest {

    @Test
    void testSimpleExpressions() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        assertEquals(7.0, evaluator.evaluate("3 + 4"), 1e-6);
        assertEquals(14.0, evaluator.evaluate("2 + 3 * 4"), 1e-6);
        assertEquals(25.0, evaluator.evaluate("(2 + 3) * 5"), 1e-6);
    }

    @Test
    void testFunctions() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        assertEquals(0.0, evaluator.evaluate("sin(0)"), 1e-6);
        assertEquals(1.0, evaluator.evaluate("cos(0)"), 1e-6);
        assertEquals(4.0, evaluator.evaluate("sqrt(16)"), 1e-6);
    }

    @Test
    void testVariables() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        double result = evaluator.evaluate("5 * 2 + 3");
        assertEquals(13.0, result, 1e-6);
    }

    @Test
    void testInvalidExpression() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("2 + * 3"));
    }

    @Test
    void testDivisionByZero() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate("5 / 0"));
    }
}
