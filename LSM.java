import java.io.*;
import java.util.*;

class Book implements Serializable {
    String title, author;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String toString() {
        return "📖 Title: " + title + ", ✍️ Author: " + author;
    }
}

class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String msg) {
        super(msg);
    }
}

class Library {
    private ArrayList<Book> books = new ArrayList<>();
    private Map<Book, String> borrowedBooks = new HashMap<>();

    public Library() {
        loadBooks();
    }

    public void addBook(Book book) {
        books.add(book);
        saveBooks();
    }

    public void removeBook(String title) throws BookNotAvailableException {
        boolean removed = books.removeIf(b -> b.title.equalsIgnoreCase(title));
        if (!removed) throw new BookNotAvailableException("Book not found!");
        saveBooks();
    }

    public void borrowBook(String title, String userName) throws BookNotAvailableException {
        for (Book b : books) {
            if (b.title.equalsIgnoreCase(title)) {
                books.remove(b);
                borrowedBooks.put(b, userName);
                saveBooks();
                return;
            }
        }
        throw new BookNotAvailableException("Book is not available for borrowing.");
    }

    public void returnBook(String title, String userName) throws BookNotAvailableException {
        for (Book b : borrowedBooks.keySet()) {
            if (b.title.equalsIgnoreCase(title) && borrowedBooks.get(b).equalsIgnoreCase(userName)) {
                books.add(b);
                borrowedBooks.remove(b);
                saveBooks();
                return;
            }
        }
        throw new BookNotAvailableException("No such borrowed book by the user.");
    }

    public void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("📭 No books available in the library.");
        } else {
            for (Book b : books) {
                System.out.println(b);
            }
        }
    }

    public void viewBorrowedUsers() {
        if (borrowedBooks.isEmpty()) {
            System.out.println("📭 No borrowed books.");
        } else {
            for (Map.Entry<Book, String> entry : borrowedBooks.entrySet()) {
                System.out.println(entry.getValue() + " borrowed ➡️ " + entry.getKey());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("library.dat"))) {
            books = (ArrayList<Book>) in.readObject();
        } catch (Exception e) {
            books = new ArrayList<>();
        }
    }

    private void saveBooks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("library.dat"))) {
            out.writeObject(books);
        } catch (IOException e) {
            System.out.println("Error saving books.");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner sc = new Scanner(System.in);
        int choice;

        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║  📚  WELCOME TO LIBRARY SYSTEM  📚  ║");
        System.out.println("╚════════════════════════════════════╝");

        do {
            System.out.println("\n╔══════════ 📋 MENU 📋 ══════════╗");
            System.out.println("│ 1. 📥 Add Book               │");
            System.out.println("│ 2. ❌ Remove Book            │");
            System.out.println("│ 3. 📖 Borrow Book            │");
            System.out.println("│ 4. 📤 Return Book            │");
            System.out.println("│ 5. 📚 View All Books         │");
            System.out.println("│ 6. 🧾 View Borrowed Users    │");
            System.out.println("│ 7. 🚪 Exit                   │");
            System.out.println("╚═══════════════════════════════╝");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter title: ");
                        String t = sc.nextLine();
                        System.out.print("Enter author: ");
                        String a = sc.nextLine();
                        library.addBook(new Book(t, a));
                        System.out.println("✅ Book added successfully!");
                        break;

                    case 2:
                        System.out.print("Enter title to remove: ");
                        String rem = sc.nextLine();
                        library.removeBook(rem);
                        System.out.println("✅ Book removed successfully!");
                        break;

                    case 3:
                        System.out.print("Enter your name: ");
                        String user = sc.nextLine();
                        System.out.print("Enter title to borrow: ");
                        String btitle = sc.nextLine();
                        library.borrowBook(btitle, user);
                        System.out.println("✅ Book borrowed successfully!");
                        break;

                    case 4:
                        System.out.print("Enter your name: ");
                        String returnUser = sc.nextLine();
                        System.out.print("Enter title to return: ");
                        String rtitle = sc.nextLine();
                        library.returnBook(rtitle, returnUser);
                        System.out.println("✅ Book returned successfully!");
                        break;

                    case 5:
                        System.out.println("📚 Available Books in Library:");
                        library.viewBooks();
                        break;

                    case 6:
                        System.out.println("🧾 Borrowed Book Records:");
                        library.viewBorrowedUsers();
                        break;

                    case 7:
                        System.out.println("👋 Exiting Library System. Goodbye!");
                        break;

                    default:
                        System.out.println("⚠️ Invalid option! Please try again.");
                }
            } catch (BookNotAvailableException e) {
                System.out.println("❗ " + e.getMessage());
            }
        } while (choice != 7);
    }
}
