<?php

	include_once('../db_connect.inc.php');

	//ensure all inputs have been sent
	if(!(isset($_POST['g_id']) || isset($_POST['e_id'])) || !isset($_POST['email']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (g_id / e_id, email)";
		echo json_encode ( $result );
		exit();
	}
	if (isset($_POST['g_id']))
	{
		$id = $_POST['g_id'];
                $stmt = $mysqli->prepare("SELECT count(gm.g_id) FROM g_messages gm join g_members gmem on gmem.g_id = gm.g_id WHERE gm.g_id = ? and gmem.last_message_read < gm.send_date and gmem.email = ?");
	}
	else
	{
		$id = $_POST['e_id'];
		$stmt = $mysqli->prepare("SELECT count(em.e_id) FROM e_messages em join e_members emem on emem.e_id = em.e_id WHERE em.e_id = ? and emem.last_message_read < em.send_date and emem.email = ?");
	}
	$email = $_POST['email'];
	
	$stmt->bind_param('ss', $id, $email);
	$stmt->execute();
	$stmt->bind_result($numUnread);
	$stmt->store_result();	
	$row_cnt = $stmt->num_rows;

	if($row_cnt > 0)
	{
		$response["success"] = 1;
		$stmt->fetch();
		$response["numUnread"] = $numUnread;

	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No messages to display."; 
	}

	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
