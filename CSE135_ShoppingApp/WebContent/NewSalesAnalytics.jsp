<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*, java.sql.* " import="ucsd.shoppingApp.models.* , java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>New Sales Analytics</title>
		<h3>New Sales Analytics</h3>
	</head>
	<body>
		<%
			long start = System.currentTimeMillis();
			if(session.getAttribute("roleName") != null) {
				String role = session.getAttribute("roleName").toString();
				if("owner".equalsIgnoreCase(role) == true){
					Connection con = ConnectionManager.getConnection();	
					CategoryDAO categoryDao = new CategoryDAO(con);
					List<CategoryModel> category_list = categoryDao.getCategories();
					
					int counter = 0;
					int counterTotal = 0;
					
					ArrayList<String> states = new ArrayList<String>();
					ArrayList<Integer> statesTotal = new ArrayList<Integer>();
					ArrayList<String> products = new ArrayList<String>();
					ArrayList<Integer> productsTotal = new ArrayList<Integer>();
					ArrayList<Integer> stateProdTotal = new ArrayList<Integer>();
					
					states = (ArrayList<String>)request.getAttribute("states");
					statesTotal = (ArrayList<Integer>)request.getAttribute("statesTotal");
					products = (ArrayList<String>)request.getAttribute("products");
					productsTotal = (ArrayList<Integer>)request.getAttribute("productsTotal");
					stateProdTotal = (ArrayList<Integer>)request.getAttribute("stateProdTotal");
					
		%>
		<table>
			<tr><td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td></tr>
		</table>
		<form action="NewSalesAnalyticsController" method="POST">
			Select Filtering
			<select name="filter">
				<option value="all">All</option>
				<%for (CategoryModel cat : category_list) {%>
				<option value="<%= cat.getCategoryName() %>"><%= cat.getCategoryName() %></option>
				<% } %>
			</select>
			<input type="submit" value="Query" name="query">
		</form>
		
				<% if(products != null) { %>
		<table border="1">
			<tr>
				<td></td>
					<%
						while(counter < products.size()){
					%>
				<td><%= products.get(counter) %> ($<%= productsTotal.get(counter) %>)</td>
					<%
							counter++;
						}
						counter = 0;
					%>
			</tr>
					<%
						while(counter < states.size()){
					%>
				<tr>
					<td><%= states.get(counter) %> ($<%= statesTotal.get(counter) %>)</td>
							<% for(int i = counterTotal; i < (counterTotal + 50); i++){ %>
					<td><%= stateProdTotal.get(i) %></td>
							<% 
								}
								counterTotal = counterTotal + 50;
								counter++;
							%>
				</tr>
					<%
						}
					%>
		</table>
		<%
					}
				}
			}
		%>
		
	</body>
</html>