<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id)";
		echo json_encode($response);
		exit();
	}
	$id = $_POST['id'];
	$stmt = $mysqli->prepare("SELECT gm.message, gm.sender, gm.send_date, u.first, u.last FROM g_messages gm join users u WHERE g_id = ? and gm.sender = u.email ORDER BY `send_date` ASC");
	$stmt->bind_param('s', $id);
	$stmt->execute();
	$stmt->bind_result($message, $sender, $sendDate, $first, $last);
	$stmt->store_result();	
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		$result = array();
		$response["success"] = 1;
		$response["messages"] = array();
		while($stmt->fetch())
		{
			$result["sendDate"] = $sendDate;
			$result["message"] = $message;
			$result["sender"] = $sender;
			$result["first"] = $first;
			$result["last"] = $last;
			array_push($response["messages"], $result);
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No group messages to display!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
