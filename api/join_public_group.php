<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email'])  || !isset($_POST['role']) || !isset($_POST['g_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, role, g_id)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$role = $_POST['role'];
	$g_id = $_POST['g_id'];

	//adding a creator (rec_date set automatically),
	//therefore, creator will automatically become a member of the group.
	
			
	$stmt = $mysqli->prepare("INSERT INTO g_members (email, g_id, role, rec_date) 
	VALUES (?,?,?, CURRENT_TIMESTAMP)");
	if($stmt === false)
	{
		$result["success"] = 0;
		$result["message"] = "Invalid SQL statement.(#1)";
		echo json_encode ( $result );
		exit();
	}

	$stmt->bind_param('sss', $email, $g_id, $role);
	

	if($stmt->execute())
	{
	    $result["success"] = 1;
   	    $result["message"] = "You have joined the group!";
   	    echo json_encode ( $result );
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Failed to join group.";
	    echo json_encode ( $result );
	}		
	
	$mysqli->close();
?>