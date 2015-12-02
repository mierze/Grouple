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
	else if (strcmp($_GET['id'], $_GET['user']) != 0)
	{
		//logged user should be same with declined events
		$response["success"] = -100;
		$response["message"] = "Detecting a mismatch in session IDs!";
		echo json_encode($response);
		exit();
	}
	$stmt = $mysqli->prepare("SELECT e.e_id, e.e_name, e.min_part, e.max_part, e.start_date FROM events e JOIN e_members em ON em.e_id = e.e_id WHERE creator = ? AND eventstate = 'Declined' AND declined is false AND em.email = e.creator AND em.hidden is false ORDER BY start_date DESC");
	$stmt->bind_param('s', $_GET['email']);
	$stmt->execute();
	$stmt->bind_result($id, $name, $min, $max, $start);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		$response["success"] = 1;
		$response["items"] = array();
		while($stmt->fetch())
		{
			$result = array();
			$result["id"] = $id;
			$result["name"] = $name;
			$result["minPart"] = $min;
			$result["maxPart"] = $max;
			$result["startDate"] = $start;
			array_push($response["items"], $result);
		}	 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "You have no declined events!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
