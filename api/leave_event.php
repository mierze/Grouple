<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (email or id)";
		echo json_encode($response);
		exit();
	} 
	$id = $_POST['id'];
	$email = $_POST['email'];
	$stmt = $mysqli->prepare("SELECT rec_date from e_members WHERE email = ? AND e_id = ?");
	if($stmt === false)
	{
		echo "Error querying server.";
	}	
	$stmt->bind_param('ss', $email, $id);
	if($stmt->execute())
	{
		$stmt->bind_result($recDate);
		$stmt->store_result();
		$stmt->fetch();
	}
	$stmt = $mysqli->prepare("DELETE from e_members WHERE email = ? AND e_id = ?");	
	if($stmt === false)
	{
		echo "Error querying server.";
	}
	$stmt->bind_param('ss', $email, $id);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$response["success"] = 1;
			if($recDate === NULL)
			{			
				$response["message"] = "Successfully declined event invite!";
			}
			else
			{
				$response["message"] = "You have left an event.";				
			}
			echo json_encode($response);
			//clear out credit for any groups this user had been invited from.			
			$stmt = $mysqli->prepare("DELETE from e_groups WHERE email = ? AND e_id = ?");
			if($stmt === false)
			{
				echo "Error querying server.";
			}		
			$stmt->bind_param('ss', $email, $id);
			$stmt->execute();
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "Error, user not found in given group. Email:" . $email . ", ID:" . $id;
			echo json_encode($response);
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "Failed to write to database";
		echo json_encode($response);
	}		
	$mysqli->close();
?>