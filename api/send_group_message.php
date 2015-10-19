<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['msg']) || !isset($_POST['sender']) || !isset($_POST['g_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (msg, sender, g_id)";
		echo json_encode ( $result );
		exit();
	} 
	
	$msg= $_POST['msg'];
	$sender = $_POST['sender'];
	$g_id = $_POST['g_id'];

	$stmt = $mysqli->prepare("insert into g_messages (message, sender, g_id, send_date) values (?, ?, ?, CURRENT_TIMESTAMP)");
	if($stmt === false)
	{
		$result["success"] = 0;
		$result["message"] = "Invalid SQL statement.(#1)";
		echo json_encode ( $result );
		exit();
	}
	$stmt->bind_param('sss', $msg, $sender, $g_id);
	
	
	if($stmt->execute())
	{
	    $result["success"] = 1;
   	    $result["message"] = "Message sent successfully!";
   	    echo json_encode ( $result );
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Message failed to send!";
	    echo json_encode ( $result );
	}		
	
	$mysqli->close();
?>