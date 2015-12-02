<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['message']) || !isset($_POST['from']) || !isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (message, sender, id)";
		echo json_encode($response);
		exit();
	}
	$stmt = $mysqli->prepare("insert into e_messages (message, sender, e_id, send_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
	if($stmt === false)
	{
		$response["success"] = -1;
		$response["message"] = "Invalid SQL statement.";
		echo json_encode($response);
		exit();
	}
	$stmt->bind_param('sss', $_POST['message'], $_POST['from'], $_POST['id']);
	if($stmt->execute())
	{
	    $response["success"] = 1;
   	    $response["message"] = "Message sent successfully!";
   	    echo json_encode($response);
	}
	else
	{
	    $result["success"] = -2;
	    $result["message"] = "Message failed to send!";
	    echo json_encode($response);
	}		
	$mysqli->close();
?>