create table counterone(
counter int);

create table countertwo(
counter int);

create table precomp as
with overall_table as 
(select pc.product_id,c.state_id,sum(pc.price*pc.quantity) as amount  
 	from products_in_cart pc  
 	inner join shopping_cart sc on (sc.id = pc.cart_id and sc.is_purchased = true)
 	inner join product p on (pc.product_id = p.id) -- add category filter if any
 	inner join person c on (sc.person_id = c.id)
 	group by pc.product_id,c.state_id
),
top_state as
(select state_id, sum(amount) as dollar from (
	select state_id, amount from overall_table
	UNION ALL
	select id as state_id, 0.0 as amount from state
	) as state_union
 group by state_id order by dollar desc
),
top_n_state as 
(select row_number() over(order by dollar desc) as state_order, state_id, dollar from top_state
),
top_prod as 
(select product_id, sum(amount) as dollar from (
	select product_id, amount from overall_table
	UNION ALL
	select id as product_id, 0.0 as amount from product
	) as product_union
group by product_id order by dollar desc 
),
top_n_prod as 
(select row_number() over(order by dollar desc) as product_order, product_id, dollar from top_prod
)
select ts.state_id, s.state_name, tp.product_id, pr.product_name, (select cg.category_name from category cg where pr.category_id = cg.id ) ,COALESCE(ot.amount, 0.0) as cell_sum, ts.dollar as state_sum, tp.dollar as product_sum
	from top_n_prod tp CROSS JOIN top_n_state ts 
	LEFT OUTER JOIN overall_table ot 
	ON ( tp.product_id = ot.product_id and ts.state_id = ot.state_id)
	inner join state s ON ts.state_id = s.id
	inner join product pr ON tp.product_id = pr.id 
	order by ts.state_order, tp.product_order

create table log as
select state.id, state.state_name, product.product_name,category.category_name , 0 as product_sum 
from state,product,category
where category.id = product.category_id;

create table log(
	state_names text,
	product_names text,
	category_names text,
	product_sums double precision
);


Create function updatelog() returns trigger as
$body$
	begin
    	if(old.is_purchased = false and new.is_purchased = true)
        then   
        		CREATE TABLE temps as (select state.state_name, product.product_name, category.category_name, products_in_cart.price * products_in_cart.quantity as product_sum
										from product,state,person,shopping_cart,products_in_cart,category
										where shopping_cart.id = new.id and
										shopping_cart.id = products_in_cart.cart_id and
										shopping_cart.person_id = person.id and 
										person.state_id = state.id and product.category_id = category.id and 
										products_in_cart.product_id = product.id);
				
				INSERT INTO log
				SELECT * from temps;
				
				drop table temps;
               
       end if;
    return new;           
    end;
$body$
language plpgsql;

create trigger logtrigger
before update on shopping_cart
for each row
execute procedure updatelog();


Create function grouplog() returns trigger as
$body$
	begin 
    create table temps as (select state_names, product_names, category_names, sum(product_sums)as product_sums
                from log group by 
                state_names, product_names, category_names);
                
    			delete from log;
                insert into log
                select * from temps;
                drop table temps;
    
    return new;           
    end;
$body$
language plpgsql;


create trigger counter1trigger
before update on counterone
for each row
execute procedure grouplog();



Create function updateprecomp() returns trigger as
$body$
	begin 
 	    update precomp set state_sum = state_sum + log.product_sums
            from log
            where state_name = log.state_names;
            
            update precomp set product_sum = product_sum + log.product_sums
            from log
            where product_name = log.product_names;
            
            update precomp set cell_sum = cell_sum + log.product_sums
            from log
            where product_name = log.product_names and state_name = log.state_names;

	    delete from log;
    
    return new;           
    end;
$body$
language plpgsql;


create trigger counter2trigger
before update on countertwo
for each row
execute procedure updateprecomp();

insert into counterone values(0);
insert into countertwo values(0);


