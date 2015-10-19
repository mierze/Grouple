<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['id']) || !isset($_POST['user']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (id or user)";
		echo json_encode($response);
		exit();
	} 
	$stmt = $mysqli->prepare("SELECT e.e_id, e.e_name, e.min_part, e.max_part, e.start_date, em.sender FROM events e JOIN e_members em ON em.e_id = e.e_id where em.rec_date IS NULL and em.email = ?");
	$stmt->bind_param('s', $_POST['id']);
	$stmt->execute();
	$stmt->bind_result($id, $name, $min, $max, $start, $sender);
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
			$result["sender"] = $sender;
			$response["items"][] = $result;
		}	 
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "You have no event invites!"; 
	}
	$stmt->close();
	echo(json_encode($response));
	$mysqli->close();
?>
