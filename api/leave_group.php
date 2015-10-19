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
	$stmt = $mysqli->prepare("DELETE from g_members WHERE email = ? AND g_id = ?");
	$stmt = $mysqli->prepare("SELECT rec_date from g_members WHERE email = ? AND g_id = ?");
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
	if($stmt === false)
	{
		echo "Error querying server";
	}
	$id = $_POST['id'];
	$email = $_POST['email'];
	$stmt->bind_param('ss', $email, $id);
	if($stmt->execute())
	{
		if($mysqli->affected_rows > 0)
		{
			$response["success"] = 1;
			if($recDate === NULL)
			{			
				$response["message"] = "Successfully declined group invite!";
			}
			else
			{
				$response["message"] = "You have left a group.";				
			}
			echo json_encode($response);
		}
		else
		{
			$response["success"] = -1;
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