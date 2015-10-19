<?php

	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['e_id']))
	{
		$result["success"] = -1;
		$result["message"] = "Missing required POST parameters (e_id)";
		echo json_encode ( $result );
		exit();
	} 

	$stmt = $mysqli->prepare("SELECT CHAT_IDS.EMAIL, REG_ID FROM CHAT_IDS,EVENTS,E_MEMBERS WHERE EVENTS.E_ID = ? AND E_MEMBERS.E_ID = EVENTS.E_ID AND E_MEMBERS.EMAIL = CHAT_IDS.EMAIL AND E_MEMBERS.REC_DATE IS NOT NULL");
	$stmt->bind_param('s', $_POST['e_id']);
	$stmt->execute();
	$stmt->bind_result($email, $id);
	$stmt->store_result();

	
	$row_cnt = $stmt->num_rows;

	$response = array();
	if($row_cnt > 0)
	{
		$response["success"] = 1;
		$response["reg_ids"] = array();
		while($stmt->fetch())
		{
			$result = array();
			$result["reg_id"] = $id;
			$result["email"] = $email;
			array_push($response["reg_ids"], $result);
		}
		 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No notifications to send"; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
	
	

?>