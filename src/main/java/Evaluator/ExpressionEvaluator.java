package Evaluator;

import java.util.*;
import java.util.function.Function;

/**
 * Класс для разбора и вычисления арифметических выражений.
 * Поддерживает числовые значения, переменные, скобки, базовые арифметические операции и функции.
 */
public class ExpressionEvaluator {
    /** Хранилище значений переменных, введённых пользователем */
    private final Map<String, Double> variables = new HashMap<>();

    /** Сканер для ввода значений переменных с консоли */
    private final Scanner scanner = new Scanner(System.in);

    /** Поддерживаемые функции и их реализация */
    private static final Map<String, Function<Double, Double>> FUNCTIONS = Map.of(
            "sin", Math::sin,
            "cos", Math::cos,
            "tan", Math::tan,
            "sqrt", Math::sqrt,
            "abs", Math::abs
    );

    /**
     * Главный метод для вычисления выражения.
     *
     * @param expression строка выражения (например, "2 + 3 * x")
     * @return результат вычисления
     * @throws IllegalArgumentException если выражение некорректно
     */
    public double evaluate(String expression) throws IllegalArgumentException {
        List<String> tokens = tokenize(expression.replaceAll("\\s+", ""));
        List<String> rpn = toRPN(tokens);
        return evaluateRPN(rpn);
    }

    /**
     * Разбивает строку выражения на токены: числа, переменные, операторы, скобки.
     *
     * @param expr выражение без пробелов
     * @return список токенов
     */
    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int i = 0;

        while (i < expr.length()) {
            char ch = expr.charAt(i);
            if (Character.isDigit(ch) || ch == '.') {
                current.setLength(0);
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    current.append(expr.charAt(i++));
                }
                tokens.add(current.toString());
            } else if (Character.isLetter(ch)) {
                current.setLength(0);
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                    current.append(expr.charAt(i++));
                }
                tokens.add(current.toString());
            } else if ("+-*/^(),".indexOf(ch) != -1) {
                tokens.add(Character.toString(ch));
                i++;
            } else {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
        }

        return tokens;
    }

    /**
     * Преобразует список токенов в обратную польскую запись (RPN).
     *
     * @param tokens список токенов
     * @return выражение в RPN
     */
    private List<String> toRPN(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        Map<String, Integer> precedence = Map.of(
                "+", 1, "-", 1, "*", 2, "/", 2, "^", 3
        );

        for (String token : tokens) {
            if (isNumber(token) || isVariable(token)) {
                output.add(token);
            } else if (FUNCTIONS.containsKey(token)) {
                stack.push(token);
            } else if (token.equals(",")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
            } else if ("+-*/^".contains(token)) {
                while (!stack.isEmpty() && precedence.containsKey(stack.peek())
                        && precedence.get(stack.peek()) >= precedence.get(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) throw new IllegalArgumentException("Mismatched brackets");
                stack.pop();
                if (!stack.isEmpty() && FUNCTIONS.containsKey(stack.peek())) {
                    output.add(stack.pop());
                }
            } else {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) throw new IllegalArgumentException("Mismatched brackets");
            output.add(stack.pop());
        }

        return output;
    }

    /**
     * Вычисляет значение выражения, записанного в обратной польской записи.
     *
     * @param rpn список токенов в RPN
     * @return результат вычисления
     */
    private double evaluateRPN(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isVariable(token)) {
                stack.push(getVariableValue(token));
            } else if (FUNCTIONS.containsKey(token)) {
                if (stack.isEmpty()) throw new IllegalArgumentException("There are not enough arguments for the function");
                double arg = stack.pop();
                stack.push(FUNCTIONS.get(token).apply(arg));
            } else if ("+-*/^".contains(token)) {
                if (stack.size() < 2) throw new IllegalArgumentException("There are not enough arguments for the operation");
                double b = stack.pop(), a = stack.pop();
                switch (token) {
                    case "+" -> stack.push(a + b);
                    case "-" -> stack.push(a - b);
                    case "*" -> stack.push(a * b);
                    case "/" -> {
                        if (b == 0) throw new IllegalArgumentException("Division by zero");
                        stack.push(a / b);
                    }
                    case "^" -> stack.push(Math.pow(a, b));
                }
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException("Error calculating the expression");
        return stack.pop();
    }

    /**
     * Проверяет, является ли токен числом.
     *
     * @param token строка
     * @return true, если это число
     */
    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Проверяет, является ли токен переменной (не функцией).
     *
     * @param token строка
     * @return true, если это переменная
     */
    private boolean isVariable(String token) {
        return token.matches("[a-zA-Z]+") && !FUNCTIONS.containsKey(token);
    }

    /**
     * Возвращает значение переменной, запрашивая у пользователя, если оно ещё не задано.
     *
     * @param var имя переменной
     * @return значение переменной
     */
    private double getVariableValue(String var) {
        if (!variables.containsKey(var)) {
            System.out.print("Enter the value of the variable " + var + ": ");
            variables.put(var, scanner.nextDouble());
        }
        return variables.get(var);
    }
}
