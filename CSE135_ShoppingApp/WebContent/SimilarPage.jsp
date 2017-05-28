<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*, java.sql.*"%>
<%@ page import="ucsd.shoppingApp.models.* , java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Similar Product</title>
<h3>Similar Product</h3>
</head>
<body>
		<%
		 final String cosine_val = 
				"with data as (select product.id as v , person.id as base, sum(products_in_cart.price * products_in_cart.quantity)as w_td " +
						"from product, person, products_in_cart, shopping_cart "+
						"where shopping_cart.is_purchased = 'true' and "+
						"product.id = products_in_cart.product_id and "+
						"products_in_cart.cart_id = shopping_cart.id and "+
						"shopping_cart.person_id = person.id "+
						"group by product.id, person.id "+
						"order by product.id), "+
						" norms as ( "+
						  "select v,"+
						       "sum(w_td * w_td) as w2 "+
						    "from data "+
						    "group by v) "+
						"select "+
						   " x.v as ego,y.v as v,nx.w2 as x2, ny.w2 as y2, "+
						    "sum(x.w_td * y.w_td) as innerproduct, "+
						    "sum(x.w_td * y.w_td) / sqrt(nx.w2 * ny.w2) as cosinesimilarity "+
						"from data as x "+
						"join data as y "+
						   " on (x.base=y.base) "+
						"join norms as nx "+
						   " on (nx.v=x.v) "+
						"join norms as ny "+
						    "on (ny.v=y.v) "+
						"where x.v < y.v "+
						"group by 1,2,3,4 "+
						"order by 6 desc ";
	//	if(session.getAttribute("roleName") != null) {
	//		String role = session.getAttribute("roleName").toString();
	//		if("owner".equalsIgnoreCase(role) == true){
				Connection con = ConnectionManager.getConnection();	
				CategoryDAO categoryDao = new CategoryDAO(con);
				List<CategoryModel> category_list = categoryDao.getCategories();
				PreparedStatement ptst = null;
				ResultSet rs = null;
				ptst = con.prepareStatement(cosine_val);
				
				rs = ptst.executeQuery();
				
				//con.close();
			%>	
				<table border = '1'>
					<tr>
						<td>Product 1 </td>
						<td>Product 2 </td>
						<td>Cosine Similarity</td>
					</tr>
					<% while(rs.next()) {%>
					<tr>
						<td> <%=rs.getInt("ego")%></td>
						<td> <%=rs.getInt("v") %></td>
						<td> <%=rs.getFloat("cosinesimilarity") %></td>
					</tr>
					<%} %>
				</table>
		<%		
			//}
		//}
		 %>
</body>
</html>