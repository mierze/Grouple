<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$response = array();
	if(!isset($_GET['id']) || !isset($_GET['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT sender FROM friends WHERE receiver = ? AND rec_date is NULL");
	$stmt->bind_param('s', $_GET['id']);
	$stmt->execute();
	$stmt->bind_result($sender);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{	//TODO: return first and last for visuals
		$result = array();
		$response["success"] = 1;
		$response["items"] = array();	
		while($stmt->fetch())
		{
			$result["id"] = $sender;
			array_push($response["items"], $result);
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "You don't have any friends yet!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>

