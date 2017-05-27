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
	private final static String GetStates = "SELECT state_name FROM state ORDER BY ? LIMIT 20 OFFSET ?";
	private final static String GetProducts = "SELECT product_name FROM product ORDER BY product_name LIMIT 20 OFFSET ?";
	private final static String GetPersons = "SELECT person_name FROM person ORDER BY ? LIMIT 20 OFFSET ?";
	
	private final static String StatePurchases = "select foo.product_name, foo.price, sum(foo.total), sum (foo.price * foo.total) AS priceTotal  from " + 
													"(select product_name, product.price ,sum(products_in_cart.quantity) as total " +
													"from product , products_in_cart, shopping_cart,state, person " +
													"where product.id = products_in_cart.product_id and " +
													"products_in_cart.cart_id = shopping_cart.id and " +
													"shopping_cart.is_purchased = 'true' and " +
													"shopping_cart.person_id = person.id and person.state_id = state.id and " +
													"state.state_name = ? " +
													"group by product_name, product.price " +
													"union " +
													"select product_name, product.price ,'0' as total " +
													"from product) " +
													"foo GROUP BY foo.product_name, foo.price " +
													"order by foo.product_name";
	
	private final static String CustPurchases = "select foo.product_name, sum(total) as pricetotal "
			+"from(select product.product_name,product.id, sum(products_in_cart.price * products_in_cart.quantity) as total "
			+"from product, products_in_cart,shopping_cart,person "
			+"where person.person_name = ? and "
			+"person.id = shopping_cart.person_id and "
			+"shopping_cart.is_purchased = 'true' and "
			+"shopping_cart.id = products_in_cart.cart_id and "
			+"products_in_cart.product_id = product.id "
			+"group by product.product_name,product.id "
			+"union "
			+"select product.product_name, product.id,'0' from product) foo "
			+"group by foo.product_name, foo.id order by foo.id";
	
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
		if(getAction != null && getAction.equals("Next 20 rows")){
			curRow = Integer.parseInt((String)request.getParameter("rowNum"));
			curCol = Integer.parseInt((String)request.getParameter("colNum"));
			ArrayList<String> rowList = sales.getRows(curRow);
			ArrayList<String> colList = sales.getCols(curCol);
			request.setAttribute("rows", rowList);
			request.setAttribute("cols", colList);
		}
		else if(getAction != null && getAction.equals("Next 20 rows")){
			curCol = Integer.parseInt((String)request.getParameter("colNum"));
			curRow = Integer.parseInt((String)request.getParameter("rowNum"));
			ArrayList<String> rowList = sales.getRows(curRow);
			ArrayList<String> colList = sales.getCols(curCol);
			request.setAttribute("rows", rowList);
			request.setAttribute("cols", colList);
		}
		
		request.setAttribute("orderType", (String)request.getParameter("orderType"));
		request.setAttribute("viewing", (String)request.getParameter("viewing"));
		request.setAttribute("statePurchases", StatePurchases);
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
		ArrayList<String> colList = sales.getCols(0);
		
		request.setAttribute("orderType", orderType);
		request.setAttribute("viewing", viewing);
		request.setAttribute("rows", rowList);
		request.setAttribute("cols", colList);
		if(viewing.equals("state")){
			if(orderOption.equals("alpha")){
				request.setAttribute("statePurchases", StatePurchases);
			}
		}
		else{
			if(orderOption.equals("alpha")){
				request.setAttribute("custPurchases", CustPurchases);
			}
		}
		this.getServletContext().getRequestDispatcher("/salesAnalytics.jsp").forward(request, response);
		
	}

}
