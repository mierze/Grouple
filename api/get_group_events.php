<?php
	include_once('../db_connect.inc.php');
	//ensure all inputs have been sent
	if(!isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id)";
		echo json_encode ($response);
		exit();
	}
	//initialize arrays
	$response = array();
	$response["pending"] = array();
	$response["past"] = array();
	$response["upcoming"] = array();
	//upcoming event fetch
	$upcomingStmt = $mysqli->prepare("SELECT e.e_id, e.e_name, e.min_part, e.max_part, e.start_date from events e join e_groups eg on e.e_id = eg.e_id where eg.g_id = ? and e.eventstate = 'Confirmed'");
	$upcomingStmt->bind_param('s', $_POST['id']);
	$upcomingStmt->execute();
	$upcomingStmt->bind_result($id, $name, $minPart, $maxPart, $startDate);
	$upcomingStmt->store_result();
	$row_cnt = $upcomingStmt->num_rows;
	if($row_cnt > 0)
	{
		while ($upcomingStmt->fetch())
		{
			$result = array();
			$result["id"] = $id;
			$result["name"] = $name;
			$result["minPart"] = $minPart;
			$result["maxPart"] = $maxPart;
			$result["startDate"] = $startDate;
			array_push($response["upcoming"], $result);
		}
	}
	$upcomingStmt->close();
	//pending event fetch
	$pendingStmt = $mysqli->prepare("SELECT e.e_id, e.e_name, e.min_part, e.max_part, e.start_date from events e join e_groups eg on e.e_id = eg.e_id where eg.g_id = ? and e.eventstate = 'Proposed'");
	$pendingStmt->bind_param('s', $_POST['id']);
	$pendingStmt->execute();
	$pendingStmt->bind_result($id, $name, $minPart, $maxPart, $startDate);
	$pendingStmt->store_result();
	$row_cnt = $pendingStmt->num_rows;
	if($row_cnt > 0)
	{
		while ($pendingStmt->fetch())
		{
			$result = array();
			$result["id"] = $id;
			$result["name"] = $name;
			$result["minPart"] = $minPart;
			$result["maxPart"] = $maxPart;
			$result["startDate"] = $startDate;
			array_push($response["pending"], $result);
		}
	}
	$pendingStmt->close();
	//past event fetch
	$pastStmt = $mysqli->prepare("SELECT e.e_id, e.e_name, e.min_part, e.max_part, e.start_date from events e join e_groups eg on e.e_id = eg.e_id where eg.g_id = ? and e.eventstate = 'Ended'");
	$pastStmt->bind_param('s', $_POST['id']);
	$pastStmt->execute();
	$pastStmt->bind_result($id, $name, $minPart, $maxPart, $startDate);
	$pastStmt->store_result();
	$row_cnt = $pastStmt->num_rows;
	if($row_cnt > 0)
	{
		while ($pastStmt->fetch())
		{
			$result = array();
			$result["id"] = $id;
			$result["name"] = $name;
			$result["minPart"] = $minPart;
			$result["maxPart"] = $maxPart;
			$result["startDate"] = $startDate;
			array_push($response["past"], $result);
		}
	}
	$pastStmt->close();
	$response["success"] = 1;
	$response["message"] = "Successfully fetched events for group!";
	echo(json_encode($response));
	$mysqli->close();
?>
	