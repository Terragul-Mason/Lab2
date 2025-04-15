package org.example;

import Evaluator.ExpressionEvaluator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter an arithmetic expression (for example: 2 + 3 * (x - 1))");
        System.out.print("> ");
        String input = scanner.nextLine();

        try {
            double result = evaluator.evaluate(input);
            System.out.println("Result: " + result);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}