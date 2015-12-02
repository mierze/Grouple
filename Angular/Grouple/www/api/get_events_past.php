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
	$stmt = $mysqli->prepare("SELECT events.e_id, events.e_name, events.min_part, events.max_part, e_members.sender, e_members.rec_date, events.start_date FROM events JOIN e_members ON e_members.e_id = events.e_id where e_members.rec_date is not null and e_members.email = ? and events.eventstate = 'Ended' AND e_members.hidden is false order by events.start_date DESC");
	$stmt->bind_param('s', $_GET['id']);
	$stmt->execute();
	$stmt->bind_result($id, $name, $min, $max, $sender, $recDate, $startDate);
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
			$result["minPart"] = $min;
			$result["maxPart"] = $max;
			$result["sender"] = $sender;
			$result["recDate"] = $recDate;
			$result["startDate"] = $startDate;
			array_push($response["items"], $result);
		}	 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "You don't have any past events!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
