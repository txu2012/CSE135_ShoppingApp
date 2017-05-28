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
						for(String c : cols){
				%>
					<td><%= c %></td>
				<% }} %>
			</tr>
					<% 
						for(String s : rows){
							if(request.getAttribute("statePurchases") != null){
								String StatePurchasesTotal = (String)request.getAttribute("statePurchases");
								PreparedStatement pstmtTotal = con.prepareStatement(StatePurchasesTotal);
								pstmtTotal.setString(1,s);
								pstmtTotal.setInt(2,curCol);
									
								ResultSet rs = pstmtTotal.executeQuery();
								total = 0;
								while(rs.next()){
									total = total + rs.getInt("pricetotal");
								}
								pstmtTotal.close();
								rs.close();
							}
							else if(request.getAttribute("custPurchases") != null){
								String CustPurchasesTotal = (String)request.getAttribute("custPurchases");
								PreparedStatement pstmtTotal = con.prepareStatement(CustPurchasesTotal);
								pstmtTotal.setString(1,s);
								pstmtTotal.setInt(2,curCol);
									
								ResultSet rs = pstmtTotal.executeQuery();
								total = 0;
								while(rs.next()){
									total = total + rs.getInt("pricetotal");
								}
								pstmtTotal.close();
								rs.close();
							}
							
							
					%>
							<tr>
								<td><%= s %> ($<%= total %>)</td>
					<%
							if(request.getAttribute("statePurchases") != null){
								String StatePurchases = (String)request.getAttribute("statePurchases");
								PreparedStatement pstmt = con.prepareStatement(StatePurchases);
								pstmt.setString(1,s);
								pstmt.setInt(2, curCol);
									
								ResultSet rs = pstmt.executeQuery();
					%>

								<% 
									while(rs.next()){ 
								%>
									<td>$<%= rs.getInt("pricetotal") %></td>
								<% 
									}
									pstmt.close();
									rs.close();
								%>
							</tr>
						
					<%
								
							}
					
							else if(request.getAttribute("custPurchases") != null){
								String CustPurchases = (String)request.getAttribute("custPurchases");
								PreparedStatement pstmt = con.prepareStatement(CustPurchases);
								pstmt.setString(1,s);
								pstmt.setInt(2, curCol);
									
								ResultSet rs = pstmt.executeQuery();
					%>

								<% 
									while(rs.next()){ 
								%>
									<td>$<%= rs.getInt("pricetotal") %></td>
								<% 
									} 
									pstmt.close();
									rs.close();
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
	</body>
</html>