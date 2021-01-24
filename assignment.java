import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;
import org.newsclub.net.unix.AFUNIXSocketFactory;

class assignment {

	private static String readEntry(String prompt) {
		try {
			StringBuffer buffer = new StringBuffer();
			System.out.print(prompt);
			System.out.flush();
			int c = System.in.read();
			while(c != '\n' && c != -1) {
				buffer.append((char)c);
				c = System.in.read();
			}
			return buffer.toString().trim();
		} catch (IOException e) {
			return "";
		}
 	}

	public static void main(String args[]) throws SQLException, IOException {
		// You should only need to fetch the connection details once
		Connection conn = getConnection();
		boolean menu_flag=true;

		// Code to present a looping menu, read in input data and call the appropriate option menu goes here
		// You may use readEntry to retrieve input data
		// Act on selected option
		while(menu_flag){
		// Display simple menu
		System.out.println("Assignment (written in VSCode)");
        System.out.println("Select a function:");
        System.out.println("1. Purchase in store");
        System.out.println("2. Purchase by collection");
        System.out.println("3. Purchase by delivery");
		System.out.println("4. List the best sellers");
        System.out.println("5. List and delete the orders that should be collected/delivered 8 days before a given date");
        System.out.println("6. List staff who have sold at least 50000 pounds");
		System.out.println("7. List the name of staffs who sold at least one of products which has been sold over 20000");
        System.out.println("8. List the name of staffs who sold at least one of products which has been sold over 20000 and over 30000 of products in a particular year");
		System.out.println("0. quit");
		// Wait for user input
		String selectedOption = readEntry("Enter your option: ");
        if (selectedOption.equals("1")) {
			ArrayList<Integer> productIDs = new ArrayList<Integer>();
			ArrayList<Integer> quantitys = new ArrayList<Integer>();
			boolean option1_flag = true;
			int current_product_id=0;
			int quantity=0;
			String sold_date="";
			int staff_id=0;

			while(option1_flag){
			boolean product_input_flag=true;
			while(product_input_flag){
				String string_product_id = readEntry("Enter a product ID: ");
				try {
					current_product_id = Integer.valueOf(string_product_id).intValue();
					//check if id exist
					if(isProductID_Valid(conn,current_product_id)){
						if(productIDs.contains(current_product_id)){
							System.out.println("this product id has been added, please try another one.");
						}
						else{
							productIDs.add(current_product_id);
							product_input_flag=false;
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("invalid input, please try again.");
				}
			}

			boolean quantity_input_flag=true;
			while(quantity_input_flag){
				String string_quantity = readEntry("Enter the quantity sold: ");
				try {
					quantity = Integer.valueOf(string_quantity).intValue();
					//check if the quantity is more than inventory
					int index=productIDs.get(productIDs.size()-1);
					//the index is the product id
					if (is_quantity_Valid(conn,index,quantity)){
						quantitys.add(quantity);
						quantity_input_flag=false;
					}
				} catch (NumberFormatException e) {
					System.out.println("invalid input, please try again.");
				}
			}

			boolean Is_another_product_input_flag=true;
			while(Is_another_product_input_flag){
				String Is_another_product = readEntry("Is there another product in the order?(Y/N): ");
				if (Is_another_product.equals("Y")){
					Is_another_product_input_flag=false;
					System.out.println("there is another product in the order");
				} else if (Is_another_product.equals("N")){
					option1_flag = false;
					Is_another_product_input_flag=false;
					boolean Is_date_valid_input_flag=true;
					while(Is_date_valid_input_flag){
						String input_date= readEntry("Enter the date sold(dd-Mon-yy): ");
						try {
							DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
							Date utilDate = new SimpleDateFormat("dd-MMM-yy").parse(input_date);
							String current_date = formatter.format(utilDate);
							sold_date = current_date;
							Is_date_valid_input_flag=false;
						} catch (Exception e) {
							System.out.println("input date is not valid");
						}
					}

					boolean stuff_id_input_flag=true;
					while(stuff_id_input_flag){
						String string_staff_id= readEntry("Enter your staff ID: ");
						try {
							staff_id = Integer.valueOf(string_staff_id).intValue();
							if(isStuffID_Valid(conn,staff_id)){
								stuff_id_input_flag=false;
							}
						} catch (NumberFormatException e) {
							System.out.println("invalid input.");
						}
					}

				} else{
					System.out.println("invalid input, please try again.");
				}
			}
			}
			int[] productIDsArray = new int[productIDs.size()];
			for(int i = 0; i < productIDs.size(); i++){
				productIDsArray[i] = productIDs.get(i);
			}
			int[] quantityArray = new int[quantitys.size()];
			for(int k = 0; k < quantitys.size(); k++){
				quantityArray[k] = quantitys.get(k);
			}
			option1(conn,productIDsArray,quantityArray,sold_date, staff_id);
        } else if (selectedOption.equals("2")) {
			ArrayList<Integer> productIDs = new ArrayList<Integer>();
			ArrayList<Integer> quantitys = new ArrayList<Integer>();
			boolean option2_flag = true;
			int current_product_id=0;
			int quantity=0;
			String sold_date="";
			String collection_date="";
			String first_name="";
			String last_name="";
			int staff_id=0;

			while(option2_flag){
				boolean product_input_flag=true;
				while(product_input_flag){
					String string_product_id = readEntry("Enter a product ID: ");
					try {
						current_product_id = Integer.valueOf(string_product_id).intValue();
						//check if id exist
						if(isProductID_Valid(conn,current_product_id)){
							if(productIDs.contains(current_product_id)){
								System.out.println("this product id has been added, please try another one.");
							}
							else{
								productIDs.add(current_product_id);
								product_input_flag=false;
							}
						}
					} catch (NumberFormatException e) {
						System.out.println("invalid input, please try again.");
					}
				}

				boolean quantity_input_flag=true;
				while(quantity_input_flag){
					String string_quantity = readEntry("Enter the quantity sold: ");
					try {
						quantity = Integer.valueOf(string_quantity).intValue();
						//check if the quantity is more than inventory
						int index=productIDs.get(productIDs.size()-1);
						//the index is the product id
						if (is_quantity_Valid(conn,index,quantity)){
							quantitys.add(quantity);
							quantity_input_flag=false;
						}
					} catch (NumberFormatException e) {
						System.out.println("invalid input, please try again.");
					}
				}

				boolean Is_another_product_input_flag=true;
				while(Is_another_product_input_flag){
					String Is_another_product = readEntry("Is there another product in the order?(Y/N): ");
					if (Is_another_product.equals("Y")){
						Is_another_product_input_flag=false;
						System.out.println("there is another product in the order");
					} else if (Is_another_product.equals("N")){
						option2_flag = false;
						Is_another_product_input_flag=false;
						boolean Is_date_valid_input_flag=true;
						while(Is_date_valid_input_flag){
							String input_date= readEntry("Enter the date sold(dd-Mon-yy): ");
							try {
								//check input format
								DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
								Date util_sold_Date = new SimpleDateFormat("dd-MMM-yy").parse(input_date);
								String current_date = formatter.format(util_sold_Date);
								sold_date = current_date;
								Is_date_valid_input_flag=false;
							} catch (Exception e) {
								System.out.println("input date is not valid");
							}
						}

						boolean collection_date_valid_flag=true;
						while(collection_date_valid_flag){
							String collection_date_input= readEntry("Enter the date collection(dd-Mon-yy): ");
							try {
								//check input format
								DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
								Date util_collection_Date = new SimpleDateFormat("dd-MMM-yy").parse(collection_date_input);
								String current_date = formatter.format(util_collection_Date);
								collection_date = current_date;
								//compare sold date with collection date
								Date util_sold_Date = new SimpleDateFormat("dd-MMM-yy").parse(sold_date);
								if(util_sold_Date.before(util_collection_Date)){
									collection_date_valid_flag=false;
								}
								else{
									System.out.println("collection date should be later than the sold date");
								}
							} catch (Exception e) {
								System.out.println("input date is not valid");
							}
						}
						boolean first_name_input_flag=true;
						while(first_name_input_flag){
							String string_first_name= readEntry("Enter collector first name: ");
							try {
								//check if first name is string
								if(string_first_name.matches("^[a-zA-Z]+$")){
									first_name= string_first_name;
									first_name_input_flag=false;
								}
								else{
									System.out.println("input contains invalid character.");
								}
							} catch (Exception e) {
								System.out.println("invalid input.");
							}
						}
						boolean last_name_input_flag=true;
						while(last_name_input_flag){
							String string_last_name= readEntry("Enter collector last name: ");
							try {
								//check if last name is string
								if(string_last_name.matches("^[a-zA-Z]+$")){
									last_name=string_last_name;
									last_name_input_flag=false;
								}

								else{
									System.out.println("input contains number.");
								}
							} catch (NumberFormatException e) {
								System.out.println("invalid input.");
							}
						}

						boolean stuff_id_input_flag=true;
						while(stuff_id_input_flag){
							String string_staff_id= readEntry("Enter your staff ID: ");
							try {
								staff_id = Integer.valueOf(string_staff_id).intValue();
								if(isStuffID_Valid(conn,staff_id)){
									stuff_id_input_flag=false;
								}
							} catch (NumberFormatException e) {
								System.out.println("invalid input.");
							}
						}
					} else{
						System.out.println("invalid input, please try again.");
					}
				}
				}
				int[] productIDsArray = new int[productIDs.size()];
				for(int i = 0; i < productIDs.size(); i++){
					productIDsArray[i] = productIDs.get(i);
				}
				int[] quantityArray = new int[quantitys.size()];
				for(int k = 0; k < quantitys.size(); k++){
					quantityArray[k] = quantitys.get(k);
				}
				option2(conn,productIDsArray, quantityArray,sold_date,collection_date,first_name,last_name,staff_id);
        } else if (selectedOption.equals("3")) {

			ArrayList<Integer> productIDs = new ArrayList<Integer>();
			ArrayList<Integer> quantitys = new ArrayList<Integer>();
			int current_product_id=0;
			int quantity=0;
			String sold_date="";
			String collection_date="";
			String first_name="";
			String last_name="";
			String house_name="";
			String street="";
			String city="";
			int staff_id=0;
            System.out.println("3");
			boolean option3_flag=true;
			while(option3_flag){
				boolean product_input_flag=true;
				while(product_input_flag){
					String string_product_id = readEntry("Enter a product ID: ");
					try {
						current_product_id = Integer.valueOf(string_product_id).intValue();
						//check if id exist
						if(isProductID_Valid(conn,current_product_id)){
							if(productIDs.contains(current_product_id)){
								System.out.println("this product id has been added, please try another one.");
							}
							else{
								productIDs.add(current_product_id);
								product_input_flag=false;
							}
						}
					} catch (NumberFormatException e) {
						System.out.println("invalid input, please try again.");
					}
				}

				boolean quantity_input_flag=true;
				while(quantity_input_flag){
					String string_quantity = readEntry("Enter the quantity sold: ");
					try {
						quantity = Integer.valueOf(string_quantity).intValue();
						//check if the quantity is more than inventory
						int index=productIDs.get(productIDs.size()-1);
						//the index is the product id
						if (is_quantity_Valid(conn,index,quantity)){
							quantitys.add(quantity);
							quantity_input_flag=false;
						}
					} catch (NumberFormatException e) {
						System.out.println("invalid input, please try again.");
					}
				}

				boolean Is_another_product_input_flag=true;
				while(Is_another_product_input_flag){
					String Is_another_product = readEntry("Is there another product in the order?(Y/N): ");
					if (Is_another_product.equals("Y")){
						Is_another_product_input_flag=false;
						System.out.println("there is another product in the order");
					} else if (Is_another_product.equals("N")){
						option3_flag = false;
						Is_another_product_input_flag=false;
						boolean Is_date_valid_input_flag=true;
						while(Is_date_valid_input_flag){
							String input_date= readEntry("Enter the date sold(dd-Mon-yy): ");
							try {
								//check input format
								DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
								Date util_sold_Date = new SimpleDateFormat("dd-MMM-yy").parse(input_date);
								String current_date = formatter.format(util_sold_Date);
								sold_date = current_date;
								Is_date_valid_input_flag=false;
							} catch (Exception e) {
								System.out.println("input date is not valid");
							}
						}

						boolean collection_date_valid_flag=true;
						while(collection_date_valid_flag){
							String collection_date_input= readEntry("Enter the date of Delivery(dd-Mon-yy): ");
							try {
								//check input format
								DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
								Date util_collection_Date = new SimpleDateFormat("dd-MMM-yy").parse(collection_date_input);
								String current_date = formatter.format(util_collection_Date);
								collection_date = current_date;
								//compare sold date with collection date
								Date util_sold_Date = new SimpleDateFormat("dd-MMM-yy").parse(sold_date);
								if(util_sold_Date.before(util_collection_Date)){
									collection_date_valid_flag=false;
								}
								else{
									System.out.println("collection date should be later than the sold date");
								}
							} catch (Exception e) {
								System.out.println("input date is not valid");
							}
						}
						boolean first_name_input_flag=true;
						while(first_name_input_flag){
							String string_first_name= readEntry("Enter reciever first name: ");
							try {
								//check if first name is string
								if(string_first_name.matches("^[a-zA-Z]+$")){
									first_name= string_first_name;
									first_name_input_flag=false;
								}
								else{
									System.out.println("input contains invalid character.");
								}
							} catch (Exception e) {
								System.out.println("invalid input.");
							}
						}
						boolean last_name_input_flag=true;
						while(last_name_input_flag){
							String string_last_name= readEntry("Enter reciever last name: ");
							try {
								//check if last name is string
								if(string_last_name.matches("^[a-zA-Z]+$")){
									last_name=string_last_name;
									last_name_input_flag=false;
								}

								else{
									System.out.println("input contains invalid character.");
								}
							} catch (NumberFormatException e) {
								System.out.println("invalid input.");
							}
						}

						boolean house_name_input_flag=true;
						while(house_name_input_flag){
							String string_house_name= readEntry("Enter the house name/no : ");
							try {
								//check if house name is null
								if(string_house_name.length()!=0){
									house_name=string_house_name;
									house_name_input_flag=false;
								}
								else{
									System.out.println("No input!.");
								}
							} catch (NumberFormatException e) {
								System.out.println("invalid input.");
							}
						}

						boolean street_name_input_flag=true;
						while(street_name_input_flag){
							String string_street_name= readEntry("Enter the street: ");
							try {
								//check if house name is null
								if(string_street_name.length()!=0){
									street=string_street_name;
									street_name_input_flag=false;
								}
								else{
									System.out.println("No input!.");
								}
							} catch (NumberFormatException e) {
								System.out.println("invalid input.");
							}
						}

						boolean city_input_flag=true;
						while(city_input_flag){
							String string_City_name= readEntry("Enter the City: ");
							try {
								//check if last name is string
								if(string_City_name.matches("^[a-zA-Z]+$")){
									city=string_City_name;
									city_input_flag=false;
								}
								else{
									System.out.println("input contains invalid character.");
								}
							} catch (NumberFormatException e) {
								System.out.println("invalid input.");
							}
						}

						boolean stuff_id_input_flag=true;
						while(stuff_id_input_flag){
							String string_staff_id= readEntry("Enter your staff ID: ");
							try {
								staff_id = Integer.valueOf(string_staff_id).intValue();
								if(isStuffID_Valid(conn,staff_id)){
									stuff_id_input_flag=false;
								}
							} catch (NumberFormatException e) {
								System.out.println("invalid input.");
							}
						}
					} else{
						System.out.println("invalid input, please try again.");
					}
				}
				}
				int[] productIDsArray = new int[productIDs.size()];
				for(int i = 0; i < productIDs.size(); i++){
					productIDsArray[i] = productIDs.get(i);
				}
				int[] quantityArray = new int[quantitys.size()];
				for(int k = 0; k < quantitys.size(); k++){
					quantityArray[k] = quantitys.get(k);
				}
				option3(conn,productIDsArray, quantityArray,sold_date,collection_date,first_name,last_name,house_name,street,city,staff_id);
        } else if (selectedOption.equals("4")) {
			option4(conn);
        } else if (selectedOption.equals("5")) {
			boolean Is_date_valid_input_flag=true;
			while(Is_date_valid_input_flag){
				String input_date= readEntry("Enter the date:(dd-Mon-yy): ");
				try {
					//check input format
					DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
					Date util_sold_Date = new SimpleDateFormat("dd-MMM-yy").parse(input_date);
					String current_date = formatter.format(util_sold_Date);
					Is_date_valid_input_flag=false;
					option5(conn,current_date);
				} catch (Exception e) {
					System.out.println("input date is not valid");
				}
			}
        } else if (selectedOption.equals("6")) {
            option6(conn);
        } else if (selectedOption.equals("7")) {
            option7(conn);
        } else if (selectedOption.equals("8")) {
			int year=0;
			boolean Is_Year_valid_input_flag=true;
			while(Is_Year_valid_input_flag){
				String input_date= readEntry("Enter the year:(YYYY): ");
				try {
					//check the form of input date.
					if(input_date.length()>4){
						System.out.println("input date should be 4 digits");
					}
					else{
						year = Integer.valueOf(input_date).intValue();
						Is_Year_valid_input_flag=false;
						option8(conn, year);
					}

				} catch (Exception e) {
					System.out.println("input date is not valid");
				}
			}
        } else if (selectedOption.equals("0")) {
			menu_flag=false;
        } else {
			System.out.println("No valid option given...exiting.");
        }
		}

		conn.close();
	}

	/**
	 * @param conn       An open database connection
	 * @param productIDs An array of productIDs associated with an order
	 * @param quantities An array of quantities of a product. The index of a
	 *                   quantity correspeonds with an index in productIDs
	 * @param orderDate  A string in the form of 'DD-Mon-YY' that represents the
	 *                   date the order was made
	 * @param staffID    The id of the staff member who sold the order
	 * @throws SQLException
	 */
	public static void option1(Connection conn, int[] productIDs, int[] quantities, String orderDate, int staffID)
			throws SQLException {
		try {
			conn.setAutoCommit(false);
			Date utilDate = new SimpleDateFormat("dd-MMM-yy").parse(orderDate);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			String insertOrder = "call INSERT_ORDERS_INSTORE_OPTION_ONE('"+sqlDate+"','"+staffID+"');";
			Statement statement = conn.createStatement();
			statement.execute(insertOrder);
			statement.close();
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}

		for(int i =0; i<productIDs.length; i++){
			try {
				conn.setAutoCommit(false);
				String Edit_Inventory = "select EDIT_INVENTORY("+productIDs[i]+","+quantities[i]+")";
				PreparedStatement prepared_statement = conn.prepareStatement(Edit_Inventory);
				ResultSet result = prepared_statement.executeQuery();
				while(result.next()){
					System.out.println(" Product ID "+productIDs[i]+" stock is now at "+result.getInt(1));
				}
				prepared_statement.close();
				result.close();
			} catch (SQLException e) {
				if(conn !=null){
					try{
						conn.rollback();
						conn.commit();
					}catch(Exception a){
						System.out.println("rollback fail: "+a.getMessage());
					}
				}
				System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
			} catch (Exception e) {
				if(conn !=null){
					try{
						conn.rollback();
						conn.commit();
					}catch(Exception a){
						System.out.println("rollback fail: "+a.getMessage());
					}
				}
				e.printStackTrace();
			}
			try{
				conn.commit();
				conn.setAutoCommit(true);
			}catch(Exception e){
				System.out.println("rollback fail: "+e.getMessage());
			}
		}
	}

	/**
	* @param conn An open database connection
	* @param productIDs An array of productIDs associated with an order
        * @param quantities An array of quantities of a product. The index of a quantity correspeonds with an index in productIDs
	* @param orderDate A string in the form of 'DD-Mon-YY' that represents the date the order was made
	* @param collectionDate A string in the form of 'DD-Mon-YY' that represents the date the order will be collected
	* @param fName The first name of the customer who will collect the order
	* @param LName The last name of the customer who will collect the order
	* @param staffID The id of the staff member who sold the order
	*/
	public static void option2(Connection conn, int[] productIDs, int[] quantities, String orderDate, String collectionDate, String fName, String LName, int staffID) {
		try {
			conn.setAutoCommit(false);
			Date utilDate = new SimpleDateFormat("dd-MMM-yy").parse(orderDate);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			Date utilCollectionDate = new SimpleDateFormat("dd-MMM-yy").parse(collectionDate);
			java.sql.Date sqlCollectionDate= new java.sql.Date(utilCollectionDate.getTime());
			//transfer the input date into sql form
			String insertOrder = "call INSERT_ORDERS_COLLECTION_OPTION_TWO('"+sqlDate+"','"+staffID+"','"+fName+"','"+LName+"','"+sqlCollectionDate+"');";
			Statement statement = conn.createStatement();
			statement.execute(insertOrder);
			statement.close();
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}

		for(int i =0; i<productIDs.length; i++){
			try {
				conn.setAutoCommit(false);
				String Edit_Inventory = "select EDIT_INVENTORY("+productIDs[i]+","+quantities[i]+")";
				PreparedStatement prepared_statement = conn.prepareStatement(Edit_Inventory);
				ResultSet result = prepared_statement.executeQuery();
				while(result.next()){
					System.out.println(" Product ID "+productIDs[i]+" stock is now at "+result.getInt(1));
				}
				prepared_statement.close();
				result.close();
			} catch (SQLException e) {
				if(conn !=null){
					try{
						conn.rollback();
						conn.commit();
					}catch(Exception a){
						System.out.println("rollback fail: "+a.getMessage());
					}
				}
				System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
			} catch (Exception e) {
				if(conn !=null){
					try{
						conn.rollback();
						conn.commit();
					}catch(Exception a){
						System.out.println("rollback fail: "+a.getMessage());
					}
				}
				e.printStackTrace();
			}
			try{
				conn.commit();
				conn.setAutoCommit(true);
			}catch(Exception e){
				System.out.println("rollback fail: "+e.getMessage());
			}
		}
	}

	/**
	* @param conn An open database connection
	* @param productIDs An array of productIDs associated with an order
        * @param quantities An array of quantities of a product. The index of a quantity correspeonds with an index in productIDs
	* @param orderDate A string in the form of 'DD-Mon-YY' that represents the date the order was made
	* @param deliveryDate A string in the form of 'DD-Mon-YY' that represents the date the order will be delivered
	* @param fName The first name of the customer who will receive the order
	* @param LName The last name of the customer who will receive the order
	* @param house The house name or number of the delivery address
	* @param street The street name of the delivery address
	* @param city The city name of the delivery address
	* @param staffID The id of the staff member who sold the order
	*/
	public static void option3(Connection conn, int[] productIDs, int[] quantities, String orderDate, String deliveryDate, String fName, String LName,
				   String house, String street, String city, int staffID) {
		try {
			conn.setAutoCommit(false);
			Date utilDate = new SimpleDateFormat("dd-MMM-yy").parse(orderDate);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			Date utilDeliveryDate = new SimpleDateFormat("dd-MMM-yy").parse(deliveryDate);
			java.sql.Date sqlDeliveryDate= new java.sql.Date(utilDeliveryDate.getTime());
			String insertOrder = "call INSERT_ORDERS_DELIVERY_OPTION_THREE('"+sqlDate+"','"+staffID+"','"+fName+"','"+LName+"','"+sqlDeliveryDate+"','"+house+"','"+street+"','"+city+"');";
			Statement statement = conn.createStatement();
			statement.execute(insertOrder);
			statement.close();
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}

		for(int i =0; i<productIDs.length; i++){
			try {
				conn.setAutoCommit(false);
				String Edit_Inventory = "select EDIT_INVENTORY("+productIDs[i]+","+quantities[i]+")";
				PreparedStatement prepared_statement = conn.prepareStatement(Edit_Inventory);
				ResultSet result = prepared_statement.executeQuery();
				while(result.next()){
					System.out.println(" Product ID "+productIDs[i]+" stock is now at "+result.getInt(1));
				}
				prepared_statement.close();
				result.close();
			} catch (SQLException e) {
				if(conn !=null){
					try{
						conn.rollback();
						conn.commit();
					}catch(Exception a){
						System.out.println("rollback fail: "+a.getMessage());
					}
				}
				System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
			} catch (Exception e) {
				if(conn !=null){
					try{
						conn.rollback();
						conn.commit();
					}catch(Exception a){
						System.out.println("rollback fail: "+a.getMessage());
					}
				}
				e.printStackTrace();
			}
			try{
				conn.commit();
				conn.setAutoCommit(true);
			}catch(Exception e){
				System.out.println("rollback fail: "+e.getMessage());
			}
		}
	}

	/**
	* @param conn An open database connection
	*/
	public static void option4(Connection conn) {
		// not finish
		// Incomplete - Code for option 4 goes here
		try {
			conn.setAutoCommit(false);
			String BestSeller = "SELECT * from total_price_sold;";
			PreparedStatement prepared_statement = conn.prepareStatement(BestSeller);
			ResultSet result = prepared_statement.executeQuery();
			while(result.next()){
				System.out.println(result.getInt(1)+" | "+result.getString(2)+" | "+result.getDouble(3));
			}
			prepared_statement.close();
			result.close();
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}

	}

	/**
	* @param conn An open database connection
	* @param date The target date to test collection deliveries against
	*/
	public static void option5(Connection conn, String date) {
		try {
			conn.setAutoCommit(false);
			Date utilDate = new SimpleDateFormat("dd-MMM-yy").parse(date);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()-8*864*100000);
			//make the input date is 8 days ago
			String option5_command_1 = "SELECT * from ORDERS where orderplaced<='"+sqlDate+"' and OrderCompleted=0;";
			int orderID=0;
			PreparedStatement prepared_statement = conn.prepareStatement(option5_command_1);
			ResultSet result = prepared_statement.executeQuery();
			while(result.next()){
				orderID=result.getInt(1);
				System.out.println("Order "+orderID+" has been cancelled");
			}
			prepared_statement.close();
			result.close();
			String option5_command_2 = "call OPTION_FIVE('"+sqlDate+"');";
			Statement statement = conn.createStatement();
			statement.execute(option5_command_2);
			statement.close();

		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}

		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}
	}

