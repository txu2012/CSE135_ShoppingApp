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

import ucsd.shoppingApp.ConnectionManager;
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
	private static String filterOption;
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
		System.out.println(request.getParameter("orderType"));

		String getAction = (String)request.getParameter("getAction");
		String getFilter = (String)request.getParameter("filter");
		ArrayList<String> rowList;
		ArrayList<String> colList;
		int curCol = 0;
		int curRow = 0;
		if(getAction.equals("Next 20 rows")){
			curRow = Integer.parseInt((String)request.getParameter("rowNum")) + 20;
			curCol = Integer.parseInt((String)request.getParameter("colNum"));
			if(getFilter.equals("all")){
				rowList = sales.getRows(curRow);
				if(orderOption.equals("topk")){
					colList = sales.getProdsTopK(viewing, curCol);
				}
				else{
					colList = sales.getCols(curCol);
				}
			}
			else{
				rowList = sales.getRowsFilter(curRow, getFilter);
				colList = sales.getColsFilter(curCol, getFilter);
			}
			request.setAttribute("rows", rowList);
			request.setAttribute("cols", colList);
		}
		else if(getAction.equals("Next 10 columns")){
			curCol = Integer.parseInt((String)request.getParameter("colNum")) + 10;
			curRow = Integer.parseInt((String)request.getParameter("rowNum"));
			if(getFilter.equals("all")){
				rowList = sales.getRows(curRow);
				if(orderOption.equals("topk")){
					colList = sales.getProdsTopK(viewing, curCol);
				}
				else{
					colList = sales.getCols(curCol);
				}
			}
			else{
				rowList = sales.getRowsFilter(curRow, getFilter);
				colList = sales.getColsFilter(curCol, getFilter);
			}
			request.setAttribute("rows", rowList);
			request.setAttribute("cols", colList);
		}
		
		if(getFilter.equals("all")){
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
		}
		else{
			if(viewing.equals("state")){
				if(orderOption.equals("alpha")){
					request.setAttribute("statePurchases", sales.getStateAlphaFilter());
				}
				else if(orderOption.equals("topk")){
					request.setAttribute("statePurchases", sales.getStateTopKFilter());
				}
			}
			else{
				if(orderOption.equals("alpha")){
					request.setAttribute("custPurchases", sales.getCustAlphaFilter());
				}
				else if(orderOption.equals("topk")){
					request.setAttribute("custPurchases", sales.getCustTopKFilter());
				}
			}
		}
		request.setAttribute("curCol", curCol);
		request.setAttribute("curRow", curRow);
		request.setAttribute("filter", getFilter);
		request.setAttribute("prodTotal", sales.getProdTotal(getFilter));
		request.setAttribute("orderType", (String)request.getParameter("orderType"));
		request.setAttribute("viewing", (String)request.getParameter("viewing"));
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
		filterOption = request.getParameter("filter");
		
		sales = new SalesAnalyticDAO(con, orderOption, viewing);
		ArrayList<String> colList = new ArrayList<String>();
		ArrayList<String> rowList = new ArrayList<String>();
		
		if(filterOption.equals("all")){
			rowList = sales.getRows(0);
			if(orderOption.equals("topk")){
				colList = sales.getProdsTopK(viewing, 0);
			}
			else{
				colList = sales.getCols(0);
			}
		}
		else{
			rowList = sales.getRowsFilter(0, filterOption);
			colList = sales.getColsFilter(0, filterOption);
		}
		
		request.setAttribute("filter", filterOption);
		request.setAttribute("orderType", orderOption);
		request.setAttribute("viewing", viewing);
		request.setAttribute("rows", rowList);
		request.setAttribute("cols", colList);
		request.setAttribute("prodTotal", sales.getProdTotal(filterOption));
		if(filterOption.equals("all")){
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
		}
		else{
			if(viewing.equals("state")){
				if(orderOption.equals("alpha")){
					request.setAttribute("statePurchases", sales.getStateAlphaFilter());
				}
				else if(orderOption.equals("topk")){
					request.setAttribute("statePurchases", sales.getStateTopKFilter());
				}
			}
			else{
				if(orderOption.equals("alpha")){
					request.setAttribute("custPurchases", sales.getCustAlphaFilter());
				}
				else if(orderOption.equals("topk")){
					request.setAttribute("custPurchases", sales.getCustTopKFilter());
				}
			}
		}
		this.getServletContext().getRequestDispatcher("/salesAnalytics.jsp").forward(request, response);
	}

}
