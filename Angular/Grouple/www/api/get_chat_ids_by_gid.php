<?php

	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if (!(isset($_POST['g_id']) || isset($_POST['e_id'])) || !isset($_POST['email']))
	{
		$result["success"] = -1;
		$result["message"] = "Missing required POST parameters (g_id or e_id,email)";
		echo json_encode ( $result );
		exit();
	}
	if (isset($_POST['e_id']))
	{
		$id = $_POST['e_id'];
		$stmt = $mysqli->prepare("SELECT chat.reg_id FROM chat_ids chat, e_members members WHERE chat.email = members.email AND members.e_id = ? AND chat.email != ? AND members.rec_date IS NOT NULL");
	}
	else
	{
		$id = $_POST['g_id'];
		$stmt = $mysqli->prepare("SELECT chat.reg_id FROM chat_ids chat, g_members members WHERE chat.email = members.email AND members.g_id = ? AND chat.email != ?");
	}

	$email = $_POST['email'];
	
	
	$stmt->bind_param('ss', $id,$email);
	$stmt->execute();
	$stmt->bind_result($chat_id);
	$stmt->store_result();	
	$row_cnt = $stmt->num_rows;

	if($row_cnt > 0)
	{
		$result = array();
		$response["success"] = 1;
		$response["chat_ids"] = array();

		while($stmt->fetch())
		{
			$result["chat_id"] = $chat_id;

			array_push($response["chat_ids"], $result);
		}
	}
	else
	{
		$response["success"] = 0;
		$response["chat_ids"] = "No chat ids."; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>