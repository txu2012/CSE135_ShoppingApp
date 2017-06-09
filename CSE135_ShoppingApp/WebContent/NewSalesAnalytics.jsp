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
			
			xmlHttp.open("POST", "NewSalesAnalyticsController?filter=",true);
			xmlHttp.send();
			xmlHttp.onreadystatechange = function() {
				if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
					var temp = document.cookie;
					temp = temp.split(",");
					
					test = xmlHttp.responseText;
					
					var separate = test.split("!");
					var refresh1 = separate[0];
					
					var newPr = separate[1];
					var oldPr = separate[2];
					var statel = separate[3];
					
					var newSplit = newPr.split(",");
					
					var oldSplit = oldPr.split(",");
					var stateSplit = statel.split(",");
					var result = [];
					
					var flag = false;
			
					for(var i = 0 ; i < oldSplit.length; i++){
						flag = false;
						for(var j = 0 ; j < newSplit.length; j++){
							if(oldSplit[i] == newSplit[j]){
								flag = true;
							}
							
						}
						if(flag == false){
							result.push(oldSplit[i]);
						}
					}
					
					var array = refresh1.replace(/[\[\]\"]+/g, '');
					array = array.replace(/\s/g, '');
					array = array.split(",");
					
					var amount = parseInt(array[0]);
					var stateList = [];
					var productList = [];
					var priceList = [];
					var tempList = [];

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
					
					
					for(var x = 0; x < stateSplit.length; x++){
						for(var y = 0 ; y < result.length; y++ ){
							document.getElementById(result[y]).style.backgroundColor = "purple";
							document.getElementById(stateSplit[x]+"_"+result[y]).style.backgroundColor = "purple";
							
						}
					}

					for(var i = 0; i < stateList.length; i++){
						var priceChange = parseInt(priceList[i]) + parseInt(document.getElementById(stateList[i]+"_"+productList[i]).innerHTML);
						var priceState = parseInt(priceList[i]) + parseInt(document.getElementById(stateList[i]).innerHTML);
						
						document.getElementById(stateList[i]+"_"+productList[i]).innerHTML = priceChange;
						document.getElementById(stateList[i]+"_"+productList[i]).style.color = "red";
						document.getElementById(stateList[i]).style.color = "red";
						document.getElementById(productList[i]).style.color = "red";
						tempList.push(stateList[i]+"_"+productList[i]);
						tempList.push(stateList[i]);
						tempList.push(productList[i]);
					}			
					
					
					document.cookie = tempList;
					if(amount === 0){
						for(var i = 0; i < temp.length; i++){
							document.getElementById(temp[i]).style.color = "black";
						}
					}
				}
			}
		}
	</script>
	<body>
		<%
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
				<td><button value="Refresh" onclick='getRequest()'>Refresh</button></td>
					<%
						while(counter < products.size()){
					%>
				<td id="<%= products.get(counter) %>"><%= products.get(counter) %> ($<%= productsTotal.get(counter) %>)</td>
					<%
							counter++;
						}
						counter = 0;
					%>
				<td><button value="Refresh" onclick='getRequest()'>Refresh</button></td>
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
				<td><button value="Refresh" onclick='getRequest()'>Refresh</button></td>
		</table>
		<%
					}
				}
			}
		%>
	</body>
</html>