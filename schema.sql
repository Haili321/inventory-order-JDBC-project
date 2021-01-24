DROP TABLE INVENTORY CASCADE;
CREATE TABLE INVENTORY (
  ProductID           integer primary key,
  ProductDesc         varchar(30) not null,
  ProductPrice        numeric(8,2) not null,
  ProductStockAmount  integer not null
);

DROP SEQUENCE ProductSequence;
CREATE SEQUENCE ProductSequence START 1 INCREMENT BY 1;

DROP TABLE ORDERS CASCADE;
CREATE TABLE ORDERS (
  OrderID         INTEGER PRIMARY KEY,
  OrderType       VARCHAR(30) NOT NULL,
  OrderCompleted  INTEGER NOT NULL,
  OrderPlaced     DATE NOT NULL
);

DROP SEQUENCE OrderSequence;
CREATE SEQUENCE OrderSequence START 1 INCREMENT BY 1;

DROP TABLE ORDER_PRODUCTS CASCADE;
CREATE TABLE ORDER_PRODUCTS (
  OrderID         INTEGER NOT NULL,
  ProductID       INTEGER NOT NULL,
  ProductQuantity INTEGER NOT NULL,
  PRIMARY KEY (OrderID, ProductID),
  FOREIGN KEY (OrderID)   REFERENCES ORDERS(OrderID) ON DELETE CASCADE,
  FOREIGN KEY (ProductID) REFERENCES INVENTORY(ProductID) ON DELETE CASCADE
);

DROP TABLE DELIVERIES CASCADE;
CREATE TABLE DELIVERIES (
  OrderID     INTEGER NOT NULL,
  FName    VARCHAR(30) NOT NULL,
  LName    VARCHAR(30) NOT NULL,
  House    VARCHAR(30) NOT NULL,
  Street   VARCHAR(30) NOT NULL,
  City     VARCHAR(30) NOT NULL,
  DeliveryDate  Date NOT NULL,
  FOREIGN KEY (OrderID) REFERENCES ORDERS(OrderID) ON DELETE CASCADE
);

DROP TABLE COLLECTIONS CASCADE;
CREATE TABLE COLLECTIONS (
  OrderID     INTEGER NOT NULL,
  FName    VARCHAR(30) NOT NULL,
  LName    VARCHAR(30) NOT NULL,
  CollectionDate  Date NOT NULL,
  FOREIGN KEY (OrderID) REFERENCES ORDERS(OrderID) ON DELETE CASCADE
);

DROP TABLE STAFF CASCADE;
CREATE TABLE STAFF (
  StaffID     INTEGER PRIMARY KEY,
  FName    VARCHAR(30) NOT NULL,
  LName    VARCHAR(30) NOT NULL
);

DROP SEQUENCE StuffSequence;
CREATE SEQUENCE StuffSequence START 1 INCREMENT BY 1;

DROP TABLE STAFF_ORDERS CASCADE;
CREATE TABLE STAFF_ORDERS (
  StaffID     INTEGER NOT NULL,
  OrderID     INTEGER NOT NULL,
  PRIMARY KEY (StaffID, OrderID),
  FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID) ON DELETE CASCADE,
  FOREIGN KEY (OrderID) REFERENCES ORDERS(OrderID) ON DELETE CASCADE
);

-------------------------Option1-3----------------------------
DROP PROCEDURE insert_orders_instore_option_one(date,integer);
CREATE OR REPLACE PROCEDURE INSERT_ORDERS_INSTORE_OPTION_ONE (orderDate DATE, Stuffid integer)
LANGUAGE plpgsql
AS $$
DECLARE
    Order_id integer;
BEGIN
    Order_id=nextval('OrderSequence');
    --get and store the order id
    INSERT INTO ORDERS VALUES (Order_id, 'InStore', 1, orderDate);
    --completeness is 1
    INSERT INTO STAFF_ORDERS VALUES (Stuffid,Order_id);
END
$$;

