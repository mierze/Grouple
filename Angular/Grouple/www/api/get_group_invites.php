<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	//ensure all inputs have been sent
	if(!isset($_GET['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (id)";
		echo json_encode ($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT g.g_id, g.g_name, gm.sender FROM groups g JOIN g_members gm ON gm.g_id = g.g_id where gm.rec_date IS NULL and gm.email = ?");
	$stmt->bind_param('s', $_GET['id']);
	$stmt->execute();
	$stmt->bind_result($id, $name, $from);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
	$response = array();
	if($row_cnt > 0)
	{
		$result = array();
		$response["success"] = 1;
		$response["items"] = array();
		while($stmt->fetch())
		{
			$result["id"] = $id;
			$result["name"]= $name;
			$result["from"]= $from;
			array_push($response["items"], $result);
		}	 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "You have no group invites!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
