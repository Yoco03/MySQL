
import java.sql.*;
import java.util.Scanner;

public class bookstore {

    /*
    Main methode is below all methodes which will call each one using a switch operator in while loop.
     */

    /*
     * Methode to Displays a menu
     */

    public static String menu() {
        Scanner input = new Scanner(System.in);
        System.out.println("""
                1 - Enter book\n
                2 - Update book\n
                3 - Delete book\n
                4 - Search books\n
                5 - Display books\n
                0 - Exit""");
        return input.nextLine();
    }

    /*
     * Methode that adds a new book to the table.
     *
     * The statement object that is connected to the database
     * SQLException For the main method to catch
     */

    //This methode will add a new book
    private static int addingBook(Statement stmt) throws SQLException {
        Scanner input = new Scanner(System.in);
        try {
            // Get input for query
            // Execute update query
            // The ID has an AUTO increase primary key
            System.out.println("Please enter a book title:");
            String newBookTitle = input.nextLine();
            System.out.println("Please enter the author:");
            String newBookAuthor = input.nextLine();
            System.out.println("Please enter the quantity of books:");
            int qty = Integer.parseInt(input.nextLine());
            return stmt.executeUpdate(String.format("INSERT INTO books VALUES(null, '%s', '%s', %d)",
                    newBookTitle, newBookAuthor, qty));
        } catch (NumberFormatException error) {
            System.out.println("Error please enter a number.");
            return addingBook(stmt);
        }
    }

    /*Methode to get books
     * Gets a book based on the ID or title
     *
     * The statement object that is connected to the database.
     * The title that the book must have/contain
     * The ID of the book
     * SQLException To be caught by the main method
     */
    private static void getBooks(Statement stmt, String title, int id) throws SQLException {
        ResultSet gettingInfo = stmt.executeQuery("SELECT * from books");
        // Loop through every book and only display the ones where the ID and Title match
        while (gettingInfo.next()) {
            if (gettingInfo.getInt("id") == id | gettingInfo.getString("title").contains(title)) {
                System.out.printf("ID: %d, Author: %s, Title: %s, Qty: %d\n",
                        gettingInfo.getInt("id"), gettingInfo.getString("author"),
                        gettingInfo.getString("title"), gettingInfo.getInt("qty"));
            }
        }
    }

    /*
     * Methode that will update your book store
     * Your ID will be updated in rows of books
     *
     * The statement object that is connected to the database
     * The amount of rows effected
     * SQLException which will be caught in the main method
     */

    private static int updatingBook(Statement stmt) throws SQLException {
        //Importing the scanner object
        Scanner input = new Scanner(System.in);
        try {
            // Getting the input
            System.out.println("Please enter the ID of the Book:");
            int id = Integer.parseInt(input.nextLine().toLowerCase());
            System.out.println("Please enter the column you want to edit:");
            String c = input.nextLine().toLowerCase();
            System.out.println("Please enter the new value:");
            String updateValue = input.nextLine();
            // Execute the query update
            return stmt.executeUpdate(String.format("UPDATE books SET %s = '%d' WHERE id = '%d'",
                    c, Integer.parseInt(updateValue), id));
        } catch (Exception error) {
            System.out.println("Error at updateBook: " + error);
            return updatingBook(stmt);
        }
    }

    /* Methode that deletes a book from the database
     * Deletes an existing book
     *
     * The statement connecting to the database
     * Returns the amount of rows affected
     * SQLException To be caught by the main method
     */
    private static int deletingBook(Statement stmt) throws SQLException {
        Scanner input = new Scanner(System.in);
        // Get the input
        System.out.println("Please enter the book ID:");
        int id = Integer.parseInt(input.nextLine());
        System.out.println("""
                Are you sure you want to delete this item:
                \n Enter (y) for yes or (n) for no:""");
        if (input.nextLine().equalsIgnoreCase("y")) {
            // Execute if yes is selected
            return stmt.executeUpdate(String.format("DELETE FROM books WHERE id = %d", id));
        } else {
            System.out.println("Cancelling delete.");
            return 0;
        }
    }



    /* Methode to display books
     * Displays all the existing books inside the database.
     *
     * The statement object that is connected to the database
     * SQLException To be caught by the main method
     */
    private static void displayingBooks(Statement stmt) throws SQLException {
        ResultSet result = stmt.executeQuery("SELECT* from books");
        // Display every row inside the result object
        while (result.next()) {
            System.out.printf("ID: %d\tAuthor: %s\tTitle: %s\tQty: %d\n",
                    result.getInt("id"), result.getString("author"),
                    result.getString("title"), result.getInt("qty"));
        }
    }




    public static void main(String[] args) {
        //Connect to the database
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bookstore?useSSL=false",
                    "root",
                    "01234");
            Statement statement = con.createStatement();

            Scanner input = new Scanner(System.in);
            System.out.println("Welcome to Book Manager store");
            boolean isRun = true;
            // Run the program until the user chooses quit
            while (isRun) {
                // Get the selected menu
                String menu = menu();
                switch (menu) {
                    // Adding a new book calling in methode addingBook()
                    case "1" -> {
                        System.out.println("Enter book");
                        System.out.println("Rows: " + addingBook(statement));
                    }
                    // Updating existing book calling in methode updatingBook
                    case "2" -> {
                        System.out.println("Update book");
                        displayingBooks(statement);
                        System.out.println("Rows " + updatingBook(statement));
                    }
                    // Delete existing book calling in methode deletingBook
                    case "3" -> {
                        displayingBooks(statement);
                        System.out.println("Delete book");
                        System.out.println("Rows: " + deletingBook(statement));
                    }
                    // Search for a book calling in methode getBooks
                    case "4" -> {
                        System.out.println("Search books");
                        System.out.println("Enter the title content:");
                        String title = input.nextLine();
                        System.out.println("Enter the id:");
                        int id = Integer.parseInt(input.nextLine());
                        getBooks(statement, title, id);
                    }
                    // Display existing books calling in methode displayingBooks
                    case "5" -> {
                        displayingBooks(statement);
                    }
                    // Exit the program calling in methode
                    case "0" -> {
                        System.out.println("Thank you, come again.\nExiting. . .");
                        isRun = false;
                    }
                    // Handle unknown input
                    default -> {
                        System.out.println("Invalid input. Try again.");
                    }
                }
            }

            // Close the connection
            con.close();
        } catch (SQLException error) {
            System.out.println("The error is: " + error);
        }
    }
}