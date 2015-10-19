<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	if(!isset($_POST['sender']) || !isset($_POST['receiver']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (sender or receiver)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT rec_date from friends WHERE ((sender = ? AND receiver = ?) OR (receiver = ? AND sender = ?))");
	if($stmt === false)
	{
		echo "Error querying server.";
	}	
	$stmt->bind_param('ssss', $_POST['receiver'], $_POST['sender'], $_POST['receiver'], $_POST['sender']);
	if($stmt->execute())
	{
		$stmt->bind_result($recDate);
		$stmt->store_result();
		$stmt->fetch();
	}
	$stmt = $mysqli->prepare("DELETE from friends WHERE ((sender = ? AND receiver = ?) OR (receiver = ? AND sender = ?))");
	if($stmt === false)
	{
		echo "Error querying server.";
	}
	$stmt->bind_param('ssss', $_POST['sender'], $_POST['receiver'], $_POST['sender'], $_POST['receiver']);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$response["success"] = 1;
			if ($recDate == null)
				$response["message"] = "Friend has been removed!";
			else
				$response["message"] = "Friend invite declined!";
			echo json_encode($response);
		}
		else
		{
			$response["success"] = -1;
			$response["message"] = "Friend was not found.";
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