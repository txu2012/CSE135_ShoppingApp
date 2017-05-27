package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SalesAnalyticDAO {

	private static int rowNum = 0;
	private static int colNum = 0;
	private static String orderType;
	private static String viewing;
	private static String orderOption;
	private ArrayList<String> tempRow;
	private final static String GetStates = "SELECT state_name FROM state ORDER BY state_name LIMIT 20 OFFSET ?";
	private final static String GetProducts = "SELECT product_name FROM product ORDER BY product_name LIMIT 20 OFFSET ?";
	private final static String GetPersons = "SELECT person_name FROM person ORDER BY person_name LIMIT 20 OFFSET ?";
	private final static String GetPersonsTopK = "select person.person_name, sum(products_in_cart.quantity * products_in_cart.price)as total "
			+ "from person, shopping_cart,products_in_cart "
			+ "where person.id = shopping_cart.person_id and "
			+ "shopping_cart.id = products_in_cart.cart_id and "
			+ "shopping_cart.is_purchased = 'true' "
			+ "group by person.person_name "                          
			+ "order by total desc limit 20 offset ?";
	
	private final static String GetStateTopK = "select state.state_name, sum(products_in_cart.quantity * products_in_cart.price)as total "
			+ "from state,person, shopping_cart,products_in_cart "
			+ "where person.id = shopping_cart.person_id and "
			+ "shopping_cart.id = products_in_cart.cart_id and "
			+ "person.state_id = state.id and "
			+ "shopping_cart.is_purchased = 'true' "
			+ "group by state.state_name "                          
			+ "order  by total desc limit 20 offset ?";
	
	private Connection con;
	
	public SalesAnalyticDAO(Connection con, String orderType, String viewing){
		this.con = con;
		this.orderOption = orderType;
		this.viewing = viewing;
	}
	
	public ArrayList<String> getRows(int row){
		rowNum = row;
		System.out.println(rowNum + " dao");
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
				//System.out.println("test");
				if(orderType.equals("state_name")){
					pstmt = con.prepareStatement(GetStates);
					//pstmt.setString(1, orderType);
					pstmt.setInt(1, rowNum);
					rs = pstmt.executeQuery();
				}
				else if(orderType.equals("price")){
					pstmt = con.prepareStatement(GetStateTopK);
					pstmt.setInt(1, rowNum);
					rs = pstmt.executeQuery();
				}
				//System.out.println("test");
				ArrayList<String> states = new ArrayList<String>();
				while(rs.next()){
					states.add(rs.getString("state_name"));
				}
				System.out.println(states);
				return states;
			} catch (SQLException e) {
				System.out.println("test");

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
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				if(orderType.equals("state_name")){
					pstmt = con.prepareStatement(GetPersons);
					//pstmt.setString(1, orderType);
					pstmt.setInt(1, rowNum);
					rs = pstmt.executeQuery();
				}
				else if(orderType.equals("price")){
					pstmt = con.prepareStatement(GetPersonsTopK);
					pstmt.setInt(1, rowNum);
					rs = pstmt.executeQuery();
				}
				
				ArrayList<String> people = new ArrayList<String>();
				while(rs.next()){
					people.add(rs.getString("person_name"));
				}
				tempRow = people;
				return people;
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
	
	public ArrayList<String> getCols(int col){
		colNum = col;
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
	
	public ArrayList<String> getNextRows(int curRow){
		rowNum = curRow;
		System.out.println(rowNum);
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
				tempRow = states;
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
				tempRow = people;
				return people;
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
}
