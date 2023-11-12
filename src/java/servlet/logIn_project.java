/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.servlet.http.Cookie;

public class logIn_project extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

       try (PrintWriter out = response.getWriter()) {
            String user_name = request.getParameter("username");
            String user_password = request.getParameter("password");
            String user_email = request.getParameter("email");

            // JDBC
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/myhiber", "root", "t@nm@y123");

                // Check if the user exists
                if (isUserExists(con, user_name, "name")) {
                    // User exists, check password and email
                    if (isUserExists(con, user_password, "password") && isUserExists(con, user_email, "email")) {
                        out.print("<h1>Welcome back, " + user_name + "!</h1>");
                    } else {
                        out.print("<h1>Password and email do not match the saved information.</h1>");
                    }
                } else {
                    // User does not exist, proceed with registration
                    insertUser(con, user_name, user_password, user_email);
                    out.print("<h1>Registration Successful........</h1>");

                    // Save user information in cookies
                    saveUserInCookies(response, user_name, user_password, user_email);
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                out.println("<h1>Error during registration</h1>");
            }
        }
    }

    private void saveUserInCookies(HttpServletResponse response, String userName, String password, String email) {
        Cookie nameCookie = new Cookie("username", userName);
        Cookie passwordCookie = new Cookie("password", password);
        Cookie emailCookie = new Cookie("email", email);

        // Set the cookies to expire in a certain time (e.g., 1 day)
        int cookieMaxAge = 24 * 60 * 60; // 1 day in seconds
        nameCookie.setMaxAge(cookieMaxAge);
        passwordCookie.setMaxAge(cookieMaxAge);
        emailCookie.setMaxAge(cookieMaxAge);

        response.addCookie(nameCookie);
        response.addCookie(passwordCookie);
        response.addCookie(emailCookie);
    }

    private boolean isUserExists(Connection con, String value, String columnName) throws SQLException {
        String query = "SELECT * FROM user WHERE " + columnName + " = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, value);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void insertUser(Connection con, String userName, String password, String email) throws SQLException {
        String query = "INSERT INTO user(name, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, userName);
            st.setString(2, password);
            st.setString(3, email);

            st.executeUpdate();
        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
