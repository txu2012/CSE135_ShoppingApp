<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*"%>
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
		/*if(session.getAttribute("roleName") != null) {
			String role = session.getAttribute("roleName").toString();
			if("owner".equalsIgnoreCase(role) == true){
				Connection con = ConnectionManager.getConnection();	
				//CategoryDAO categoryDao = new CategoryDAO(con);
				//List<CategoryModel> category_list = categoryDao.getCategories();
				//con.close();*/
				
		%>
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
			<input type="submit" value="Query" name="query">
		</form>
		<% 
			ArrayList<String> rows = new ArrayList<String>();
			if(request.getAttribute("rows") != null) {
				rows = (ArrayList<String>) request.getAttribute("rows");
				System.out.println(rows);
		%>
		<table border="1">
			<tr>
				<td></td>
			</tr>
			<% 

						for(String s : rows){
							System.out.println(s);
				%>
					
						<tr><td><%= s %></td></tr>
					
				<%
						}
					}
				%>
		</table>
		
		<%
			//}
		//}
		%>
	</body>
</html>