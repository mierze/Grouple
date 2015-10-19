<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['remove']) || !isset($_POST['role']) || !isset($_POST['g_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, remove, role, g_id)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$remove = $_POST['remove'];
	$role = $_POST['role'];
	$g_id = $_POST['g_id'];

	//updating a member
	if (strcmp($remove, "no") == 0) 
	{
		$stmt = $mysqli->prepare("UPDATE g_members set role = ? where email = ? and g_id = ?");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "Invalid SQL statement.(#1)";
			echo json_encode ( $result );
			exit();
		}
		$stmt->bind_param('ssi', $role, $email, $g_id);
	}
	//removing a user
	else
	{			
		$stmt = $mysqli->prepare("DELETE from g_members where email = ? and g_id = ?");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "Invalid SQL statement.(#1)";
			echo json_encode ( $result );
			exit();
		}

		$stmt->bind_param('si', $email, $g_id);
	}

	if($stmt->execute())
	{
	    $result["success"] = 1;
   	    $result["message"] = "Updated group member successfully.";
   	    echo json_encode ( $result );
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Failed to update group member.";
	    echo json_encode ( $result );
	}		
	
	$mysqli->close();
?>