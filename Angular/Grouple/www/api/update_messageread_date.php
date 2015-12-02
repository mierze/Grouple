<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['receiver']) || !isset($_POST['sender']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (sender, receiver)";
		echo json_encode ( $result );
		exit();
	} 
	
	$sender = $_POST['sender'];
	$receiver = $_POST['receiver'];

	//updating a read_date of two users
	$stmt = $mysqli->prepare("UPDATE messages set read_date = CURRENT_TIMESTAMP where sender = ? AND receiver = ?");
	if($stmt === false)
	{
		$result["success"] = 0;
		$result["message"] = "Invalid SQL statement.(#1)";
		echo json_encode ( $result );
		exit();
	}
	
	$stmt->bind_param('ss', $sender, $receiver);

	if($stmt->execute())
	{
	    $result["success"] = 1;
   	    $result["message"] = "Updated message read_date successfully.";
   	    echo json_encode ( $result );
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Failed to update read_date.";
	    echo json_encode ( $result );
	}		
	
	$mysqli->close();
?>