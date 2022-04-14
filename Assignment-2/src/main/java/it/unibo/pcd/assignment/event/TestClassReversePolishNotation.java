package it.unibo.pcd.assignment.event;

import java.util.Stack;
import java.util.stream.Stream;

public class TestClassReversePolishNotation {

    public static int ONE_BILLION = 1000000000;
    private double memory = 0;

    /**
     * 18 * Takes reverse polish notation style string and returns the resulting calculat\
     * 19 ion.
     * 20 *
     * 21 * @param input mathematical expression in the reverse Polish notation format
     * 22 * @return the calculation result
     * 23
     */
    public Double calc(String input) {

        String[] tokens = input.split(" ");
        Stack<Double> numbers = new Stack<>();

        Stream.of(tokens).forEach(t -> {
            double a;
            double b;
            switch (t) {
                case "+":
                    b = numbers.pop();
                    a = numbers.pop();
                    numbers.push(a + b);
                    break;
                case "/":
                    b = numbers.pop();
                    a = numbers.pop();
                    numbers.push(a / b);
                    break;
                case "-":
                    b = numbers.pop();
                    a = numbers.pop();
                    numbers.push(a - b);
                    break;
                case "*":
                    b = numbers.pop();
                    a = numbers.pop();
                    numbers.push(a * b);
                    break;
                default:
                    numbers.push(Double.valueOf(t));
            }
        });

        return numbers.pop();
    }

    /**
     * 62 * Memory Recall uses the number in stored memory, defaulting to 0.
     * 63 *
     * 64 * @return the double
     * 65
     */
    public double memoryRecall() {
        return memory;
    }

    /**
     * 71 * Memory Clear sets the memory to 0.
     * 72
     */
    public void memoryClear() {
        memory = 0;
    }

    public void memoryStore(double value) {
        memory = value;
    }
}
