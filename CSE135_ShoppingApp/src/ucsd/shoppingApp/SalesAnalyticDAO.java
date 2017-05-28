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
	
	// Row query
	private final static String GetStates = "SELECT state_name FROM state ORDER BY state_name LIMIT 20 OFFSET ?";
	
	// Column query
	private final static String GetProducts = "SELECT product_name FROM product ORDER BY product_name LIMIT 10 OFFSET ?";
	
	// Row query
	private final static String GetPersons = "SELECT person_name FROM person ORDER BY person_name LIMIT 20 OFFSET ?";
	
	// Row query
	public final String GetPersonsTopK = "select person.person_name, sum(products_in_cart.quantity * products_in_cart.price)as total "
			+ "from person, shopping_cart,products_in_cart "
			+ "where person.id = shopping_cart.person_id and "
			+ "shopping_cart.id = products_in_cart.cart_id and "
			+ "shopping_cart.is_purchased = 'true' "
			+ "group by person.person_name "                          
			+ "order by total desc limit 20 offset ?";
	
	// Row query
	public final String GetStateTopK = "select state.state_name, sum(products_in_cart.quantity * products_in_cart.price)as pricetotal "
			+ "from state,person, shopping_cart,products_in_cart "
			+ "where person.id = shopping_cart.person_id and "
			+ "shopping_cart.id = products_in_cart.cart_id and "
			+ "person.state_id = state.id and "
			+ "shopping_cart.is_purchased = 'true' "
			+ "group by state.state_name "                          
			+ "order by pricetotal desc limit 20 offset ?";
	
	// Column query
	public final String StateTopKProd = "select foo.product_name, sum(foo.total)as pricetotal "
			+ "from(select product.product_name ,sum(products_in_cart.price * products_in_cart.quantity) as total "
			+ "from state, product,person,shopping_cart,products_in_cart "
			+ "where state.state_name = ? and "
			+ "person.state_id = state.id and "
			+ "person.id = shopping_cart.person_id and "
			+ "shopping_cart.id = products_in_cart.cart_id and "
			+ "product.id = products_in_cart.product_id "
			+ "group by product_name "
			+ "union "
			+ "select product_name,'0' from product)foo "
			+ "group by foo.product_name "
			+ "order by pricetotal desc limit 10 offset ?";
	
	// Column query
	public final String CustTopKProd = "select foo.product_name, sum(foo.total)as pricetotal from"
			 + "(select product.product_name ,sum(products_in_cart.price * products_in_cart.quantity) as total "
			 + "from product,person,shopping_cart,products_in_cart "
			 + "where person.person_name = ? and "
			 + "person.id = shopping_cart.person_id and "
			 + "shopping_cart.id = products_in_cart.cart_id and "
			 + "product.id = products_in_cart.product_id "
			 + "group by product_name "
			 + "union "
			 + "select product_name,'0' from product)foo "
			 + "group by foo.product_name "
			 + "order by pricetotal desc limit 10 offset ?";
	
	// Column query
	public final String StatePurchases = "select foo.product_name, foo.price, sum(foo.total), sum (foo.price * foo.total) AS priceTotal  from "  
			+"(select product_name, product.price ,sum(products_in_cart.quantity) as total " 
			+"from product , products_in_cart, shopping_cart,state, person " 
			+"where product.id = products_in_cart.product_id and " 
			+"products_in_cart.cart_id = shopping_cart.id and " 
			+"shopping_cart.is_purchased = 'true' and " 
			+"shopping_cart.person_id = person.id and person.state_id = state.id and " 
			+"state.state_name = ? " 
			+"group by product_name, product.price " 
			+"union " 
			+"select product_name, product.price ,'0' as total " 
			+"from product) " 
			+"foo GROUP BY foo.product_name, foo.price " 
			+"order by foo.product_name limit 10 offset ?";
	
	// Column query
	public final String CustPurchases = "select foo.product_name, sum(total) as pricetotal "
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
			+"group by foo.product_name, foo.id order by foo.id limit 10 offset ?";
	
	private Connection con;
	
	public SalesAnalyticDAO(Connection con, String orderType, String viewing){
		this.con = con;
		this.orderOption = orderType;
		this.viewing = viewing;
	}
	
	public String getCustPurchases(){
		return this.CustPurchases;
	}
	
	public String getStatePurchases(){
		return this.StatePurchases;
	}
	
	public String getStateTopKProd(){
		return this.StateTopKProd;
	}
	
	public String getCustTopKProd(){
		return this.CustTopKProd;
	}
	
	// Get an arraylist of the topk of the products
	public ArrayList<String> getProdsTopK(String view, int num){
		if(view.equals("person")){
			PreparedStatement pstmt = null;
			PreparedStatement pstmt2 = null;
			ResultSet rs = null;
			ResultSet rs2 = null;
			try{
				ArrayList<String> prodList = new ArrayList<String>();
				pstmt = con.prepareStatement(GetPersonsTopK);
				pstmt.setInt(1, 0);
				rs = pstmt.executeQuery();
				
				if(rs.next()){
					pstmt2 = con.prepareStatement(CustTopKProd);
					pstmt2.setString(1,  rs.getString("person_name"));
					pstmt2.setInt(2, num);
					rs2 = pstmt2.executeQuery();
					
					while(rs2.next()){
						prodList.add(rs2.getString("product_name"));
					}
				}
				return prodList;
			}
			catch (SQLException e) {
				System.out.println("test");

				e.printStackTrace();
			} 
			finally {
				if (rs != null ) {
					try {
						rs.close();
						rs2.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
						pstmt2.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		else if(view.equals("state")){
			PreparedStatement pstmt = null;
			PreparedStatement pstmt2 = null;
			ResultSet rs = null;
			ResultSet rs2 = null;
			try{
				ArrayList<String> prodList = new ArrayList<String>();
				pstmt = con.prepareStatement(GetStateTopK);
				pstmt.setInt(1, 0);
				rs = pstmt.executeQuery();
				
				if(rs.next()){
					pstmt2 = con.prepareStatement(StateTopKProd);
					pstmt2.setString(1,  rs.getString("state_name"));
					pstmt2.setInt(2, num);
					rs2 = pstmt2.executeQuery();
					
					while(rs2.next()){
						prodList.add(rs2.getString("product_name"));
					}
				}
				return prodList;
			}
			catch (SQLException e) {
				System.out.println("test");

				e.printStackTrace();
			} 
			finally {
				if (rs != null ) {
					try {
						rs.close();
						rs2.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
						pstmt2.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}
	
	// Get an arraylist of the rows
	public ArrayList<String> getRows(int row){
		rowNum = row;
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
				if(orderType.equals("person_name")){
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
	
	// Get an arraylist of the columns
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
}
