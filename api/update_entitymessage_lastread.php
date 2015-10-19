<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!(isset($_POST['g_id']) || isset($_POST['e_id'])) || !isset($_POST['email']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (g_id / e_id, email)";
		echo json_encode ( $result );
		exit();
	} 
	
	if (isset($_POST['g_id']))
	{
		$id = $_POST['g_id'];
		$stmt = $mysqli->prepare("UPDATE g_members set last_message_read = CURRENT_TIMESTAMP where g_id = ? AND email = ?");
	}
	else
	{
		$id = $_POST['e_id'];
		$stmt = $mysqli->prepare("UPDATE e_members set last_message_read = CURRENT_TIMESTAMP where e_id = ? AND email = ?");
	}
	$email = $_POST['email'];
	
	if($stmt === false)
	{
		$result["success"] = 0;
		$result["message"] = "Invalid SQL statement.(#1)";
		echo json_encode ( $result );
		exit();
	}
	$stmt->bind_param('ss', $id, $email);

	if($stmt->execute())
	{
	    $result["success"] = 1;
   	    $result["message"] = "Updated last message read date successfully.";
   	    echo json_encode ( $result );
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Failed to update last message read date.";
	    echo json_encode ( $result );
	}		
	
	$mysqli->close();
?>