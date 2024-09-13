# Library Management System (LMS)

## Table of Contents
- [Introduction](#introduction)
- [Features](#key-functionalities)
  - [Book Management](#book-management)
  - [Member Management](#member-management)
  - [Borrowing and Returning Books](#borrowing-and-returning)
- [UML Diagram](#uml-diagram)
- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

## Introduction
This project meticulously implements a robust Library Management System (LMS) in Java, leveraging the power of Maven for streamlined build automation and Swing for an intuitive graphical user interface (GUI). Its primary objective is to significantly enhance library operations by automating essential book and member management tasks, fostering efficiency and accuracy.


I'd be glad to provide the refactored README code, incorporating the best aspects of the previous responses and addressing identified shortcomings:

Markdown
# Library Management System (LMS)

## Introduction

This project meticulously implements a robust Library Management System (LMS) in Java, leveraging the power of Maven for streamlined build automation and Swing for an intuitive graphical user interface (GUI). Its primary objective is to significantly enhance library operations by automating essential book and member management tasks, fostering efficiency and accuracy.

## Key Functionalities:

### Book Management:
  **Meticulous Book Addition:** Effortlessly create new book entries, meticulously capturing crucial details such as title, author, ISBN (International Standard Book Number), publication date, and current availability status.
  **Streamlined Book Updates:** Modify existing book information and availability status with ease, ensuring the library catalog remains up-to-date.
  **Efficient Book Removal:** When books are no longer part of the library's collection, seamlessly remove them from the catalog, maintaining a streamlined database.

### Member Management:
  **Effortless Member Registration:** Welcome new members by adding their personal details, including name and unique membership ID, to the system.
  **Member Profile Updates:** Maintain accurate member information by facilitating edits to profiles as needed.
  **Clear Borrowing History Tracking:** Employ the system to meticulously track borrowed books and their corresponding due dates for each member, enabling efficient overdue management.

### Borrowing and Returning:
  **Streamlined Book Borrowing:** Empower members to effortlessly check out desired books, recording borrowing dates for accurate tracking.
  **Efficient Book Returns:** Process book returns and update availability status in the system.

## UML Diagram

 <br>
<p align="center" margin="auto">
    <kbd>
<img align="center" 
            src="https://github.com/KhaledAshrafH/LMS/blob/main/images/UML.jpg"
            alt="KhaledAshrafH"  height="300" style="border-radius: 20px;"/>
    </kbd>
  </p>
 <h1 align="center"></h1>


The diagram should depict the following classes and relationships:

* **Book:** Attributes include title, author, ISBN, publication date, and availability status.
* **Member:** Attributes include name, membership ID, contact information, and a collection of borrowed books (`List<Book>`).
* **Library:** Methods include `addBook()`, `removeBook()`, `registerMember()`, `borrowBook()`, and `returnBook()`.
* Relationships:
    * Association: The `Library` class has a one-to-many relationship with both `Book` and `Member`.
    * Aggregation: A `Member` can have multiple borrowed `Books`.

            
## Technologies Used
- Java with Maven
- MySQL Database
- Swing for GUI
- SLF4J for logging
- JUnit for testing
- Lombok for reducing boilerplate code


## Setup and Installation
To set up the project on your local machine, follow the steps below:

1. Clone the Repository:
    ```bash
    git clone https://github.com/KhaledAshrafH/LMS.git
    cd LibraryManagementSystem
    ```

2. Set Up MySQL Database:
    - Create a new database named `lms_db`.
    - Execute the SQL scripts to create the necessary tables for books, members, and borrowings.

3. Configure the Database Connection:
    - Update the MAIN class in `com.lms` with your MySQL database credentials.

4. Build the Project:
    ```bash
    mvn clean install
    ```

5. Run the Application:
    ```bash
    mvn exec:java -Dexec.mainClass="com.lms.Main"
    ```

## Usage
After launching, the application will present a GUI with tabs for managing books, members, and borrowing/returning books. Utilize the respective tab functionalities for the operations you wish to perform.

## Screenshots
### Library Management System GUI
 <br>
<p align="center" margin="auto">
    <kbd>
<img align="center" 
            src="https://github.com/KhaledAshrafH/LMS/blob/main/images/GUI1.jpg"
            alt="KhaledAshrafH"  height="500" style="border-radius: 20px;"/>
    </kbd>
  </p>
 <h1 align="center"></h1>

  <br>
<p align="center" margin="auto">
    <kbd>
<img align="center" 
            src="https://github.com/KhaledAshrafH/LMS/blob/main/images/GUI2.jpg"
            alt="KhaledAshrafH"  height="500" style="border-radius: 20px;"/>
    </kbd>
  </p>
 <h1 align="center"></h1>

## Contributing
Contributions are welcome! If you'd like to contribute, please fork the repository and create a pull request with your changes.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


