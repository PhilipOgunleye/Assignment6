/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import credentials.Credentials;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.Connection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author c0641048
 */
@WebServlet("/Product")
public class SampleServlet extends HttpServlet {

    /**
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Content-Type", "text/plain-text");
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                out.println(getResults("SELECT * FROM product"));
            } else {
                int id = Integer.parseInt(request.getParameter("productID"));
                out.println(getResults("SELECT * FROM product WHERE productID = ?", String.valueOf(id)));
            }
        } catch (IOException ex) {
            Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getResults(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            sb.append("[");
            while (rs.next()) {
                sb.append(String.format("{ \"productID\" : %s , \"Name\" : \"%s\", \"Description\" : \"%s\", \"Quantity\" : %s }" + ",\n", rs.getInt("productID"), rs.getString("Name"), rs.getString("Description"), rs.getInt("Quantity")));
                //sb.append(", ");

            }

            sb.delete(sb.length() - 2, sb.length() - 1);
            sb.append("]");
        } catch (SQLException ex) {
            Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("Name") && keySet.contains("Description") && keySet.contains("Quantity")) {
                // There are some parameters                
                String Name = request.getParameter("Name");
                String Description = request.getParameter("Description");
                String Quantity = request.getParameter("Quantity");
                doUpdate("INSERT INTO product (Name, Description, Quantity) VALUES (?, ?, ?)", Name, Description, Quantity);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?Name=XXX&Description=XXX&Quantity");
            }
        } catch (IOException ex) {
            Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("Name") && keySet.contains("Description") && keySet.contains("Quantity") && keySet.contains("productID")) {
                // There are some parameters                
                String productID = request.getParameter("productID");
                String Name = request.getParameter("Name");
                String Description = request.getParameter("Description");
                String Quantity = request.getParameter("Quantity");
                doUpdate("UPDATE product SET  Name=?, Description=?, Quantity=? WHERE productID=?", Name, Description, Quantity, productID);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?Name=XXX&Description=XXX&Quantity=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("productID")) {
                // There are some parameters                
                String productID = request.getParameter("productID");
                doUpdate("DELETE from product WHERE productID=?", productID);
            } else {
                // There are no parameters at all
                out.println("Error: Not enough data to input. Please use a URL of the form /servlet?Name=XXX&Description=XXX&Quantity=XXX");
            }
        } catch (IOException ex) {
            Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }

}
