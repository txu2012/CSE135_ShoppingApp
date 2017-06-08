<%@ page language="java" import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*, java.sql.*, ucsd.shoppingApp.models.* , java.util.*, java.io.*, org.json.*"%>

<%
String getTable = "select state_name, product_name, cell_sum, state_sum, product_sum, rn "+
		"from (select state_name, product_name, cell_sum, state_sum, product_sum, ROW_NUMBER() "+
		"over(PARTITION BY state_name "+
		"order by state_sum desc, product_sum desc) as rn "+
		"from precomp) temp "+
		"where rn < 51 "+
		"order by state_sum desc, rn";
String getTableFilter = "select state_name, product_name, cell_sum, state_sum, product_sum, rn, category_name "+
		"from (select state_name, product_name, cell_sum, state_sum, product_sum, category_name, ROW_NUMBER() "+
		"over(PARTITION BY state_name "+
		"order by state_sum desc, product_sum desc) as rn "+
		"from precomp where category_name = ? ) temp "+
		"where rn < 51 "+
		"order by state_sum desc, rn";
String getCatProdAmount = "select category.category_name, count(*) as num "+
		"from category, product "+
		"where category.id = product.category_id and category.category_name = ? "+
		"group by category.category_name";
String updateCounter1 = "update counterone set counter = counter + 1";
String updateCounter2 = "update countertwo set counter = counter + 1";

	Connection con = ConnectionManager.getConnection();;
	String getLog = "SELECT * FROM log";
	PreparedStatement count1 = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	//System.out.println("test2");
	try{
		count1 = con.prepareStatement(updateCounter1);
		count1.executeUpdate();
		
		pstmt = con.prepareStatement(getLog);
		rs = pstmt.executeQuery();
		ArrayList<String> states = new ArrayList<String>();
		ArrayList<String> products = new ArrayList<String>();
		ArrayList<Integer> prices = new ArrayList<Integer>();
		JSONArray array = new JSONArray();
		while(rs.next()){
			states.add(rs.getString("state_names"));
			products.add(rs.getString("product_names"));
			prices.add(rs.getInt("product_sums"));
			JSONObject obj = new JSONObject();
			obj.put("state",rs.getString("state_names"));
			obj.put("product", rs.getString("product_names"));
			obj.put("price", rs.getInt("product_sums"));
			array.put(obj);
			
		}
		String s = array.toString();
		System.out.println(s);
		response.setContentType("text/html");
		PrintWriter output = response.getWriter();
		output.println("check");
		response.getWriter().write("checker");
		output.close();
		
	}
	catch(Exception e){
		e.printStackTrace();
	}
	finally{
		try{
			count1.close();
			pstmt.close();
			rs.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
%>