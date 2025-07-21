import java.util.Scanner;

public class AddNumbers {
    public static void main(String[] args) {
        // Create a Scanner object to read input from user
        Scanner scanner = new Scanner(System.in);
        
        // Prompt user for first number
        System.out.print("Enter the first number: ");
        double num1 = scanner.nextDouble();
        
        // Prompt user for second number
        System.out.print("Enter the second number: ");
        double num2 = scanner.nextDouble();
        
        // Calculate the sum
        double sum = num1 + num2;
        
        // Display the result
        System.out.println("The sum of " + num1 + " and " + num2 + " is: " + sum);
        
        // Close the scanner
        scanner.close();
    }
}