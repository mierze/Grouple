<?php
	//This php will return the complete profile of a given user in the users table

	header('Content-type: application/json');
	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(isset($_POST['email']))
	{
		$email = $_POST['email'];
		$stmt = $mysqli->prepare("SELECT reg_id FROM `chat_ids` WHERE email = ?");
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
		$stmt->fetch();
		$response["success"] = 1;
		$response["regid"] = $id;	
	}
	else
	{
		$response["success"] = 2;
		$response["message"] = "Invalid email address!";		
	}	
	
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
