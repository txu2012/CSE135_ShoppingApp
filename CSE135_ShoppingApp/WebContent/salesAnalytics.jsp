<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*, java.sql.*"%>
<%@ page import="ucsd.shoppingApp.models.* , java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Sales Analytics</title>
		<h3>Sales Analytics</h3>
	</head>
	<body>
		<%
		System.out.println("test");
		long start = System.currentTimeMillis();
		if(session.getAttribute("roleName") != null) {
			String role = session.getAttribute("roleName").toString();
			if("owner".equalsIgnoreCase(role) == true){
				Connection con = ConnectionManager.getConnection();	
				CategoryDAO categoryDao = new CategoryDAO(con);
				List<CategoryModel> category_list = categoryDao.getCategories();			
		%>
		<table>
			<tr><td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td></tr>
		</table>
		<form action="salesAnalyticController" method="POST">
			Select View By
			<select name="viewing">
				<option value="person">Customers</option>
				<option value="state">States</option>
			</select>
			<br>
			Select Ordering Type
			<select name="order">
				<option value="alpha">Alphabetically</option>
				<option value="topk">Top-K</option>
			</select>
			<br>
			Select Filtering
			<select name="filter">
				<option value="all">All</option>
				<%for (CategoryModel cat : category_list) {%>
				<option value="<%= cat.getCategoryName() %>"><%= cat.getCategoryName() %></option>
				<% } %>
			</select>
			<input type="submit" value="Query" name="query">
			
		</form>
		<% 
			ArrayList<String> rows = new ArrayList<String>();
			ArrayList<String> cols = new ArrayList<String>();
			int total = 0;
			int curRow = 0;
			int curCol = 0; 
			if(request.getAttribute("curCol") != null || request.getAttribute("curRow") != null){
				curCol = (int)request.getAttribute("curCol");
				curRow = (int)request.getAttribute("curRow");
			}
			if(request.getAttribute("rows") != null) {
				session.setAttribute("rows", (ArrayList<String>) request.getAttribute("rows"));
				rows = (ArrayList<String>) session.getAttribute("rows");
				
		%>
		<table border="1">
			<tr>
				<td></td>
				<%
					if(request.getAttribute("cols") != null){
						cols = (ArrayList<String>) request.getAttribute("cols");
						PreparedStatement prodTotalStmt = null;
						ResultSet rs = null;
						for(String c : cols){
							String prodTotal = (String)request.getAttribute("prodTotal");
							prodTotalStmt = con.prepareStatement(prodTotal);
							if(request.getAttribute("filter").equals("all")){
								prodTotalStmt.setInt(1, curRow);
								prodTotalStmt.setString(2, c);
							}
							else{
								prodTotalStmt.setString(1, (String)request.getAttribute("filter"));
								prodTotalStmt.setInt(2, curRow);
								prodTotalStmt.setString(3, c);
							}
							rs = prodTotalStmt.executeQuery();
							total = 0;
							if(rs.next()){
				%>
					<td><h3><%= c %></h3> ($<%= rs.getInt("pricetotal") %>)</td>
				<% 		
							}
						}
						prodTotalStmt.close();
						rs.close();
					} 
				%>
			</tr>
					<% 
						for(String s : rows){
							if(request.getAttribute("statePurchases") != null){
								String query = "select sum(products_in_cart.price * products_in_cart.quantity) as pricetotal "
										+"from person, products_in_cart, shopping_cart, product, state "
										+"where shopping_cart.id = products_in_cart.cart_id and "
										+"shopping_cart.person_id = person.id and person.state_id = state.id and "
										+"products_in_cart.product_id = product.id and "
										+"product.product_name = ? and "
										+"state.state_name = ? and "
										+"shopping_cart.is_purchased = 'true'";
								int count = 0;
								total = 0;
								while(count < cols.size()){ 
									PreparedStatement pstmt2 = con.prepareStatement(query);	
									pstmt2.setString(1, cols.get(count));
									pstmt2.setString(2, s);
									ResultSet rs2 = pstmt2.executeQuery();
									if(rs2.next()){
										total = total + rs2.getInt("pricetotal");
									}
									
									count++;
									pstmt2.close();
									rs2.close();
								}
								
							}
							else if(request.getAttribute("custPurchases") != null){
								String query = "select sum(products_in_cart.price * products_in_cart.quantity) as pricetotal "
										+"from person, products_in_cart, shopping_cart,product "
										+"where shopping_cart.id = products_in_cart.cart_id and "
										+"shopping_cart.person_id = person.id and "
										+"products_in_cart.product_id = product.id and "
										+"product.product_name = ? and "
										+"person.person_name = ? and "
										+"shopping_cart.is_purchased = 'true'";
								
								int count = 0;
								total = 0;
								while(count < cols.size()){ 
									PreparedStatement pstmt2 = con.prepareStatement(query);	
									pstmt2.setString(1, cols.get(count));
									pstmt2.setString(2, s);
									ResultSet rs2 = pstmt2.executeQuery();
									if(rs2.next()){
										total = total + rs2.getInt("pricetotal");
									}
									
									count++;
									pstmt2.close();
									rs2.close();
								}
							}
							
							
					%>
							<tr>
								<td><h3><%= s %></h3> ($<%= total %>)</td>
					<%
							if(request.getAttribute("statePurchases") != null){
								String query = "select sum(products_in_cart.price * products_in_cart.quantity) as pricetotal "
										+"from person, products_in_cart, shopping_cart, product, state "
										+"where shopping_cart.id = products_in_cart.cart_id and "
										+"shopping_cart.person_id = person.id and person.state_id = state.id and "
										+"products_in_cart.product_id = product.id and "
										+"product.product_name = ? and "
										+"state.state_name = ? and "
										+"shopping_cart.is_purchased = 'true'";
								
								int count = 0;
								while(count < cols.size()){ 
									PreparedStatement pstmt2 = con.prepareStatement(query);	
									pstmt2.setString(1, cols.get(count));
									pstmt2.setString(2, s);
									ResultSet rs2 = pstmt2.executeQuery();
										
									if(rs2.next()){
										if(rs2.getInt("pricetotal") > 0){
								%>
									<td>$<%= rs2.getInt("pricetotal") %></td>
								<% 
										}
										else{
										%>	<td>$0</td> <%
										}
									}
									count++;
									pstmt2.close();
									rs2.close();
								} 
								%>
							</tr>
						
					<%
								
							}
					
							else if(request.getAttribute("custPurchases") != null){
								String query = "select sum(products_in_cart.price * products_in_cart.quantity) as pricetotal "
										+"from person, products_in_cart, shopping_cart,product "
										+"where shopping_cart.id = products_in_cart.cart_id and "
										+"shopping_cart.person_id = person.id and "
										+"products_in_cart.product_id = product.id and "
										+"product.product_name = ? and "
										+"person.person_name = ? and "
										+"shopping_cart.is_purchased = 'true'";

								int count = 0;
								while(count < cols.size()){ 
									PreparedStatement pstmt2 = con.prepareStatement(query);	
									pstmt2.setString(1, cols.get(count));
									pstmt2.setString(2, s);
									ResultSet rs2 = pstmt2.executeQuery();
										
									if(rs2.next()){
										if(rs2.getInt("pricetotal") > 0){
								%>
									<td>$<%= rs2.getInt("pricetotal") %></td>
								<% 
										}
										else{
										%>	<td>$0</td> <%
										}
									}
									count++;
									pstmt2.close();
									rs2.close();
								} 
								%>
							</tr>
						
					<%
							}
						}
					con.close();
					}
				%>
		</table>
		
		<%
			if(rows.size() >= 20){
			%>
				<form method="GET" action="salesAnalyticController">
					<input type="hidden" value="<%= curRow %>" name="rowNum">
					<input type="hidden" value="<%= (String)request.getAttribute("orderType") %>" name="orderType">
					<input type="hidden" value="<%= (String)request.getAttribute("viewing") %>" name="viewing">
					<input type="hidden" value="<%= curCol %>" name="colNum">
					<input type="hidden" value="<%= (String)request.getAttribute("filter") %>" name="filter">
					<input type="submit" value="Next 20 rows" name="getAction">
				</form>
			
			<%
			}
			if(cols.size() >= 10){ System.out.println(curRow + "form");
			%>
				<form method="GET" action="salesAnalyticController">
					<input type="hidden" value="<%= curCol %>" name="colNum">
					<input type="hidden" value="<%= (String)request.getAttribute("orderType") %>" name="orderType">
					<input type="hidden" value="<%= (String)request.getAttribute("viewing") %>" name="viewing">
					<input type="hidden" value="<%= curRow %>"name="rowNum">
					<input type="hidden" value="<%= (String)request.getAttribute("filter") %>" name="filter">
					<input type="submit" value="Next 10 columns" name="getAction">
				</form>
			
			<%
			}
		}
		else { %>
			<h3>This page is available to owners only</h3>
		<%
		}
	}
	else { %>
			<h3>Please <a href = "./login.jsp">login</a> before viewing the page</h3>
	<%} %>
	<% 
		long end = System.currentTimeMillis();
		long timeTaken = end - start; 
		System.out.println(timeTaken + "ms time");
	%>
	</body>
</html>