	/**
	* @param conn An open database connection
	*/
	public static void option6(Connection conn) {
		// Incomplete - Code for option 6 goes here
		try {
			conn.setAutoCommit(false);
			String BestSeller = "select * from Life_time_final;";
			PreparedStatement prepared_statement = conn.prepareStatement(BestSeller);
			ResultSet result = prepared_statement.executeQuery();
			System.out.println("EmployeeName,  \tTotalValueSold");
			while(result.next()){
				System.out.println(result.getString(1)+" "+result.getString(2)+", \t"+result.getDouble(3));
			}
			prepared_statement.close();
			result.close();
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}
	}

	/**
	* @param conn An open database connection
	*/
	public static void option7(Connection conn) {
		// Incomplete - Code for option 7 goes here
		ArrayList<Integer> pid_array = new ArrayList<Integer>();
		ArrayList<String> fnameArray = new ArrayList<String>();
		ArrayList<String> lnameArray = new ArrayList<String>();
		try {
			conn.setAutoCommit(false);

			String GET_ROW_NUMBER = "select * from GET_ROW_NUMBER();";
			PreparedStatement prepared_statement = conn.prepareStatement(GET_ROW_NUMBER);
			ResultSet result = prepared_statement.executeQuery();

			String GET_COLUMN_NUMBER = "select * from GET_COLUMN_NUMBER();";
			prepared_statement = conn.prepareStatement(GET_COLUMN_NUMBER);
			result = prepared_statement.executeQuery();

			String OPTION7_1 = "select * from Life_Time4;";
			prepared_statement = conn.prepareStatement(OPTION7_1);
			result = prepared_statement.executeQuery();
			while(result.next()){
				fnameArray.add(result.getString(1));
				lnameArray.add(result.getString(2));
			}

			String OPTION7_2 = "select DISTINCT ProductID from test6;";
			prepared_statement = conn.prepareStatement(OPTION7_2);
			result = prepared_statement.executeQuery();
			while(result.next()){
				pid_array.add(result.getInt(1));
			}

			String OPTION7_3 = "select * from test6;";
			prepared_statement = conn.prepareStatement(OPTION7_3);
			result = prepared_statement.executeQuery();

			System.out.print("Full name,   ");
			for(int i =0;i<pid_array.size();i++){
				System.out.print("\tProductID"+pid_array.get(i)+"   ");
			}
			System.out.println("");

			for(int j=0;j<fnameArray.size();j++){
				System.out.print(fnameArray.get(j)+" "+lnameArray.get(j)+"  \t");
				for(int i =0;i<pid_array.size();i++){
					try {
						String check_value = "select productquantity from test6 where ProductID="+pid_array.get(i)+" and fname='"+fnameArray.get(j)+"' and lname='"+lnameArray.get(j)+"';";
						PreparedStatement check = conn.prepareStatement(check_value);
						ResultSet test = check.executeQuery();
						int t=1;
						while(test.next()){
							t=0;
							System.out.print("\t , "+test.getInt(1));
						}
						if(t==1){
							System.out.print("\t , "+" 0  ");
						}

					} catch (SQLException e) {
						System.out.print(" \t, "+" 0  ");
					} catch (Exception e){
						System.out.print(" \t, "+" 0  ");
					}
				}
				System.out.println("");
			}
			prepared_statement.close();
			result.close();
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}
	}


