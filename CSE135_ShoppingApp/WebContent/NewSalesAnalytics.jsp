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
	<script type="text/javascript">
		var test = "new";
		function getRequest(){
			var xmlHttp = new XMLHttpRequest();
			xmlHttp.open("POST", "NewSalesAnalyticsController",true);
			xmlHttp.send(null);
			xmlHttp.onreadystatechange = function() {
				if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
					test = xmlHttp.responseText;
					
					//var array = JSON.parse('[' + test + ']');
					var array = test.replace(/[\[\]\"]+/g, '');
					array = array.replace(/\s/g, '');
					array = array.split(",");
					document.getElementById("demo").innerHTML = array[0];
					
					var amount = parseInt(array[0]);
					var stateList = [];
					var productList = [];
					var priceList = [];

					for(var i = 1; i < array.length; i++){
						if(i % 3 === 1){
							stateList.push(array[i]);
						}
						else if(i % 3 === 2){
							productList.push(array[i]);
						}
						else if(i % 3 === 0){
							priceList.push(parseInt(array[i]));
						}
					}
					
					document.getElementById("demo1").innerHTML = stateList;
					document.getElementById("demo2").innerHTML = productList;
					document.getElementById("demo3").innerHTML = priceList;
					
					for(var i = 0; i < states.length; i++){
						var string = stateList[0]+"_"+productList[0]
						document.getElementById(string).innerHTML = "'"+priceList[0];
						document.getElementById("'"+stateList[0]+"_"+productList[0]+"'").style.color = "red";
					}	
				}
			}
		}
	</script>
	<p id="demo"></p>
	<p id="demo1"></p>
	<p id="demo2"></p>
	<p id="demo3"></p>
	<button value="Refresh" onclick='getRequest()'>Refresh</button>
	<body>
		<%
		//"http://localhost:8080/CSE135_ShoppingApp/NewSalesAnalyticsController"
			long start = System.currentTimeMillis();
			if(session.getAttribute("roleName") != null) {
				String role = session.getAttribute("roleName").toString();
				if("owner".equalsIgnoreCase(role) == true){
					Connection con = ConnectionManager.getConnection();	
					CategoryDAO categoryDao = new CategoryDAO(con);
					List<CategoryModel> category_list = categoryDao.getCategories();
					
					int counter = 0;
					int counterTotal = 0;
					int counterProd = 0;
					
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
		<form action="NewSalesAnalyticsController" method="GET">
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
				<td id="<%= products.get(counter) %>"><%= products.get(counter) %> ($<%= productsTotal.get(counter) %>)</td>
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
					<td id="<%= states.get(counter) %>"><%= states.get(counter) %> ($<%= statesTotal.get(counter) %>)</td>
					
							<% for(int i = counterTotal; i < (counterTotal + 50); i++){ %>
							
					<td id="<%= states.get(counter) %>_<%= products.get(counterProd) %>"><%= stateProdTotal.get(i) %></td>
					
							<% 
									counterProd++;
								}
								counterTotal = counterTotal + 50;
								counter++;
								counterProd = 0;
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