DROP PROCEDURE INSERT_ORDERS_COLLECTION_OPTION_TWO(date,integer,VARCHAR,VARCHAR,Date);
CREATE OR REPLACE PROCEDURE INSERT_ORDERS_COLLECTION_OPTION_TWO (orderDate DATE,Stuffid integer,first_name VARCHAR,last_name VARCHAR,Collection_Date DATE)
LANGUAGE plpgsql
AS $$
DECLARE
    Order_id integer;
BEGIN
    Order_id=nextval('OrderSequence');
    --get and store the order id
    INSERT INTO ORDERS VALUES (Order_id, 'Collection', 0, orderDate);
    --completeness is 0
    INSERT INTO STAFF_ORDERS VALUES (Stuffid,Order_id);
    INSERT INTO COLLECTIONS VALUES (Order_id, first_name, last_name, Collection_Date);
END
$$;

DROP PROCEDURE INSERT_ORDERS_DELIVERY_OPTION_THREE(date,integer,VARCHAR,VARCHAR,Date,VARCHAR,VARCHAR,VARCHAR);
CREATE OR REPLACE PROCEDURE INSERT_ORDERS_DELIVERY_OPTION_THREE (orderDate DATE,Stuffid integer,first_name VARCHAR,last_name VARCHAR,Delivery_Date DATE,house_input VARCHAR, street_input VARCHAR, city_input VARCHAR)
LANGUAGE plpgsql
AS $$
DECLARE
    Order_id integer;
BEGIN
    Order_id=nextval('OrderSequence');
    --get and store the order id
    INSERT INTO ORDERS VALUES (Order_id, 'Delivery', 0, orderDate);
    --completeness is 0
    --add to STAFF_ORDERS
    INSERT INTO STAFF_ORDERS VALUES (Stuffid,Order_id);
    --add to DELIVERIES
    INSERT INTO DELIVERIES VALUES (Order_id, first_name, last_name, house_input,street_input,city_input, Delivery_Date);
END
$$;

DROP FUNCTION EDIT_INVENTORY(integer,integer);
CREATE OR REPLACE FUNCTION EDIT_INVENTORY (product_id integer, quantity integer)
RETURNS integer AS $currentStock$
DECLARE
    currentStock integer;
    Order_id integer;
BEGIN
    currentStock=0;
    Order_id=0;
    SELECT Max(OrderID) into Order_id FROM STAFF_ORDERS;
    --get and store the order id
    select ProductStockAmount into currentStock from INVENTORY where ProductID=product_id;
    UPDATE INVENTORY SET ProductStockAmount=currentStock-quantity WHERE ProductID=product_id;
    currentStock=currentStock-quantity;
    --edit the stock amount
    INSERT INTO ORDER_PRODUCTS VALUES (Order_id, product_id, quantity);
    --add to ORDER_PRODUCTS
    return currentStock;
END
$currentStock$ LANGUAGE plpgsql;

-------------------------Option4----------------------------
CREATE VIEW total_quantity_sold AS
SELECT ProductID, sum(ProductQuantity) AS total_quantity FROM ORDER_PRODUCTS GROUP BY ProductID;

CREATE VIEW total_price_sold AS
SELECT INVENTORY.ProductID, INVENTORY.ProductDesc, (total_quantity_sold.total_quantity*INVENTORY.ProductPrice) AS TotalValueSold
FROM INVENTORY INNER JOIN total_quantity_sold ON INVENTORY.ProductID = total_quantity_sold.ProductID
GROUP BY INVENTORY.ProductID, INVENTORY.ProductDesc, total_quantity_sold.total_quantity, TotalValueSold
ORDER BY total_quantity_sold.total_quantity DESC;



-------------------------Option5----------------------------
DROP PROCEDURE OPTION_FIVE(date);
CREATE OR REPLACE PROCEDURE OPTION_FIVE (input_date date)
LANGUAGE plpgsql
AS $$
DECLARE
    currentStock integer;
    stock_add integer;
    Order_id integer;
    delete_times integer;
    product_id integer;
    product_items integer;
    i integer;
    n integer;
