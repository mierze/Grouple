<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_GET['id']) || !isset($_GET['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required GET parameters (id or user)";
		echo json_encode($response);
		exit();
	}
	$stmt = $mysqli->prepare("SELECT events.e_id, events.e_name, events.start_date FROM events JOIN e_members ON e_members.e_id = events.e_id where events.eventstate = 'Confirmed' and e_members.email = ? and events.start_date >= CURRENT_TIMESTAMP and e_members.rec_date is not null order by events.start_date");
	$stmt->bind_param('s', $_GET['id']);
	$stmt->execute();
	$stmt->bind_result($id, $name, $startDate);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		$result = array();
		$response["success"] = 1;
		$response["items"] = array();
		while($stmt->fetch())
		{
			$result["id"] = $id;
			$result["name"] = $name;
			$result["startDate"] = $startDate;
			array_push($response["items"], $result);
		}	 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No events upcoming!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
