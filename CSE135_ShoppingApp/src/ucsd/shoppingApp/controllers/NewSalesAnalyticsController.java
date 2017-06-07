package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ucsd.shoppingApp.ConnectionManager;

/**
 * Servlet implementation class NewSalesAnalyticsController
 */
//@WebServlet("/NewSalesAnalyticsController")
public class NewSalesAnalyticsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;
	private final String getTable = "select state_name, product_name, cell_sum, state_sum, product_sum, rn "+
									"from (select state_name, product_name, cell_sum, state_sum, product_sum, ROW_NUMBER() "+
									"over(PARTITION BY state_name "+
									"order by state_sum desc, product_sum desc) as rn "+
									"from precomp) temp "+
									"where rn < 51 "+
									"order by state_sum desc, rn";
	private final String getTableFilter = "select state_name, product_name, cell_sum, state_sum, product_sum, rn, category_name "+
									"from (select state_name, product_name, cell_sum, state_sum, product_sum, category_name, ROW_NUMBER() "+
									"over(PARTITION BY state_name "+
									"order by state_sum desc, product_sum desc) as rn "+
									"from precomp where category_name = ? ) temp "+
									"where rn < 51 "+
									"order by state_sum desc, rn";
	private final String getCatProdAmount = "select category.category_name, count(*) as num "+
									"from category, product "+
									"where category.id = product.category_id and category.category_name = ? "+
									"group by category.category_name";
	private final String updateCounter1 = "update counterone set counter = counter + 1";
	private final String updateCounter2 = "update countertwo set counter = counter + 1";
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		String getFilter = request.getParameter("filter");
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet rs = null;
		try{
			con.setAutoCommit(false);
			pstmt2 = con.prepareStatement(updateCounter1);
			pstmt3 = con.prepareStatement(updateCounter2);
			
			pstmt2.executeUpdate();
			pstmt3.executeUpdate();
			con.commit();
			con.setAutoCommit(true);
			
			int catNum = 50;
			
			if(getFilter != null && getFilter.equals("all")){
				pstmt = con.prepareStatement(getTable);
			}
			else{
				pstmt = con.prepareStatement(getTableFilter);
				pstmt.setString(1,getFilter);
				
				PreparedStatement catNumStmt = con.prepareStatement(getCatProdAmount);
				catNumStmt.setString(1,getFilter);
				
				ResultSet rs1 = catNumStmt.executeQuery();
				
				if(rs1.next()){
					if(rs1.getInt("num") < catNum){
						catNum = rs1.getInt("num");
					}
				}
				rs1.close();
				catNumStmt.close();
			}
			rs = pstmt.executeQuery();
			
			int counter = 0;
			int counterStates = 0;
			ArrayList<String> states = new ArrayList<String>();
			ArrayList<Integer> statesTotal = new ArrayList<Integer>();
			ArrayList<String> products = new ArrayList<String>();
			ArrayList<Integer> productsTotal = new ArrayList<Integer>();
			ArrayList<Integer> stateProdTotal = new ArrayList<Integer>();
			
			while(rs.next()){
				if(rs.getInt("rn") == 1){
					states.add(rs.getString("state_name"));
					statesTotal.add(rs.getInt("state_sum"));
				}
				
				if(counter < catNum){
					products.add(rs.getString("product_name"));
					productsTotal.add(rs.getInt("product_sum"));
				}
				counterStates++;
				counter++;
				stateProdTotal.add(rs.getInt("cell_sum"));
			}
			
			request.setAttribute("states", states);
			request.setAttribute("statesTotal", statesTotal);
			request.setAttribute("products", products);
			request.setAttribute("productsTotal", productsTotal);
			request.setAttribute("stateProdTotal", stateProdTotal);
			
			System.out.println(states);
			
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			if (pstmt != null) {
				try {
					pstmt.close();
					pstmt2.close();
					pstmt3.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(rs != null){
				try{
					rs.close();
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
			this.getServletContext().getRequestDispatcher("/NewSalesAnalytics.jsp").forward(request, response);
		}
	}

}
