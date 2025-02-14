package myJDBC;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class studentERP extends JFrame {

	private JTextField rollNoField, nameField, contactField, courseField, feesField;
	private JButton submitButton, verifyButton, resetButton, fetchButton, updateButton, marksheetButton;
	private Connection conn;

	public studentERP() {
		setTitle("Student Enrollment Form");
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		connectToDb();

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 5, 25, 30));

		panel.add(new JLabel("Roll No:"));
		rollNoField = new JTextField();
		panel.add(rollNoField);

		panel.add(new JLabel("Name:"));
		nameField = new JTextField();
		panel.add(nameField);

		panel.add(new JLabel("Contact:"));
		contactField = new JTextField();
		panel.add(contactField);

		panel.add(new JLabel("Course:"));
		courseField = new JTextField();
		panel.add(courseField);

		panel.add(new JLabel("Fees:"));
		feesField = new JTextField();
		panel.add(feesField);

		submitButton = new JButton("Submit");
		verifyButton = new JButton("Delete");
		fetchButton = new JButton("Fetch");
		resetButton = new JButton("Reset");
		updateButton = new JButton("Update");
		marksheetButton = new JButton("MarkSheet");

		panel.add(submitButton);
		panel.add(verifyButton);
		panel.add(fetchButton);
		panel.add(resetButton);
		panel.add(updateButton);
		panel.add(marksheetButton);

		add(panel);

		submitButton.addActionListener(e -> insertValue());
		verifyButton.addActionListener(e -> delete());
		fetchButton.addActionListener(e -> selectData());
		resetButton.addActionListener(e -> reset());
		updateButton.addActionListener(e -> update());
		marksheetButton.addActionListener(e -> marksheet());

	}

	private void connectToDb() {
		try {
			String url = "jdbc:mysql://127.0.0.1:3306/student_erp";
			String username = "root";
			String password = "Pass@123";
			conn = DriverManager.getConnection(url, username, password);
			System.out.println("Database connected successfully!");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void insertValue() {
		String rollno = rollNoField.getText();
		String name = nameField.getText();
		String contact = contactField.getText();
		String course = courseField.getText();
		String fees = feesField.getText();

		if (rollno.isEmpty() || name.isEmpty() || contact.isEmpty() || course.isEmpty() || fees.isEmpty()) {
			JOptionPane.showMessageDialog(this, "All fields are required!", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String query = "INSERT INTO erp (stud_rollno, stud_name, contact, course, fees) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, rollno);
			stmt.setString(2, name);
			stmt.setString(3, contact);
			stmt.setString(4, course);
			stmt.setString(5, fees);
			int feedback = stmt.executeUpdate();

			if (feedback > 0) {
				JOptionPane.showMessageDialog(this, "Form successfully submitted!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Insert data failed!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void selectData() {
		String rollno = rollNoField.getText();
		if (rollno.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Enter roll number to fetch data!", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String query = "SELECT * FROM erp WHERE stud_rollno = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, rollno);
			ResultSet result = stmt.executeQuery();

			if (result.next()) {
				nameField.setText(result.getString("stud_name"));
				contactField.setText(result.getString("contact"));
				courseField.setText(result.getString("course"));
				feesField.setText(result.getString("fees"));

				JOptionPane.showMessageDialog(this, "Data fetched successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Data not found!", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
			result.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error fetching data!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void delete() {
		String rollno = rollNoField.getText().trim();

		if (rollno.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Roll number cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String query = "DELETE FROM erp WHERE stud_rollno = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, rollno);

			int feedback = stmt.executeUpdate();
			if (feedback > 0) {
				JOptionPane.showMessageDialog(null, "Data deleted successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "No record found with roll number: " + rollno, "Not Found",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Delete operation failed!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void update() {
		String rollno = rollNoField.getText().trim();
		String name = nameField.getText().trim();
		String contact = contactField.getText().trim();
		String course = courseField.getText().trim();
		String fees = feesField.getText().trim();

		if (rollno.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Roll number cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		StringBuilder query = new StringBuilder("UPDATE erp SET ");
		boolean hasUpdates = false;

		if (!name.isEmpty()) {
			query.append("stud_name = ?, ");
			hasUpdates = true;
		}
		if (!contact.isEmpty()) {
			query.append("contact = ?, ");
			hasUpdates = true;
		}
		if (!course.isEmpty()) {
			query.append("course = ?, ");
			hasUpdates = true;
		}
		if (!fees.isEmpty()) {
			query.append("fees = ?, ");
			hasUpdates = true;
		}

		if (!hasUpdates) {
			JOptionPane.showMessageDialog(null, "No data to update!", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		query.setLength(query.length() - 2); // Remove last comma and space
		query.append(" WHERE stud_rollno = ?");

		try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
			int paramIndex = 1;

			if (!name.isEmpty())
				stmt.setString(paramIndex++, name);
			if (!contact.isEmpty())
				stmt.setString(paramIndex++, contact);
			if (!course.isEmpty())
				stmt.setString(paramIndex++, course);
			if (!fees.isEmpty())
				stmt.setString(paramIndex++, fees);

			stmt.setString(paramIndex, rollno);

			int feedback = stmt.executeUpdate();
			if (feedback > 0) {
				JOptionPane.showMessageDialog(null, "Data updated successfully!", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "No record found with roll number: " + rollno, "Not Found",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Update operation failed!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void marksheet() {
		String rollno = rollNoField.getText().trim();
		if (rollno.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Enter roll number to fetch marks!", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Use a JOIN query to fetch marks along with student details from the erp table
		String query = "SELECT m.Math, m.Java, m.DSA, m.total, e.stud_name "
				+ "FROM marksheet m JOIN erp e ON m.stud_rollno = e.stud_rollno " + "WHERE m.stud_rollno = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, rollno);
			ResultSet result = stmt.executeQuery();

			if (result.next()) {
				// Retrieve marks and additional details
				int sub1 = result.getInt("Math");
				int sub2 = result.getInt("Java");
				int sub3 = result.getInt("DSA");
				double total = result.getDouble("total");
				String name = result.getString("stud_name");

				// Display the marksheet details in a dialog box
				String marksDetails = "Marksheet for Roll No: " + rollno + "\n" + "Name: " + name + "\n" + "Math: "
						+ sub1 + "\n" + "Java: " + sub2 + "\n" + "DSA: " + sub3 + "\n" + "Aggregate: " + total+"%";
				JOptionPane.showMessageDialog(this, marksDetails, "Marksheet", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "No marks data found for Roll No: " + rollno, "Info",
						JOptionPane.INFORMATION_MESSAGE);
			}
			result.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error fetching marks!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void reset() {
		rollNoField.setText("");
		nameField.setText("");
		contactField.setText("");
		courseField.setText("");
		feesField.setText("");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new studentERP().setVisible(true));
	}
}