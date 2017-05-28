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
import ucsd.shoppingApp.SalesAnalyticDAO;

/**
 * Servlet implementation class salesAnalyticController
 */
//@WebServlet("/src/ucsd/shoppingApp/controllers/salesAnalyticController")
public class salesAnalyticController extends HttpServlet {
	//private static final long serialVersionUID = 1L;
      
	private Connection con;
	private SalesAnalyticDAO sales;
	private static final long serialVersionUID = 1243242L;
	private static int rowNum = 0;
	private static int colNum = 0;
	private static String orderType;
	private static String viewing;
	private static String orderOption;
	private ArrayList<String> tempRow;
	
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
		sales = new SalesAnalyticDAO(con, (String)request.getParameter("orderType"), (String)request.getParameter("viewing"));

		String getAction = (String)request.getParameter("getAction");
		int curCol = 0;
		int curRow = 0;
		if(getAction.equals("Next 20 rows")){
			curRow = Integer.parseInt((String)request.getParameter("rowNum")) + 20;
			curCol = Integer.parseInt((String)request.getParameter("colNum"));
			ArrayList<String> rowList = sales.getRows(curRow);
			ArrayList<String> colList;
			if(orderOption.equals("topk")){
				colList = sales.getProdsTopK(viewing, curCol);
			}
			else{
				colList = sales.getCols(curCol);
			}
			request.setAttribute("rows", rowList);
			request.setAttribute("cols", colList);
			System.out.println("testRows");
		}
		else if(getAction.equals("Next 10 columns")){
			curCol = Integer.parseInt((String)request.getParameter("colNum")) + 10;
			curRow = Integer.parseInt((String)request.getParameter("rowNum"));
			System.out.println(curRow + "Controller row");
			System.out.println(curCol + "Controller");
			ArrayList<String> rowList = sales.getRows(curRow);
			ArrayList<String> colList;
			if(orderOption.equals("topk")){
				colList = sales.getProdsTopK(viewing, curCol);
			}
			else{
				colList = sales.getCols(curCol);
			}
			request.setAttribute("rows", rowList);
			request.setAttribute("cols", colList);
		}
		
		request.setAttribute("orderType", (String)request.getParameter("orderType"));
		request.setAttribute("viewing", (String)request.getParameter("viewing"));
		if(viewing.equals("state")){
			if(orderOption.equals("alpha")){
				request.setAttribute("statePurchases", sales.getStatePurchases());
			}
			else if(orderOption.equals("topk")){
				request.setAttribute("statePurchases", sales.getStateTopKProd());
			}
		}
		else{
			if(orderOption.equals("alpha")){
				request.setAttribute("custPurchases", sales.getCustPurchases());
			}
			else if(orderOption.equals("topk")){
				request.setAttribute("custPurchases", sales.getCustTopKProd());
			}
		}
		request.setAttribute("curCol", curCol);
		request.setAttribute("curRow", curRow);
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
		
		sales = new SalesAnalyticDAO(con, orderOption, viewing);
		
		ArrayList<String> rowList = sales.getRows(0);
		ArrayList<String> colList = new ArrayList<String>();
		if(orderOption.equals("topk")){
			colList = sales.getProdsTopK(viewing, 0);
		}
		else{
			colList = sales.getCols(0);
		}
		
		request.setAttribute("orderType", orderType);
		request.setAttribute("viewing", viewing);
		request.setAttribute("rows", rowList);
		request.setAttribute("cols", colList);
		if(viewing.equals("state")){
			if(orderOption.equals("alpha")){
				request.setAttribute("statePurchases", sales.getStatePurchases());
			}
			else if(orderOption.equals("topk")){
				request.setAttribute("statePurchases", sales.getStateTopKProd());
			}
		}
		else{
			if(orderOption.equals("alpha")){
				request.setAttribute("custPurchases", sales.getCustPurchases());
			}
			else if(orderOption.equals("topk")){
				request.setAttribute("custPurchases", sales.getCustTopKProd());
			}
		}
		this.getServletContext().getRequestDispatcher("/salesAnalytics.jsp").forward(request, response);
	}

}
