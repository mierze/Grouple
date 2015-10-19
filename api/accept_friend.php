<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	if (!isset($_POST['sender']) || !isset($_POST['receiver']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (sender or receiver)";
		echo json_encode($response);
		exit();
	}
	$stmt = $mysqli->prepare("UPDATE friends SET rec_date = CURRENT_TIMESTAMP where sender = ? AND receiver = ?");
	$removeRequests = $mysqli->prepare("DELETE from friends WHERE sender = ? AND receiver = ? AND rec_date IS NULL");
	$stmt->bind_param('ss', $_POST['sender'], $_POST['receiver']);
	$removeRequests->bind_param('ss', $_POST['receiver'], $_POST['sender']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$response["success"] = 1;
			$response["message"] = "Friend invite accepted!";
			echo json_encode($response);
			$removeRequests->execute();
		}
		else
		{
			$response["success"] = -1;
			$response["message"] = "Friend invite was not found!";
			echo json_encode($response);
		}
	}
	else
	{
		$response["success"] = -2;
		$response["message"] = "Failed to write to database";
		echo json_encode($response);
	}	
	$mysqli->close();	
?>