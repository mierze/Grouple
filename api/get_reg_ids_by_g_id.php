<?php

	header('Content-type: application/json');

	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['g_id']))
	{
		$result["success"] = -1;
		$result["message"] = "Missing required POST parameters (g_id)";
		echo json_encode ( $result );
		exit();
	} 

	$stmt = $mysqli->prepare("SELECT REG_ID, CHAT_IDS.EMAIL FROM CHAT_IDS,GROUPS,G_MEMBERS WHERE GROUPS.G_ID = ? AND G_MEMBERS.G_ID = GROUPS.G_ID AND G_MEMBERS.EMAIL = CHAT_IDS.EMAIL");
	$stmt->bind_param('s', $_POST['g_id']);
	$stmt->execute();
	$stmt->bind_result($id, $email);
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