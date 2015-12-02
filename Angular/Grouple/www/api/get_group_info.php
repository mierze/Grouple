<?php
	//This php will return the complete profile of a given user in the users table
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_GET['id']) || !isset($_GET['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT g_name, about, creator, public, date_created FROM groups WHERE g_id = ?");
	$stmt->bind_param('s', $_GET['id']);
	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($name, $about, $creator, $public, $dateCreated);
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		$stmt->fetch();
		$result = array();
		$result["id"] = $_GET["id"];
		$result["name"] = $name;
		$result["creator"] = $creator;
		$result["public"] = $public;
		$result["about"] = $about;
		$result["dateCreated"] = $dateCreated;
		$response["info"] = $result;
		$response["success"] = 1;
	}
	else
	{
		$response["success"] = -1;
		$response["message"] = "Invalid ID supplied!";		
	}	
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>