	/**
	* @param conn An open database connection
	* @param year The target year we match employee and product sales against
	*/
	public static void option8(Connection conn, int year) {
		// Incomplete - Code for option 8 goes here
		try {
			conn.setAutoCommit(false);
			String command1 = "CREATE OR REPLACE VIEW OPTION8_STEP1 AS "
			+"(SELECT view_c.ProductID "
			+"FROM INVENTORY view_c "
			+"INNER JOIN "
			+"(SELECT view_a.ProductID, SUM(ProductQuantity) AS ProductQuantity "
			+"FROM ORDER_PRODUCTS view_a "
			+"INNER JOIN ORDERS view_b "
			+"ON view_a.OrderID = view_b.OrderID "
			+"WHERE extract(year from view_b.OrderPlaced) ="+year+" "//here is the input year
			+"GROUP BY view_a.ProductID) view_d "
			+"ON view_c.ProductID = view_d.ProductID "
			+"WHERE ProductQuantity * ProductPrice > 20000); "; //find the product sold amount more than 20000

			String command2 = "SELECT FName, LName "
			+"FROM STAFF view_z INNER JOIN "
			+"(SELECT StaffID "
			+"FROM "
			+"(SELECT StaffID, COUNT(DISTINCT ProductID) AS NumUniqueProductsSold "
			+"FROM ORDER_PRODUCTS view_x "
			+"INNER JOIN "
			+"(SELECT OrderID, view_a.StaffID "
			+"FROM STAFF_ORDERS view_a "
			+"INNER JOIN "
			+"(SELECT StaffID FROM "
			+"(SELECT StaffID, SUM(ProductQuantity * ProductPrice) AS TotalValueSold "
			+"FROM INVENTORY view_c "
			+"INNER JOIN "
			+"(SELECT ProductID, ProductQuantity, StaffID "
			+"FROM ORDER_PRODUCTS view_e "
			+"INNER JOIN "
			+"(SELECT view_a.OrderID, StaffID "
			+"FROM STAFF_ORDERS view_a "
			+"INNER JOIN ORDERS view_b "
			+"ON view_a.OrderID = view_b.OrderID "
			+"WHERE extract(year from view_b.OrderPlaced) = "+year+") view_f "
			+"ON view_e.OrderID = view_f.OrderID) view_d "
			+"ON view_c.ProductID = view_d.ProductID "
			+"GROUP BY StaffID) AS derivedTable2 "
			+"WHERE TotalValueSold >= 30000) view_b " //amount should be large than 30000
			+"ON view_a.StaffID = view_b.StaffID) view_y "
			+"ON view_x.OrderID = view_y. OrderID "
			+"WHERE ProductID IN "
			+"(SELECT ProductID FROM OPTION8_STEP1) "
			+"GROUP BY StaffID) AS derivedTable4 "
			+"WHERE NumUniqueProductsSold = (SELECT COUNT(DISTINCT ProductID) FROM OPTION8_STEP1)) view_w "
			+"ON view_z.StaffID = view_w.StaffID;";
			PreparedStatement create_option8_view = conn.prepareStatement(command1);
			create_option8_view.executeUpdate();
			PreparedStatement option8_final = conn.prepareStatement(command2);
			ResultSet result = option8_final.executeQuery();

			System.out.format("\n%-30s\n", "EmployeeName ");
			while (result.next()) {
				System.out.format("%-30s\n", result.getString(1) + " " + result.getString(2));
			}

			create_option8_view.close();
			option8_final.close();
			result.close();
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}
	}