BEGIN
    delete_times=0;
    SELECT COUNT(*) into delete_times FROM ORDERS where orderplaced<=input_date and OrderCompleted=0;

    i := 1;
    --the first loop to get each order id
    while i<=delete_times loop
      currentStock=0;
      Order_id=0;
      stock_add=0;
      product_items=0;
      SELECT Max(OrderID) into Order_id FROM ORDERS where orderplaced<=input_date and OrderCompleted=0;
      SELECT COUNT(*) into product_items FROM ORDER_PRODUCTS where OrderID=Order_id;
      n := 1;
      --the second loop to get each product id under the same order id
      while n<=product_items loop
        product_id=0;
        --get and store product id
        SELECT Max(ProductID) into product_id FROM ORDER_PRODUCTS where OrderID=Order_id;
        SELECT ProductQuantity into stock_add FROM ORDER_PRODUCTS where OrderID=Order_id and ProductID= product_id;

        --add back stock amount
        SELECT ProductStockAmount into currentStock FROM INVENTORY where ProductID= product_id;
        UPDATE INVENTORY SET ProductStockAmount=currentStock+stock_add WHERE ProductID=product_id;
        DELETE FROM ORDER_PRODUCTS where OrderID = Order_id and ProductID = product_id;
        --delete this item after processing
        n := n+1;
      end loop;
      --delete any related orders
      DELETE FROM ORDER_PRODUCTS where OrderID=Order_id;
      DELETE FROM ORDERS where OrderID=Order_id;
      i := i+1;
    end loop;

END
$$;


-------------------------Option6----------------------------
--get the sum price of each order id
CREATE VIEW Life_Time AS
SELECT ORDER_PRODUCTS.OrderID, (ORDER_PRODUCTS.ProductQuantity*INVENTORY.ProductPrice) as sigle_product_price
FROM INVENTORY
INNER JOIN ORDER_PRODUCTS
ON INVENTORY.ProductID=ORDER_PRODUCTS.ProductID;

--combine result
CREATE VIEW Life_Time2 AS
SELECT OrderID, SUM(sigle_product_price) FROM Life_Time GROUP BY OrderID;

--inner join staff id to prepare to next step
CREATE VIEW Life_Time3 AS
SELECT STAFF_ORDERS.StaffID,Life_Time2.SUM
FROM STAFF_ORDERS
INNER JOIN Life_Time2
ON STAFF_ORDERS.OrderID=Life_Time2.OrderID
ORDER BY Life_Time2.SUM DESC;

--change staff id into staff name
CREATE VIEW Life_Time4 AS
SELECT STAFF.FName,STAFF.LName ,Life_Time3.SUM
FROM STAFF
INNER JOIN Life_Time3
ON STAFF.StaffID=Life_Time3.StaffID
ORDER BY Life_Time3.SUM DESC;

--select required amount of monetory
CREATE VIEW Life_Time_final AS
SELECT * FROM Life_Time4 WHERE SUM>50000;

-------------------------Option7----------------------------
DROP FUNCTION GET_ROW_NUMBER();
CREATE OR REPLACE FUNCTION GET_ROW_NUMBER ()
RETURNS integer AS $rows_number$
DECLARE
    rows_number integer;
BEGIN
    rows_number=0;

    SELECT COUNT(fname) into rows_number FROM test1;
    return rows_number;
END
$rows_number$ LANGUAGE plpgsql;

DROP FUNCTION GET_COLUMN_NUMBER();
CREATE OR REPLACE FUNCTION GET_COLUMN_NUMBER ()
RETURNS integer AS $column_number$
DECLARE
    column_number integer;
BEGIN
    column_number=0;

    SELECT COUNT(ProductID) into column_number FROM test2;
    return column_number;
END
$column_number$ LANGUAGE plpgsql;

CREATE VIEW test1 AS
SELECT FName,LName FROM Life_Time4;

