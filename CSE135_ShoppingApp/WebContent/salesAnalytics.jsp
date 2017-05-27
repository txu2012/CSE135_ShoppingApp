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
		//Connection con = ConnectionManager.getConnection();	
		if(session.getAttribute("roleName") != null) {
			String role = session.getAttribute("roleName").toString();
			if("owner".equalsIgnoreCase(role) == true){
				Connection con = ConnectionManager.getConnection();	
				CategoryDAO categoryDao = new CategoryDAO(con);
				List<CategoryModel> category_list = categoryDao.getCategories();
				//con.close();
				
		%>
		<table>
			<tr><td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td></tr>
		</table>
		<form action="salesAnalyticController" method="POST">
			<p>Select View By</p>
			<select name="viewing">
				<option value="person">Customers</option>
				<option value="state">States</option>
			</select>
			<p>Select Ordering Type</p>
			<select name="order">
				<option value="alpha">Alphabetically</option>
				<option value="topk">Top-K</option>
			</select>
			<label>
					<input type="radio" name="category_id" value="-1" checked="checked">
				all
			</label>
			<%
			int checked = -1;
			if (request.getAttribute("category_id") != null){
				checked = Integer.parseInt(request.getAttribute("category_id").toString());
			}
			for (CategoryModel cat : category_list) {
			%>
				<label>
					<input type="radio" name="category_id" value="<%=cat.getId()%>" <%if (cat.getId() == checked) { %>checked="checked" <%} %> >
					<%=cat.getCategoryName()%>
				</label>
			<%
			}
			%>
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
									
								ResultSet rs = pstmtTotal.executeQuery();
								total = 0;
								while(rs.next()){
									total = total + rs.getInt("pricetotal");
								}
							}
							
					%>
							<tr>
								<td><%= s %> $<%= total %></td>
					<%
							if(request.getAttribute("statePurchases") != null){
								//System.out.println("check");
								String StatePurchases = (String)request.getAttribute("statePurchases");
								PreparedStatement pstmt = con.prepareStatement(StatePurchases);
								pstmt.setString(1,s);
									
								ResultSet rs = pstmt.executeQuery();
					%>

								<% while(rs.next()){ 
								%>
									<td>$<%= rs.getInt("pricetotal") %></td>
								<% } %>
							</tr>
						
					<%
								
							}
					
							else if(request.getAttribute("custPurchases") != null){
								String CustPurchases = (String)request.getAttribute("custPurchases");
								PreparedStatement pstmt = con.prepareStatement(CustPurchases);
								pstmt.setString(1,s);
									
								ResultSet rs = pstmt.executeQuery();
					%>

								<% while(rs.next()){ 
								%>
									<td>$<%= rs.getInt("pricetotal") %></td>
								<% } %>
							</tr>
						
					<%
							}
						}
					}
				//}
				%>
		</table>
		
		<%
			if(rows.size() >= 20){
			%>
				<form method="GET" action="salesAnalyticController">
					<input type="hidden" value="rowVals" name="rowVals">
					<input type="hidden" value="<%= curRow = curRow + rows.size() %>" name="rowNum">
					<input type="submit" value="Next 20 rows" name="getAction">
					<input type="hidden" value="<%= request.getAttribute("orderType") %>" name="orderType">
					<input type="hidden" value="<%= request.getAttribute("viewing") %>" name="viewing">
					<input type="hidden" value="<%= curCol %>" name="colNum">
				</form>
			
			<%
			}
			if(cols.size() >= 20){
			%>
				<form method="GET" action="salesAnalyticController">
					<input type="hidden" value="colVals" name="colVals">
					<input type="hidden" value="<%= curCol = curCol + cols.size() %>" name="colNum">
					<input type="submit" value="Next 20 columns" name="getAction">
					<input type="hidden" value="<%= request.getAttribute("orderType") %>" name="orderType">
					<input type="hidden" value="<%= request.getAttribute("viewing") %>" name="viewing">
					<input type="hidden" value="<%= rows %>" name="rows">
				</form>
			
			<%
			}
			}
		}
		%>
	</body>
</html>