	public static Date stringToDate(String source) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy");
		Date date = null;
		try {
			date = simpleDateFormat.parse(source);
		} catch (Exception e) {
		}
		return date;
	}

	public static boolean isProductID_Valid(Connection conn,int ProductID) {
		try {
			conn.setAutoCommit(false);
			String Test_If_Exist = "SELECT * FROM INVENTORY WHERE ProductID="+ProductID+";";
			PreparedStatement prepared_test_statement = conn.prepareStatement(Test_If_Exist);
			ResultSet result_of_test = prepared_test_statement.executeQuery();
			if(!result_of_test.next()){
				System.out.println("the current_product_id does not exist!");
				prepared_test_statement.close();
				result_of_test.close();
				return false;
			}
			prepared_test_statement.close();
			result_of_test.close();
			return true;

		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}
		return false;
	}

	public static boolean is_quantity_Valid(Connection conn,int index,int quantity){
		try {
			conn.setAutoCommit(false);
			String MaxStockAmountRequest = "SELECT ProductStockAmount FROM INVENTORY WHERE ProductID="+index+";";
			PreparedStatement prepared_statement = conn.prepareStatement(MaxStockAmountRequest);
			ResultSet result = prepared_statement.executeQuery();
			int MaxStockAmount=0;
			while(result.next()){
				System.out.println(result.getInt(1));
				MaxStockAmount=result.getInt(1);
			}
			if(MaxStockAmount>=quantity&&quantity>0){
				System.out.println("ready for update");
				return true;
			}
			else{
				System.out.println("quantity is invalid or larger than maximum of stock amount");
			}
			prepared_statement.close();
			result.close();
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}
		return false;
	}

	public static boolean isStuffID_Valid(Connection conn,int staff_id) {
		try {
			conn.setAutoCommit(false);
			String Test_If_Exist = "SELECT * FROM STAFF WHERE StaffID="+staff_id+";";
			PreparedStatement prepared_test_statement = conn.prepareStatement(Test_If_Exist);
			ResultSet result_of_test = prepared_test_statement.executeQuery();
			if(!result_of_test.next()){
				System.out.println("the current_staff_id does not exist!");
				prepared_test_statement.close();
				result_of_test.close();
				return false;
			}
			prepared_test_statement.close();
			result_of_test.close();
			return true;
		} catch (SQLException e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			System.err.format("SQL State: %s\n%s\n\n", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			if(conn !=null){
				try{
					conn.rollback();
					conn.commit();
				}catch(Exception a){
					System.out.println("rollback fail: "+a.getMessage());
				}
			}
			e.printStackTrace();
		}
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(Exception e){
			System.out.println("rollback fail: "+e.getMessage());
		}
		return false;
	}

	public static boolean isNumeric(String str){

		for(int i=str.length();--i>=0;){
		   int chr=str.charAt(i);
		   if(str.equals("")||str==null)
			  return false;
		   if(chr>=48 && chr<=57)
			  return false;
		}
		return true;
	 }


    public static Connection getConnection(){
        Properties props = new Properties();
        props.setProperty("socketFactory", "org.newsclub.net.unix.AFUNIXSocketFactory$FactoryArg");

        props.setProperty("socketFactoryArg",System.getenv("PGHOST") + "/.s.PGSQL.5432");
        Connection conn;
        try{
          conn = DriverManager.getConnection("jdbc:postgresql://localhost/deptstore", props);
          return conn;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
	}

/*    public static Connection getConnection() {
        //This version of getConnection uses ports to connect to the server rather than sockets
        //If you use this method, you should comment out the above getConnection method, and comment out lines 19 and 21
        String user = "me";
        String passwrd = "mypassword";
        Connection conn;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException x) {
            System.out.println("Driver could not be loaded");
        }

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:15432/deptstore?user="+ user +"&password=" + passwrd);

            return conn;
        } catch(SQLException e) {
                e.printStackTrace();
            System.out.println("Error retrieving connection");
            return null;
        }

    }*/


}
