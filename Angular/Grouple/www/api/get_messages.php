<?php
	//This php will return the complete profile of a given user in the users table
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_GET['user']) || !isset($_GET['contact']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (user or contact)";
		echo json_encode($response);
		exit();
	}
	$user = $_GET['user'];
	$contact = $_GET['contact'];
	$stmt = $mysqli->prepare("SELECT `send_date`, `message`, `sender`, `receiver` FROM `messages` WHERE sender = ? and receiver = ? OR sender = ? AND receiver = ? ORDER BY `SEND_DATE` ASC");
	$stmt->bind_param('ssss', $contact, $user, $user, $contact);
	$stmt->execute();
	$stmt->bind_result($sendDate, $message, $from, $to);
	$stmt->store_result();	
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		$result = array();
		$response["success"] = 1;
		$response["messages"] = array();
		while($stmt->fetch())
		{
			$result["side"] = strcmp($from, $user) == 0 ? "right" : "left";
			$result["sendDate"] = $sendDate;
			$result["to"] = $to;
			$result["from"] = $from;
			$result["message"] = $message;
			$result["contact"] = $contact;
			array_push($response["messages"], $result);
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No messages to display."; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
