<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	//ensure all inputs have been sent
	if(!isset($_POST['message']) || !isset($_POST['user']) || !isset($_POST['contact']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (message, user, contact)";
		echo json_encode($response);
		exit();
	} 	
	$message = $_POST['message'];
	$user = $_POST['user'];
	$contact = $_POST['contact'];
	$stmt = $mysqli->prepare("insert into messages (message, sender, receiver, send_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
	if($stmt === false)
	{
		$response["success"] = -1;
		$response["message"] = "Invalid SQL statement.(#1)";
		echo json_encode($response);
		exit();
	}
	$stmt->bind_param('sss', $message, $user, $contact);
	if($stmt->execute())
	{
	    $response["success"] = 1;
   	    $response["message"] = "Message sent successfully!";
   	    echo json_encode($response);
	}
	else
	{
	    $response["success"] = -2;
	    $response["message"] = "Message failed to send!";
	    echo json_encode($response);
	}		
	$mysqli->close();
?>