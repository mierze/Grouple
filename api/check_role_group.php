<?php

	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	
	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['gid']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, gid)";
		echo json_encode ($result );
		exit();
	} 

	$stmt = $mysqli->prepare("SELECT role from `g_members` WHERE email=? AND g_id=?");
	$stmt->bind_param('si', $_POST['email'], $_POST['gid']);
	$stmt->execute();
	$stmt->bind_result($role);
	$stmt->store_result();

	$result = array();
	$row_cnt = $stmt->num_rows;

	$response = array();
	if($row_cnt > 0)
	{
		$stmt->fetch();
		$response["success"] = 1;
		$response["role"] = $role;
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "SQL error"; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
	
	

?>
	