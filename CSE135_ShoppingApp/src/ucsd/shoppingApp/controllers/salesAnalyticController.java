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
@WebServlet("/salesAnalyticController")
public class salesAnalyticController extends HttpServlet {
	//private static final long serialVersionUID = 1L;
      
	private Connection con = null;
	
	private static int rowNum;
	private static int colNum;
	private static String orderType;
	private static String viewing;
	private static String orderOption;
	private final static String GetStates = "SELECT state_name FROM state ORDER BY " + orderType + "LIMIT 20 OFFSET " + rowNum; 
	private final static String GetProducts = "SELECT product_name FROM product ORDER BY product_name LIMIT 20 OFFSET " + colNum;
	private final static String GetPersons = "SELECT person_name FROM person ORDER BY " + orderType + "LIMIT 20 OFFSET " + rowNum;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public salesAnalyticController(Connection con) {
        this.con = con;
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
		request.getRequestDispatcher("/salesAnalytics.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
		request.getRequestDispatcher("/salesAnalytics.jsp").forward(request, response);
		
	}
	
	private ArrayList<String> getRows(){
		if(viewing != null && viewing.equals("state")){
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = con.prepareStatement(GetStates);
				rs = pstmt.executeQuery();
				
				ArrayList<String> states = new ArrayList<String>();
				while(rs.next()){
					states.add(rs.getString("state_name"));
				}
				
				if(!states.isEmpty()){
					return states;
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
		else if(viewing != null && viewing.equals("person")){
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = con.prepareStatement(GetPersons);
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
