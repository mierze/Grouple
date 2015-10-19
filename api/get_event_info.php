<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['id']) || !isset($_POST['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT e_name, eventstate, start_date, end_date, recurring_type, category, about, location, min_part, max_part, creator, public FROM events WHERE e_id = ?");
	$stmt->bind_param('s', $_POST['id']);
	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($name, $state, $startDate, $endDate, $recType, $category, $about, $location, $minPart, $maxPart, $creator, $public);
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		$stmt->fetch();
		$result = array();
		$result["id"] = $_POST['id'];
		$result["name"] = $name;
		$result["state"] = $state;
		$result["startDate"] = $startDate; 
		$result["endDate"] = $endDate;
		$result["recType"] = $recType;
		$result["category"] = $category;
		$result["about"] = $about;
		$result["location"] = $location;
		$result["minPart"] = $minPart;
		$result["maxPart"] = $maxPart;
		//PANDA make creator return name too make it a sub array
		$result["creator"] = $creator;
		$result["public"] = $public;
		$response["info"] =$result;
		$response["success"] = 1;
	}
	else
	{
		$response["success"] = 2;
		$response["message"] = "Invalid email address!";		
	}		
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>