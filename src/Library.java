import java.sql.*;
import java.util.Scanner;

public class Library {

    private static Connection connection;

    public static void main(String[] args) {
        initializeDatabase();
        showMenu();
    }

    private static void initializeDatabase() {

        String databaseURL, userName, password;
        Scanner input = new Scanner(System.in);

        System.out.print("Please enter the database URL: ");
        databaseURL = input.nextLine();

        System.out.print("Please enter the username: ");
        userName = input.nextLine();

        System.out.print("Please enter the password: ");
        password = input.nextLine();

        try {
            connection = DriverManager.getConnection(databaseURL, userName, password);
        } catch (SQLException e) {
            System.out.println("Can not connect to the database");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Successfully connected to the database!");
    }

    private static void showMenu() {

        String inputString;

        do {
            System.out.println("\n*** Welcome to the library ***\n1. add \"book name\" \"author\"" +
                    "\n2. remove book_id\n3. list\nEnter 0 to exit");

            //Taking the first word entered by the user
            Scanner input = new Scanner(System.in);
            inputString = input.next();

            switch (inputString) {
                case "add":
                    String bookName, author;
                    inputString = input.nextLine().trim();

                    //Getting the book name
                    int firstIndex = inputString.indexOf("\"") + 1;
                    bookName = inputString.substring(firstIndex, inputString.indexOf("\"", firstIndex));

                    //Getting the author
                    inputString = inputString.replace("\"" + bookName + "\"", "");
                    firstIndex = inputString.indexOf("\"") + 1;
                    author = inputString.substring(firstIndex, inputString.lastIndexOf("\""));

                    add(bookName, author);
                    break;

                case "remove":
                    //Getting the id
                    String id = input.next().trim().replaceAll("\"", "");

                    remove(id);
                    break;

                case "list":
                    try {
                        //Listing the books
                        list();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    if (!inputString.equals("0"))
                        System.out.println("Wrong entry!");
            }
        } while (!inputString.equals("0"));

    }

    private static void add(String bookName, String author) {

        PreparedStatement preparedStatement;
        int numberOfRowsAffected = 0;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO books (name, author) VALUES (?,?)");
            preparedStatement.setString(1, bookName);
            preparedStatement.setString(2, author);
            numberOfRowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (numberOfRowsAffected == 0)
            System.out.println("Failed to add!");
        else
            System.out.println("Successfully added!");
    }

    private static void remove(String bookID) {

        PreparedStatement preparedStatement;
        int numberOfRowsAffected = 0;
        try {
            preparedStatement = connection.prepareStatement("delete from library.books where id=?");
            preparedStatement.setString(1, bookID);
            numberOfRowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (numberOfRowsAffected == 0)
            System.out.println("Failed to remove!");
        else
            System.out.println("Successfully removed!");
    }

    private static void list() throws SQLException {

        System.out.printf("\n|%-10s|%-30s|%-30s|\n", "ID", "NAME", "AUTHOR");

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from books");

        while (resultSet.next()) {
            System.out.printf("|%-10s|%-30s|%-30s|\n", resultSet.getString(1),
                    resultSet.getString(2), resultSet.getString(3));
        }
    }
}