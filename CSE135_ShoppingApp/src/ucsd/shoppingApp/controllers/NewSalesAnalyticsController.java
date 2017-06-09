package ucsd.shoppingApp.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.util.*;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.json.*;

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
	private final String updateCounter2 = "update countertwo set counter = counter + 1";
	
	private final String getTopProd = "select distinct product_name, product_sum from precomp " +
	                                "order by product_sum desc limit 50";
	private final String getTopProdwithFilter = "select distinct product_name, product_sum from precomp where category_name = ? " +
                                    "order by product_sum desc limit 50";
	private final String insertOld = "insert into oldProd select distinct product_name, product_sum from precomp " +
                                     "order by product_sum desc limit 50 ";
	private final String insertOldWithFilter = "insert into oldProd select distinct product_name, product_sum from precomp where category_name = ? " +
                                      "order by product_sum desc limit 50 ";
	private final String deleteOld = "delete from oldProd";
	
	private final String getOld  = "select * from oldProd order by  product_sum desc";
	
	private final String getState = "select state_name from state";
	
	private final String deleteFilter = "delete from filt";
	
	private final String insertFilter = "insert into filt(filters) values(?)";
	
	private final String getFilt = "select filters from filt";
	
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//this.getServletContext().getRequestDispatcher("/NewSalesAnalytics.jsp").forward(request, response);
		String getLog = "SELECT * FROM log";
		String getFilter = "";
		PreparedStatement count1 = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 =null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null;
		PreparedStatement pstmt5 = null;
		ResultSet ors = null;
		ResultSet prs = null;
		ResultSet rs = null;
		ResultSet ss = null;
		ResultSet fs = null;
		
		try{
			
			pstmt = con.prepareStatement(getLog);
			rs = pstmt.executeQuery();
			
			count1 = con.prepareStatement(updateCounter2);
			count1.executeUpdate();
			
			pstmt5 = con.prepareStatement(getFilt);
			fs = pstmt5.executeQuery();

	
			while(fs.next()){
				getFilter = fs.getString("filters");
			}
			
			if(getFilter != null && getFilter.equals("all")){
				pstmt2 = con.prepareStatement(getTopProd);
				
			}
			
			else{
				pstmt2 = con.prepareStatement(getTopProdwithFilter);
				pstmt2.setString(1, getFilter);
				
			}
			prs = pstmt2.executeQuery();
			
			pstmt3 = con.prepareStatement(getOld);
			ors = pstmt3.executeQuery();
			
			pstmt4 = con.prepareStatement(getState);
			ss = pstmt4.executeQuery();

			ArrayList<String> TopProd = new ArrayList<String>();
			
			ArrayList<String> Oldproducts = new ArrayList<String>();
			ArrayList<String> statelist= new ArrayList<String>();
			while(ss.next()){
				statelist.add(ss.getString("state_name"));
			}
			
			while(ors.next()){
				Oldproducts.add(ors.getString("product_name"));
			}
			
			//JSONArray array = new JSONArray();
			
			while(prs.next()){
				TopProd.add(prs.getString("product_name"));
			}
			request.setAttribute("checkTop", TopProd);
			
			int counter = 0;
			String c = "";
			
			String d = "";
			
			for(String s : TopProd)
				d = s +","+ d;
			
			String e = "";
			
			for(String s : Oldproducts)
				e = s +","+ e ;
			
			String f = "";
			for(String s : statelist)
				f = s + "," + f;
			
			while(rs.next()){
				c = c + ", " + rs.getString("state_names") + ", " + rs.getString("product_names") + ", " + Integer.toString(rs.getInt("product_sums"));

				counter++;
			}

			c = Integer.toString(counter) + c;
			
			String combine = c + "!" + d + "!" + e + "!" + f;
			response.getWriter().write(combine);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				count1.close();
				pstmt.close();
				rs.close();
				pstmt2.close();
				prs.close();
				ors.close();
				pstmt3.close();
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		String getFilter = request.getParameter("filter");
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		PreparedStatement pstmt4 = null;
		PreparedStatement pstmt5 = null;
		PreparedStatement pstmt6 = null;
		PreparedStatement pstmt7 = null;
		ResultSet rs = null;
		try{
			con.setAutoCommit(false);

			pstmt3 = con.prepareStatement(updateCounter2);
			
			
			pstmt3.executeUpdate();
			con.commit();
			con.setAutoCommit(true);
			
			int catNum = 50;
			
			pstmt6 = con.prepareStatement(deleteFilter);
			pstmt6.executeUpdate();
			
			
			pstmt7 = con.prepareStatement(insertFilter);
			pstmt7.setString(1, getFilter);
			pstmt7.executeUpdate();
			
			
			pstmt5 = con.prepareStatement(deleteOld);
			pstmt5.executeUpdate();
			
			System.out.println(getFilter);
			if(getFilter != null && getFilter.equals("all")){
				pstmt = con.prepareStatement(getTable);
				pstmt4 = con.prepareStatement(insertOld);
			}
			else{
				pstmt = con.prepareStatement(getTableFilter);
				pstmt.setString(1,getFilter);
				
				pstmt4 = con.prepareStatement(insertOldWithFilter);
				pstmt4.setString(1, getFilter);
				
				
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
			pstmt4.executeUpdate();
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
					pstmt4.close();
					pstmt5.close();
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
