package ucsd.shoppingApp.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import ucsd.shoppingApp.CategoryDAO;
import ucsd.shoppingApp.ConnectionManager;
import ucsd.shoppingApp.PersonDAO;
import ucsd.shoppingApp.ProductDAO;
import ucsd.shoppingApp.ShoppingCartDAO;
import ucsd.shoppingApp.models.CategoryModel;
import ucsd.shoppingApp.models.ProductModel;
import ucsd.shoppingApp.models.ShoppingCartModel;

/**
 * Servlet implementation class salesAnalyticController
 */
//@WebServlet("/src/ucsd/shoppingApp/controllers/salesAnalyticController")
public class salesAnalyticController extends HttpServlet {
	//private static final long serialVersionUID = 1L;
      
	private Connection con;
	private static final long serialVersionUID = 1243242L;
	private static int rowNum = 0;
	private static int colNum = 0;
	private static String orderType;
	private static String viewing;
	private static String orderOption;
	private final static String GetStates = "SELECT state_name FROM state ORDER BY ? LIMIT 20 OFFSET ?";
	private final static String GetProducts = "SELECT product_name FROM product ORDER BY product_name LIMIT 20 OFFSET ?";
	private final static String GetPersons = "SELECT person_name FROM person ORDER BY ? LIMIT 20 OFFSET ?";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    //public salesAnalyticController(Connection con) {
    //    this.con = con;
        // TODO Auto-generated constructor stub
    //}

	public void init() {
		con = ConnectionManager.getConnection();
	}
	
	public void destroy() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		viewing = request.getParameter("viewing");
		orderOption = request.getParameter("order");
		if(orderOption != null && orderOption.equals("alpha")){
			orderType = "person_name";
		}
		else if(orderOption != null && orderOption.equals("topk")){
			orderType = "price";
		}
		
		ArrayList<String> rowList = getRows();
		ArrayList<String> colList = getCols();
		
		request.setAttribute("rows", rowList);
		request.setAttribute("cols", colList);
		this.getServletContext().getRequestDispatcher("/salesAnalytics.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		viewing = request.getParameter("viewing");
		orderOption = request.getParameter("order");
		
		ArrayList<String> rowList = getRows();
		ArrayList<String> colList = getCols();
		
		request.setAttribute("rows", rowList);
		request.setAttribute("cols", colList);
		this.getServletContext().getRequestDispatcher("/salesAnalytics.jsp").forward(request, response);
		
	}
	
	private ArrayList<String> getRows(){
		if(viewing != null && viewing.equals("state")){
			if(orderOption != null && orderOption.equals("alpha")){
				orderType = "state_name";
				
			}
			else if(orderOption != null && orderOption.equals("topk")){
				orderType = "price";
			}
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = con.prepareStatement(GetStates);
				pstmt.setString(1, orderType);
				pstmt.setInt(2, rowNum);
				rs = pstmt.executeQuery();
				
				ArrayList<String> states = new ArrayList<String>();
				while(rs.next()){
					states.add(rs.getString("state_name"));
				}
				return states;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		else if(viewing != null && viewing.equals("person")){
			if(orderOption != null && orderOption.equals("alpha")){
				orderType = "person_name";
				
			}
			else if(orderOption != null && orderOption.equals("topk")){
				orderType = "price";
			}
			
			System.out.println(orderType);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = con.prepareStatement(GetPersons);
				pstmt.setString(1, orderType);
				pstmt.setInt(2, rowNum);
				rs = pstmt.executeQuery();
				
				ArrayList<String> people = new ArrayList<String>();
				while(rs.next()){
					people.add(rs.getString("person_name"));
				}
				
				if(!people.isEmpty()){
					return people;
				}
				else{
					return null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	private ArrayList<String> getCols(){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = con.prepareStatement(GetProducts);
			pstmt.setInt(1, colNum);
			rs = pstmt.executeQuery();
			
			ArrayList<String> products = new ArrayList<String>();
			while(rs.next()){
				products.add(rs.getString("product_name"));
			}
			
			if(!products.isEmpty()){
				return products;
			}
			else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
