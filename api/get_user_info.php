<?php
	//This php will return the complete profile of a given user in the users table
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sen
	if(!isset($_POST['id']) || !isset($_POST['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT first, last, age, about, location, gender FROM users WHERE email = ?");
	$stmt->bind_param('s', $_POST['id']);
	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($first, $last, $birthday, $about, $location, $gender);
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		//PANDA: possibly parse date in here
		$stmt->fetch();
		$result = array();
		$result["id"] = $_POST["id"];
		$result["first"] = $first;
		$result["last"] = $last;
		$result["birthday"] = $birthday;
		$result["about"] = $about;
		$result["location"] = $location;
		$result["gender"] = $gender;
		$response["info"] = $result;
		$response["success"] = 1;
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No user info found!";		
	}	
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>