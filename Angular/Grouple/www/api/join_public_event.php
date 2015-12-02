<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['role']) || !isset($_POST['e_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, role, e_id)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$role = $_POST['role'];
	$e_id = $_POST['e_id'];
	
	//check to see if event is currently joinable before adding member
	$stmt = $mysqli->prepare("SELECT joinable FROM events WHERE e_id = ?");
	if($stmt === false)
	{
		$result["success"] = 0;
		$result["message"] = "Invalid SQL statement.(#1)";
		echo json_encode ( $result );
		exit();
	}

	$stmt->bind_param('s', $e_id);
	
	if($stmt->execute())
	{
	    $stmt->bind_result($joinable);
		$stmt->store_result();
		$stmt->fetch();
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Failed to add member.";
	    echo json_encode ( $result );
		exit();
	}		
			
	//check value of joinable. must be "Yes" to add new members
	if($joinable === 'Yes')
	{
		//adding a non-creator to e_members table		
		$stmt = $mysqli->prepare("INSERT INTO e_members (email, e_id, role, rec_date) 
		VALUES (?,?,?,CURRENT_TIMESTAMP)");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "Invalid SQL statement.(#2)";
			echo json_encode ( $result );
			exit();
		}

		$stmt->bind_param('sss', $email, $e_id, $role);
	
		if($stmt->execute())
		{
			$result["success"] = 1;
			$result["message"] = "You have joined the event!";
			echo json_encode ( $result );
		}
		else
		{
			$result["success"] = 0;
			$result["message"] = "Failed to join event.";
			echo json_encode ( $result );
		}		
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Failed to join event.";
		echo json_encode ( $result );
	}
			
	
	$mysqli->close();
?>