<?php
	//This php will return the complete profile of a given user in the users table

	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(isset($_POST['email']) && $_POST['reg_id'])
	{
		$email = $_POST['email'];
        $regid = $_POST['reg_id'];
		//$stmt = $mysqli->prepare("INSERT into `chat_ids` (email, reg_id) values (?, ?)");
        $stmt = $mysqli->prepare("SELECT EMAIL FROM CHAT_IDS WHERE EMAIL  = ?");
		$stmt->bind_param('s', $email);
	}
	else
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email)";
		echo json_encode ( $result );
		exit();	
	}

	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($id);
	$row_cnt = $stmt->num_rows;
	
	if($row_cnt > 0)
	{	
		//Then we need to update the reg id.
		$stmt = $mysqli->prepare("UPDATE `chat_ids` SET reg_id = ? WHERE email = ?");
		$stmt->bind_param('ss', $regid, $email);
		$stmt->execute();

		//Check for success
		if($mysqli->affected_rows > 0)
		{
			$stmt->fetch();
			$response["success"] = 1;
			$response["response"] = "You have sucessfully added a chat id!";
		}
		else
		{
			$response["success"] = 2;
			$response["response"] = "Error on updating the chat id.";
		}	
	}
	else
	{
		//Else we just insert.
		$stmt = $mysqli->prepare("INSERT into `chat_ids` (email, reg_id) values (?, ?)");
		$stmt->bind_param('ss', $email, $regid);
		$stmt->execute();
				
		//Check for success
		if($mysqli->affected_rows > 0)
		{
			$stmt->fetch();
			$response["success"] = 1;
			$response["response"] = "You have sucessfully added a chat id!";
		}
		else
		{
			$response["success"] = 2;
			$response["response"] = "Error on inserting the chat id.";
		}
	}	
	
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>