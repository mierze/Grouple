<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	//PANDA use user to check that no one is
	if(!isset($_POST['id']) || !isset($_POST['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id or user)";
		echo json_encode($response);
		exit();
	}
	else if (strcmp($_POST['id'], $_POST['user']) != 0)
	{
		//logged user should be same with declined events
		$response["success"] = -100;
		$response["message"] = "Detecting a mismatch in session IDs!";
		echo json_encode($response);
		exit();
	}
	$stmt = $mysqli->prepare("SELECT e.e_id, e.e_name, e.min_part, e.max_part, e.start_date FROM events e JOIN e_members em ON em.e_id = e.e_id WHERE creator = ? AND eventstate = 'Declined' AND declined is false AND em.email = e.creator AND em.hidden is false ORDER BY start_date DESC");
	$stmt->bind_param('s', $_POST['email']);
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