CREATE VIEW test2 AS
SELECT ProductID FROM total_price_sold WHERE totalvaluesold>20000;

CREATE VIEW test3 AS
SELECT STAFF_ORDERS.StaffID, ORDER_PRODUCTS.ProductID, ORDER_PRODUCTS.ProductQuantity
FROM ORDER_PRODUCTS
INNER JOIN STAFF_ORDERS
ON ORDER_PRODUCTS.OrderID=STAFF_ORDERS.OrderID;

CREATE VIEW test4 AS
SELECT test3.StaffID, test3.ProductQuantity, test2.ProductID
FROM test3
INNER JOIN test2
ON test2.ProductID=test3.ProductID;

CREATE VIEW test5 AS
SELECT Life_Time3.StaffID, test4.ProductID, test4.ProductQuantity,Life_Time3.SUM
FROM test4
INNER JOIN Life_Time3
ON Life_Time3.StaffID=test4.StaffID
ORDER BY SUM DESC;

CREATE VIEW test6 AS
SELECT test5.productid, STAFF.FName,STAFF.LName ,test5.productquantity,test5.SUM
FROM STAFF
INNER JOIN test5
ON test5.StaffID=STAFF.StaffID
ORDER BY SUM DESC;

CREATE VIEW test7 AS
SELECT
  fname,
  lname,
  unnest(ARRAY ['productid' :: TEXT, 'productquantity' :: TEXT])
  AS descriton,
  unnest(
      ARRAY [productid :: NUMERIC, productquantity:: NUMERIC]) AS value
FROM test6;

-------------------------Option8(used in java)----------------------------
CREATE OR REPLACE VIEW OPTION8_STEP1 AS
(SELECT view_c.ProductID
FROM INVENTORY view_c
INNER JOIN
(SELECT view_a.ProductID, SUM(ProductQuantity) AS ProductQuantity
FROM ORDER_PRODUCTS view_a
INNER JOIN ORDERS view_b
ON view_a.OrderID = view_b.OrderID
WHERE extract(year from view_b.OrderPlaced) =2020--year input
GROUP BY view_a.ProductID) view_d
ON view_c.ProductID = view_d.ProductID
WHERE ProductQuantity * ProductPrice > 2);


CREATE OR REPLACE VIEW OPTION8 AS
SELECT FName, LName
FROM STAFF view_z
INNER JOIN
(SELECT StaffID
FROM
(SELECT StaffID, COUNT(DISTINCT ProductID) AS NumUniqueProductsSold
FROM ORDER_PRODUCTS view_x
INNER JOIN
(SELECT OrderID, view_a.StaffID
FROM STAFF_ORDERS view_a
INNER JOIN
(SELECT StaffID
FROM
(SELECT StaffID, SUM(ProductQuantity * ProductPrice) AS TotalValueSold
FROM INVENTORY view_c
INNER JOIN
(SELECT ProductID, ProductQuantity, StaffID
FROM ORDER_PRODUCTS view_e
INNER JOIN
(SELECT view_a.OrderID, StaffID
FROM STAFF_ORDERS view_a
INNER JOIN ORDERS view_b
ON view_a.OrderID = view_b.OrderID
WHERE extract(year from view_b.OrderPlaced) = 2020) view_f --year input
ON view_e.OrderID = view_f.OrderID) view_d
ON view_c.ProductID = view_d.ProductID
GROUP BY StaffID) AS derivedTable2
WHERE TotalValueSold >= 3) view_b --30000
ON view_a.StaffID = view_b.StaffID) view_y
ON view_x.OrderID = view_y. OrderID
WHERE ProductID IN
(SELECT ProductID FROM OPTION8_STEP1)
GROUP BY StaffID) AS derivedTable4
WHERE NumUniqueProductsSold = (SELECT COUNT(DISTINCT ProductID) FROM OPTION8_STEP1)) view_w
ON view_z.StaffID = view_w.StaffID;
