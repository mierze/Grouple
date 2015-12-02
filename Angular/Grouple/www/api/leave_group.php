<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['user']) || !isset($_POST['id']) || !isset($_POST['type']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (email, id, type)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("DELETE from g_members WHERE email = ? AND g_id = ?");
	$stmt->bind_param('ss', $_POST['user'], $_POST['id']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$response["success"] = 1;
			if($_POST['type'] == 'decline')		
				$response["message"] = "Successfully declined group invite!";
			else
				$response["message"] = "You have left group.";				
		}
		else
		{
			$response["success"] = -1;
			$response["message"] = "Error, user not found in given group. Email:" . $email . ", ID:" . $id;
		}
	}
	else
	{
		$response["success"] = -2;
		$response["message"] = "Failed to write to database";
	}
	echo json_encode($response);
	$mysqli->close();
	exit();
?>